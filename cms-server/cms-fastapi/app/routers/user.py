"""账号与登录（/user/*）：登录限流、修改密码、账号管理。"""
from datetime import datetime

from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import RedirectResponse
from sqlalchemy.orm import Session

from .. import deps
from ..config import PREFIX
from ..database import get_db
from ..models import Account
from ..utils import LoginRateLimiter, md5

router = APIRouter()


@router.post("/user/login")
def login(request: Request, username: str = Form(""), password: str = Form(""),
          db: Session = Depends(get_db)):
    key = f"{username}|{request.client.host if request.client else 'unknown'}"
    if LoginRateLimiter.is_locked(key):
        return deps.render(request, "login.html", msg="登录失败次数过多，请 10 分钟后再试")

    account = (
        db.query(Account)
        .filter(Account.username == username, Account.password == md5(password))
        .first()
    )
    if account is not None:
        LoginRateLimiter.on_success(key)
        request.session["global.account"] = {
            "id": account.id,
            "username": account.username,
        }
        return RedirectResponse(f"{PREFIX}/home", status_code=302)

    LoginRateLimiter.on_failure(key)
    return deps.render(request, "login.html", msg="用户名密码不存在,请重新登陆")


@router.get("/user/home")
def user_home(request: Request, db: Session = Depends(get_db),
              msg: str = ""):
    account = deps.current_account(request)
    if account is None:
        return RedirectResponse(f"{PREFIX}/admin", status_code=302)
    accounts = db.query(Account).order_by(Account.id.asc()).all()
    return deps.render(
        request, "userHome.html", account=account, accountList=accounts, msg=msg
    )


@router.post("/user/changePassword")
def change_password(request: Request, oldPassword: str = Form(""),
                    newPassword: str = Form(""), confirmPassword: str = Form(""),
                    db: Session = Depends(get_db)):
    account = deps.current_account(request)
    if account is None:
        return RedirectResponse(f"{PREFIX}/admin", status_code=302)

    db_account = db.query(Account).filter(Account.id == account["id"]).first()

    def back(msg):
        accounts = db.query(Account).order_by(Account.id.asc()).all()
        return deps.render(
            request, "userHome.html", account=account, accountList=accounts, msg=msg
        )

    if db_account is None:
        return back("账号不存在")
    if not newPassword or len(newPassword.strip()) < 6:
        return back("新密码长度不能少于 6 位")
    if newPassword != confirmPassword:
        return back("两次输入的新密码不一致")
    if md5(oldPassword) != db_account.password:
        return back("原密码不正确")

    db_account.password = md5(newPassword)
    db_account.updattime = datetime.now()
    db.commit()
    # 密码已变更，强制重新登录
    request.session.pop("global.account", None)
    return deps.render(
        request, "login.html", msg="密码修改成功，请使用新密码重新登录"
    )


@router.post("/user/addAccount")
def add_account(request: Request, username: str = Form(""), password: str = Form(""),
                confirmPassword: str = Form(""), db: Session = Depends(get_db)):
    account = deps.current_account(request)
    if account is None:
        return RedirectResponse(f"{PREFIX}/admin", status_code=302)

    def back(msg):
        accounts = db.query(Account).order_by(Account.id.asc()).all()
        return deps.render(
            request, "userHome.html", account=account, accountList=accounts, msg=msg
        )

    username = (username or "").strip()
    if not 4 <= len(username) <= 20:
        return back("用户名长度需在 4 ~ 20 位之间")
    if not password or len(password.strip()) < 6:
        return back("密码长度不能少于 6 位")
    if password != confirmPassword:
        return back("两次输入的密码不一致")
    exists = db.query(Account).filter(Account.username == username).first()
    if exists is not None:
        return back("用户名已存在")

    now = datetime.now()
    db.add(Account(username=username, password=md5(password),
                   creattime=now, updattime=now))
    db.commit()
    return back(f"账号 {username} 创建成功")


@router.get("/user/deleteAccount")
def delete_account(request: Request, id: int, db: Session = Depends(get_db)):
    account = deps.current_account(request)
    if account is None:
        return RedirectResponse(f"{PREFIX}/admin", status_code=302)

    accounts = db.query(Account).order_by(Account.id.asc()).all()
    msg = ""
    if len(accounts) <= 1:
        msg = "至少保留一个账号，无法删除"
    elif account["id"] == id:
        msg = "不能删除当前登录的账号"
    else:
        target = db.query(Account).filter(Account.id == id).first()
        if target is not None:
            db.delete(target)
            db.commit()

    accounts = db.query(Account).order_by(Account.id.asc()).all()
    return deps.render(
        request, "userHome.html", account=account, accountList=accounts, msg=msg
    )


@router.get("/user/logout")
def logout(request: Request):
    request.session.pop("global.account", None)
    return RedirectResponse(f"{PREFIX}/admin", status_code=302)

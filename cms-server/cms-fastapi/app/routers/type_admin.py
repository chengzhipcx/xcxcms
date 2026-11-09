"""分类管理（/type/*）。"""
from datetime import datetime

from fastapi import APIRouter, Depends, Form, Request
from fastapi.responses import RedirectResponse
from sqlalchemy.orm import Session

from .. import deps
from ..config import PREFIX
from ..database import get_db
from ..models import Type

router = APIRouter(dependencies=[Depends(deps.require_login)])


@router.get("/type/home")
def home(request: Request, db: Session = Depends(get_db)):
    rows = db.query(Type).order_by(Type.weight.asc()).all()
    return deps.render(request, "titleHome.html", titleList=rows)


@router.get("/type/showSavaPage")
def show_save_page(request: Request, id: int = 0, db: Session = Depends(get_db)):
    t = db.query(Type).filter(Type.id == id).first() if id else None
    return deps.render(request, "titleEdit.html", t=t)


@router.post("/type/save")
def save(request: Request, title: str = Form(""), weight: str = Form(None),
         id: int = Form(0), db: Session = Depends(get_db)):
    try:
        title = (title or "").strip()
        if not title:
            raise ValueError("请输入分类标题")
        weight_val = weight if weight not in (None, "") else None
        now = datetime.now()
        if id:
            t = db.query(Type).filter(Type.id == id).first()
            if t is None:
                raise ValueError("分类不存在")
            t.title = title
            t.weight = weight_val
            t.updattime = now
        else:
            t = Type(title=title, weight=weight_val, creattime=now, updattime=now)
            db.add(t)
        db.commit()
        return RedirectResponse(f"{PREFIX}/type/home", status_code=303)
    except Exception as e:  # noqa: BLE001 回填编辑页并提示
        db.rollback()
        t = Type(id=id or None, title=title, weight=weight)
        return deps.render(
            request, "titleEdit.html", t=t,
            error="保存失败：" + str(e),
        )


@router.get("/type/deleteById")
def delete_by_id(request: Request, id: int, db: Session = Depends(get_db)):
    try:
        t = db.query(Type).filter(Type.id == id).first()
        if t is not None:
            db.delete(t)
            db.commit()
    except Exception:  # noqa: BLE001
        db.rollback()
    return RedirectResponse(f"{PREFIX}/type/home", status_code=302)

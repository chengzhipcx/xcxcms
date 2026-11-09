"""文章管理（/item/*）：新增/编辑/删除，图片上传白名单。"""
import uuid
from datetime import datetime
from pathlib import Path

from fastapi import APIRouter, Depends, File, Form, Request, UploadFile
from fastapi.responses import RedirectResponse
from sqlalchemy.orm import Session

from .. import deps
from ..config import ALLOWED_IMG_EXT, PREFIX, UPLOAD_DIR
from ..database import get_db
from ..models import Article, Type

router = APIRouter(dependencies=[Depends(deps.require_login)])


def _save_logo(logo: UploadFile) -> str:
    """校验扩展名并保存上传图片，返回 /img/xxx 相对路径。

    必须白名单：应用内目录若允许任意文件，.jsp 会被 Tomcat/容器执行，
    构成远程代码执行漏洞。
    """
    old_name = logo.filename or ""
    dot = old_name.rfind(".")
    ext = old_name[dot:].lower() if dot >= 0 else ""
    if ext not in ALLOWED_IMG_EXT:
        raise ValueError("只支持上传图片文件（jpg/jpeg/png/gif/webp）")

    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    image_name = f"{uuid.uuid4()}{ext}"
    target = UPLOAD_DIR / image_name
    with open(target, "wb") as f:
        f.write(logo.file.read())
    return f"/img/{image_name}"


def _back_to_edit(request, db, tb, is_add_banner, error):
    return deps.render(
        request, "articleEdit.html",
        titleList=db.query(Type).order_by(Type.weight.asc()).all(),
        article=tb,
        isAddBanner=is_add_banner,
        error=error,
    )


@router.get("/item/showArticleSave")
def show_save(request: Request, id: int = 0, isAddBanner: int = 0,
              db: Session = Depends(get_db)):
    title_list = db.query(Type).order_by(Type.weight.asc()).all()
    if not title_list:
        return RedirectResponse(f"{PREFIX}/type/showSavaPage", status_code=302)

    article = None
    if id:
        article = db.query(Article).filter(Article.id == id).first()
        if article is not None:
            # 编辑页隐藏域保存旧图，未重新上传则保留
            article.imgurl = article.imgurl or ""

    return deps.render(
        request, "articleEdit.html",
        titleList=title_list, article=article, isAddBanner=isAddBanner,
    )


@router.post("/item/save")
def save(
    request: Request,
    title: str = Form(""),
    content: str = Form(""),
    pid: str = Form(""),
    isbanner: str = Form(None),
    weight: str = Form(None),
    imgurl: str = Form(""),
    id: int = Form(0),
    logo: UploadFile = File(None),
    db: Session = Depends(get_db),
):
    is_add_banner = request.query_params.get("isAddBanner", "0")
    try:
        # 表单字段规整
        title = (title or "").strip()
        if not title:
            raise ValueError("请输入标题")
        img = imgurl or ""
        is_banner = isbanner == "1" or is_add_banner == "1"
        pid_val = None
        if not is_banner:
            if not pid:
                raise ValueError("请选择分类")
            pid_val = int(pid)
        weight_val = int(weight) if weight not in (None, "") else None

        if logo is not None and logo.filename:
            img = _save_logo(logo)

        now = datetime.now()
        if id:
            tb = db.query(Article).filter(Article.id == id).first()
            if tb is None:
                raise ValueError("文章不存在")
            tb.title = title
            tb.content = content
            tb.imgurl = img
            tb.pid = pid_val
            tb.isbanner = is_banner
            tb.weight = str(weight_val) if weight_val is not None else None
            tb.updattime = now
        else:
            tb = Article(
                title=title, content=content, imgurl=img, pid=pid_val,
                isbanner=is_banner,
                weight=str(weight_val) if weight_val is not None else None,
                creattime=now, updattime=now,
            )
            db.add(tb)
        db.commit()

        if tb.pid is None:
            return RedirectResponse(f"{PREFIX}/banner/home", status_code=303)
        return RedirectResponse(f"{PREFIX}/home?id={tb.pid}", status_code=303)
    except Exception as e:  # noqa: BLE001 与经典版一致：回填编辑页并提示
        db.rollback()
        tb = Article(
            id=id, title=title, content=content, imgurl=imgurl,
            pid=None if not pid or is_add_banner == "1" else int(pid or 0) or None,
            isbanner=is_add_banner == "1" or isbanner == "1",
            weight=weight,
        )
        return _back_to_edit(
            request, db, tb, is_add_banner,
            "保存失败：" + str(e),
        )


@router.get("/item/deleteById")
def delete_by_id(request: Request, id: int, pid: int = 0,
                 db: Session = Depends(get_db)):
    try:
        tb = db.query(Article).filter(Article.id == id).first()
        if tb is not None:
            db.delete(tb)
            db.commit()
    except Exception:  # noqa: BLE001
        db.rollback()
    return RedirectResponse(f"{PREFIX}/home?id={pid}", status_code=302)

"""后台页面路由：登录页 / 文章管理首页 / 轮播图管理首页。"""
from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session

from .. import deps
from ..config import PAGESIZE
from ..database import get_db
from ..models import Article, Type
from ..utils import truncate

router = APIRouter()


@router.get("/")
def root(request: Request):
    return deps.render(request, "login.html")


@router.get("/home", dependencies=[Depends(deps.require_login)])
def home(request: Request, id: int = 0, db: Session = Depends(get_db)):
    title_list = db.query(Type).order_by(Type.weight.asc()).all()
    if not title_list:
        from fastapi.responses import RedirectResponse

        from ..config import PREFIX

        return RedirectResponse(f"{PREFIX}/type/home", status_code=302)

    top = next((t for t in title_list if t.id == id), title_list[0])

    total = (
        db.query(Article)
        .filter(Article.pid == top.id, Article.isbanner.is_(False))
        .count()
    )
    total_pages = deps.paginate_meta(total)

    rows = (
        db.query(Article)
        .filter(Article.pid == top.id, Article.isbanner.is_(False))
        .order_by(
            deps.weight_order_clause(),
            Article.updattime.desc(),
        )
        .limit(PAGESIZE)
        .all()
    )
    for a in rows:
        a.content = truncate(a.content, 50)

    return deps.render(
        request, "home.html",
        titleList=title_list, topTitle=top,
        totalCount=total_pages, itemList=rows,
    )


@router.get("/banner/home", dependencies=[Depends(deps.require_login)])
def banner_home(request: Request, db: Session = Depends(get_db)):
    rows = (
        db.query(Article)
        .filter(Article.isbanner.is_(True))
        .order_by(Article.updattime.desc())
        .all()
    )
    return deps.render(request, "bannerHome.html", bannerList=rows)


@router.get("/admin")
def admin(request: Request):
    from fastapi.responses import RedirectResponse

    from ..config import PREFIX

    if deps.current_account(request):
        return RedirectResponse(f"{PREFIX}/home", status_code=302)
    return deps.render(request, "login.html")

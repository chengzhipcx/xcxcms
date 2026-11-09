"""小程序数据接口（与经典版 ApiController 路径/响应完全一致）。"""
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import text
from sqlalchemy.orm import Session

from .. import deps
from ..config import API_PAGE_SIZE, PAGESIZE
from ..database import get_db
from ..models import Article
from datetime import datetime

router = APIRouter()


@router.get("/api/list")
def list_banners(db: Session = Depends(get_db)):
    """轮播图列表"""
    rows = (
        db.query(Article)
        .filter(Article.isbanner.is_(True))
        .order_by(Article.updattime.desc())
        .all()
    )
    return [deps.article_public(a) for a in rows]


@router.get("/api/listApiType")
def list_types(db: Session = Depends(get_db)):
    """分类列表（权重升序；与经典版一致按字符串列排序，此处 CAST 修正数值语义）"""
    from ..models import Type

    rows = (
        db.query(Type)
        .order_by(text("CAST(weight AS SIGNED)"))
        .all()
    )
    out = []
    for t in rows:
        d = {"id": t.id, "title": t.title}
        if t.weight is not None:
            d["weight"] = t.weight
        if t.updattime:
            d["updattime"] = deps.ms(t.updattime)
        if t.creattime:
            d["creattime"] = deps.ms(t.creattime)
        out.append(d)
    return out


@router.get("/api/listApiArticle")
def list_articles(id: int, loadTime: int = 0, db: Session = Depends(get_db)):
    """小程序分类文章分页（loadTime 从 0 开始；每页 15 条）"""
    page = loadTime + 1
    rows = (
        db.query(Article)
        .filter(Article.pid == id, Article.isbanner.is_(False))
        .order_by(
            text("IFNULL(CAST(weight AS SIGNED), 999999)"),
            Article.updattime.desc(),
        )
        .offset((page - 1) * API_PAGE_SIZE)
        .limit(API_PAGE_SIZE)
        .all()
    )
    out = []
    for a in rows:
        d = deps.article_public(a)
        d["content"] = deps.preview(d.get("content", ""))
        out.append(d)
    return out


@router.get("/api/findByIdApi")
def article_detail(id: int, db: Session = Depends(get_db)):
    """文章详情；不存在返回 404 + JSON（经典版为空指针 500，已修复）"""
    from fastapi.responses import JSONResponse

    a = db.query(Article).filter(Article.id == id).first()
    if a is None:
        # API 端点直接返回 JSON，避免被全局 HTML 错误页处理器接管
        return JSONResponse(status_code=404, content={"error": "article not found"})
    d = deps.article_public(a)
    if a.updattime:
        d["date"] = a.updattime.strftime("%Y-%m-%d")
    return d


@router.get("/api/listById")
def list_by_id(id: int, now: int = 1, db: Session = Depends(get_db)):
    """后台管理页按分类分页查询（AdminLTE 版 AJAX 使用）

    与经典版一致：list 与 pageCount 两段结构，简介截断 50 字，
    图片为相对路径（页面用上下文路径拼接）。
    """
    rows = (
        db.query(Article)
        .filter(Article.pid == id, Article.isbanner.is_(False))
        .order_by(
            text("IFNULL(CAST(weight AS SIGNED), 999999)"),
            Article.updattime.desc(),
        )
        .offset((now - 1) * PAGESIZE)
        .limit(PAGESIZE)
        .all()
    )
    total = (
        db.query(Article)
        .filter(Article.pid == id, Article.isbanner.is_(False))
        .count()
    )
    page_count = total // PAGESIZE + (1 if total % PAGESIZE else 0)

    out = []
    for a in rows:
        d = deps.article_public(a, prefix_img=False)
        d["content"] = deps.preview(d.get("content", ""))
        out.append(d)
    return {"list": out, "pageCount": page_count}

"""公共依赖：登录态、模板渲染、API 序列化。"""
from datetime import datetime

from fastapi import Request
from fastapi.templating import Jinja2Templates
from pathlib import Path

from .config import IP, PREFIX, PAGESIZE
from .models import Article
from .utils import truncate

templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent / "templates"))


class NotLoggedIn(Exception):
    """未登录访问受保护页面：全局处理器负责 302 到登录页。"""


def current_account(request: Request):
    return request.session.get("global.account")


def require_login(request: Request):
    """页面路由依赖：未登录抛 NotLoggedIn，由全局处理器重定向。"""
    if not current_account(request):
        raise NotLoggedIn()


def render(request: Request, name: str, status_code: int = 200, **ctx):
    """模板渲染：注入 request / ctx(上下文路径) / account(当前登录账号)。"""
    ctx.setdefault("ctx", PREFIX)
    ctx.setdefault("account", current_account(request))
    return templates.TemplateResponse(
        request, name, ctx, status_code=status_code
    )


def absolute_img_url(imgurl):
    """图片相对路径拼接完整 URL；空值或已是完整 URL 时原样返回。"""
    if not imgurl:
        return imgurl
    if imgurl.startswith("http://") or imgurl.startswith("https://"):
        return imgurl
    return IP + imgurl


def ms(dt):
    """datetime -> 毫秒时间戳（经典版 fastjson 序列化 Date 的行为）。"""
    return int(dt.timestamp() * 1000) if dt else None


def article_public(a: Article, with_content: bool = True, prefix_img: bool = True):
    """按经典版 fastjson 序列化的字段形态输出文章（省略 None 字段）。"""
    d = {"id": a.id, "pid": a.pid}
    if prefix_img and a.imgurl:
        d["imgurl"] = absolute_img_url(a.imgurl)
    elif a.imgurl:
        d["imgurl"] = a.imgurl
    if a.title:
        d["title"] = a.title
    if a.tourl:
        d["tourl"] = a.tourl
    if a.weight is not None:
        try:
            d["weight"] = int(a.weight)
        except (TypeError, ValueError):
            pass
    if a.isbanner is not None:
        d["isbanner"] = bool(a.isbanner)
    if a.updattime:
        d["updattime"] = ms(a.updattime)
    if a.creattime:
        d["creattime"] = ms(a.creattime)
    if with_content and a.content:
        d["content"] = a.content
    return d


def weight_order_clause():
    """权重排序表达式：未设权重的排最后（等价经典版 IFNULL(weight, 999999)）。"""
    from sqlalchemy import text

    return text("IFNULL(CAST(weight AS SIGNED), 999999)")


def paginate_meta(total: int) -> int:
    """后台分页总页数（与经典版计数逻辑一致）。"""
    if total % PAGESIZE == 0:
        return total // PAGESIZE
    return total // PAGESIZE + 1


def preview(content: str, max_chars: int = 50):
    return truncate(content, max_chars)

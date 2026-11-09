"""FastAPI 应用入口。

对应经典版 Spring MVC 应用的等价迁移：同样的 MySQL 库、同样的
小程序接口、同样的 AdminLTE 管理界面（Jinja2 模板渲染）。

启动：uvicorn app.main:app --host 0.0.0.0 --port 8000
访问：http://localhost:8000/cms/
"""
from pathlib import Path

from fastapi import FastAPI, Request
from fastapi.responses import RedirectResponse
from starlette.exceptions import HTTPException as StarletteHTTPException
from starlette.middleware.sessions import SessionMiddleware
from starlette.staticfiles import StaticFiles

from . import deps
from .config import PREFIX, SESSION_MAX_AGE, SESSION_SECRET, UPLOAD_DIR
from .routers import api, item, pages, type_admin, user

app = FastAPI(title="xcxcms", docs_url=None, redoc_url=None)

# 会话（签名 Cookie，30 分钟无操作过期，与经典版一致）
app.add_middleware(SessionMiddleware, secret_key=SESSION_SECRET,
                   max_age=SESSION_MAX_AGE)

app.include_router(pages.router, prefix=PREFIX)
app.include_router(api.router, prefix=PREFIX)
app.include_router(item.router, prefix=PREFIX)
app.include_router(type_admin.router, prefix=PREFIX)
app.include_router(user.router, prefix=PREFIX)

# 静态资源与上传图片
STATIC_DIR = Path(__file__).resolve().parent / "static"
app.mount(f"{PREFIX}/css", StaticFiles(directory=STATIC_DIR / "css"))
app.mount(f"{PREFIX}/js", StaticFiles(directory=STATIC_DIR / "js"))
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
app.mount(f"{PREFIX}/img", StaticFiles(directory=str(UPLOAD_DIR)))


class NotLoggedIn(Exception):
    """兼容旧引用（实际使用 deps.NotLoggedIn）。"""


app.add_exception_handler(
    deps.NotLoggedIn,
    lambda request, exc: RedirectResponse(f"{PREFIX}/admin", status_code=302),
)


@app.exception_handler(StarletteHTTPException)
async def http_exception_handler(request: Request, exc: StarletteHTTPException):
    """404/500 等统一渲染友好错误页（与经典版 error.jsp 一致）。"""
    return deps.render(
        request, "error.html",
        status_code=exc.status_code,
        status=exc.status_code,
    )


@app.exception_handler(Exception)
async def unhandled_exception_handler(request: Request, exc: Exception):
    return deps.render(request, "error.html", status_code=500, status=500)

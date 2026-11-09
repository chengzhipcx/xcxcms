"""应用配置：全部支持环境变量覆盖，便于本地/生产切换。"""
import os

from pathlib import Path

# 应用根目录（app/ 的上一级）
BASE_DIR = Path(__file__).resolve().parent.parent

# 数据库连接（与经典版同一个库，直接执行根目录 cms.sql 初始化）
DB_URL = os.environ.get(
    "CMS_DB_URL",
    "mysql+pymysql://root:123456@localhost:3306/weichatarticle?charset=utf8mb4",
)

# 服务端对外地址（含上下文路径），用于拼接图片完整 URL 返回给小程序端
IP = os.environ.get("CMS_IP", "http://localhost:8000/cms")

# 上下文路径（经典版 war 部署在 /cms，这里保持一致，小程序端无需改动）
PREFIX = os.environ.get("CMS_PREFIX", "/cms")

# 图片上传目录（应用外存储，重启不丢；通过 /cms/img/** 对外提供）
UPLOAD_DIR = Path(
    os.environ.get("CMS_UPLOAD_DIR", str(BASE_DIR / "uploads"))
).resolve()

# 会话签名密钥（生产环境务必通过环境变量设置随机值）
SESSION_SECRET = os.environ.get("CMS_SESSION_SECRET", "dev-secret-please-change")

# 后台会话有效期（秒）
SESSION_MAX_AGE = 30 * 60

# 分页条数（与经典版 ConstantPool 保持一致）
PAGESIZE = 5        # 后台文章管理列表
API_PAGE_SIZE = 15  # 小程序接口

# 允许上传的图片扩展名（白名单，防止 .jsp 等可执行文件落盘）
ALLOWED_IMG_EXT = (".jpg", ".jpeg", ".png", ".gif", ".webp")

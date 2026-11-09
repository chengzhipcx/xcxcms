"""通用工具：MD5 / 安全截断 / 登录限流（与经典版 Java 工具类行为一致）。"""
import hashlib
import time


def md5(string: str) -> str:
    """固定输出 32 位小写 md5。

    经典版旧实现用 BigInteger.toString(16) 会在摘要首字节 < 0x10 时
    丢失前导零（输出 31 位），导致对应密码永远无法登录；此处按标准
    十六进制格式化，与数据库 tb_account.password 的值保持一致。
    """
    return hashlib.md5(string.encode("utf-8")).hexdigest()


def truncate(content, max_chars: int):
    """按字符截断到 max_chars 个字符。

    Python 3 的 str 本身就是 Unicode 码点序列（不像 Java 的 UTF-16
    code unit），len() 与按码点计数天然一致，emoji 不会被截成半个
    代理对；入参为 None 时原样返回。
    """
    if content is None:
        return None
    if len(content) <= max_chars:
        return content
    return content[:max_chars]


class LoginRateLimiter:
    """简单登录失败限流：同一 key 连续失败 MAX_ATTEMPTS 次后锁定 LOCK_SECONDS 秒。"""

    MAX_ATTEMPTS = 5
    LOCK_SECONDS = 10 * 60

    _state: dict = {}  # key -> {"fails": int, "lock_until": float}

    @classmethod
    def is_locked(cls, key: str) -> bool:
        state = cls._state.get(key)
        if not state:
            return False
        if state["lock_until"] and time.time() < state["lock_until"]:
            return True
        if state["lock_until"]:
            cls._state.pop(key, None)
        return False

    @classmethod
    def on_failure(cls, key: str) -> None:
        state = cls._state.setdefault(key, {"fails": 0, "lock_until": 0})
        state["fails"] += 1
        if state["fails"] >= cls.MAX_ATTEMPTS:
            state["lock_until"] = time.time() + cls.LOCK_SECONDS
            state["fails"] = 0

    @classmethod
    def on_success(cls, key: str) -> None:
        cls._state.pop(key, None)

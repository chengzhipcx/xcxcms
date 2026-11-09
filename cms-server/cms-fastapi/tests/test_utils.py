"""utils 单元测试（与经典版 Java 测试用例等价）。"""
import pytest

from app.utils import LoginRateLimiter, md5, truncate


class TestMd5:
    def test_32_lowercase_hex(self):
        digest = md5("123456")
        assert len(digest) == 32
        assert digest == digest.lower()

    def test_known_vectors(self):
        # 与数据库初始化数据（cms.sql）中的标准 MD5 保持一致
        assert md5("123456") == "e10adc3949ba59abbe56e057f20f883e"
        assert md5("abc") == "900150983cd24fb0d6963f7d28e17f72"
        assert md5("") == "d41d8cd98f00b204e9800998ecf8427e"

    def test_leading_zero_regression(self):
        # 回归：摘要首字节 < 0x10 时旧实现丢前导零输出 31 位
        import hashlib
        input_str = None
        for i in range(2000):
            s = f"lead-zero-{i}"
            if hashlib.md5(s.encode()).digest()[0] < 0x10:
                input_str = s
                break
        assert input_str is not None
        assert len(md5(input_str)) == 32
        assert md5(input_str) == hashlib.md5(input_str.encode()).hexdigest()


class TestTruncate:
    def test_null_and_short(self):
        assert truncate(None, 10) is None
        assert truncate("", 10) == ""
        assert truncate("abc", 10) == "abc"
        assert truncate("abcde", 5) == "abcde"

    def test_ascii_limit(self):
        assert len(truncate("a" * 80, 50)) == 50

    def test_chinese_counts_as_one(self):
        assert truncate("一二三四五六七八九十十一十二十三", 10) == "一二三四五六七八九十"

    def test_emoji_not_split(self):
        # emoji 在 Python str 中就是一个码点，不会被截半
        content = "文章标题" + "😀" * 50
        out = truncate(content, 50)
        assert len(out) <= 50
        assert all(ord(c) >= 0x20 for c in out)
        assert not any(0xD800 <= ord(c) <= 0xDFFF for c in out)


class TestLoginRateLimiter:
    def test_lock_after_failures(self):
        key = "test-user|1.2.3.4"
        LoginRateLimiter._state.pop(key, None)
        for _ in range(5):
            assert not LoginRateLimiter.is_locked(key)
            LoginRateLimiter.on_failure(key)
        assert LoginRateLimiter.is_locked(key)

    def test_success_clears(self):
        key = "test-user|5.6.7.8"
        LoginRateLimiter._state.pop(key, None)
        LoginRateLimiter.on_failure(key)
        LoginRateLimiter.on_failure(key)
        LoginRateLimiter.on_success(key)
        assert not LoginRateLimiter.is_locked(key)

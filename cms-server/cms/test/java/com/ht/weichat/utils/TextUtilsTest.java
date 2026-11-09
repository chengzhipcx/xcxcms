package com.ht.weichat.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * TextUtils 单元测试
 */
public class TextUtilsTest {

    @Test
    public void truncateShouldHandleNullAndShortInput() {
        Assert.assertNull(TextUtils.truncate(null, 10));
        Assert.assertEquals("", TextUtils.truncate("", 10));
        Assert.assertEquals("abc", TextUtils.truncate("abc", 10));
        Assert.assertEquals("abcde", TextUtils.truncate("abcde", 5));
    }

    @Test
    public void truncateShouldLimitAsciiLength() {
        Assert.assertEquals(50, TextUtils.truncate(new String(new char[80]).replace('\0', 'a'), 50).length());
    }

    @Test
    public void truncateShouldCountChineseAsOneChar() {
        // 中文按字符数而非 UTF-16 code unit 计数
        String content = "一二三四五六七八九十十一十二十三";
        Assert.assertEquals("一二三四五六七八九十", TextUtils.truncate(content, 10));
    }

    /**
     * 关键用例：emoji 是代理对（两个 char），截断点不能落在代理对中间
     */
    @Test
    public void truncateShouldNotSplitSurrogatePairs() {
        String content = "文章标题😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀😀";
        String truncated = TextUtils.truncate(content, 50);
        Assert.assertTrue(truncated.codePointCount(0, truncated.length()) <= 50);
        // 不应包含孤立的低位代理
        for (int i = 0; i < truncated.length(); i++) {
            char c = truncated.charAt(i);
            Assert.assertFalse(Character.isLowSurrogate(c) && !Character.isHighSurrogate(truncated.charAt(i - 1)));
        }
    }
}

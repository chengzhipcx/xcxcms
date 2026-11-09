package com.ht.weichat.utils;

/**
 * 文本处理工具
 *
 * @author cz
 */
public class TextUtils {

    private TextUtils() {
    }

    /**
     * 按字符（Unicode 码点）截断到 maxChars 个字符。
     * 直接用 String.substring(0, n) 按 char 截断会把 emoji 等
     * 增补平面字符（代理对）从中间切开，产生乱码。
     *
     * @param content  原文本，可为 null
     * @param maxChars 最大字符数（按码点计）
     * @return 截断后的文本；入参为 null 时返回 null
     */
    public static String truncate(String content, int maxChars) {
        if (content == null || maxChars < 0
                || content.codePointCount(0, content.length()) <= maxChars) {
            return content;
        }
        return content.substring(0, content.offsetByCodePoints(0, maxChars));
    }
}

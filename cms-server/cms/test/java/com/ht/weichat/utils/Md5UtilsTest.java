package com.ht.weichat.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Md5Utils 单元测试
 */
public class Md5UtilsTest {

    @Test
    public void encodeShouldReturn32LowercaseHex() {
        String md5 = Md5Utils.encode("123456");
        Assert.assertEquals(32, md5.length());
        Assert.assertEquals(md5, md5.toLowerCase());
    }

    @Test
    public void encodeShouldMatchKnownVectors() {
        // 与数据库初始化数据（cms.sql）中的标准 MD5 保持一致
        Assert.assertEquals("e10adc3949ba59abbe56e057f20f883e", Md5Utils.encode("123456"));
        Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", Md5Utils.encode("abc"));
        Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", Md5Utils.encode(""));
    }

    /**
     * 回归用例：摘要首字节小于 0x10（十六进制以 0 开头）时，
     * 旧实现用 BigInteger.toString(16) 会丢失前导零输出 31 位，导致该密码永远无法登录。
     */
    @Test
    public void encodeShouldKeepLeadingZero() throws Exception {
        String input = null;
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        // 暴力找一个 md5 以 0 开头的输入
        for (int i = 0; i < 2000; i++) {
            String candidate = "lead-zero-" + i;
            byte[] digest = md.digest(candidate.getBytes());
            if ((digest[0] & 0xF0) == 0) {
                input = candidate;
                break;
            }
        }
        Assert.assertNotNull("未找到可用于回归验证的样例", input);
        Assert.assertEquals(32, Md5Utils.encode(input).length());
        // 与标准 md5sum 行为一致
        String expected = toHex(md.digest(input.getBytes()));
        Assert.assertEquals(expected, Md5Utils.encode(input));
    }

    private static String toHex(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

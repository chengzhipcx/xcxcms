package com.ht.weichat.utils;

import java.security.MessageDigest;

/**
 * Created by  on 16/12/13.
 */
public class Md5Utils {
    /**
     * 对字符串md5加密
     *
     * 注意：必须输出固定 32 位小写十六进制，与数据库 tb_account.password 中存储的
     * 标准 MD5 值保持一致。原先使用 BigInteger.toString(16) 会在摘要首字节小于
     * 0x10 时丢失前导零（只剩 31 位），导致对应密码永远无法登录。
     *
     * @param string 待加密字符串
     * @return 32 位小写 md5，异常时返回 null
     */
    public static String encode(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

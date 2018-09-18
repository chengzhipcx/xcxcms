package com.ht.weichat.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by  on 16/12/13.
 */
public class Md5Utils {
    /**
     * 对字符串md5加密
     *
     * @param string
     * @return
     */
    public static String encode(String string) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(string.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

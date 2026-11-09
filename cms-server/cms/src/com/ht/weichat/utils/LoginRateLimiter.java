package com.ht.weichat.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的登录失败限流器（内存版，单实例部署够用）。
 * 同一用户名+IP 连续登录失败达到阈值后锁定一段时间，
 * 用于减缓后台口令暴力破解。
 *
 * @author cz
 */
public class LoginRateLimiter {

    /** 最大连续失败次数 */
    private static final int MAX_ATTEMPTS = 5;

    /** 锁定时长（毫秒）：10 分钟 */
    private static final long LOCK_MILLIS = 10 * 60 * 1000L;

    /** key -> {失败次数, 锁定截止时间}，锁定截止时间为 0 表示未锁定 */
    private static final Map<String, long[]> ATTEMPTS = new ConcurrentHashMap<String, long[]>();

    private LoginRateLimiter() {
    }

    /**
     * 该 key 当前是否处于锁定状态（不可登录）
     */
    public static boolean isLocked(String key) {
        long[] state = ATTEMPTS.get(key);
        if (state == null) {
            return false;
        }
        if (state[1] > 0 && System.currentTimeMillis() < state[1]) {
            return true;
        }
        if (state[1] > 0) {
            // 锁定已过期，清除记录重新计数
            ATTEMPTS.remove(key);
        }
        return false;
    }

    /**
     * 记录一次登录失败；达到阈值后锁定
     */
    public static synchronized void onFailure(String key) {
        long[] state = ATTEMPTS.get(key);
        if (state == null) {
            state = new long[]{0, 0};
            ATTEMPTS.put(key, state);
        }
        state[0]++;
        if (state[0] >= MAX_ATTEMPTS) {
            state[1] = System.currentTimeMillis() + LOCK_MILLIS;
            state[0] = 0;
        }
    }

    /**
     * 登录成功，清除失败记录
     */
    public static void onSuccess(String key) {
        ATTEMPTS.remove(key);
    }

    /**
     * 清理已过期的记录，防止长期运行后 Map 无限增长（顺手清理，非精确调度）
     */
    public static void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, long[]>> it = ATTEMPTS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, long[]> entry = it.next();
            long[] state = entry.getValue();
            if (state[1] > 0 && now >= state[1]) {
                it.remove();
            }
        }
    }
}

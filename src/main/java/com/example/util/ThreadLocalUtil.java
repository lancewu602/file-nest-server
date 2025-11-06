package com.example.util;

import com.example.bean.entity.User;

/**
 * @author WuQinglong
 * @date 2025/11/4 11:17
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User value) {
        userThreadLocal.set(value);
    }

    public static User getCurrentUser() {
        return userThreadLocal.get();
    }

    public static void removeUser() {
        userThreadLocal.remove();
    }
}

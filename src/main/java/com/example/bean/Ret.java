package com.example.bean;

import lombok.Getter;

/**
 * @author WuQinglong
 * @date 2025/9/2 23:41
 */
@Getter
public class Ret<T> {

    private final int code;
    private final String message;
    private final T data;

    private Ret(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Ret<T> success() {
        return new Ret<>(200, null, null);
    }

    public static <T> Ret<T> success(T data) {
        return new Ret<>(200, null, data);
    }

    public static <T> Ret<T> fail(String error) {
        return new Ret<>(500, error, null);
    }

    public static <T> Ret<T> fail(int code, String error) {
        return new Ret<>(code, error, null);
    }

    public static <T> Ret<T> fail(int code, String error, T data) {
        return new Ret<>(code, error, data);
    }

}

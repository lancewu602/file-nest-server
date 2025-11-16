package com.example.config;

import lombok.Getter;

/**
 * @author WuQinglong
 * @date 2025/9/8 14:25
 */
public class BizException extends RuntimeException {

    @Getter
    private int code = 500;

    public BizException(String message) {
        super(message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

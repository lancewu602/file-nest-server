package com.example.config;

import com.example.bean.Ret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author WuQinglong
 * @date 2025/9/4 09:38
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    @ResponseStatus(code = HttpStatus.OK)
    public Ret<?> handleException(BizException ex) {
        log.error(ex.getMessage(), ex);
        return Ret.fail(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.OK)
    public Ret<?> handleException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return Ret.fail(ex.getMessage());
    }

}

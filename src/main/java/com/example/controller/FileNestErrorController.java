package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * 让 Spring Boot 在找不到 API 或静态资源时，返回 index.html，这样 Vue Router 才能接管路由。
 * @author WuQinglong
 * @date 2025/10/11 09:32
 */
@Controller
public class FileNestErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 排除 API、静态资源、已知文件类型
        if (uri.startsWith("/api") ||
            uri.startsWith("/assets") ||
            uri.equals("/favicon.ico")) {
            // 返回真正的 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 否则视为前端路由，返回 index.html
        return "forward:/index.html";
    }
}
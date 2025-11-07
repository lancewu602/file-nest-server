package com.example.config;

import com.example.bean.entity.User;
import com.example.service.UserService;
import com.example.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author WuQinglong
 * @date 2025/11/4 11:15
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // log
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("file-nest-token");
        User user = userService.verify(token);
        Assert.notNull(user, "用户不存在");
        ThreadLocalUtil.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        ThreadLocalUtil.removeUser();
    }
}

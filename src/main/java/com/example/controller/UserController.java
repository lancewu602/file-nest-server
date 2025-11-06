package com.example.controller;

import com.example.bean.Ret;
import com.example.bean.request.LoginRequest;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WuQinglong
 * @date 2025/11/4 11:21
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/user/login")
    public Ret<?> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return Ret.success(token);
    }

}

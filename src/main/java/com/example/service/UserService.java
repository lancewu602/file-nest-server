package com.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bean.entity.User;
import com.example.mapper.UserMapper;
import com.example.util.JacksonUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WuQinglong
 * @date 2025/11/3 16:51
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    // log
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final String secret = "secret-file-nest-jwt-hello-world";

    private final Algorithm algorithm = Algorithm.HMAC512(secret);

    // 缓存用户信息
    private final Cache<Integer, User> userCache = CacheBuilder.newBuilder()
        .maximumSize(50)
        .build();

    @PostConstruct
    public void init() {
        // 缓存所有用户信息
        List<User> userList = list();
        for (User user : userList) {
            userCache.put(user.getId(), user);
            log.info("cache user：{}", user.getName());
        }
    }

    /**
     * 登录
     */
    public String login(String username, String password) {
        Assert.hasLength(username, "用户名不能为空");
        Assert.hasLength(password, "密码不能为空");

        User user = getOne(new LambdaQueryWrapper<User>()
            .eq(User::getName, username)
        );
        Assert.notNull(user, "用户不存在");
        Assert.isTrue(user.getPassword().equals(password), "密码错误");

        Map<String, Integer> payload = new HashMap<>();
        payload.put("userId", user.getId());

        return JWT.create()
            .withIssuer("FileNest")
            .withPayload(payload)
            .sign(algorithm);
    }

    /**
     * 验证
     */
    public User verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("FileNest")
                .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(decodedJWT.getPayload());
            Map<String, Object> payloadMap = JacksonUtil.toMap(new String(decodedBytes, StandardCharsets.UTF_8));
            Integer userId = (Integer) payloadMap.get("userId");

            User user = userCache.getIfPresent(userId);
            if (user == null) {
                user = getById(userId);
                userCache.put(userId, user);
            }
            return user;
        } catch (Exception e) {
            log.error("token verify error", e);
        }
        return null;
    }

}

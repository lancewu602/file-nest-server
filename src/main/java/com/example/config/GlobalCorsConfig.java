package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author WuQinglong
 * @date 2025/9/3 09:44
 */
@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口生效（可改为指定路径，如 "/api/**"）
            .allowedOrigins("*") // 允许的前端域名（*表示允许所有，生产环境不推荐）
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP请求方法
            .allowedHeaders("*") // 允许的请求头（如Content-Type、Authorization）
            .maxAge(3600); // 预检请求（OPTIONS）的缓存时间（秒），减少重复预检
    }
}

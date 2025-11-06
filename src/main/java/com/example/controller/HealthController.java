package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WuQinglong
 * @date 2025/9/6 17:00
 */
@RestController
public class HealthController {

    @GetMapping("/api/healthcheck")
    public String healthcheck() {
        return "success";
    }

}

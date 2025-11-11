package com.example.bean.request;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/5 09:58
 */
@Data
public class RestoreMediumRequest {

    private List<Integer> mediumIds;
}

package com.example.bean.request;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/4 23:04
 */
@Data
public class MediumFavoriteRequest {

    private List<Integer> mediumIds;

    private Boolean favorite;

}

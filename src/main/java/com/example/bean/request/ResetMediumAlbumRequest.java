package com.example.bean.request;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/8 23:23
 */
@Data
public class ResetMediumAlbumRequest {

    private Integer mediumId;

    private List<Integer> albumIds;
}

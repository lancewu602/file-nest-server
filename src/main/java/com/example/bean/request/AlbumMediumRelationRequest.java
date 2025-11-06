package com.example.bean.request;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/3 18:04
 */
@Data
public class AlbumMediumRelationRequest {
    private List<Integer> albumIds;

    private List<Integer> mediumIds;

    private Boolean removeDeletedFlag;

    private Boolean overrideAlbums;
}

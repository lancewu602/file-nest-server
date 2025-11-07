package com.example.bean.response;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/10/31 16:20
 */
@Data
public class MediumResp {

    private Integer id;
    private String type;
    private String name;
    private Long size;
    private Boolean favorite;
    private String dateToken;
    private String lastModified;
    private String thumbnailPath;
    private String originalPath;
    private Integer width;
    private Integer height;
    private Integer duration;
    private List<Integer> inAlbumIds;

}

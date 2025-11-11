package com.example.bean.response;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/10/31 16:20
 */
@Data
public class MediumInfo {

    private Integer id;
    private String type;
    private String name;
    private Long size;
    private Integer width;
    private Integer height;
    private Integer duration;
    private String tokenDate;
    private String tokenDateTime;
    private String lastModified;
    private String thumbnailPath;
    private String originalPath;
    private String durationText;
    private Boolean favorite;
    private Boolean deleted;
    private List<Integer> inAlbumIds;

}

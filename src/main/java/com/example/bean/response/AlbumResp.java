package com.example.bean.response;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/11/3 17:01
 */
@Data
public class AlbumResp {

    private Integer id;
    private String name;
    private Integer coverMediaId;
    private Long mediaCount;
    private boolean system;

}

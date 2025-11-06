package com.example.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author WuQinglong
 * @date 2025/9/2 22:48
 */
@Data
@TableName("medium")
public class Medium {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String type;
    private String name;
    private Long size;
    private LocalDateTime dateToken;
    private LocalDateTime lastModified;
    private String exif;
    private String phash;
    private String dhash;
    private String originalPath;
    private String thumbnailPath;
    private Boolean deleted;
    private Boolean favorite;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

}

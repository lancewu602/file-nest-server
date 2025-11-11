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
@TableName("album_medium_mapping")
public class AlbumMediumMapping {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer albumId;
    private Integer mediumId;
    private LocalDateTime createdAt;

}

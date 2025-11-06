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
@TableName("album")
public class Album {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String name;
    private Integer coverMediumId;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

}

package com.example.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/2 22:48
 */
@Data
@TableName("storage")
public class Storage {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String name;
    private String mountPath;
    private String trashName;

}

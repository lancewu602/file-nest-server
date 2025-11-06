package com.example.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/11/4 11:11
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String password;
    private Boolean isAdmin;
    private Boolean enabled;

}

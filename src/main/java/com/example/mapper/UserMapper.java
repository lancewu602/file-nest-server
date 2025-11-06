package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bean.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author WuQinglong
 * @date 2025/9/2 22:49
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

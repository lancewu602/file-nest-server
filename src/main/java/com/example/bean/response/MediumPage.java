package com.example.bean.response;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/10 16:15
 */
@Data
public class MediumPage<T> {

    // 总数
    private Integer total;

    // 本页的数据
    private List<T> data;

    // 本页的数据大小
    private Integer size;

}

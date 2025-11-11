package com.example.bean.response;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author WuQinglong
 * @date 2025/11/1 07:58
 */
@Data
public class MediumsRow {

    private String key = UUID.randomUUID().toString();

    // 日期
    private String title = "";

    // N 个图片
    private List<MediumInfo> mediums = Collections.emptyList();

    public static MediumsRow of(String title) {
        MediumsRow row = new MediumsRow();
        row.setTitle(title);
        return row;
    }

    public static MediumsRow of(List<MediumInfo> list) {
        MediumsRow row = new MediumsRow();
        row.setMediums(list);
        return row;
    }

}

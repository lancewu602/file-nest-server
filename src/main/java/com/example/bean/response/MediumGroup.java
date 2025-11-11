package com.example.bean.response;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/1 07:58
 */
@Data
public class MediumGroup {

    // 日期
    private String title = "";

    // N 个图片
    private List<MediumResp> mediums = Collections.emptyList();

    public MediumGroup() {
    }

    public MediumGroup(String title, List<MediumResp> mediums) {
        this.title = title;
        this.mediums = mediums;
    }
}

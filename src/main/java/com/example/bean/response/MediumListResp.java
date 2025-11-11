package com.example.bean.response;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/11/1 07:58
 */
@Data
public class MediumListResp {

    private String nextFetchDate;
    private List<MediumInfo> mediums;

    public MediumListResp(String nextFetchDate, List<MediumInfo> mediums) {
        this.nextFetchDate = nextFetchDate;
        this.mediums = mediums;
    }
}

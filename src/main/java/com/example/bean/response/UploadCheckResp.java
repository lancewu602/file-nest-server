package com.example.bean.response;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/10/22 09:10
 */
@Data
public class UploadCheckResp {

    /**
     * 已上传的最新分片索引
     */
    private int maxChunkIndex;

    public UploadCheckResp(int maxChunkIndex) {
        this.maxChunkIndex = maxChunkIndex;
    }
}

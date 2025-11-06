package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/10/28 11:03
 */
@Data
public class CheckChunkRequest {

    private String fileId;
    private Long chunkSize;
    private Long totalSize;
    private Integer totalChunks;

}

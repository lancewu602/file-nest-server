package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/12 17:40
 */
@Data
public class VideoMergeChunkRequest {

    private String fileId;
    private String fileName;
    private Long dateToken;
    private Long lastModified;
    private Long chunkSize;
    private Long totalSize;
    private Integer totalChunks;

}

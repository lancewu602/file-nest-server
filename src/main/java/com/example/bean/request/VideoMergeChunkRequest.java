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
    private Long chunkSize;
    private Long totalSize;
    private Integer totalChunks;

    private Long dateToken;
    private Long lastModified;
    private Integer width;
    private Integer height;
    private Integer duration;

}

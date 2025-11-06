package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/12 17:40
 */
@Data
public class FileMergeChunkRequest {

    private String storageId;
    private String directoryPath;

    private String fileId;
    private String fileName;
    private Long chunkSize;
    private Long totalSize;
    private Integer totalChunks;

}

package com.example.bean.request;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/9/8 16:44
 */
@Data
public class DeleteFileRequest {

    private Integer storageId;
    private String directoryPath;
    private List<String> fileNames;

}

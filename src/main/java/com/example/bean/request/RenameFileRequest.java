package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/8 16:44
 */
@Data
public class RenameFileRequest {

    private Integer storageId;
    private String path;
    private String oldName;
    private String newName;

}

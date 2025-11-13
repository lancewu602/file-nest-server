package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/9 11:44
 */
@Data
public class CreateDirectoryRequest {

    private Integer storageId;
    private String path;
    private String name;

}

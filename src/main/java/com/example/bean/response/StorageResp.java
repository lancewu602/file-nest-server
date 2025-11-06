package com.example.bean.response;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/3 08:40
 */
@Data
public class StorageResp {

    private Integer id;
    private String name;
    private String mountPath;
    private String trashName;
    private boolean exists;

}

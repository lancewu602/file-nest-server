package com.example.bean.request;

import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/9/4 09:37
 */
@Data
public class MountRequest {

    private Integer id;

    private String name;
    private String mountPath;
    private String trashName;

}

package com.example.bean.response;

import lombok.Data;

import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/9/2 23:39
 */
@Data
public class FileInfoResp {

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件的路径
     */
    private String path;

    /**
     * 最后修改时间
     */
    private String lastModified;

    /**
     * 文件大小
     */
    private String displaySize;

    /**
     * 子文件数量
     */
    private Integer subFileCount;

    /**
     * 子文件列表
     */
    private List<FileInfoResp> children;

}

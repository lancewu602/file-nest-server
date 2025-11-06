package com.example.bean.entity;

import com.example.util.JacksonUtil;
import lombok.Data;

/**
 * @author WuQinglong
 * @date 2025/10/30 17:16
 */
@Data
public class ImageExif {

    private String iso;
    private String make;

    public static ImageExif fromJson(String exif) {
        return JacksonUtil.fromJson(exif, ImageExif.class);
    }
}

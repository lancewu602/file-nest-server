package com.example.util;

import java.io.IOException;
import java.nio.file.Path;

import static com.example.bean.Constants.THUMBNAIL_HEIGHT;
import static com.example.bean.Constants.THUMBNAIL_WIDTH;

/**
 * @author WuQinglong
 * @date 2025/10/31 10:39
 */
public class MagickUtil {

    /**
     * 缩放并转为灰度图
     */
    public static Path resizeAndGray(Path imagePath, int width, int height) throws IOException {
        return CommandUtil.resizeAndGray(imagePath, width, height);
    }

    /**
     * 生成缩略图
     */
    public static void generateThumbnail(Path imagePath, Path thumbnailPath) throws IOException {
        CommandUtil.createThumbnail(imagePath, thumbnailPath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

}

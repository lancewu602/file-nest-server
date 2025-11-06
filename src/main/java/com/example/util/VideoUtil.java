package com.example.util;

import java.io.IOException;
import java.nio.file.Path;

import static com.example.bean.Constants.THUMBNAIL_HEIGHT;
import static com.example.bean.Constants.THUMBNAIL_WIDTH;

/**
 * @author WuQinglong
 * @date 2025/10/31 12:26
 */
public class VideoUtil {

    /**
     * 生成视频的封面缩略图
     */
    public static void generateThumbnail(Path videoPath, Path thumbnailPath) throws IOException {
        Path frameFilePath = CommandUtil.getVideoFirstFrame(videoPath);

        // 转为缩略图
        CommandUtil.createThumbnail(frameFilePath, thumbnailPath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

}

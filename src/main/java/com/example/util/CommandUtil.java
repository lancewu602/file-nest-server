package com.example.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author WuQinglong
 * @date 2025/10/29 20:06
 */
public final class CommandUtil {

    private static final Logger log = LoggerFactory.getLogger(CommandUtil.class);

    private static final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();

    /**
     * 缩放并转为灰度图
     */
    public static Path resizeAndGray(Path imagePath, int width, int height) throws IOException {
        String baseName = FilenameUtils.getBaseName(imagePath.toString());
        Path targetImagePath = Paths.get(FileUtils.getTempDirectoryPath())
            .resolve(UUID.randomUUID() + "_" + baseName + ".png");

        String cmd = "magick " + imagePath.toAbsolutePath()
            + " -resize " + width + "x" + height + "! "
            + " -colorspace Gray "
            + targetImagePath.toAbsolutePath();
        log.info("exec cmd: {}", cmd);
        CommandLine cmdLine = CommandLine.parse(cmd);

        DefaultExecutor executor = new DefaultExecutor.Builder<>()
            .setThreadFactory(THREAD_FACTORY)
            .get();
        executor.execute(cmdLine);
        return targetImagePath;
    }

    /**
     * 创建缩略图
     */
    public static void createThumbnail(Path imagePath, Path thumbnailPath, int width, int height) throws IOException {
        String cmd = "magick " + imagePath.toAbsolutePath()
            + " -thumbnail " + width + "x" + height + "^ "
            + "-gravity Center "
            + "-extent " + width + "x" + height
            + " " + thumbnailPath;
        log.info("exec cmd: {}", cmd);
        CommandLine cmdLine = CommandLine.parse(cmd);

        DefaultExecutor executor = new DefaultExecutor.Builder<>()
            .setThreadFactory(THREAD_FACTORY)
            .get();
        executor.execute(cmdLine);
    }

    /**
     * 获取图片的 EXIF 信息，依赖于 exiftool 工具
     */
    public static String getExif(Path imagePath) throws IOException {
        String cmd = "exiftool " + imagePath.toAbsolutePath();
        log.info("exec cmd: {}", cmd);
        CommandLine cmdLine = CommandLine.parse(cmd);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DefaultExecutor executor = new DefaultExecutor.Builder<>()
            .setThreadFactory(THREAD_FACTORY)
            .setExecuteStreamHandler(new PumpStreamHandler(outputStream))
            .get();
        try {
            executor.execute(cmdLine);
        } catch (Exception e) {
            log.error("exiftool error: {}", e.getMessage());
        }
        return outputStream.toString();
    }

    /**
     * 使用 ffmpeg 获取视频的第一帧
     */
    public static Path getVideoFirstFrame(Path videoPath) throws IOException {
        String baseName = FilenameUtils.getBaseName(videoPath.toString());
        Path targetImagePath = Paths.get(FileUtils.getTempDirectoryPath())
            .resolve(UUID.randomUUID() + "_" + baseName + ".png");

        String cmd = "ffmpeg -i " + videoPath.toAbsolutePath()
            + " -ss 00:00:01.000 -vframes 1 "
            + targetImagePath.toAbsolutePath();
        log.info("exec cmd: {}", cmd);
        CommandLine cmdLine = CommandLine.parse(cmd);

        DefaultExecutor executor = new DefaultExecutor.Builder<>()
            .setThreadFactory(THREAD_FACTORY)
            .get();
        executor.execute(cmdLine);
        return targetImagePath;
    }

}

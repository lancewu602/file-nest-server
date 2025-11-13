package com.example.service;

import com.example.bean.response.MergeResultResp;
import com.example.bean.response.UploadCheckResp;
import com.example.config.BizException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.bean.Constants.CHUNK_BUFFER_SIZE;

/**
 * @author WuQinglong
 * @date 2025/10/31 11:37
 */
@Service
public class UploadService {

    // log
    private static final Logger log = LoggerFactory.getLogger(UploadService.class);

    private static final Cache<String, MergeResultResp> MERGE_PROGRESS = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofDays(1))
        .maximumSize(1000)
        .build();

    /**
     * 检查分片信息
     */
    public UploadCheckResp checkUploadedChunks(String fileId, long chunkSize, long fileSize, int totalChunks) {
        Path chunkDirPath = Paths.get(FileUtils.getTempDirectoryPath()).resolve(fileId);
        if (Files.notExists(chunkDirPath)) {
            return new UploadCheckResp(0);
        }

        // 计算最后一个分片的大小
        long lastFileSize = fileSize - (chunkSize * (totalChunks - 1));

        int chunkIndex = 0;
        try {
            while (chunkIndex < totalChunks) {
                Path chunkFilePath = chunkDirPath.resolve(generateFilePartName(fileId, chunkIndex));
                if (Files.notExists(chunkFilePath)) {
                    break;
                }
                if (!Files.isRegularFile(chunkFilePath)) {
                    break;
                }

                boolean isLastChunk = chunkIndex == totalChunks - 1;
                if (isLastChunk && Files.size(chunkFilePath) != lastFileSize) {
                    break;
                }
                if (!isLastChunk && Files.size(chunkFilePath) != chunkSize) {
                    break;
                }

                chunkIndex++;
            }
        } catch (IOException e) {
            log.error("检查分片信息异常", e);
        }

        return new UploadCheckResp(chunkIndex);
    }

    /**
     * 上传分片
     */
    public void uploadChunk(String fileId, Integer chunkIndex, MultipartFile chunk) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 分片目录不存在，创建一个
        Path chunkDirPath = Paths.get(FileUtils.getTempDirectoryPath()).resolve(fileId);
        if (Files.notExists(chunkDirPath)) {
            Files.createDirectories(chunkDirPath);
        }

        // 转存分片文件
        Path chunkFilePath = chunkDirPath.resolve(generateFilePartName(fileId, chunkIndex));
        chunk.transferTo(chunkFilePath);

        stopWatch.stop();
        log.info("保存分片成功。耗时：{}ms，分片路径：{}", stopWatch.getTotalTimeMillis(),
            chunkFilePath.toAbsolutePath());
    }

    /**
     * 合并分片, 异步进行合并，客户端需要轮训合并结果
     */
    public boolean mergeChunks(
        String fileId, long chunkSize, long fileSize, int totalChunks, Path targetFilePath
    ) {
        try {
            // 分片目录是否存在
            Path chunkDirPath = Paths.get(FileUtils.getTempDirectoryPath()).resolve(fileId);
            if (Files.notExists(chunkDirPath)) {
                throw new BizException("分片目录不存在");
            }

            // 计算最后一个分片的大小
            long lastFileSize = fileSize - (chunkSize * (totalChunks - 1));

            // 验证所有分片并按序号排序
            List<Path> chunkPaths = new ArrayList<>(totalChunks);
            for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                Path chunkFilePath = chunkDirPath.resolve(generateFilePartName(fileId, chunkIndex));
                if (Files.notExists(chunkFilePath)) {
                    throw new BizException("部分分片不完整，请重新上传");
                }

                // 校验分片的大小是否一致
                boolean isLastChunk = chunkIndex == totalChunks - 1;
                if (isLastChunk && Files.size(chunkFilePath) != lastFileSize) {
                    throw new BizException("部分分片不完整，请重新上传");
                }
                if (!isLastChunk && Files.size(chunkFilePath) != chunkSize) {
                    throw new BizException("部分分片不完整，请重新上传");
                }

                chunkPaths.add(chunkFilePath);
            }

            // 初始化进度
            MERGE_PROGRESS.put(fileId, MergeResultResp.merging(0));

            // 合并分片
            try (FileChannel targetChannel = FileChannel.open(
                targetFilePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(CHUNK_BUFFER_SIZE);

                for (int i = 0; i < chunkPaths.size(); i++) {
                    Path chunkPath = chunkPaths.get(i);
                    try (FileChannel sourceChannel = FileChannel.open(chunkPath, StandardOpenOption.READ)) {
                        buffer.clear();
                        while (sourceChannel.read(buffer) > 0) {
                            buffer.flip();
                            while (buffer.hasRemaining()) {
                                targetChannel.write(buffer);
                            }
                            buffer.clear();
                        }
                    }

                    // 更新合并进度
                    double progress = i * 1.0 / chunkPaths.size();
                    MERGE_PROGRESS.put(fileId, MergeResultResp.merging(progress));
                }
                log.info("合并分片完成. fileId:{}", fileId);

                // 校验文件的大小
                if (targetChannel.size() != fileSize) {
                    throw new BizException("合并后分片大小不一致.");
                }

                // 删除空的分片目录
                PathUtils.delete(chunkDirPath);
                log.info("合并分片完成，删除分片目录. fileId:{}", fileId);

                // 合并完成
                MERGE_PROGRESS.put(fileId, MergeResultResp.success());
                return true;
            }

        } catch (BizException e) {
            MERGE_PROGRESS.put(fileId,
                MergeResultResp.failed(e.getMessage())
            );

        } catch (Exception e) {
            log.error("合并异常", e);
            MERGE_PROGRESS.put(fileId,
                MergeResultResp.failed("合并文件异常，请重新上传")
            );
        }

        return false;
    }

    public MergeResultResp pollMergeResult(String fileId) {
        MergeResultResp resp = MERGE_PROGRESS.getIfPresent(fileId);
        // 若合并完成，则从缓存中删除，防止客户端上传同一个文件，导致获取的合并结果有问题
        if (resp != null && !Objects.equals(resp.getStatus(), "MERGING")) {
            MERGE_PROGRESS.invalidate(fileId);
        }
        return resp;
    }

    /**
     * 生成分片文件的分片文件名
     */
    private static String generateFilePartName(String fileId, Integer chunkIndex) {
        return fileId + "-" + chunkIndex + ".part";
    }

}

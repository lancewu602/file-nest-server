package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bean.Ret;
import com.example.bean.entity.Medium;
import com.example.bean.request.CheckChunkRequest;
import com.example.bean.request.DeleteMediumRequest;
import com.example.bean.request.MediumFavoriteRequest;
import com.example.bean.request.MergeResultRequest;
import com.example.bean.request.ResetMediumAlbumRequest;
import com.example.bean.request.RestoreMediumRequest;
import com.example.bean.request.VideoMergeChunkRequest;
import com.example.service.MediumService;
import com.example.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author WuQinglong
 * @date 2025/10/28 15:29
 */
@RestController
public class MediumController {

    @Autowired
    private MediumService mediumService;

    /**
     * 获取媒体列表
     */
    @GetMapping("/api/medium/list")
    public Ret<?> listMediums(
        @RequestParam(required = false, defaultValue = "1") int pageNum,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        return Ret.success(
            mediumService.listMediums(null, null, false, pageNum, pageSize)
        );
    }

    /**
     * 获取相册的媒体列表
     */
    @GetMapping("/api/medium/album")
    public Ret<?> listAlbumMediums(
        @RequestParam Integer albumId,
        @RequestParam(required = false, defaultValue = "1") int pageNum,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        return Ret.success(
            mediumService.listAlbumMediums(albumId, pageNum, pageSize)
        );
    }

    /**
     * 单个媒体文件信息
     */
    @GetMapping("/api/medium/info")
    public Ret<?> mediumInfo(
        @RequestParam Integer mediumId
    ) {
        return Ret.success(
            mediumService.mediumInfo(mediumId)
        );
    }

    /**
     * 重置媒体文件的相册
     */
    @PostMapping("/api/medium/reset-album")
    public Ret<?> resetMediumAlbums(@RequestBody ResetMediumAlbumRequest request) {
        mediumService.resetMediumAlbums(request.getMediumId(), request.getAlbumIds());
        return Ret.success();
    }

    /**
     * 删除媒体文件
     */
    @PostMapping("/api/medium/delete")
    public Ret<?> deleteMediums(@RequestBody DeleteMediumRequest request) {
        mediumService.deleteMediums(request);
        return Ret.success();
    }

    /**
     * 恢复媒体文件
     */
    @PostMapping("/api/medium/restore")
    public Ret<?> restoreMediums(@RequestBody RestoreMediumRequest request) {
        mediumService.restoreMediums(request.getMediumIds());
        return Ret.success();
    }

    /**
     * 永久删除媒体文件
     */
    @PostMapping("/api/medium/delete-forever")
    public Ret<?> deleteForeverMediums(@RequestBody DeleteMediumRequest request) {
        mediumService.deleteForeverMediums(request);
        return Ret.success();
    }

    /**
     * 收藏 或 取消收藏
     */
    @PostMapping("/api/medium/favorite")
    public Ret<?> toggleFavorite(@RequestBody MediumFavoriteRequest request) {
        mediumService.toggleFavorite(request.getMediumIds(), request.getFavorite());
        return Ret.success();
    }

    /**
     * 清除已删除的媒体文件
     */
    @PostMapping("/api/medium/purge-deleted")
    public Ret<?> purgeDeleted() {
        mediumService.purgeDeleted();
        return Ret.success();
    }

    /**
     * 统计最近的媒体文件数量
     */
    @GetMapping("/api/medium/count-recently")
    public Ret<?> countRecently() {
        return Ret.success(
            mediumService.count(new LambdaQueryWrapper<Medium>()
                .eq(Medium::getUserId, ThreadLocalUtil.getCurrentUser().getId())
                .eq(Medium::getDeleted, false)
            )
        );
    }

    /**
     * 上传文件
     */
    @PostMapping("/api/medium/upload/direct")
    public Ret<?> uploadDirect(
        @RequestParam String type,
        @RequestParam String fileName,
        @RequestParam Long dateToken,
        @RequestParam Long lastModified,
        @RequestParam Integer favorite,
        @RequestParam Integer width,
        @RequestParam Integer height,
        @RequestParam Integer duration,
        @RequestPart("file") MultipartFile multipartFile
    ) throws IOException {
        mediumService.uploadDirect(type, fileName, dateToken, lastModified, favorite,
            width, height, duration, multipartFile);
        return Ret.success();
    }

    /**
     * 检查分片是否已上传
     */
    @PostMapping("/api/medium/upload/check-chunks")
    public Ret<?> checkUploadedChunks(@RequestBody CheckChunkRequest request) {
        return Ret.success(
            mediumService.checkUploadedChunks(request)
        );
    }

    /**
     * 上传分片
     */
    @PostMapping("/api/medium/upload/chunk")
    public Ret<?> uploadChunk(
        @RequestParam String fileId,
        @RequestParam Integer chunkIndex,
        @RequestPart("chunk") MultipartFile chunk
    ) throws IOException {
        mediumService.uploadChunk(fileId, chunkIndex, chunk);
        return Ret.success();
    }

    /**
     * 通知合并分片
     */
    @PostMapping("/api/medium/upload/notify-merge-chunks")
    public Ret<?> notifyMergeChunks(@RequestBody VideoMergeChunkRequest request) throws IOException {
        mediumService.notifyMergeChunks(request);
        return Ret.success();
    }

    /**
     * 轮询合并结果
     */
    @PostMapping("/api/medium/upload/poll-merge-result")
    public Ret<?> pollMergeResult(@RequestBody MergeResultRequest request) {
        return Ret.success(
            mediumService.pollMergeResult(request.getFileId())
        );
    }

}

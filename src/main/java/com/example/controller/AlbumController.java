package com.example.controller;

import com.example.bean.Ret;
import com.example.bean.request.AlbumMediumRelationRequest;
import com.example.bean.request.CreateAlbumRequest;
import com.example.bean.request.DeleteAlbumRequest;
import com.example.bean.request.EditAlbumRequest;
import com.example.bean.request.SetAlbumCoverRequest;
import com.example.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WuQinglong
 * @date 2025/11/3 16:51
 */
@RestController
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 获取相册列表
     */
    @GetMapping("/api/album/list")
    public Ret<?> listAlbums(
        // 是否统计媒体数量
        @RequestParam(required = false, defaultValue = "true") Boolean countMedium,
        // 是否排除系统相册
        @RequestParam(required = false, defaultValue = "false") boolean excludeSystem
    ) {
        return Ret.success(
            albumService.listAlbums(countMedium, excludeSystem)
        );
    }

    /**
     * 获取相册信息
     */
    @GetMapping("/api/album/info")
    public Ret<?> albumInfo(
        @RequestParam Integer albumId,
        // 是否统计媒体数量
        @RequestParam(required = false, defaultValue = "false") Boolean countMedium
    ) {
        return Ret.success(
            albumService.albumInfo(albumId, countMedium)
        );
    }

    /**
     * 设置相册封面
     */
    @PostMapping("/api/album/set-cover")
    public Ret<?> setCover(
        @RequestBody SetAlbumCoverRequest request
    ) {
        albumService.setCover(request.getAlbumId(), request.getMediumId());
        return Ret.success();
    }

    /**
     * 添加媒体文件到相册
     */
    @PostMapping("/api/album/add-medium")
    public Ret<?> addMediumToAlbums(@RequestBody AlbumMediumRelationRequest request) {
        albumService.addMediumToAlbums(request.getAlbumIds(), request.getMediumIds());
        return Ret.success();
    }

    /**
     * 移除媒体文件
     */
    @PostMapping("/api/album/remove-medium")
    public Ret<?> removeMediumFromAlbum(@RequestBody AlbumMediumRelationRequest request) {
        albumService.removeMediumFromAlbum(request.getAlbumIds(), request.getMediumIds());
        return Ret.success();
    }

    /**
     * 创建相册
     */
    @PostMapping("/api/album/create")
    public Ret<?> create(@RequestBody CreateAlbumRequest request) {
        albumService.create(request);
        return Ret.success();
    }

    /**
     * 编辑相册
     */
    @PostMapping("/api/album/edit")
    public Ret<?> edit(@RequestBody EditAlbumRequest request) {
        albumService.edit(request);
        return Ret.success();
    }

    /**
     * 删除相册
     */
    @PostMapping("/api/album/delete")
    public Ret<?> deleteAlbum(@RequestBody DeleteAlbumRequest request) {
        albumService.delete(request.getId());
        return Ret.success();
    }

}

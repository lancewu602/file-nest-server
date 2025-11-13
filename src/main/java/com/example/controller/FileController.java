package com.example.controller;

import com.example.bean.Ret;
import com.example.bean.request.CheckChunkRequest;
import com.example.bean.request.ClearTrashRequest;
import com.example.bean.request.CreateDirectoryRequest;
import com.example.bean.request.DeleteFileRequest;
import com.example.bean.request.FileMergeChunkRequest;
import com.example.bean.request.MergeResultRequest;
import com.example.bean.request.MoveOrCopyFileRequest;
import com.example.bean.request.RenameFileRequest;
import com.example.service.LocalStorageService;
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
 * @date 2025/9/2 22:51
 */
@RestController
public class FileController {

    @Autowired
    private LocalStorageService localStorageService;

    @GetMapping("/api/file/list")
    public Ret<?> listFiles(
        @RequestParam Integer storageId,
        @RequestParam(defaultValue = "") String path,
        @RequestParam(required = false, defaultValue = "false") boolean excludeFile,
        @RequestParam(required = false, defaultValue = "false") boolean excludeHidden,
        @RequestParam(required = false, defaultValue = "false") boolean isTrash
    ) throws IOException {
        return Ret.success(
            localStorageService.listFiles(storageId, path, excludeFile, excludeHidden, isTrash)
        );
    }

    @PostMapping("/api/file/create-directory")
    public Ret<?> createDirectory(@RequestBody CreateDirectoryRequest request) throws IOException {
        localStorageService.createDirectory(request);
        return Ret.success();
    }

    @PostMapping("/api/file/move-to-trash")
    public Ret<?> moveFilesToTrash(@RequestBody DeleteFileRequest request) throws IOException {
        localStorageService.moveFilesToTrash(request);
        return Ret.success();
    }

    @PostMapping("/api/file/rename")
    public Ret<?> renameFile(@RequestBody RenameFileRequest request) throws IOException {
        localStorageService.renameFile(request);
        return Ret.success();
    }

    @PostMapping("/api/file/move")
    public Ret<?> moveFiles(@RequestBody MoveOrCopyFileRequest request) throws IOException {
        localStorageService.moveOrCopyFiles(request, true);
        return Ret.success();
    }

    @PostMapping("/api/file/copy")
    public Ret<?> copyFiles(@RequestBody MoveOrCopyFileRequest request) throws IOException {
        localStorageService.moveOrCopyFiles(request, false);
        return Ret.success();
    }

    @PostMapping("/api/file/delete")
    public Ret<?> deleteFiles(@RequestBody DeleteFileRequest request) throws IOException {
        localStorageService.deleteFiles(request);
        return Ret.success();
    }

    @PostMapping("/api/file/clear-trash")
    public Ret<?> clearTrash(@RequestBody ClearTrashRequest request) throws IOException {
        localStorageService.clearTrash(request);
        return Ret.success();
    }

    @PostMapping("/api/file/upload/check")
    public Ret<?> checkChunks(@RequestBody CheckChunkRequest request) {
        return Ret.success(
            localStorageService.checkUploadedChunks(request)
        );
    }

    @PostMapping("/api/file/upload/chunk")
    public Ret<?> uploadChunk(
        @RequestParam String fileId,
        @RequestParam Integer chunkIndex,
        @RequestPart("chunk") MultipartFile chunk
    ) throws IOException {
        localStorageService.uploadChunk(fileId, chunkIndex, chunk);
        return Ret.success();
    }

    @PostMapping("/api/file/upload/merge")
    public Ret<?> uploadMerge(@RequestBody FileMergeChunkRequest request) {
        localStorageService.notifyMergeChunks(request);
        return Ret.success();
    }

    @PostMapping("/api/file/upload/merge/result")
    public Ret<?> uploadMergeResult(@RequestBody MergeResultRequest request) {
        return Ret.success(
            localStorageService.pollMergeResult(request.getFileId())
        );
    }

}

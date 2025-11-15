package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bean.entity.Storage;
import com.example.bean.enums.FileType;
import com.example.bean.request.CheckChunkRequest;
import com.example.bean.request.ClearTrashRequest;
import com.example.bean.request.CreateDirectoryRequest;
import com.example.bean.request.DeleteFileRequest;
import com.example.bean.request.FileMergeChunkRequest;
import com.example.bean.request.MoveOrCopyFileRequest;
import com.example.bean.request.RenameFileRequest;
import com.example.bean.response.FileInfoResp;
import com.example.bean.response.MergeResultResp;
import com.example.bean.response.UploadCheckResp;
import com.example.config.BizException;
import com.example.mapper.StorageMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author WuQinglong
 * @date 2025/9/3 08:27
 */
@Service
public class LocalStorageService extends ServiceImpl<StorageMapper, Storage> {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);

    private static final ThreadPoolExecutor FILE_EXECUTOR = new ThreadPoolExecutor(
        10, 10, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(1000)
    );

    @Autowired
    private UploadService uploadService;

    /**
     * 列出存储下的文件信息
     * @param path 相对应存储的路径，是一个相对路径
     * 获取根目录下的文件列表，传 "",
     * 获取根目录下 abc 目录下的文件列表，传 ”abc“
     * 获取根目录下 abc 目录下 dev 目录下的文件列表，传 ”abc/def“
     */
    public List<FileInfoResp> listFiles(
        Integer storageId, String path, boolean excludeFile, boolean excludeHidden, boolean isTrash
    ) throws IOException {

        // 找到路径所属的存储
        Storage storage = getById(storageId);
        if (storage == null) {
            return null;
        }

        // 当前基目录，若是回收站，则以 trashPath 为基目录
        Path mountPath = Paths.get(storage.getMountPath());
        if (isTrash) {
            mountPath = Paths.get(storage.getMountPath(), storage.getTrashName());
        }

        Path directoryPath = mountPath.resolve(path);
        if (Files.notExists(directoryPath) || !Files.isDirectory(directoryPath)) {
            return null;
        }

        // 遍历目录下的文件
        List<FileInfoResp> children = new ArrayList<>();
        try (Stream<Path> stream = Files.list(directoryPath)) {
            List<Path> subPaths = stream.sorted(Comparator
                    // 先按照类型排序
                    .comparing((Function<Path, Boolean>) Files::isRegularFile)
                    // 再按照文件名排序
                    .thenComparing(Path::getFileName)
                )
                .filter(subPath -> {
                    // 过滤掉回收站（在根路径下）
                    if (StringUtils.isEmpty(path)) {
                        return !Objects.equals(subPath.getFileName().toString(), storage.getTrashName());
                    }
                    return true;
                })
                .filter(subPath -> {
                    if (excludeHidden) {
                        try {
                            return !Files.isHidden(subPath);
                        } catch (IOException e) {
                            return true;
                        }
                    }
                    return true;
                })
                .filter(subPath -> {
                    if (excludeFile) {
                        return !Files.isRegularFile(subPath);
                    }
                    return true;
                })
                .toList();

            for (Path subPath : subPaths) {
                BasicFileAttributes fileAttributes = Files.readAttributes(subPath, BasicFileAttributes.class);

                FileInfoResp info = new FileInfoResp();
                info.setName(subPath.getFileName().toString());

                if (fileAttributes.isRegularFile()) {
                    info.setType(FileType.FILE.getValue());

                    String fileDisplaySize = FileUtils.byteCountToDisplaySize(
                        fileAttributes.size()
                    ).replace(" ", "");
                    info.setDisplaySize(fileDisplaySize);

                } else if (fileAttributes.isDirectory()) {
                    info.setType(FileType.DIRECTORY.getValue());

                    IOFileFilter fileFilter = excludeFile ? DirectoryFileFilter.INSTANCE : TrueFileFilter.INSTANCE;
                    File[] subDirSubFiles = subPath.toFile().listFiles((FileFilter) fileFilter);
                    info.setSubFileCount(subDirSubFiles == null ? 0 : subDirSubFiles.length);
                    info.setChildren(info.getSubFileCount() > 0 ? Collections.emptyList() : null);

                } else {
                    info.setType(FileType.UNKNOWN.getValue());
                }

                long lastModifiedTime = fileAttributes.lastModifiedTime().to(TimeUnit.MILLISECONDS);
                info.setLastModified(DateFormatUtils.format(new Date(lastModifiedTime), "yyyy-MM-dd HH:mm:ss"));

                info.setPath(mountPath.relativize(subPath).toString());

                children.add(info);
            }
        }

        return children;
    }

    /**
     * 创建目录
     */
    public void createDirectory(CreateDirectoryRequest request) throws IOException {
        Assert.notNull(request.getStorageId(), "存储id不能为空");
        Assert.hasLength(request.getName(), "目录名称不能为空");

        // 获取存储信息
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        // 获取目录路径
        Path mountPath = Paths.get(storage.getMountPath());
        Path directoryPath = mountPath.resolve(request.getPath());
        Path newDirectoryPath = directoryPath.resolve(request.getName());

        if (Files.exists(newDirectoryPath)) {
            throw new BizException("目录已存在");
        }

        Files.createDirectories(newDirectoryPath);
    }

    /**
     * 移动到回收站
     */
    public void moveFilesToTrash(DeleteFileRequest request) throws IOException {
        Assert.notNull(request.getStorageId(), "存储id不能为空");

        // 获取存储信息
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        // 回收站目录
        Path mountPath = Paths.get(storage.getMountPath());
        Path trashPath = mountPath.resolve(storage.getTrashName());
        Assert.isTrue(Files.exists(trashPath), "回收站不存在");
        Assert.isTrue(Files.isDirectory(trashPath), "回收站必须是目录");

        // 待删除文件的目录
        Path directoryPath = mountPath.resolve(request.getPath());
        for (String fileName : request.getFileNames()) {
            Path filePath = directoryPath.resolve(fileName);
            Path filePathInTrash = trashPath.resolve(fileName);
            if (Files.exists(filePathInTrash)) {
                String fileNameInTrash;
                if (Files.isRegularFile(filePath)) {
                    // 若是文件，需要在文件后缀名前面加上一个时间
                    fileNameInTrash = FilenameUtils.getBaseName(filePath.getFileName().toString())
                        + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")
                        + "." + FilenameUtils.getExtension(filePath.getFileName().toString());

                } else {
                    // 若是目录，需要在目录名后面加上一个时间
                    fileNameInTrash = filePath.getFileName().toString()
                        + "_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
                }

                filePathInTrash = trashPath.resolve(fileNameInTrash);
            }

            Files.move(filePath, filePathInTrash);
        }
    }

    /**
     * 重命名文件
     */
    public void renameFile(RenameFileRequest request) throws IOException {
        Assert.notNull(request.getStorageId(), "存储id不能为空");

        // 获取存储信息
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        Path mountPath = Paths.get(storage.getMountPath());
        Path directoryPath = mountPath.resolve(request.getPath());

        Path oldFilePath = directoryPath.resolve(request.getOldName());
        Assert.isTrue(Files.exists(oldFilePath), "源文件不存在");

        Path newFilePath = directoryPath.resolve(request.getNewName());
        Assert.isTrue(Files.notExists(newFilePath), request.getNewName() + " 已存在");

        Files.move(oldFilePath, newFilePath);
    }

    /**
     * 移动或复制文件
     */
    public void moveOrCopyFiles(MoveOrCopyFileRequest request, boolean isMove) throws IOException {
        Assert.notNull(request.getStorageId(), "存储id不能为空");

        // 获取存储信息
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        Path mountPath = Paths.get(storage.getMountPath());
        Path targetDirectoryPath = mountPath.resolve(request.getTargetPath());

        // 校验
        for (String fileName : request.getFileNames()) {
            Path targetPath = targetDirectoryPath.resolve(fileName);
            Assert.isTrue(Files.notExists(targetPath), "目标目录下已存在重命的文件");
        }

        // 移动
        Path sourceDirectoryPath = mountPath.resolve(request.getSourcePath());
        for (String fileName : request.getFileNames()) {
            Path sourcePath = sourceDirectoryPath.resolve(fileName);
            if (Files.notExists(sourcePath)) {
                continue;
            }

            Path targetPath = targetDirectoryPath.resolve(fileName);
            if (isMove) {
                Files.move(sourcePath, targetPath);
            } else {
                Files.copy(sourcePath, targetPath);
            }
        }
    }

    /**
     * 删除文件
     */
    public void deleteFiles(DeleteFileRequest request) throws IOException {
        // 获取存储信息
        Assert.notNull(request.getStorageId(), "存储id不能为空");
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        // 限制只能删除回收站中的文件
        if (!request.getPath().contains(storage.getTrashName())) {
            return;
        }

        Path mountPath = Paths.get(storage.getMountPath());
        Path directoryPath = mountPath.resolve(request.getPath());
        for (String fileName : request.getFileNames()) {
            Path filePath = directoryPath.resolve(fileName);
            if (Files.notExists(filePath)) {
                continue;
            }
            PathUtils.delete(filePath);
        }
    }

    public void clearTrash(ClearTrashRequest request) throws IOException {
        // 获取存储信息
        Assert.notNull(request.getStorageId(), "存储id不能为空");
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            return;
        }

        Path mountPath = Paths.get(storage.getMountPath());
        Path trashPath = mountPath.resolve(storage.getTrashName());
        try (Stream<Path> stream = Files.list(trashPath)) {
            List<Path> subPaths = stream.toList();
            for (Path subPath : subPaths) {
                PathUtils.delete(subPath);
            }
        }
    }

    /**
     * 检查分片信息
     */
    public UploadCheckResp checkUploadedChunks(CheckChunkRequest request) {
        return uploadService.checkUploadedChunks(
            request.getFileId(), request.getChunkSize(), request.getTotalSize(), request.getTotalChunks()
        );
    }

    /**
     * 上传分片
     */
    public void uploadChunk(String fileId, Integer chunkIndex, MultipartFile chunk) throws IOException {
        uploadService.uploadChunk(fileId, chunkIndex, chunk);
    }

    /**
     * 合并分片
     */
    public void notifyMergeChunks(FileMergeChunkRequest request) {
        Assert.notNull(request.getStorageId(), "存储id不能为空");

        // 获取存储信息
        Storage storage = getById(request.getStorageId());
        if (storage == null) {
            throw new BizException("存储目录不存在，上传失败！");
        }

        // 分片目录是否存在
        Path chunkDirPath = Paths.get(FileUtils.getTempDirectoryPath()).resolve(request.getFileId());
        if (Files.notExists(chunkDirPath)) {
            throw new BizException("分片目录不存在");
        }

        // 获取合并之后的目标文件
        Path mountPath = Paths.get(storage.getMountPath());
        Path directoryPath = mountPath.resolve(request.getPath());
        Path targetFilePath = directoryPath.resolve(request.getFileName());

        FILE_EXECUTOR.submit(() -> {
            uploadService.mergeChunks(
                request.getFileId(), request.getChunkSize(), request.getTotalSize(), request.getTotalChunks(),
                targetFilePath
            );
        });
    }

    public MergeResultResp pollMergeResult(String fileId) {
        return uploadService.pollMergeResult(fileId);
    }

}

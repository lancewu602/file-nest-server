package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bean.Constants;
import com.example.bean.entity.Album;
import com.example.bean.entity.AlbumMediumMapping;
import com.example.bean.entity.Medium;
import com.example.bean.entity.User;
import com.example.bean.model.ExifInfo;
import com.example.bean.request.CheckChunkRequest;
import com.example.bean.request.DeleteMediumRequest;
import com.example.bean.request.VideoMergeChunkRequest;
import com.example.bean.response.MediumResp;
import com.example.bean.response.MediumsRow;
import com.example.bean.response.MergeResultResp;
import com.example.config.BizException;
import com.example.mapper.AlbumMapper;
import com.example.mapper.AlbumMediumMappingMapper;
import com.example.mapper.MediumMapper;
import com.example.util.DHashUtil;
import com.example.util.ExifUtil;
import com.example.util.JacksonUtil;
import com.example.util.MagickUtil;
import com.example.util.ThreadLocalUtil;
import com.example.util.VideoUtil;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author WuQinglong
 * @date 2025/10/28 15:29
 */
@Service
public class MediumService extends ServiceImpl<MediumMapper, Medium> {

    // log
    private static final Logger log = LoggerFactory.getLogger(MediumService.class);

    @Value("${filenest.media.data.path}")
    private String dataPath;

    @Autowired
    private UploadService uploadService;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private AlbumMediumMappingMapper albumMediumMappingMapper;

    private static final String LIBRARY = "library"; // 原始文件
    private static final String THUMBS = "thumbs"; // 文件缩略图

    private static Set<String> mediaTypes = Sets.newHashSet("image", "video");

    private static ThreadPoolExecutor mediaExecutor = new ThreadPoolExecutor(
        10, 10, 1, TimeUnit.HOURS, new LinkedBlockingQueue<>(1000)
    );

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<MediumsRow> listMediums(
        List<Integer> mediumIds, Boolean favorite, Boolean deleted, boolean groupByDate, int rowSize
    ) {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Medium> media = list(new LambdaQueryWrapper<Medium>()
            .eq(Medium::getUserId, currentUser.getId())
            .in(CollectionUtils.isNotEmpty(mediumIds), Medium::getId, mediumIds)
            .eq(favorite != null, Medium::getFavorite, favorite)
            .eq(Medium::getDeleted, deleted != null && deleted) // 默认查找未删除的
            .orderByDesc(Medium::getDateToken)
        );

        List<MediumsRow> mediumsRows = new ArrayList<>();
        List<MediumResp> mediums = new ArrayList<>();
        String currentTitle = "";
        for (Medium medium : media) {
            String title = medium.getDateToken().toLocalDate().format(dateFormatter);
            if (groupByDate && !Objects.equals(currentTitle, title)) {

                if (!mediums.isEmpty()) {
                    for (int i = mediums.size(); i < rowSize; i++) {
                        mediums.add(null);
                    }
                    mediumsRows.add(MediumsRow.of(mediums));
                    mediums = new ArrayList<>();
                }

                // 添加一个日期行
                mediumsRows.add(MediumsRow.of(title));

                currentTitle = title;
            }

            MediumResp resp = new MediumResp();
            resp.setId(medium.getId());
            resp.setType(medium.getType());
            resp.setName(medium.getName());
            resp.setSize(medium.getSize());
            resp.setFavorite(medium.getFavorite());
            resp.setThumbnailPath(medium.getThumbnailPath());
            resp.setOriginalPath(medium.getOriginalPath());
            resp.setDateToken(dateTimeFormatter.format(medium.getDateToken()));
            resp.setLastModified(dateTimeFormatter.format(medium.getLastModified()));

            mediums.add(resp);

            if (mediums.size() == rowSize) {
                mediumsRows.add(MediumsRow.of(mediums));
                mediums = new ArrayList<>();
            }
        }

        if (!mediums.isEmpty()) {
            for (int i = mediums.size(); i < rowSize; i++) {
                mediums.add(null);
            }
            mediumsRows.add(MediumsRow.of(mediums));
        }

        return mediumsRows;
    }

    public MediumResp mediumInfo(Integer mediumId) {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        Medium medium = getById(mediumId);
        Assert.isTrue(medium != null && medium.getUserId().equals(currentUser.getId()), "媒体不存在");

        MediumResp resp = new MediumResp();
        resp.setId(medium.getId());
        resp.setType(medium.getType());
        resp.setName(medium.getName());
        resp.setSize(medium.getSize());
        resp.setFavorite(medium.getFavorite());
        resp.setThumbnailPath(medium.getThumbnailPath());
        resp.setOriginalPath(medium.getOriginalPath());
        resp.setDateToken(dateTimeFormatter.format(medium.getDateToken()));
        resp.setLastModified(dateTimeFormatter.format(medium.getLastModified()));

        List<Integer> albumIds = albumMediumMappingMapper.selectList(
            new LambdaQueryWrapper<AlbumMediumMapping>()
                .eq(AlbumMediumMapping::getMediumId, mediumId)
        ).stream().map(AlbumMediumMapping::getAlbumId).distinct().toList();
        resp.setInAlbumIds(albumIds);

        return resp;
    }

    public List<MediumsRow> listAlbumMediums(Integer albumId, Integer rowSize) {
        Assert.notNull(albumId, "相册ID不能为空");
        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 最近上传
        if (Objects.equals(albumId, Constants.RECENTLY_ALBUM_ID)) {
            return listMediums(null, null, false, false, rowSize);
        }

        // 我的收藏
        if (Objects.equals(albumId, Constants.FAVORITE_ALBUM_ID)) {
            return listMediums(null, true, false, false, rowSize);
        }

        // 最近删除
        if (Objects.equals(albumId, Constants.DELETED_ALBUM_ID)) {
            return listMediums(null, null, true, false, rowSize);
        }

        // 自定义相册
        Album album = albumMapper.selectById(albumId);
        Assert.notNull(album, "相册不存在");
        Assert.isTrue(album.getUserId().equals(currentUser.getId()), "无权限访问该相册");

        // 查询相册已关联的媒体
        List<Integer> mediumIds = albumMediumMappingMapper.selectList(new LambdaQueryWrapper<AlbumMediumMapping>()
            .eq(AlbumMediumMapping::getAlbumId, albumId)
        ).stream().map(AlbumMediumMapping::getMediumId).toList();

        // 指定相册查询，但是相册是空的
        if (mediumIds.isEmpty()) {
            return Collections.emptyList();
        }

        return listMediums(mediumIds, null, null, false, rowSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMediums(DeleteMediumRequest request) {
        if (CollectionUtils.isEmpty(request.getMediumIds())) {
            return;
        }

        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Medium> mediums = list(new LambdaQueryWrapper<Medium>()
            .in(Medium::getId, request.getMediumIds())
            .eq(Medium::getUserId, currentUser.getId())
        );
        if (mediums.isEmpty()) {
            return;
        }

        List<Integer> mediumIds = mediums.stream().map(Medium::getId).toList();

        // 批量软删除
        update(new LambdaUpdateWrapper<Medium>()
            .in(Medium::getId, mediumIds)
            .set(Medium::getFavorite, false)
            .set(Medium::getDeleted, true)
        );

        // 删除相册的关联关系
        albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
            .in(AlbumMediumMapping::getMediumId, mediumIds)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteForeverMediums(DeleteMediumRequest request) {
        if (CollectionUtils.isEmpty(request.getMediumIds())) {
            return;
        }

        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Medium> mediums = list(new LambdaQueryWrapper<Medium>()
            .in(Medium::getId, request.getMediumIds())
            .eq(Medium::getUserId, currentUser.getId())
        );
        if (mediums.isEmpty()) {
            return;
        }

        // 永久删除
        List<Integer> mediumIds = mediums.stream().map(Medium::getId).toList();
        removeBatchByIds(mediumIds);

        // 删除相册的关联关系
        albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
            .in(AlbumMediumMapping::getMediumId, mediumIds)
        );

        // TODO 删除对应的文件（先移动到某个目录下，再进行后台删除）
    }

    public void toggleFavorite(List<Integer> mediumIds, Boolean favorite) {
        Assert.notNull(mediumIds, "媒体ID不能为空");
        Assert.notNull(favorite, "收藏状态不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();

        update(new LambdaUpdateWrapper<Medium>()
            .eq(Medium::getUserId, currentUser.getId())
            .in(Medium::getId, mediumIds)
            .set(Medium::getFavorite, favorite)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void purgeDeleted() {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Medium> mediums = list(new LambdaQueryWrapper<Medium>()
            .eq(Medium::getUserId, currentUser.getId())
            .eq(Medium::getDeleted, true)
        );
        List<Integer> mediumIds = mediums.stream().map(Medium::getId).toList();
        removeBatchByIds(mediumIds);

        // TODO 删除对应的文件（先移动到某个目录下，再进行后台删除）
    }

    /**
     * 上传图片
     * @param imageName 图片名称
     * @param multipartFile 图片文件
     */
    public void uploadDirect(
        String type, String imageName, Long dateToken, Long lastModified, Integer isFavorite,
        MultipartFile multipartFile
    ) throws IOException {
        Assert.isTrue(mediaTypes.contains(type), "只允许上传图片和视频");
        Assert.hasLength(imageName, "名称不能为空");
        Assert.isTrue(dateToken != null && dateToken > 0, "最后修改时间格式错误");
        Assert.notNull(multipartFile, "文件不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 先落库，生成 id，然后将这个 id 追加到文件名中，防止文件名重复
        Medium medium = new Medium();
        medium.setUserId(currentUser.getId());
        medium.setType(type);
        medium.setName(imageName);
        medium.setSize(multipartFile.getSize());
        medium.setDateToken(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(dateToken), ZoneId.systemDefault())
        );
        medium.setLastModified(
            LocalDateTime.ofInstant(Instant.ofEpochSecond(lastModified), ZoneId.systemDefault())
        );
        medium.setFavorite(isFavorite != null && isFavorite == 1);
        save(medium);

        // 找到存储目录
        Path mediaDirectoryPath = getMediaDirectoryPath(medium.getDateToken(), currentUser.getName(), false);
        if (Files.notExists(mediaDirectoryPath)) {
            Files.createDirectories(mediaDirectoryPath);
        }

        // 生成新的文件名
        String baseName = FilenameUtils.getBaseName(imageName);
        String extension = FilenameUtils.getExtension(imageName);
        String newImageName = baseName + "_" + medium.getId() + "." + extension;
        Path mediaFilePath = mediaDirectoryPath.resolve(newImageName);

        // 将文件保存到指定目录
        try {
            multipartFile.transferTo(mediaFilePath);
        } catch (Exception e) {
            throw new RuntimeException("上传失败", e);
        }
        log.info("上传成功：{}", mediaFilePath);

        // 后置任务（异步）
        mediaExecutor.submit(() -> {
            if ("image".equals(type)) {
                imagePostTask(medium, mediaFilePath, newImageName, currentUser.getName());
            }
            if ("video".equals(type)) {
                videoPostTask(medium, mediaFilePath, newImageName, currentUser.getName());
            }
        });
    }

    /**
     * 检查分片信息
     */
    public Object checkUploadedChunks(CheckChunkRequest request) {
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
    public void notifyMergeChunks(VideoMergeChunkRequest request) throws IOException {
        // 分片目录是否存在
        Path chunkDirPath = Paths.get(FileUtils.getTempDirectoryPath()).resolve(request.getFileId());
        if (Files.notExists(chunkDirPath)) {
            throw new BizException("分片目录不存在");
        }

        Long dateToken = request.getDateToken();
        Long lastModified = request.getLastModified();

        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 落库
        Medium medium = new Medium();
        medium.setUserId(currentUser.getId());
        medium.setName(request.getFileName());
        medium.setType("video");
        medium.setSize(request.getTotalSize());
        medium.setDateToken(
            LocalDateTime.ofInstant(Instant.ofEpochSecond(dateToken), ZoneId.systemDefault())
        );
        medium.setLastModified(
            LocalDateTime.ofInstant(Instant.ofEpochSecond(lastModified), ZoneId.systemDefault())
        );
        save(medium);

        // 找到图片的存储目录
        Path mediaDirectoryPath = getMediaDirectoryPath(medium.getDateToken(), currentUser.getName(), true);
        if (Files.notExists(mediaDirectoryPath)) {
            Files.createDirectories(mediaDirectoryPath);
        }

        // 生成新的文件名
        String newFileName = FilenameUtils.getBaseName(request.getFileName())
            + "_" + medium.getId()
            + "." + FilenameUtils.getExtension(request.getFileName());
        Path newMediaFilePath = mediaDirectoryPath.resolve(newFileName);

        // 异步合并
        mediaExecutor.submit(() -> {
            boolean success = uploadService.mergeChunks(
                request.getFileId(), request.getChunkSize(), request.getTotalSize(), request.getTotalChunks(),
                newMediaFilePath
            );
            if (success) {
                videoPostTask(medium, newMediaFilePath, newFileName, currentUser.getName());
            } else {
                // 删除这条记录
                removeById(medium.getId());
            }
        });
    }

    /**
     * 轮询合并结果
     */
    public MergeResultResp pollMergeResult(String fileId) {
        return uploadService.pollMergeResult(fileId);
    }

    /**
     * 图片的后置任务
     */
    private void imagePostTask(Medium medium, Path mediaFilePath, String mediaFileName, String username) {
        try {
            // 1、提取 exif 信息
            ExifInfo exifInfo = ExifUtil.extract(mediaFilePath);
            if (exifInfo != null) {
                System.out.println(medium.getName() + ": " + exifInfo.getFileModificationDateTime());
            }

            // 2、计算 dhash 和 phash
            String dhash = "";
            try {
                dhash = DHashUtil.calculateDHash(mediaFilePath);
            } catch (IOException e) {
                log.error("计算 dhash 失败", e);
            }

            String phash = "";
            try {
                phash = DHashUtil.calculateDHash(mediaFilePath);
            } catch (IOException e) {
                log.error("计算 phash 失败", e);
            }

            // 3、创建缩略图
            Path thumbnailDirectoryPath = getMediaDirectoryPath(medium.getLastModified(), username, true);
            if (Files.notExists(thumbnailDirectoryPath)) {
                Files.createDirectories(thumbnailDirectoryPath);
            }
            String thumbnailFileName = FilenameUtils.getBaseName(mediaFileName) + "_thumbnail.webp";
            Path thumbnailFilePath = thumbnailDirectoryPath.resolve(thumbnailFileName);
            MagickUtil.generateThumbnail(mediaFilePath, thumbnailFilePath);

            // 更新图片信息
            Medium updateMedium = new Medium();
            updateMedium.setId(medium.getId());
            updateMedium.setName(mediaFileName);
            updateMedium.setExif(JacksonUtil.toJson(exifInfo));
            updateMedium.setPhash(phash);
            updateMedium.setDhash(dhash);
            // 存储相对路径
            updateMedium.setOriginalPath(Paths.get(dataPath).relativize(mediaFilePath).toString());
            updateMedium.setThumbnailPath(Paths.get(dataPath).relativize(thumbnailFilePath).toString());
            updateById(updateMedium);
        } catch (IOException e) {
            log.error("处理图片信息异常", e);
        }
    }

    /**
     * 视频的后置任务
     */
    private void videoPostTask(Medium medium, Path newMediaFilePath, String mediaFileName, String username) {
        try {
            // 创建缩略图
            Path thumbnailFilePath = null;
            try {
                Path thumbnailDirectoryPath = getMediaDirectoryPath(medium.getLastModified(), username, true);
                if (Files.notExists(thumbnailDirectoryPath)) {
                    Files.createDirectories(thumbnailDirectoryPath);
                }
                String thumbnailFileName = FilenameUtils.getBaseName(mediaFileName) + "_thumbnail.webp";
                thumbnailFilePath = thumbnailDirectoryPath.resolve(thumbnailFileName);

                VideoUtil.generateThumbnail(newMediaFilePath, thumbnailFilePath);
            } catch (IOException e) {
                log.error("生成缩略图异常", e);
            }

            // 更新数据库
            Medium updateMedium = new Medium();
            updateMedium.setId(medium.getId());
            updateMedium.setName(mediaFileName);
            // 存储相对路径
            updateMedium.setOriginalPath(Paths.get(dataPath).relativize(newMediaFilePath).toString());
            if (thumbnailFilePath != null) {
                updateMedium.setThumbnailPath(Paths.get(dataPath).relativize(thumbnailFilePath).toString());
            }
            updateById(updateMedium);

        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 获取媒体文件的存储目录。格式：library|thumbs/用户/年/月/日
     */
    private Path getMediaDirectoryPath(LocalDateTime dateTime, String username, boolean thumbs) {
        return Paths.get(dataPath)
            .resolve(thumbs ? THUMBS : LIBRARY)
            .resolve(username)
            .resolve(String.valueOf(dateTime.getYear()))
            .resolve(String.valueOf(dateTime.getMonthValue()))
            .resolve(String.valueOf(dateTime.getDayOfMonth()));
    }
}

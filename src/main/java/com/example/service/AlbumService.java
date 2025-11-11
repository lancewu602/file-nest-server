package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bean.Constants;
import com.example.bean.entity.Album;
import com.example.bean.entity.AlbumMediumMapping;
import com.example.bean.entity.Medium;
import com.example.bean.entity.User;
import com.example.bean.request.CreateAlbumRequest;
import com.example.bean.request.EditAlbumRequest;
import com.example.bean.response.AlbumInfo;
import com.example.mapper.AlbumMapper;
import com.example.mapper.AlbumMediumMappingMapper;
import com.example.mapper.MediumMapper;
import com.example.util.ThreadLocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author WuQinglong
 * @date 2025/11/3 16:51
 */
@Service
public class AlbumService extends ServiceImpl<AlbumMapper, Album> {

    // log
    private static final Logger log = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    private AlbumMediumMappingMapper albumMediumMappingMapper;
    @Autowired
    private MediumMapper mediumMapper;
    @Autowired
    private PhotoService photoService;

    public List<AlbumInfo> listAlbums(Boolean countMedium, boolean excludeSystem) {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Album> albums = list(new LambdaQueryWrapper<Album>()
            .eq(Album::getUserId, currentUser.getId())
        );

        List<AlbumInfo> list = new ArrayList<>();
        if (!excludeSystem) {
            // 最近上传
            AlbumInfo resp = new AlbumInfo();
            resp.setId(Constants.RECENTLY_ALBUM_ID);
            resp.setName(Constants.RECENTLY_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, false)
                ));
            }
            // 查询最后一个上传的媒体缩略图
            Medium coverMedium = mediumMapper.selectOne(new LambdaQueryWrapper<Medium>()
                .eq(Medium::getUserId, currentUser.getId())
                .eq(Medium::getDeleted, false)
                .orderByDesc(Medium::getCreatedAt)
                .last("limit 1")
            );
            if (coverMedium == null) {
                resp.setCoverPath("");
            } else {
                resp.setCoverPath(coverMedium.getThumbnailPath());
            }
            resp.setSystem(true);
            list.add(resp);

            // 我的收藏
            resp = new AlbumInfo();
            resp.setId(Constants.FAVORITE_ALBUM_ID);
            resp.setName(Constants.FAVORITE_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, false)
                    .eq(Medium::getFavorite, true)
                ));
            }
            coverMedium = mediumMapper.selectOne(new LambdaQueryWrapper<Medium>()
                .eq(Medium::getUserId, currentUser.getId())
                .eq(Medium::getDeleted, false)
                .eq(Medium::getFavorite, true)
                .orderByDesc(Medium::getCreatedAt)
                .last("limit 1")
            );
            if (coverMedium == null) {
                resp.setCoverPath("");
            } else {
                resp.setCoverPath(coverMedium.getThumbnailPath());
            }
            resp.setSystem(true);
            list.add(resp);
        }

        for (Album album : albums) {
            AlbumInfo resp = new AlbumInfo();
            resp.setId(album.getId());
            resp.setName(album.getName());

            Medium medium = mediumMapper.selectById(album.getCoverMediumId());
            if (medium == null) {
                resp.setCoverPath("");
            } else {
                resp.setCoverPath(medium.getThumbnailPath());
            }

            if (countMedium) {
                Long count = albumMediumMappingMapper.selectCount(new LambdaQueryWrapper<AlbumMediumMapping>()
                    .eq(AlbumMediumMapping::getAlbumId, album.getId())
                );
                resp.setMediaCount(count);
            }

            list.add(resp);
        }

        return list;
    }

    public AlbumInfo albumInfo(Integer albumId, Boolean countMedium) {
        Assert.isTrue(albumId != null, "相册ID不能为空");
        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 最近上传
        if (Objects.equals(albumId, Constants.RECENTLY_ALBUM_ID)) {
            AlbumInfo resp = new AlbumInfo();
            resp.setId(Constants.RECENTLY_ALBUM_ID);
            resp.setName(Constants.RECENTLY_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, false)
                ));
            }
            resp.setSystem(true);
            return resp;
        }

        // 我的收藏
        if (Objects.equals(albumId, Constants.FAVORITE_ALBUM_ID)) {
            AlbumInfo resp = new AlbumInfo();
            resp.setId(Constants.FAVORITE_ALBUM_ID);
            resp.setName(Constants.FAVORITE_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, false)
                    .eq(Medium::getFavorite, true)
                ));
            }
            resp.setSystem(true);
            return resp;
        }

        // 最近删除
        if (Objects.equals(albumId, Constants.DELETED_ALBUM_ID)) {
            AlbumInfo resp = new AlbumInfo();
            resp.setId(Constants.DELETED_ALBUM_ID);
            resp.setName(Constants.DELETED_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, true)
                ));
            }
            resp.setSystem(true);
            return resp;
        }

        // 自定义相册
        Album album = getById(albumId);
        Assert.notNull(album, "相册不存在");

        AlbumInfo resp = new AlbumInfo();
        resp.setId(album.getId());
        resp.setName(album.getName());

        Medium medium = mediumMapper.selectById(album.getCoverMediumId());
        if (medium == null) {
            resp.setCoverPath("");
        } else {
            resp.setCoverPath(medium.getThumbnailPath());
        }

        if (countMedium) {
            Long count = albumMediumMappingMapper.selectCount(new LambdaQueryWrapper<AlbumMediumMapping>()
                .eq(AlbumMediumMapping::getAlbumId, album.getId())
            );
            resp.setMediaCount(count);
        }

        return resp;
    }

    public void setCover(Integer albumId, Integer mediumId) {
        Assert.notNull(albumId, "相册ID不能为空");
        Assert.notNull(mediumId, "媒体ID不能为空");

        Album album = getById(albumId);
        Assert.notNull(album, "相册不存在");

        Medium medium = mediumMapper.selectById(mediumId);
        Assert.notNull(medium, "媒体文件不存在");
        Assert.isTrue(medium.getDeleted().equals(false), "媒体已删除");

        update(new LambdaUpdateWrapper<Album>()
            .eq(Album::getId, albumId)
            .set(Album::getCoverMediumId, mediumId)
        );
    }

    /**
     * 添加照片到相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMediumToAlbums(List<Integer> albumIds, List<Integer> mediumIds) {
        Assert.notEmpty(albumIds, "请选择相册");
        Assert.notEmpty(mediumIds, "请选择照片");

        for (Integer albumId : albumIds) {
            Album album = getById(albumId);
            Assert.notNull(album, "相册不存在");

            // 查询相册已关联的媒体文件
            List<Integer> existMediumIds = albumMediumMappingMapper.selectList(
                new LambdaQueryWrapper<AlbumMediumMapping>()
                    .eq(AlbumMediumMapping::getAlbumId, albumId)
            ).stream().map(AlbumMediumMapping::getMediumId).toList();

            // 去掉已经关联的
            mediumIds = mediumIds.stream()
                .filter(mediumId -> !existMediumIds.contains(mediumId)).toList();
            for (Integer mediumId : mediumIds) {
                AlbumMediumMapping mapping = new AlbumMediumMapping();
                mapping.setAlbumId(albumId);
                mapping.setMediumId(mediumId);
                albumMediumMappingMapper.insert(mapping);
            }
            photoService.refreshAlbumCover(album);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeMediumFromAlbum(List<Integer> albumIds, List<Integer> mediumIds) {
        Assert.notEmpty(mediumIds, "请选择照片");
        Assert.notEmpty(albumIds, "请选择相册");

        User currentUser = ThreadLocalUtil.getCurrentUser();
        for (Integer albumId : albumIds) {
            Album album = getById(albumId);
            Assert.notNull(album, "相册不存在");
            Assert.isTrue(album.getUserId().equals(currentUser.getId()), "无权限操作");

            albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
                .eq(AlbumMediumMapping::getAlbumId, albumId)
                .in(AlbumMediumMapping::getMediumId, mediumIds)
            );
            photoService.refreshAlbumCover(album);
        }
    }

    public void create(CreateAlbumRequest request) {
        Assert.hasLength(request.getName(), "相册名称不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Album> albums = list(new LambdaQueryWrapper<Album>()
            .eq(Album::getUserId, currentUser.getId())
        );

        Assert.isTrue(
            albums.stream().noneMatch(album -> Objects.equals(album.getName(), request.getName())),
            "相册名称已存在"
        );

        Album album = new Album();
        album.setUserId(currentUser.getId());
        album.setName(request.getName());
        save(album);
    }

    public void edit(EditAlbumRequest request) {
        Integer id = request.getId();
        Assert.notNull(id, "请选择相册");
        String name = request.getName();
        Assert.hasLength(name, "相册名称不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();
        List<Album> albums = list(new LambdaQueryWrapper<Album>()
            .eq(Album::getUserId, currentUser.getId())
        );
        Assert.isTrue(albums.stream().anyMatch(album -> album.getId().equals(id)), "相册不存在");

        Assert.isTrue(
            albums.stream()
                .filter(album -> !album.getId().equals(id))
                .noneMatch(album -> Objects.equals(album.getName(), name)),
            "相册名称已存在"
        );

        Album album = new Album();
        album.setId(id);
        album.setName(name);
        updateById(album);
    }

    public void delete(Integer id) {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        Album album = getById(id);

        Assert.notNull(album, "相册不存在");
        Assert.isTrue(album.getUserId().equals(currentUser.getId()), "您没有权限删除此相册");

        removeById(id);
        albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
            .eq(AlbumMediumMapping::getAlbumId, id)
        );
    }
}

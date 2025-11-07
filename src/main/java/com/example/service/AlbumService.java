package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bean.Constants;
import com.example.bean.entity.Album;
import com.example.bean.entity.AlbumMediumMapping;
import com.example.bean.entity.Medium;
import com.example.bean.entity.User;
import com.example.bean.response.AlbumResp;
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

    public List<AlbumResp> listAlbums(Boolean countMedium, boolean excludeSystem) {
        User currentUser = ThreadLocalUtil.getCurrentUser();

        List<Album> albums = list(new LambdaQueryWrapper<Album>()
            .eq(Album::getUserId, currentUser.getId())
        );

        // 固定追加两个系统相册：最近上传、我的收藏

        List<AlbumResp> list = new ArrayList<>();
        if (!excludeSystem) {
            // 最近上传
            AlbumResp resp = new AlbumResp();
            resp.setId(Constants.RECENTLY_ALBUM_ID);
            resp.setName(Constants.RECENTLY_ALBUM_NAME);
            if (countMedium) {
                resp.setMediaCount(mediumMapper.selectCount(new LambdaQueryWrapper<Medium>()
                    .eq(Medium::getUserId, currentUser.getId())
                    .eq(Medium::getDeleted, false)
                ));
            }

            resp.setSystem(true);
            list.add(resp);

            // 我的收藏
            resp = new AlbumResp();
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
            list.add(resp);
        }

        for (Album album : albums) {
            AlbumResp resp = new AlbumResp();
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

    public AlbumResp albumInfo(Integer albumId, Boolean countMedium) {
        Assert.isTrue(albumId != null, "相册ID不能为空");
        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 最近上传
        if (Objects.equals(albumId, Constants.RECENTLY_ALBUM_ID)) {
            AlbumResp resp = new AlbumResp();
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
            AlbumResp resp = new AlbumResp();
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
            AlbumResp resp = new AlbumResp();
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
        Assert.isTrue(album != null && album.getUserId().equals(currentUser.getId()), "相册不存在");

        AlbumResp resp = new AlbumResp();
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

        User currentUser = ThreadLocalUtil.getCurrentUser();
        Album album = getById(albumId);
        Assert.isTrue(album != null && album.getUserId().equals(currentUser.getId()), "相册不存在");

        Medium medium = mediumMapper.selectById(mediumId);
        Assert.isTrue(medium != null && medium.getUserId().equals(currentUser.getId()), "媒体不存在");

        update(new LambdaUpdateWrapper<Album>()
            .eq(Album::getId, albumId)
            .set(Album::getCoverMediumId, mediumId)
        );
    }

    /**
     * 添加照片到相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMediumToAlbums(List<Integer> albumIds, List<Integer> mediumIds, Boolean removeDeletedFlag,
        Boolean overrideAlbums) {
        Assert.notEmpty(albumIds, "请选择相册");
        Assert.notEmpty(mediumIds, "请选择照片");

        for (Integer albumId : albumIds) {
            Album album = getById(albumId);
            if (album == null) {
                continue;
            }

            // 查询相册已关联的照片
            List<Integer> existMediumIds = albumMediumMappingMapper.selectList(
                new LambdaQueryWrapper<AlbumMediumMapping>()
                    .eq(AlbumMediumMapping::getAlbumId, albumId)
            ).stream().map(AlbumMediumMapping::getMediumId).toList();

            // 去重
            List<Integer> distinctMediumIds = mediumIds.stream()
                .filter(mediumId -> !existMediumIds.contains(mediumId)).toList();
            if (distinctMediumIds.isEmpty()) {
                continue;
            }

            // 批量保存
            for (Integer mediumId : distinctMediumIds) {
                AlbumMediumMapping mapping = new AlbumMediumMapping();
                mapping.setAlbumId(albumId);
                mapping.setMediumId(mediumId);
                albumMediumMappingMapper.insert(mapping);
            }
        }

        // 移除删除标记
        if (removeDeletedFlag) {
            mediumMapper.update(new LambdaUpdateWrapper<Medium>()
                .in(Medium::getId, mediumIds)
                .set(Medium::getDeleted, false)
            );
        }

        // 查询媒体已关联的相册，取消不在 albumIds 中的关联
        if (overrideAlbums) {
            List<Integer> oldAlbumIds = albumMediumMappingMapper.selectList(new LambdaQueryWrapper<AlbumMediumMapping>()
                .in(AlbumMediumMapping::getMediumId, mediumIds)
            ).stream().map(AlbumMediumMapping::getAlbumId).toList();

            // 找出取消关联的相册
            List<Integer> cancelAlbumIds = oldAlbumIds.stream()
                .filter(albumId -> !albumIds.contains(albumId)).toList();

            // 删除关联关系
            albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
                .in(AlbumMediumMapping::getAlbumId, cancelAlbumIds)
                .in(AlbumMediumMapping::getMediumId, mediumIds)
            );
        }

    }

    /**
     * 移除照片
     */
    public void removeMediumFromAlbum(List<Integer> albumIds, List<Integer> mediumIds) {
        Assert.notEmpty(mediumIds, "请选择照片");
        Assert.notEmpty(albumIds, "请选择相册");

        Integer albumId = albumIds.get(0);
        Assert.isTrue(getById(albumId) != null, "相册不存在");

        albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
            .eq(AlbumMediumMapping::getAlbumId, albumId)
            .in(AlbumMediumMapping::getMediumId, mediumIds)
        );
    }

    /**
     * 编辑或创建相册
     */
    public void edit(Integer id, String name) {
        Assert.hasLength(name, "相册名称不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();

        boolean exist = id != null && getById(id) != null;
        if (exist) {
            // 相册名不能重复
            Assert.isTrue(
                list().stream().noneMatch(album -> !album.getId().equals(id) && album.getName().equals(name)),
                "相册名称已存在"
            );

            Album album = new Album();
            album.setId(id);
            album.setName(name);
            updateById(album);
            log.info("编辑相册成功: {}", name);

        } else {
            Assert.isTrue(
                list().stream().noneMatch(album -> album.getName().equals(name)),
                "相册名称已存在"
            );

            Album album = new Album();
            album.setUserId(currentUser.getId());
            album.setName(name);
            save(album);
            log.info("创建相册成功: {}", name);
        }
    }

    /**
     * 删除相册
     */
    public void deleteAlbum(Integer id) {
        boolean exist = id != null && getById(id) != null;
        Assert.isTrue(exist, "相册不存在");

        removeById(id);
        albumMediumMappingMapper.delete(new LambdaQueryWrapper<AlbumMediumMapping>()
            .eq(AlbumMediumMapping::getAlbumId, id)
        );
    }
}

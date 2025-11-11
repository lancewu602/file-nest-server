package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bean.entity.Album;
import com.example.bean.entity.AlbumMediumMapping;
import com.example.bean.entity.Medium;
import com.example.mapper.AlbumMapper;
import com.example.mapper.AlbumMediumMappingMapper;
import com.example.mapper.MediumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WuQinglong
 * @date 2025/11/9 13:50
 */
@Service
public class PhotoService {
    @Autowired
    private MediumMapper mediumMapper;
    @Autowired
    private AlbumMediumMappingMapper albumMediumMappingMapper;
    @Autowired
    private AlbumMapper albumMapper;

    public void refreshAlbumCover(Album album) {
        AlbumMediumMapping mapping = albumMediumMappingMapper.selectOne(
            new LambdaQueryWrapper<AlbumMediumMapping>()
                .eq(AlbumMediumMapping::getAlbumId, album.getId())
                .orderByDesc(AlbumMediumMapping::getCreatedAt)
                .last("limit 1")
        );
        Medium medium = mediumMapper.selectById(mapping.getMediumId());

        Album updateAlbum = new Album();
        updateAlbum.setId(album.getId());
        updateAlbum.setCoverMediumId(medium.getId());
        albumMapper.updateById(updateAlbum);
    }

}

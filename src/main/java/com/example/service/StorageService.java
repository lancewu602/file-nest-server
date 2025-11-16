package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bean.entity.Storage;
import com.example.bean.entity.User;
import com.example.bean.request.MountRequest;
import com.example.bean.response.StorageResp;
import com.example.mapper.StorageMapper;
import com.example.util.ThreadLocalUtil;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author WuQinglong
 * @date 2025/9/15 15:21
 */
@Service
public class StorageService {

    @Autowired
    private StorageMapper storageMapper;

    /**
     * 挂载存储
     */
    public void mount(MountRequest request) throws IOException {
        Assert.hasLength(request.getName(), "存储名称不能为空");
        Assert.hasLength(request.getMountPath(), "挂载路径不能为空");

        User currentUser = ThreadLocalUtil.getCurrentUser();

        // 物理路径必须存在
        Path mountPath = Paths.get(request.getMountPath());
        Assert.isTrue(Files.exists(mountPath), "挂载路径不存在");

        // 映射路径不能重复
        List<Storage> storages = storageMapper.selectList(new LambdaQueryWrapper<Storage>()
            .eq(Storage::getUserId, currentUser.getId())
        );
        boolean mountNameExist = storages.stream().anyMatch(storage -> {
            if (request.getId() == null) {
                return Objects.equals(storage.getName(), request.getName());
            } else {
                return !Objects.equals(request.getId(), storage.getId())
                    && Objects.equals(storage.getName(), request.getName());
            }
        });
        Assert.isTrue(!mountNameExist, "挂载名称已存在");

        // 创建一个固定的目录，作为回收站
        Assert.hasLength(request.getTrashName(), "回收站名称不能为空");
        Assert.isTrue(!Strings.CS.contains(request.getTrashName(), File.separator), "回收站名称不能包含子目录");
        Path trashPath = mountPath.resolve(request.getTrashName());
        if (Files.exists(trashPath)) {
            // 已存在时，校验是不是一个目录
            Assert.isTrue(Files.isDirectory(trashPath), "回收站已存在，并且不是一个目录");
        } else {
            // 不存在时，创建目录
            Files.createDirectories(trashPath);
        }

        // 持久化存储信息
        Storage storage = new Storage();
        storage.setId(request.getId());
        storage.setUserId(currentUser.getId());
        storage.setName(request.getName());
        storage.setMountPath(mountPath.toString());
        storage.setTrashName(request.getTrashName());
        storageMapper.insertOrUpdate(storage);
    }

    /**
     * 卸载存储
     */
    public void unmount(MountRequest request) {
        if (request.getId() == null) {
            return;
        }

        User currentUser = ThreadLocalUtil.getCurrentUser();
        Storage storage = storageMapper.selectOne(new LambdaQueryWrapper<Storage>()
            .eq(Storage::getUserId, currentUser.getId())
            .eq(Storage::getId, request.getId())
        );
        if (storage == null) {
            return;
        }

        storageMapper.deleteById(storage);
    }

    /**
     * 列出所有的存储
     */
    public List<StorageResp> listStorages() {
        User currentUser = ThreadLocalUtil.getCurrentUser();
        // 列出所有的存储
        List<Storage> storages = storageMapper.selectList(new LambdaQueryWrapper<Storage>()
            .eq(Storage::getUserId, currentUser.getId())
        );

        // 转一下，返给前端
        List<StorageResp> list = new ArrayList<>();
        for (Storage storage : storages) {
            StorageResp response = new StorageResp();
            response.setId(storage.getId());
            response.setName(storage.getName());
            response.setMountPath(storage.getMountPath());
            response.setTrashName(storage.getTrashName());

            Path trashPath = Paths.get(storage.getMountPath());
            response.setExists(Files.exists(trashPath));

            list.add(response);
        }

        return list;
    }

}

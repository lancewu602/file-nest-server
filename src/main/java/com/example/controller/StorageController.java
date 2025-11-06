package com.example.controller;

import com.example.bean.Ret;
import com.example.bean.request.MountRequest;
import com.example.bean.response.StorageResp;
import com.example.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author WuQinglong
 * @date 2025/9/15 15:20
 */
@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/api/storage/mount")
    public Ret<?> mount(@RequestBody MountRequest request) throws IOException {
        storageService.mount(request);
        return Ret.success();
    }

    @PostMapping("/api/storage/unmount")
    public Ret<?> unmount(@RequestBody MountRequest request) {
        storageService.unmount(request);
        return Ret.success();
    }

    @GetMapping("/api/storage/list")
    public Ret<?> listStorages() {
        List<StorageResp> vos = storageService.listStorages();
        return Ret.success(vos);
    }

}

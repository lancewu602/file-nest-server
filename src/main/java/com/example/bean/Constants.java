package com.example.bean;

/**
 * @author WuQinglong
 * @date 2025/9/15 15:22
 */
public interface Constants {

    String TRASH_NAME = ".FileNestTrash";

    int CHUNK_BUFFER_SIZE = 16 * 1024 * 1024;

    int THUMBNAIL_WIDTH = 200;
    int THUMBNAIL_HEIGHT = 200;

    int RECENTLY_ALBUM_ID = -10;
    String RECENTLY_ALBUM_NAME = "最近上传";
    int FAVORITE_ALBUM_ID = -20;
    String FAVORITE_ALBUM_NAME = "我的收藏";
    int DELETED_ALBUM_ID = -30;
    String DELETED_ALBUM_NAME = "最近删除";

}

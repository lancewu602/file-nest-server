create table medium
(
    id             INT          NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id        INT          NOT NULL COMMENT '所属用户',
    type           VARCHAR(100) NOT NULL COMMENT '文件类型',
    name           VARCHAR(200) NOT NULL COMMENT '文件名',
    size           BIGINT       NOT NULL COMMENT '文件大小',
    width          INT          NOT NULL DEFAULT 0 COMMENT '宽度',
    height         INT          NOT NULL DEFAULT 0 COMMENT '高度',
    duration       INT          NOT NULL DEFAULT 0 COMMENT '时长，毫秒',
    date_token     DATETIME     NOT NULL COMMENT '拍摄时间',
    last_modified  DATETIME     NOT NULL COMMENT '修改时间',
    original_path  VARCHAR(500) NOT NULL DEFAULT '' COMMENT '原图路径',
    thumbnail_path VARCHAR(500) NOT NULL DEFAULT '' COMMENT '缩略图路径',
    exif           TEXT         NULL COMMENT 'EXIF信息',
    phash          VARCHAR(100) NOT NULL DEFAULT '',
    dhash          VARCHAR(100) NOT NULL DEFAULT '',
    deleted        TINYINT      NOT NULL DEFAULT 0 COMMENT '是否被删除',
    favorite       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否收藏',
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id_deleted` (`user_id`, `deleted`)
) engine = InnoDB
  charset = utf8mb4 COMMENT ='媒体表';

ALTER TABLE medium
    AUTO_INCREMENT = 1000000;

create table album
(
    id              INT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id         INT         NOT NULL COMMENT '所属用户',
    name            VARCHAR(20) NOT NULL DEFAULT '' COMMENT '相册名',
    cover_medium_id INT         NOT NULL DEFAULT 0 COMMENT '封面id',
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP()
) engine = InnoDB
  charset = utf8mb4 COMMENT ='相册表';

ALTER TABLE album
    AUTO_INCREMENT = 1000;

create table album_medium_mapping
(
    id         INT      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    album_id   INT      NOT NULL DEFAULT 0 COMMENT '相册id',
    medium_id  INT      NOT NULL DEFAULT 0 COMMENT '媒体id',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() NOT NULL COMMENT '关联时间',
    INDEX `idx_album_id` (`album_id`),
    INDEX `idx_medium_id` (`medium_id`)
) engine = InnoDB
  charset = utf8mb4 COMMENT ='关联表';
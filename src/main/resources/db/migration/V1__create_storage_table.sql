create table storage
(
    id         INT                          NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name       VARCHAR(100) DEFAULT ''      NOT NULL COMMENT '名称',
    type       VARCHAR(100) DEFAULT 'Local' NOT NULL COMMENT '存储类型',
    mount_path VARCHAR(100) DEFAULT ''      NOT NULL COMMENT '物理路径',
    trash_name VARCHAR(100) DEFAULT ''      NOT NULL COMMENT '回收站名称'
) engine = InnoDB
  charset = utf8mb4 COMMENT ='存储表';
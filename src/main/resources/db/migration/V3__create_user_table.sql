create table user
(
    id           INT          NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name         VARCHAR(100) NOT NULL COMMENT '用户名',
    password     VARCHAR(100) NOT NULL COMMENT '密码',
    is_admin     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否是管理员',
    enable_login TINYINT      NOT NULL DEFAULT 0 COMMENT '是否允许登录'
) engine = InnoDB
  charset = utf8mb4 COMMENT ='用户表';

-- 初始化用户，用户名为admin，密码为admin
INSERT INTO user(name, password, is_admin, enable_login)
VALUES ('admin',
        '$argon2id$v=19$m=16384,t=2,p=1$UG9jgPnyeyvfSDr6npj8Cg$FH5cOQ2UXdLUtfCwNHEUYfdBRY2vA7fMfKyENO6OLkc',
        1,
        1);
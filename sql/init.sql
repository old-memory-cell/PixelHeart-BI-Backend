-- ============================================================
-- PixelHeart-BI 数据库初始化脚本
-- 适用于 MySQL 8.0+
-- ============================================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `pixel-heart-bi`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `pixel-heart-bi`;

-- 2. 创建用户表
CREATE TABLE IF NOT EXISTS `user`
(
    `id`            BIGINT AUTO_INCREMENT COMMENT 'id',
    `user_role`     VARCHAR(256)  DEFAULT 'user'            NOT NULL COMMENT '用户角色：user/admin',
    `user_name`     VARCHAR(256)                            NULL COMMENT '用户昵称',
    `user_account`  VARCHAR(256)                            NOT NULL COMMENT '账号',
    `user_password` VARCHAR(512)                            NOT NULL COMMENT '密码',
    `user_avatar`   VARCHAR(1024)                           NULL COMMENT '用户头像',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     TINYINT       DEFAULT 0                 NOT NULL COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_account` (`user_account`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '用户';

-- 3. 创建图表信息表
CREATE TABLE IF NOT EXISTS `chart`
(
    `id`          BIGINT AUTO_INCREMENT COMMENT 'id',
    `chart_type`  VARCHAR(128)                       NULL COMMENT '图表类型',
    `chart_name`  VARCHAR(128)                       NULL COMMENT '图标名称',
    `goal`        TEXT                               NULL COMMENT '分析目标',
    `chart_data`  TEXT                               NULL COMMENT '图表数据',
    `gen_chart`   TEXT                               NULL COMMENT '生成的图表数据',
    `gen_result`  TEXT                               NULL COMMENT '生成的分析结论',
    `status`      TINYINT  DEFAULT 0                 NOT NULL COMMENT '生成状态: 0-等待中,1-生成中,2-成功,3-失败',
    `error_msg`   VARCHAR(512)                       NULL COMMENT '错误信息',
    `user_id`     BIGINT                             NULL COMMENT '创建用户 id',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '图表信息表';
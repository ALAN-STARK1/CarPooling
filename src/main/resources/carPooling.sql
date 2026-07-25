CREATE DATABASE IF NOT EXISTS `org.example.carpooling`
    DEFAULT CHARACTER SET utf8mb4;
USE `org.example.carpooling`;
DROP TABLE IF EXISTS user;


CREATE TABLE `user` (
                        `id`            BIGINT          PRIMARY KEY COMMENT '主键ID',
                        `username`      VARCHAR(32)     NOT NULL UNIQUE COMMENT '用户名',
                        sex             VARCHAR(32)     COMMENT '性别',
                        `password`      VARCHAR(128)    NOT NULL COMMENT '加密密码',
                        `email`         VARCHAR(64)     UNIQUE COMMENT '邮箱',
                        `phoneNumber`         VARCHAR(20)     UNIQUE COMMENT '手机号',
                        address         VARCHAR(64)     COMMENT '地址',
                        `avatar`        VARCHAR(255)    COMMENT '头像URL',
                        `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                        `role`          ENUM('user','driver','admin') NOT NULL DEFAULT 'user' COMMENT '角色',

                        INDEX `idx_phone` (`phoneNumber`),
                        INDEX `idx_status` (`status`)
) COMMENT='用户表';




-- 行程表
CREATE TABLE `trip` (
                        `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `driver_id` BIGINT NOT NULL COMMENT '司机ID',
                        `start_lng` DECIMAL(10,7) NOT NULL COMMENT '起点经度',
                        `start_lat` DECIMAL(10,7) NOT NULL COMMENT '起点纬度',
                        `start_address` VARCHAR(255) COMMENT '起点地址',
                        `end_lng` DECIMAL(10,7) NOT NULL COMMENT '终点经度',
                        `end_lat` DECIMAL(10,7) NOT NULL COMMENT '终点纬度',
                        `end_address` VARCHAR(255) COMMENT '终点地址',
                        `route_path` JSON COMMENT '路线路径点',
                        `distance` INT COMMENT '总距离(米)',
                        `duration` INT COMMENT '预计时间(秒)',
                        `depart_time` DATETIME NOT NULL COMMENT '出发时间',
                        `seats_total` TINYINT NOT NULL DEFAULT 4 COMMENT '总座位',
                        `seats_left` TINYINT NOT NULL DEFAULT 4 COMMENT '剩余座位',
                        `price` DECIMAL(10,2) COMMENT '每人价格',
                        `status` TINYINT DEFAULT 0 COMMENT '0-发布中 1-进行中 2-已完成 3-已取消',
                        `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        INDEX `idx_depart_time` (`depart_time`),
                        INDEX `idx_status` (`status`)
) COMMENT='行程表';

-- 预订表
CREATE TABLE `booking` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                           `trip_id` BIGINT NOT NULL,
                           `passenger_id` BIGINT NOT NULL,
                           `pickup_lng` DECIMAL(10,7) COMMENT '上车点经度',
                           `pickup_lat` DECIMAL(10,7) COMMENT '上车点纬度',
                           `dropoff_lng` DECIMAL(10,7) COMMENT '下车点经度',
                           `dropoff_lat` DECIMAL(10,7) COMMENT '下车点纬度',
                           `status` TINYINT DEFAULT 0 COMMENT '0-待确认 1-已确认 2-已取消',
                           `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='预订表';

-- 实时位置表（Redis 存储更合适，这里做备份）
CREATE TABLE `location_log` (
                                `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                `trip_id` BIGINT NOT NULL,
                                `user_id` BIGINT NOT NULL,
                                `lng` DECIMAL(10,7) NOT NULL,
                                `lat` DECIMAL(10,7) NOT NULL,
                                `recorded_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                INDEX `idx_trip_time` (`trip_id`, `recorded_at`)
) COMMENT='位置记录表';

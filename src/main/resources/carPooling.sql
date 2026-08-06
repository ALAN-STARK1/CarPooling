CREATE DATABASE IF NOT EXISTS `org.example.carpooling`
    DEFAULT CHARACTER SET utf8mb4;
USE `org.example.carpooling`;
DROP TABLE IF EXISTS user;


CREATE TABLE `user`(
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


CREATE TABLE 'order' (

            'id'    BIGINT  PRIMARY KEY COMMENT '主键ID',
            'order_id'  BIGINT COMMENT '订单号',
            'passenger_id' BIGINT COMMENT '乘客ID',
            'driver_id' BIGINT COMMENT '司机ID',
            'create_time' DATETIME COMMENT '创建时间',
            'end_time'  DATETIME COMMENT '结束时间',
            'passenger_cost' DOUBLE COMMENT '乘客花费',
            'driver_income' DOUBLE COMMENT '司机收入'

) COMMENT = '订单表';

CREATE TABLE 'position' (
        'id' BIGINT PRIMARY KEY  COMMENT '主键ID',
        'city' VARCHAR(20) COMMENT '城市',
        'name' VARCHAR(20) COMMENT '名字',
        'lng'   DOUBLE COMMENT '经度',
        'lat'   DOUBLE COMMENT '纬度'
) COMMENT = '位置表';
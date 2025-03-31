CREATE TABLE `group_info` (
    `group_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NOT NULL  COMMENT '群ID',
    `group_name` varchar(20) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群组名',
    `group_owner_id` varchar(12)  CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群主ID',
    `crate_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `group_notice` varchar(500) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群公告',
    `join_type` tinyint(1) NULL DEFAULT NULL COMMENT '0:直接加入 1:管理员同意',
    `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1:正常 0:解散',
    PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

CREATE TABLE `user_contact` (
    `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NOT NULL  COMMENT '用户ID',
    `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NOT NULL  COMMENT '联系人ID或群组ID',
    `contact_type` tinyint(1)  NULL DEFAULT NULL  COMMENT '联系人类型 0:好友 1:群组',
    `creat_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0:非好友 1:好友 2:已删除好友 3:被好友删除 4:已拉黑好友 5:被好友拉黑',
    `last_update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`user_id`,`contact_id`) USING BTREE,
    INDEX  `idx_contact_id` (`contact_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系人' ROW_FORMAT = DYNAMIC;

CREATE TABLE `user_contact_apply` (
    `apply_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `apply_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NOT NULL COMMENT '申请人ID',
    `receive_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NOT NULL COMMENT '接收人ID',
    `contact_type` tinyint(1) NOT NULL COMMENT '联系人类型 0:好友 1:群组',
    `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人群组ID',
    `last_apply_time` bigint(20) NULL DEFAULT NULL COMMENT '最后申请时间',
    `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0:待处理 1:已同意 2:已拒绝 3:已拉黑',
    `apply_info` varchar(100) CHARACTER SET utf8mb4 COLLATE  utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请信息',
    PRIMARY KEY (`apply_id`) USING BTREE,
    UNIQUE INDEX  `idx_key` (`apply_user_id`,`receive_user_id`,`contact_id`) USING BTREE,
    INDEX  `idx_last_apply_time` (`last_apply_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系人申请' ROW_FORMAT = DYNAMIC;


CREATE TABLE `user_contact` (
) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系人' ROW_FORMAT = DYNAMIC;

















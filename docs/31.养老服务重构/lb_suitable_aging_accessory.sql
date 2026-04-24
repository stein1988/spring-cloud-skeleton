-- Database: lb_suitable_aging_accessory
-- Tables only, no data
-- Exported at: 2026-04-21 19:26:10


-- Table: lb_account
CREATE TABLE `lb_account` (
  `account_id` varchar(32) NOT NULL COMMENT '主键',
  `group_id` varchar(32) NOT NULL DEFAULT '' COMMENT '团体(企业|家庭)主键',
  `account_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账号类型（1-平台，2-小程序）',
  `username` varchar(32) NOT NULL DEFAULT '' COMMENT '登录用户名',
  `password` varchar(32) NOT NULL DEFAULT '' COMMENT '登录密码',
  `display_name` varchar(32) NOT NULL DEFAULT '' COMMENT '昵称',
  `phone_num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别（1-男，2-女）',
  `birthday` bigint(20) DEFAULT NULL COMMENT '生日',
  `head_image` varchar(255) NOT NULL DEFAULT '' COMMENT '用户头像',
  `region_id` varchar(32) NOT NULL DEFAULT '' COMMENT '行政区域标识',
  `region_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区域描述',
  `is_admin` tinyint(1) NOT NULL DEFAULT '2' COMMENT '管理员标识（1-管理员，2-非管理员）',
  `role_id` varchar(32) NOT NULL DEFAULT '' COMMENT '角色主键',
  `token` text COMMENT '令牌',
  `live_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '令牌有效期(0:永久)',
  `password_change_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '密码修改时间',
  `open_id` varchar(32) NOT NULL DEFAULT '' COMMENT '小程序下用户唯一标识',
  `union_id` varchar(32) NOT NULL DEFAULT '' COMMENT '微信开放平台下用户唯一标识',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`account_id`) USING BTREE,
  KEY `enterprise_id` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='账号信息表';


-- Table: lb_account_device_enterprise
CREATE TABLE `lb_account_device_enterprise` (
  `relation_id` varchar(32) NOT NULL COMMENT '主键',
  `account_id` varchar(32) NOT NULL DEFAULT '' COMMENT '账号id',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业id',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除 1:删除',
  PRIMARY KEY (`relation_id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='账号可添加设备企业表';


-- Table: lb_activity_record
CREATE TABLE `lb_activity_record` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `activity_type` tinyint(4) NOT NULL COMMENT '活动类型',
  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
  `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
  `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '持续时间',
  `show_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '显示类型（1-开始结束都正常，2-开始正常，结束异常，3-开始异常，结束正常）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='活动记录表';


-- Table: lb_alarm_mode
CREATE TABLE `lb_alarm_mode` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `object_id` varchar(64) NOT NULL DEFAULT '' COMMENT '设置对象标识',
  `alarm_type` tinyint(4) NOT NULL COMMENT '报警类型',
  `alarm_mode` tinyint(4) NOT NULL DEFAULT '1' COMMENT '报警模式（1-提醒，2-报警）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `object_id` (`object_id`,`alarm_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='报警模式设置表';


-- Table: lb_alarm_record
CREATE TABLE `lb_alarm_record` (
  `alarm_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `track_id` varchar(32) NOT NULL DEFAULT '' COMMENT '轨迹主键',
  `last_track_id` varchar(32) NOT NULL DEFAULT '' COMMENT '上一次轨迹主键',
  `alarm_type` tinyint(1) NOT NULL COMMENT '报警类型',
  `alarm_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '报警描述',
  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
  `alarm_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '报警时间',
  `dispose_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '处警开始时间',
  `dispose_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '处警结束时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '报警状态（1-待处理，3-开始处理，4-处理完成）',
  `is_automatic` tinyint(1) NOT NULL DEFAULT '0' COMMENT '自动报警标识（0-主动，1-自动）',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `device_type` tinyint(1) NOT NULL COMMENT '设备类型',
  `device_type_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '设备类型描述',
  `device_iteration_number` int(11) NOT NULL DEFAULT '0' COMMENT '设备迭代版本号',
  `receive_alarm_time` int(11) NOT NULL DEFAULT '180' COMMENT '接警时间',
  `alarm_set` text COMMENT '处警设置',
  `is_automatic_dispose` tinyint(1) NOT NULL DEFAULT '0' COMMENT '自动处警标识（0-主动处理，1-自动处理，2-不参与处理）',
  `not_dispose_account` text COMMENT '不处理报警账号',
  `is_sign` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标记标识（0-否，1-是）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  `binding_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '绑定描述',
  `commodity_id` varchar(32) NOT NULL DEFAULT '' COMMENT '商品主键',
  `commodity_name` varchar(32) NOT NULL DEFAULT '' COMMENT '商品名称',
  PRIMARY KEY (`alarm_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `track_id` (`track_id`) USING BTREE,
  KEY `last_track_id` (`last_track_id`) USING BTREE,
  KEY `alarm_type` (`alarm_type`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE,
  KEY `commodity_id` (`commodity_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='报警记录表';


-- Table: lb_alarm_set
CREATE TABLE `lb_alarm_set` (
  `alarm_set_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭id',
  `alarm_mode` tinyint(1) NOT NULL COMMENT '处警模式',
  `report_device_type` int(3) NOT NULL COMMENT '报警设备上报类型',
  `device_iteration_number` int(3) NOT NULL DEFAULT '0' COMMENT '设备迭代版本号',
  `alarm_mode_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '处警模式类型',
  `set_time` int(11) NOT NULL DEFAULT '1' COMMENT '处警修改持续时间 0为永久',
  `alarm_set_name` varchar(32) NOT NULL DEFAULT '' COMMENT '处警设置人姓名（处警设置人类型为客服时，为要求人姓名）',
  `alarm_set_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '处警设置人类型',
  `account_id` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人Id',
  `time_type` int(3) NOT NULL DEFAULT '1' COMMENT '时间段设置: 1 全天 2 白天 3 晚上',
  `start_time` varchar(32) NOT NULL COMMENT '处警设置开始时间',
  `end_time` varchar(32) NOT NULL COMMENT '处警设置开始时间',
  `relation_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '关系描述',
  `csr_id` varchar(64) NOT NULL DEFAULT '' COMMENT '客服Id（处警设置人类型为客服时存在）',
  `is_use` tinyint(1) NOT NULL DEFAULT '0' COMMENT '设置是否过期  0已过期 1未过期',
  `recovery_time` bigint(20) NOT NULL DEFAULT '1' COMMENT '处警修改恢复时间,持续时间为永久时该字段为处警设置时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_open` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否开启 0 关闭 1开启',
  `device_type` int(3) NOT NULL DEFAULT '0' COMMENT '设备类型',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除0:未删除 1:删除',
  PRIMARY KEY (`alarm_set_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `alarm_mode_type` (`alarm_mode_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='处警设置表';


-- Table: lb_binding_device
CREATE TABLE `lb_binding_device` (
  `device_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL COMMENT '企业主键',
  `family_id` varchar(64) NOT NULL COMMENT '绑定家庭主键',
  `commodity_id` varchar(32) NOT NULL COMMENT '商品主键',
  `device_type` tinyint(4) NOT NULL COMMENT '设备类型',
  `device_iteration_number` tinyint(4) NOT NULL DEFAULT '0' COMMENT '设备迭代版本号',
  `serial_num` tinyint(3) DEFAULT '0' COMMENT '序号',
  `imei` varchar(32) NOT NULL DEFAULT '' COMMENT '设备编号',
  `mac` varchar(32) DEFAULT '' COMMENT 'mac地址',
  `binding_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '绑定描述',
  `version` smallint(1) unsigned DEFAULT '1' COMMENT '下发信息版本号',
  `nb_version` varchar(32) DEFAULT '' COMMENT '版本号',
  `software_version` varchar(32) DEFAULT '' COMMENT '设备软件版本号',
  `hardware_version` varchar(32) DEFAULT '' COMMENT '硬件版本号',
  `online_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '在线时间',
  `status` int(3) NOT NULL DEFAULT '0' COMMENT '1:在线 0:离线',
  `shield_time` bigint(20) DEFAULT '0' COMMENT '离线不提醒时间',
  `dump_energy` int(11) DEFAULT '0' COMMENT '电量',
  `power_status` tinyint(1) DEFAULT '0' COMMENT '电量状态(0:正常，1:低电量,2充电中)',
  `energy_check_time` bigint(20) DEFAULT '0' COMMENT '电量检查时间',
  `electricity_mode` tinyint(1) DEFAULT '2' COMMENT '应用的电量模式,0:eDRX,1:DRX,2:PSM',
  `electricity_mode_code` varchar(32) DEFAULT '' COMMENT '手表电量模式信息',
  `power_shield_time` bigint(20) DEFAULT '0' COMMENT '低电量不提醒时间',
  `wear_status` tinyint(1) DEFAULT NULL COMMENT '佩戴状态（0 未佩戴， 1 佩戴中）',
  `motion_status` tinyint(1) DEFAULT NULL COMMENT '运动状态（0 静止， 1 活动，2 行走）',
  `iot_product_id` varchar(32) DEFAULT '' COMMENT '产品id',
  `iot_device_id` varchar(64) DEFAULT NULL COMMENT '设备id（电信云平台注册返回）',
  `iot_card_num` varchar(64) DEFAULT '' COMMENT '卡号',
  `iot_app_id` varchar(64) DEFAULT '' COMMENT '电信平台所属应用id',
  `real_time_track_status` tinyint(1) DEFAULT '0' COMMENT '实时轨迹功能状态，0关闭，1开启中',
  `real_time_track_last_time` bigint(20) DEFAULT '0' COMMENT '上一次开启实时轨迹的开始时间',
  `heart_time` int(3) DEFAULT '0' COMMENT '设备每小时上报心跳的时间分钟数（秒）',
  `heart_period` int(3) DEFAULT '7200' COMMENT '心跳周期，单位秒',
  `rand_num` int(11) DEFAULT '0' COMMENT '设备心跳随机数',
  `is_mode_change` tinyint(1) DEFAULT '1' COMMENT '设备模式是否切换完成 0 切换中 1切换完成',
  `last_change_time` bigint(20) DEFAULT '0' COMMENT '设备上次切换时间',
  `power_consumption_mode` tinyint(1) DEFAULT '1' COMMENT '功耗模式 0 超低功耗 1 正常功耗',
  `config_send_time` bigint(20) DEFAULT '0' COMMENT '配置下发更新时间',
  `is_restore` tinyint(1) DEFAULT '0' COMMENT '是否需要恢复出厂设置',
  `send_num` tinyint(3) DEFAULT '1' COMMENT '更新次数',
  `fault` varchar(16) DEFAULT NULL COMMENT '故障信息（1 心率故障，2 六轴故障，3 WIFI芯片故障，4 生命探头故障）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `last_position` varchar(32) DEFAULT '' COMMENT '前次位置（区/县）',
  `report_device_type` tinyint(4) DEFAULT NULL COMMENT 'NB上报设备类型',
  `software_type` tinyint(4) DEFAULT NULL COMMENT '软件类型',
  `startup_time` varchar(16) DEFAULT NULL COMMENT '开机时间',
  `at_home` tinyint(1) DEFAULT NULL COMMENT '是否在家（0-否，1-是）',
  `out_home_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '离/回家时间',
  `contact_device_id` varchar(32) NOT NULL DEFAULT '' COMMENT '关联设备主键',
  `power_report_num` tinyint(1) NOT NULL DEFAULT '0' COMMENT '电量上报次数',
  PRIMARY KEY (`device_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `commodity_id` (`commodity_id`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE,
  KEY `imei` (`imei`) USING BTREE,
  KEY `mac` (`mac`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='绑定设备信息表';


-- Table: lb_blood_oxygen_record_statistics
CREATE TABLE `lb_blood_oxygen_record_statistics` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `record_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录开始时间',
  `record_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录结束时间',
  `content` text COMMENT '血氧数据',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `record_time_date` datetime NOT NULL COMMENT '记录时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='血氧记录统计表';


-- Table: lb_blood_pressure_record_statistics
CREATE TABLE `lb_blood_pressure_record_statistics` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `record_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录开始时间',
  `record_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录结束时间',
  `content` text COMMENT '血压数据',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `record_time_date` datetime NOT NULL COMMENT '记录时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='血压记录统计表';


-- Table: lb_body_temperature_record_statistics
CREATE TABLE `lb_body_temperature_record_statistics` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `record_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录开始时间',
  `record_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录结束时间',
  `content` text COMMENT '体温数据',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `record_time_date` datetime NOT NULL COMMENT '记录时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='体温记录统计表';


-- Table: lb_breathe_record_statistics
CREATE TABLE `lb_breathe_record_statistics` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `record_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录开始时间',
  `record_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录结束时间',
  `content` text COMMENT '呼吸数据',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `record_time_date` datetime NOT NULL COMMENT '记录时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='呼吸记录统计表';


-- Table: lb_care_config
CREATE TABLE `lb_care_config` (
  `care_config_id` varchar(32) NOT NULL COMMENT '主键',
  `object_id` varchar(64) NOT NULL DEFAULT '' COMMENT '设置对象标识',
  `report_device_type` tinyint(1) DEFAULT NULL COMMENT 'NB上报设备类型',
  `device_iteration_number` tinyint(1) DEFAULT NULL COMMENT '设备迭代版本号',
  `care_config_type` tinyint(1) NOT NULL COMMENT '监护配置类型',
  `protocol_config_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '通讯协议配置类型（0-非通讯协议配置，其他参见`LB_NB通讯协议`）',
  `param` text NOT NULL COMMENT '参数',
  `param_length` tinyint(1) NOT NULL DEFAULT '1' COMMENT '参数长度（非通讯协议配置无效）',
  `param_order` tinyint(1) NOT NULL DEFAULT '1' COMMENT '参数顺序',
  `param_desc` varchar(256) NOT NULL DEFAULT '' COMMENT '参数描述',
  `is_open` tinyint(1) NOT NULL DEFAULT '0' COMMENT '开放标识（0-禁止配置，1-开放配置）',
  `show_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '显示类型（0-不显示，其他参见`监护设置显示类型枚举`）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`care_config_id`),
  KEY `object_id` (`object_id`) USING BTREE,
  KEY `device_type` (`report_device_type`,`device_iteration_number`) USING BTREE,
  KEY `care_config_type` (`care_config_type`) USING BTREE,
  KEY `protocol_config_type` (`protocol_config_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='监护配置表';


-- Table: lb_commodity
CREATE TABLE `lb_commodity` (
  `commodity_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `lonbon_device_id` varchar(32) NOT NULL DEFAULT '' COMMENT '来邦设备主键',
  `commodity_name` varchar(32) NOT NULL DEFAULT '' COMMENT '商品名称',
  `commodity_image` varchar(255) NOT NULL DEFAULT '' COMMENT '商品图片',
  `commodity_type` varchar(32) NOT NULL DEFAULT '' COMMENT '商品类型',
  `commodity_type_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '商品类型描述',
  `creater_id` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人主键',
  `reviser_id` varchar(32) NOT NULL DEFAULT '' COMMENT '修改人主键',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`commodity_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `lonbon_device_id` (`lonbon_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品信息表';


-- Table: lb_commodity_sale_record
CREATE TABLE `lb_commodity_sale_record` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `region_id` varchar(32) NOT NULL DEFAULT '' COMMENT '行政区域标识',
  `commodity_id` varchar(32) NOT NULL DEFAULT '' COMMENT '商品主键',
  `commodity_name` varchar(32) NOT NULL DEFAULT '' COMMENT '商品名称',
  `imei` varchar(32) DEFAULT '' COMMENT '设备编号',
  `mac` varchar(32) DEFAULT '' COMMENT 'mac地址',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `commodity_id` (`commodity_id`) USING BTREE,
  KEY `imei` (`imei`) USING BTREE,
  KEY `mac` (`mac`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='商品销售记录表';


-- Table: lb_common_account
CREATE TABLE `lb_common_account` (
  `id` int(32) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '类型（1-账号，2-密码）',
  `value` varchar(32) NOT NULL DEFAULT '' COMMENT '值',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `type` (`type`) USING BTREE,
  KEY `value` (`value`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22840 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='常见账号/密码表';


-- Table: lb_csr_dispose_record
CREATE TABLE `lb_csr_dispose_record` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `alarm_id` varchar(32) NOT NULL DEFAULT '' COMMENT '报警主键',
  `disposer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '处理人主键',
  `dispose_type` tinyint(4) NOT NULL COMMENT '处理类型',
  `dispose_content` text COMMENT '处理内容',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `alarm_id` (`alarm_id`) USING BTREE,
  KEY `dispose_type` (`dispose_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='客服处警记录表';


-- Table: lb_data_dictionary
CREATE TABLE `lb_data_dictionary` (
  `dictionary_id` int(32) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `dictionary_type` varchar(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `dictionary_key` varchar(32) NOT NULL DEFAULT '' COMMENT '字典键',
  `dictionary_value` varchar(64) NOT NULL DEFAULT '' COMMENT '字典值',
  `serial_num` tinyint(4) NOT NULL DEFAULT '1' COMMENT '序号',
  `dictionary_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '字典描述',
  `is_open` tinyint(1) NOT NULL DEFAULT '2' COMMENT '开放标识（1-开放查询，2-禁止查询）',
  `is_editable` tinyint(1) NOT NULL DEFAULT '2' COMMENT '编辑标识（1-可编辑，2-不可编辑）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`dictionary_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `dictionary_type` (`dictionary_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='数据字典表';


-- Table: lb_device_care_config
CREATE TABLE `lb_device_care_config` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `care_config_type` tinyint(1) NOT NULL COMMENT '监护配置类型',
  `param_one` text COMMENT '参数一',
  `param_two` varchar(128) NOT NULL DEFAULT '' COMMENT '参数二',
  `param_three` varchar(128) NOT NULL DEFAULT '' COMMENT '参数三',
  `param_four` varchar(128) NOT NULL DEFAULT '' COMMENT '参数四',
  `param_five` varchar(128) NOT NULL DEFAULT '' COMMENT '参数五',
  `param_six` varchar(128) NOT NULL DEFAULT '' COMMENT '参数六',
  `param_seven` varchar(128) NOT NULL DEFAULT '' COMMENT '参数七',
  `param_eight` varchar(128) NOT NULL DEFAULT '' COMMENT '参数八',
  `param_nine` varchar(128) NOT NULL DEFAULT '' COMMENT '参数九',
  `param_ten` varchar(128) NOT NULL DEFAULT '' COMMENT '参数十',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_id` (`iot_device_id`) USING BTREE,
  KEY `care_config_type` (`care_config_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='设备监护配置表';


-- Table: lb_device_change_record
CREATE TABLE `lb_device_change_record` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `account_id` varchar(32) NOT NULL DEFAULT '' COMMENT '操作人主键',
  `imei` varchar(32) DEFAULT '' COMMENT 'imei',
  `mac` varchar(32) NOT NULL DEFAULT '' COMMENT 'mac地址',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `action_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型（1-添加，2-解绑，3-换绑）',
  `device_type` int(11) DEFAULT NULL COMMENT '设备类型',
  `key_id` varchar(32) DEFAULT '' COMMENT '设备所属',
  `last_key_id` varchar(32) DEFAULT '' COMMENT '操作前设备所属',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='设备变更记录表';


-- Table: lb_device_energy
CREATE TABLE `lb_device_energy` (
  `device_energy_id` varchar(32) NOT NULL COMMENT '主键',
  `mac` varchar(32) NOT NULL DEFAULT '' COMMENT '设备mac信息',
  `energy` int(3) NOT NULL COMMENT '电量百分比',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `rsrp` int(3) DEFAULT '-1' COMMENT 'cat1信号强度，-1代表无效',
  PRIMARY KEY (`device_energy_id`) USING BTREE,
  KEY `mac` (`mac`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='设备电量记录表';


-- Table: lb_device_report
CREATE TABLE `lb_device_report` (
  `iot_device_id` varchar(64) NOT NULL COMMENT '设备id（电信云平台注册返回）',
  `report_type` varchar(32) NOT NULL DEFAULT '' COMMENT '上报消息类型',
  `report_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '上报时间',
  `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态，0未推送，1已推送',
  `send_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发送类型',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`iot_device_id`,`report_type`,`send_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='设备上报时间表';


-- Table: lb_device_white_list_record
CREATE TABLE `lb_device_white_list_record` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `msisdn` varchar(64) NOT NULL DEFAULT '' COMMENT '卡号',
  `phone` varchar(32) NOT NULL COMMENT '变动号码',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '移动语音白名单配置类型： 1：新增 4：删除',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录状态 1未生效 2操作中 3 已生效',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除1:删除',
  `is_use` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否使用 0 未使用 1 使用中',
  `is_operable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '当前是否可执行 0不可执行 1可执行',
  `account_id` varchar(32) NOT NULL COMMENT '账号id',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `isp_type` (`msisdn`) USING BTREE,
  KEY `phone` (`phone`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='白名单变更记录表';


-- Table: lb_enterprise
CREATE TABLE `lb_enterprise` (
  `enterprise_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_name` varchar(32) NOT NULL DEFAULT '' COMMENT '企业名称',
  `region_id` varchar(32) NOT NULL DEFAULT '' COMMENT '行政区域标识',
  `region_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区域描述',
  `detailed_address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `contacts_name` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人',
  `phone_num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `lb_is_participate` tinyint(1) NOT NULL DEFAULT '2' COMMENT '来邦参与处警标识（1-参与，2-不参与）',
  `lb_dispose_time` varchar(64) NOT NULL DEFAULT '' COMMENT '来邦处警时间段',
  `call_center_name` varchar(32) NOT NULL DEFAULT '' COMMENT '呼叫中心名称',
  `platform_title` varchar(32) NOT NULL DEFAULT '' COMMENT '平台标题',
  `platform_logo` varchar(255) NOT NULL DEFAULT '' COMMENT '平台LOGO',
  `change_password_cycle` int(11) NOT NULL DEFAULT '0' COMMENT '修改密码周期(天)（0：长期）',
  `login_fail_num` tinyint(1) NOT NULL DEFAULT '5' COMMENT '登录失败次数',
  `login_limit_time` int(11) NOT NULL DEFAULT '300' COMMENT '登录限制时间(秒)',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`enterprise_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='企业信息表';


-- Table: lb_family
CREATE TABLE `lb_family` (
  `family_id` varchar(64) NOT NULL COMMENT '主键',
  `region_id` varchar(32) NOT NULL DEFAULT '' COMMENT '行政区域标识',
  `region_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区域描述',
  `detailed_address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `longitude` varchar(32) NOT NULL DEFAULT '' COMMENT '经度',
  `latitude` varchar(32) NOT NULL DEFAULT '' COMMENT '纬度',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除 1:删除',
  `coordsys` tinyint(1) DEFAULT '2' COMMENT '坐标系类型，0：WGS-84，1：百度，2：高德',
  PRIMARY KEY (`family_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='家庭信息表';


-- Table: lb_family_contacts_account
CREATE TABLE `lb_family_contacts_account` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭id',
  `account_id` varchar(32) NOT NULL DEFAULT '' COMMENT '账号id',
  `is_admin` tinyint(1) NOT NULL DEFAULT '2' COMMENT '1 管理员 2 关注人',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除0:未删除 1:删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='家庭关注人表';


-- Table: lb_family_contacts_member
CREATE TABLE `lb_family_contacts_member` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭id',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人姓名',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '联系人手机号码',
  `relation_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '关系类型 1紧急联系人 2亲属',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除 1:删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='家庭联系成员表';


-- Table: lb_flow_record
CREATE TABLE `lb_flow_record` (
  `flow_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `record_id` varchar(32) NOT NULL DEFAULT '' COMMENT '关联记录主键',
  `flow_type` int(11) NOT NULL COMMENT '流水类型',
  `flow_content` text COMMENT '流水内容',
  `record_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `read_account` text COMMENT '已读账号',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  `group_id` varchar(32) NOT NULL DEFAULT '' COMMENT '分组标识',
  `record_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '记录类型（0-未知记录，1-报警记录，2-健康报告）',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  PRIMARY KEY (`flow_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `flow_type` (`flow_type`) USING BTREE,
  KEY `record_id` (`record_id`) USING BTREE,
  KEY `group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='流水记录表';


-- Table: lb_function
CREATE TABLE `lb_function` (
  `func_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `parent_code` varchar(32) NOT NULL DEFAULT '' COMMENT '父级菜单编码',
  `func_name` varchar(32) NOT NULL DEFAULT '' COMMENT '菜单名称',
  `func_code` varchar(32) NOT NULL DEFAULT '' COMMENT '菜单编码',
  `level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '菜单级别',
  `serial_num` tinyint(4) NOT NULL DEFAULT '1' COMMENT '排列序号',
  `route` varchar(255) NOT NULL DEFAULT '' COMMENT '菜单路由',
  `icon_path` varchar(255) NOT NULL DEFAULT '' COMMENT '图标路径',
  `is_open` tinyint(1) NOT NULL DEFAULT '1' COMMENT '开放标识（1-开放选择，2-禁止选择）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`func_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE,
  KEY `parent_code` (`parent_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='菜单目录表';


-- Table: lb_heartbeat_record_statistics
CREATE TABLE `lb_heartbeat_record_statistics` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `record_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录开始时间',
  `record_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录结束时间',
  `content` text COMMENT '心率数据',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `record_time_date` datetime NOT NULL COMMENT '记录时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='心率记录统计表';


-- Table: lb_iot_app
CREATE TABLE `lb_iot_app` (
  `id` int(11) NOT NULL COMMENT '主键',
  `app_id` varchar(256) DEFAULT '' COMMENT '应用唯一标识',
  `secret` varchar(256) DEFAULT '' COMMENT '应用密码',
  `secret_password` varchar(32) DEFAULT '' COMMENT 'secret密钥',
  `electricity_mode` tinyint(1) DEFAULT '1' COMMENT '应用的电量模式 0:eDRX 1:DRX  2:PSM',
  `device_amount` int(11) DEFAULT '0' COMMENT '设备已注册总数',
  `is_subscribe` tinyint(1) DEFAULT '0' COMMENT '应用是否订阅标识,0未订阅,1已订阅',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `is_register` tinyint(4) DEFAULT '0' COMMENT '是否为注册应用',
  `api_url` varchar(256) DEFAULT NULL COMMENT '接口请求路由',
  `call_back_message_url` varchar(100) DEFAULT NULL COMMENT '消息推送地址',
  `call_back_cmd_url` varchar(100) DEFAULT NULL COMMENT '命令推送地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Table: lb_iot_device_track
CREATE TABLE `lb_iot_device_track` (
  `track_id` varchar(32) NOT NULL COMMENT '主键',
  `track_group_id` varchar(32) NOT NULL DEFAULT '' COMMENT '轨迹组名',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型',
  `use_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录用途类型，1查找长者,2查找设备',
  `record_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `is_outdoor` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0信标位置,1非信标位置(粗定位,gps,wifi)',
  `position_mac` varchar(32) NOT NULL DEFAULT '' COMMENT '信标位置mac地址',
  `lat` varchar(32) NOT NULL DEFAULT '' COMMENT '纬度',
  `lng` varchar(32) NOT NULL DEFAULT '' COMMENT '经度',
  `lat_primitive` varchar(32) NOT NULL DEFAULT '' COMMENT '粗纬度',
  `lng_primitive` varchar(32) NOT NULL DEFAULT '' COMMENT '粗经度',
  `position_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '位置叙述',
  `position_status` text COMMENT '位置状态记录',
  `gps_is_detail` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'gps是否为精确gps,0粗定位,1GPS定位,2不显示定位类型,3获取粗定位失败,4定位到其他家庭设备',
  `report_reason` tinyint(1) NOT NULL DEFAULT '0' COMMENT '上报位置的原因，0心跳上报，1室外报警上报',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `device_type` tinyint(1) NOT NULL COMMENT '设备类型',
  `device_iteration_number` int(11) NOT NULL DEFAULT '0' COMMENT '设备迭代版本号',
  `device_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '设备佩带状态，0不显示佩带情况，1佩戴中，2未佩戴，3充电中',
  `device_status_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '佩带状态持续时长',
  `device_status_is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示佩戴状态 0不显示,1显示',
  `power` smallint(6) NOT NULL DEFAULT '-1' COMMENT '设备电量',
  `is_exercise_measure` tinyint(1) NOT NULL DEFAULT '0' COMMENT '设备运动状态，1运动测，0静止测',
  `heart_rate` int(3) NOT NULL DEFAULT '-1' COMMENT '心率值',
  `heart_rate_is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示心率数据，1显示，0不显示',
  `systolic_pressure` int(3) NOT NULL DEFAULT '-1' COMMENT '收缩压',
  `diastolic_pressure` int(3) NOT NULL DEFAULT '-1' COMMENT '舒张压',
  `is_applets_measure` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否小程序测量值 0:否1:是',
  `blood_pressure_is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示血压数据，1显示，0不显示',
  `is_sphygmomanometer` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有血压计 0:没有 1:有',
  `uncalibrated_day_num` int(11) DEFAULT NULL COMMENT '血压计未校准天数',
  `blood_oxygen` int(3) NOT NULL DEFAULT '-1' COMMENT '血氧值',
  `blood_oxygen_is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示血氧数据，1显示，0不显示',
  `body_temperature` decimal(4,2) NOT NULL DEFAULT '-1.00' COMMENT '体温',
  `body_temperature_is_show` tinyint(1) DEFAULT '0' COMMENT '是否显示体温数据，1显示，0不显示',
  `temperature` tinyint(3) NOT NULL DEFAULT '-1' COMMENT '温度',
  `humidity` tinyint(3) NOT NULL DEFAULT '-1' COMMENT '湿度',
  `is_humiture_sensor` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否有温湿度传感器 0:没有 1:有',
  `content` text COMMENT '内容',
  `track_open_id` varchar(32) NOT NULL DEFAULT '' COMMENT '轨迹开启人id',
  `track_end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '轨迹统计记录结束时间',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录是否显示，0不显示，1显示',
  `is_intact` tinyint(1) NOT NULL DEFAULT '0' COMMENT '当前数据是否完整，1完整，0,不完整',
  `is_health_intact` tinyint(1) NOT NULL DEFAULT '0' COMMENT '当前健康数据是否完整，1完整，0,不完整',
  `is_position_intact` tinyint(1) NOT NULL DEFAULT '0' COMMENT '当前位置数据是否完整，1完整，0,不完整',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `handle_name` varchar(32) NOT NULL DEFAULT '' COMMENT '开启/关闭人',
  `device_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '设备描述',
  `binding_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '绑定描述',
  `breath` int(3) NOT NULL DEFAULT '-1' COMMENT '呼吸值',
  `breath_is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '呼吸数据显示标识（0-不显示，1-显示）',
  `coordsys` tinyint(1) DEFAULT '2' COMMENT '坐标系类型，0：WGS-84，1：百度，2：高德',
  PRIMARY KEY (`track_id`) USING BTREE,
  KEY `record_time` (`record_time`) USING BTREE,
  KEY `track_group_id` (`track_group_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE,
  KEY `position_mac` (`position_mac`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='轨迹记录表';


-- Table: lb_lonbon_device
CREATE TABLE `lb_lonbon_device` (
  `lonbon_device_id` varchar(32) NOT NULL COMMENT '主键',
  `device_name` varchar(32) NOT NULL DEFAULT '' COMMENT '设备名称',
  `device_image` varchar(255) NOT NULL DEFAULT '' COMMENT '设备图片',
  `device_type` tinyint(4) NOT NULL COMMENT '设备类型',
  `device_type_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '设备类型描述',
  `device_version` varchar(32) NOT NULL COMMENT '设备型号',
  `commodity_type` varchar(32) NOT NULL DEFAULT '' COMMENT '商品类型（参见字典：commodity_type）',
  `commodity_type_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '商品类型描述',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`lonbon_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='来邦设备信息表';


-- Table: lb_message_interact_record
CREATE TABLE `lb_message_interact_record` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `flow_id` varchar(32) NOT NULL DEFAULT '' COMMENT '流水记录主键',
  `message_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '信息类型（1-文本，2-图片，3-语音，4-视频）',
  `message_content` text COMMENT '信息内容',
  `picture_width` int(11) NOT NULL DEFAULT '0' COMMENT '图片宽度(px)',
  `picture_height` int(11) NOT NULL DEFAULT '0' COMMENT '图片高度(px)',
  `voice_duration` int(11) NOT NULL DEFAULT '0' COMMENT '语音/视频时长',
  `interactor_id` varchar(32) NOT NULL DEFAULT '' COMMENT '互动人主键',
  `interactor_name` varchar(32) NOT NULL DEFAULT '' COMMENT '互动人姓名',
  `interactor_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '互动人类型',
  `dispose_type` tinyint(4) DEFAULT NULL COMMENT '处警类型',
  `interact_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '互动时间',
  `read_account` text COMMENT '已读账号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '信息状态（0-正常，1-撤回）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `user_type` tinyint(1) DEFAULT NULL COMMENT '用户类型',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `interactor_id` (`interactor_id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `flow_id` (`flow_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='信息互动记录表';


-- Table: lb_nb_hardware_version
CREATE TABLE `lb_nb_hardware_version` (
  `nb_hardware_version_id` varchar(32) NOT NULL COMMENT '主键ID',
  `hardware_version` varchar(32) NOT NULL DEFAULT '' COMMENT '硬件版本号',
  `support_function` varchar(64) NOT NULL DEFAULT '' COMMENT '版本支持功能',
  `device_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '版本所属设备类型',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`nb_hardware_version_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='NB设备硬件版本信息表';


-- Table: lb_region
CREATE TABLE `lb_region` (
  `region_id` int(11) NOT NULL COMMENT '主键',
  `region_name` varchar(32) DEFAULT '' COMMENT '区域名称',
  `parent_path` varchar(255) DEFAULT NULL COMMENT '区域路径',
  `parent_id` int(11) DEFAULT '0' COMMENT '父级区域',
  `has_child` tinyint(1) DEFAULT '2' COMMENT '是否有子节点 1 是 2 否',
  `adcode` int(11) DEFAULT '0' COMMENT '城市编码',
  `center` varchar(32) DEFAULT '' COMMENT '区域中心点',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `level` tinyint(3) DEFAULT '0' COMMENT '级别',
  `chinese_initials` varchar(32) DEFAULT '' COMMENT '简拼',
  `status` tinyint(1) DEFAULT '0' COMMENT '更新状态（0 历史，1 新增，2 更新， 3 删除）',
  PRIMARY KEY (`region_id`),
  KEY `parent_id` (`parent_id`) USING BTREE,
  KEY `adcode` (`adcode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='行政区域';


-- Table: lb_region_new
CREATE TABLE `lb_region_new` (
  `region_id` int(11) NOT NULL COMMENT '主键',
  `region_name` varchar(32) DEFAULT '' COMMENT '区域名称',
  `parent_path` varchar(255) DEFAULT NULL COMMENT '区域路径',
  `parent_id` int(11) DEFAULT '0' COMMENT '父级区域',
  `has_child` tinyint(1) DEFAULT '2' COMMENT '是否有子节点 1 是 2 否',
  `adcode` int(11) DEFAULT '0' COMMENT '城市编码',
  `center` varchar(32) DEFAULT '' COMMENT '区域中心点',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `level` tinyint(3) DEFAULT '0' COMMENT '级别',
  `chinese_initials` varchar(32) DEFAULT '' COMMENT '简拼',
  PRIMARY KEY (`region_id`),
  KEY `parent_id` (`parent_id`) USING BTREE,
  KEY `adcode` (`adcode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='行政区域(实时)';


-- Table: lb_remind_task
CREATE TABLE `lb_remind_task` (
  `task_id` varchar(32) NOT NULL COMMENT '主键',
  `task_content` varchar(64) NOT NULL DEFAULT '' COMMENT '任务内容',
  `task_hour` varchar(32) NOT NULL DEFAULT '' COMMENT '任务设定小时',
  `task_minute` varchar(32) NOT NULL DEFAULT '' COMMENT '任务设定分钟',
  `is_set` tinyint(1) NOT NULL DEFAULT '0' COMMENT '设备是否设置完成 0未完成，1完成',
  `serial_number` tinyint(1) NOT NULL DEFAULT '1' COMMENT '序号',
  `org_id` varchar(32) NOT NULL DEFAULT '' COMMENT '机构唯一标识',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除1:删除',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `device_id` varchar(32) NOT NULL DEFAULT '' COMMENT '设备id ',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='提醒任务表';


-- Table: lb_report_show_config
CREATE TABLE `lb_report_show_config` (
  `report_type` tinyint(1) NOT NULL COMMENT '报告类型（流水类型）',
  `report_device_type` tinyint(1) NOT NULL COMMENT '上报设备类型',
  `device_iteration_number` tinyint(1) NOT NULL DEFAULT '0' COMMENT '设备迭代版本号',
  `show_item` varchar(32) NOT NULL DEFAULT '' COMMENT '显示项',
  `show_item_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '显示项描述',
  `show_item_icon` varchar(32) NOT NULL DEFAULT '' COMMENT '显示项图标',
  `is_show` tinyint(1) NOT NULL COMMENT '显示标识（0-不显示，1-显示）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`report_type`,`report_device_type`,`device_iteration_number`,`show_item`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='统计报告显示配置表';


-- Table: lb_role
CREATE TABLE `lb_role` (
  `role_id` varchar(32) NOT NULL COMMENT '主键',
  `enterprise_id` varchar(32) NOT NULL DEFAULT '' COMMENT '企业主键',
  `role_name` varchar(32) NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_type` tinyint(4) NOT NULL COMMENT '角色类型',
  `is_admin` tinyint(1) NOT NULL DEFAULT '2' COMMENT '管理员标识（1-管理员，2-非管理员）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`role_id`) USING BTREE,
  KEY `enterprise_id` (`enterprise_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='角色信息表';


-- Table: lb_role_function
CREATE TABLE `lb_role_function` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_id` varchar(32) NOT NULL DEFAULT '' COMMENT '角色主键',
  `func_id` varchar(32) NOT NULL DEFAULT '' COMMENT '菜单主键',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  KEY `func_id` (`func_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='角色菜单关联关系表';


-- Table: lb_service_benefits
CREATE TABLE `lb_service_benefits` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `object_id` varchar(32) NOT NULL DEFAULT '' COMMENT '服务对象标识',
  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
  `expire_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '到期时间',
  `user_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户类型（1-体验用户，2-付费用户）',
  `is_give` tinyint(1) NOT NULL DEFAULT '0' COMMENT '赠送标识（0-未赠送，1-已赠送）',
  `device_imei` varchar(32) NOT NULL DEFAULT '' COMMENT '设备标识',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `object_id` (`object_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='服务权益表';


-- Table: lb_service_payment_price
CREATE TABLE `lb_service_payment_price` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `payment_duration` tinyint(1) NOT NULL COMMENT '缴费时长（时长单位见描述）',
  `duration_desc` varchar(32) NOT NULL DEFAULT '' COMMENT '时长描述',
  `discount` double NOT NULL DEFAULT '0' COMMENT '折扣',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `payment_channel` tinyint(1) NOT NULL DEFAULT '1' COMMENT '缴费渠道（1-帮守护小程序）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='服务缴费价目表';


-- Table: lb_service_payment_record
CREATE TABLE `lb_service_payment_record` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `object_id` varchar(32) NOT NULL DEFAULT '' COMMENT '服务对象标识',
  `family_address` text NOT NULL COMMENT '家庭住址',
  `expire_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '到期时间',
  `payer_id` varchar(32) NOT NULL DEFAULT '' COMMENT '缴费人标识',
  `payer_name` varchar(32) NOT NULL DEFAULT '' COMMENT '缴费人姓名',
  `payment_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '缴费类型（1-支付，2-初始赠送，3-平台赠送，4-设备赠送）',
  `payment_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '缴费时间',
  `payment_duration` varchar(32) NOT NULL DEFAULT '' COMMENT '缴费时长',
  `payment_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '缴费金额',
  `payment_detail` text COMMENT '缴费详情',
  `trade_order_id` varchar(32) NOT NULL DEFAULT '' COMMENT '交易订单标识（支付缴费有效）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`record_id`),
  KEY `object_id` (`object_id`) USING BTREE,
  KEY `trade_order_id` (`trade_order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='服务缴费记录表';


-- Table: lb_sim
CREATE TABLE `lb_sim` (
  `acc_num` varchar(64) NOT NULL COMMENT 'nb卡接入号',
  `iccid` varchar(64) DEFAULT '' COMMENT 'nb卡号',
  `isp_type` char(1) DEFAULT '' COMMENT 'sim卡运营商类型：1 电信 2 移动',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `operation_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作时间',
  PRIMARY KEY (`acc_num`),
  KEY `iccid` (`iccid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Table: lb_steps_record
CREATE TABLE `lb_steps_record` (
  `record_id` varchar(32) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `steps_number` int(11) DEFAULT '0' COMMENT '步数',
  `add_steps_number` int(11) DEFAULT '0' COMMENT '新增步数',
  `record_time` bigint(20) DEFAULT '0' COMMENT '记录时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT ' 修改时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `iot_device_id` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='步数记录表';


-- Table: lb_toilet_probe_area
CREATE TABLE `lb_toilet_probe_area` (
  `toilet_id` varchar(32) NOT NULL COMMENT '区域主键',
  `length` int(3) NOT NULL DEFAULT '40' COMMENT '区域长度',
  `width` int(3) NOT NULL DEFAULT '40' COMMENT '区域宽度',
  `toilet_map_content` text COMMENT '探测区域信息',
  `device_wall_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '设备安装墙面类型 1.长  2宽',
  `device_id` varchar(32) NOT NULL DEFAULT '' COMMENT '绑定设备id',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除1:删除',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `serial_num` int(3) NOT NULL DEFAULT '1' COMMENT '序号',
  `device_wall_distance` int(3) NOT NULL DEFAULT '20' COMMENT '离所在墙壁左端距离',
  `hight` int(3) DEFAULT '10' COMMENT '安装高度',
  PRIMARY KEY (`toilet_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='洗手间探测区域';


-- Table: lb_toilet_shield_area
CREATE TABLE `lb_toilet_shield_area` (
  `area_id` varchar(32) NOT NULL COMMENT '屏蔽区域主键',
  `toilet_id` varchar(32) NOT NULL COMMENT '所属区域主键',
  `x_start` int(3) NOT NULL DEFAULT '20' COMMENT '屏蔽区域x轴起点',
  `x_stop` int(3) NOT NULL DEFAULT '20' COMMENT '屏蔽区域x轴终点',
  `y_start` int(3) NOT NULL DEFAULT '20' COMMENT '屏蔽区域y轴起点',
  `y_stop` int(3) NOT NULL DEFAULT '20' COMMENT '屏蔽区域y轴终点',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0:未删除1:删除',
  PRIMARY KEY (`area_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='洗手间探测区域屏蔽区域';


-- Table: lb_trade_order
CREATE TABLE `lb_trade_order` (
  `trade_order_id` varchar(32) NOT NULL COMMENT '主键',
  `prepay_id` varchar(64) DEFAULT '' COMMENT '预支付交易会话标识',
  `trade_no` varchar(64) NOT NULL DEFAULT '' COMMENT '交易订单号',
  `trade_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '交易时间',
  `trade_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '交易金额',
  `platform_type` tinyint(1) NOT NULL COMMENT '平台类型（1-支付宝，2-微信）',
  `order_type` tinyint(1) NOT NULL COMMENT '订单类型（1-支付，2-退款）',
  `order_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '订单状态',
  `pay_trade_no` varchar(64) DEFAULT '' COMMENT '支付交易订单号（退款订单有效）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`trade_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='交易订单表';


-- Table: lb_websocket_push_record
CREATE TABLE `lb_websocket_push_record` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `family_id` varchar(32) NOT NULL DEFAULT '' COMMENT '家庭主键',
  `client_id` varchar(32) NOT NULL DEFAULT '' COMMENT '客户端标识',
  `client_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '客户端类型',
  `message_id` varchar(32) NOT NULL DEFAULT '' COMMENT '消息主键',
  `message_type` int(11) NOT NULL COMMENT '消息类型',
  `message_content` text COMMENT '消息内容',
  `message_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消息状态（0-未读， 1-已读，2-撤回）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识（0-未删除，1-已删除）',
  `iot_device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '电信平台设备标识',
  `badge` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角标',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `family_id` (`family_id`) USING BTREE,
  KEY `client_id` (`client_id`) USING BTREE,
  KEY `message_id` (`message_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='websocket推送记录表';


-- Table: lb_worker_node
CREATE TABLE `lb_worker_node` (
  `worker_node_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `host_name` varchar(64) NOT NULL COMMENT '主机host',
  `port` varchar(64) NOT NULL COMMENT '主机端口',
  `type` int(11) NOT NULL COMMENT '节点类型:实例或容器',
  `launch_date` date NOT NULL COMMENT '发布日期',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`worker_node_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分布式UID Generator生成策略依赖表';


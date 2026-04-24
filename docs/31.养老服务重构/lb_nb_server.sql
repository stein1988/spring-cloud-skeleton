-- Database: lb_nb_server
-- Tables only, no data
-- Exported at: 2026-04-21 19:26:09


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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Table: lb_iot_app_expand
CREATE TABLE `lb_iot_app_expand` (
  `id` varchar(32) NOT NULL DEFAULT '' COMMENT 'id',
  `app_id` varchar(256) DEFAULT '' COMMENT '应用唯一标识',
  `secret` varchar(256) DEFAULT '' COMMENT '应用密码',
  `secret_password` varchar(32) DEFAULT '' COMMENT 'secret密钥',
  `electricity_mode` tinyint(1) DEFAULT '1' COMMENT '应用的电量模式 0:eDRX 1:DRX  2:PSM',
  `device_amount` int(11) DEFAULT '0' COMMENT '设备已注册总数',
  `type` tinyint(3) DEFAULT '1' COMMENT '1:海曼设备',
  `is_subscribe` tinyint(1) DEFAULT '0' COMMENT '应用是否订阅标识,0未订阅,1已订阅',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生产时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `is_register` tinyint(4) DEFAULT '0' COMMENT '是否为注册应用',
  `api_url` varchar(256) DEFAULT NULL COMMENT '接口请求路由',
  `call_back_message_url` varchar(100) DEFAULT NULL COMMENT '消息推送地址',
  `call_back_cmd_url` varchar(100) DEFAULT NULL COMMENT '命令推送地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='增加应用扩展表';


-- Table: lb_iot_app_factory
CREATE TABLE `lb_iot_app_factory` (
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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂测试NB应用表';


-- Table: lb_iot_device
CREATE TABLE `lb_iot_device` (
  `iot_device_id` varchar(64) NOT NULL COMMENT '主键',
  `imei` varchar(32) DEFAULT '' COMMENT '设备编号',
  `psk` varchar(32) DEFAULT '' COMMENT '加密密钥',
  `is_use` tinyint(1) DEFAULT '1' COMMENT '设备注册信息是否有效，1有效，0已失效',
  `iot_app_id` varchar(32) DEFAULT '' COMMENT '设备注册的应用id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型枚举,参见枚举文档',
  `product_id` varchar(32) DEFAULT NULL COMMENT '产品id',
  `feature_str` varchar(192) DEFAULT NULL COMMENT '特征串',
  `device_iteration_number` int(3) DEFAULT '-1' COMMENT '设备版本迭代号',
  `org_id` varchar(64) DEFAULT NULL COMMENT '机构id',
  `platform_type` tinyint(1) DEFAULT '0' COMMENT '平台类型，0：OC，1：AEP',
  `device_id` varchar(64) DEFAULT NULL COMMENT '电信平台id',
  `device_model` int(3) DEFAULT '0' COMMENT '设备型号',
  `is_bind` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否绑定，1是0否',
  `is_enter` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为录入设备，1是0否',
  `device_category` smallint(6) NOT NULL DEFAULT '-1' COMMENT '设备种类',
  `position_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '位置类型',
  PRIMARY KEY (`iot_device_id`) USING BTREE,
  KEY `lb_iot_device_device_id_IDX` (`device_id`) USING BTREE,
  KEY `imei` (`imei`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备注册';


-- Table: lb_iot_device22
CREATE TABLE `lb_iot_device22` (
  `iot_device_id` varchar(64) NOT NULL COMMENT '主键',
  `imei` varchar(32) DEFAULT '' COMMENT '设备编号',
  `psk` varchar(32) DEFAULT '' COMMENT '加密密钥',
  `is_use` tinyint(1) DEFAULT '1' COMMENT '设备注册信息是否有效，1有效，0已失效',
  `iot_app_id` varchar(32) DEFAULT '' COMMENT '设备注册的应用id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型枚举,参见枚举文档',
  `product_id` varchar(32) DEFAULT NULL COMMENT '产品id',
  `feature_str` varchar(192) DEFAULT NULL COMMENT '特征串',
  `device_iteration_number` int(3) DEFAULT '-1' COMMENT '设备版本迭代号',
  `org_id` varchar(64) DEFAULT NULL COMMENT '机构id',
  `platform_type` tinyint(1) DEFAULT '0' COMMENT '平台类型，0：OC，1：AEP',
  `device_id` varchar(64) DEFAULT NULL COMMENT '电信平台id',
  `device_model` int(3) DEFAULT '0' COMMENT '设备型号',
  `is_bind` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否绑定，1是0否',
  `is_enter` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为录入设备，1是0否',
  PRIMARY KEY (`iot_device_id`),
  KEY `lb_iot_device_device_id_IDX` (`device_id`) USING BTREE,
  KEY `imei` (`imei`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备注册';


-- Table: lb_iot_device33
CREATE TABLE `lb_iot_device33` (
  `iot_device_id` varchar(64) NOT NULL COMMENT '主键',
  `imei` varchar(32) DEFAULT '' COMMENT '设备编号',
  `psk` varchar(32) DEFAULT '' COMMENT '加密密钥',
  `is_use` tinyint(1) DEFAULT '1' COMMENT '设备注册信息是否有效，1有效，0已失效',
  `iot_app_id` varchar(32) DEFAULT '' COMMENT '设备注册的应用id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型枚举,参见枚举文档',
  `product_id` varchar(32) DEFAULT NULL COMMENT '产品id',
  `feature_str` varchar(192) DEFAULT NULL COMMENT '特征串',
  `device_iteration_number` int(3) DEFAULT '-1' COMMENT '设备版本迭代号',
  `org_id` varchar(64) DEFAULT NULL COMMENT '机构id',
  `platform_type` tinyint(1) DEFAULT '0' COMMENT '平台类型，0：OC，1：AEP',
  `device_id` varchar(64) DEFAULT NULL COMMENT '电信平台id',
  `device_model` int(3) DEFAULT '0' COMMENT '设备型号',
  `is_bind` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否绑定，1是0否',
  `is_enter` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为录入设备，1是0否',
  PRIMARY KEY (`iot_device_id`),
  KEY `lb_iot_device_device_id_IDX` (`device_id`) USING BTREE,
  KEY `imei` (`imei`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备注册';


-- Table: lb_iot_product
CREATE TABLE `lb_iot_product` (
  `iot_product_id` varchar(32) NOT NULL COMMENT '主键',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型',
  `device_model` varchar(32) DEFAULT '' COMMENT '设备型号',
  `factory_id` varchar(32) DEFAULT '' COMMENT '厂商id',
  `factory_name` varchar(32) DEFAULT NULL COMMENT '厂商名称',
  `protocol_type` varchar(32) DEFAULT '' COMMENT '协议类型',
  `device_photo` varchar(32) DEFAULT '' COMMENT '设备图片',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `feature_str` varchar(64) DEFAULT NULL COMMENT '特征串',
  `master_key` varchar(64) DEFAULT '' COMMENT '令牌',
  `protocol` tinyint(1) DEFAULT '0' COMMENT '通信协议,0mq,1tcp,2lwm',
  `electricity_mode` tinyint(1) DEFAULT '1' COMMENT '应用的电量模式 0:eDRX 1:DRX  2:PSM',
  `platform_type` tinyint(1) DEFAULT '1' COMMENT '平台类型，1电信aep，2移动onenet',
  `org_id` varchar(64) NOT NULL DEFAULT 'lonbon' COMMENT '所属项目',
  `id` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义产品';


-- Table: lb_iot_product_upgrade_task
CREATE TABLE `lb_iot_product_upgrade_task` (
  `task_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `iot_product_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '产品id',
  `start_version` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '升级开始版本',
  `end_version` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '升级结束版本',
  `task_file_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '文件id',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `update_time` bigint(20) NOT NULL DEFAULT '0',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Table: lb_iw_data
CREATE TABLE `lb_iw_data` (
  `iw_data_id` varchar(32) NOT NULL COMMENT '主键',
  `device_id` varchar(32) NOT NULL DEFAULT '' COMMENT '设备id',
  `data_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '数据类型，1睡眠',
  `data` mediumtext NOT NULL COMMENT '数据内容',
  `data_date` varchar(32) NOT NULL DEFAULT '' COMMENT '数据所属日期',
  `report_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '数据上报时间',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `seq` int(11) NOT NULL DEFAULT '0' COMMENT '分包序号',
  PRIMARY KEY (`iw_data_id`),
  KEY `lb_iw_data_device_id_IDX` (`device_id`) USING BTREE,
  KEY `lb_iw_data_data_date_IDX` (`data_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='埃微设备上报记录表';


-- Table: lb_nb_api_result
CREATE TABLE `lb_nb_api_result` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `app_id` varchar(32) DEFAULT NULL COMMENT '应用id',
  `action_type` tinyint(1) DEFAULT NULL COMMENT '事件类型',
  `result` mediumtext COMMENT '结果',
  `iot_device_id` varchar(64) DEFAULT NULL COMMENT '设备id',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `command_data` mediumtext COMMENT '命令内容',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `lb_nb_api_result_iot_device_id_IDX` (`iot_device_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电信应用api调用记录表';


-- Table: lb_nb_data20260420
CREATE TABLE `lb_nb_data20260420` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `msg_type` varchar(255) DEFAULT '' COMMENT '上报/下发',
  `device_id` varchar(64) DEFAULT '' COMMENT '设备id',
  `data_base64` mediumtext COMMENT '数据编码值',
  `data_binary` mediumtext COMMENT '数据十六进制值',
  `data_binary_decrypt` mediumtext COMMENT '报文解密后的数据',
  `crc_origin` int(11) DEFAULT '0' COMMENT '循环校验码十进制值',
  `crc_calculate` int(11) DEFAULT '0' COMMENT 'crc本地计算值',
  `is_data_error` varchar(255) DEFAULT '' COMMENT '正确/错误',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `receive_time` bigint(20) DEFAULT '0' COMMENT '接收时间',
  `last_china_telecom_response_time` bigint(20) DEFAULT '0' COMMENT '电信上次的响应时间',
  `last_lb_server_response_time` bigint(20) DEFAULT '0' COMMENT '服务器最近的响应时间',
  `command_id` varchar(64) DEFAULT '' COMMENT '电信接口创建的命令主键',
  `command_type` varchar(32) DEFAULT '' COMMENT '数据区自定义消息类型',
  `command_mid` varchar(11) DEFAULT '' COMMENT '命令消息条数编号',
  `status` tinyint(1) DEFAULT '0' COMMENT '0已下发，1执行成功，2执行失败，3超时',
  `event_time` bigint(20) DEFAULT '0' COMMENT '事件时间',
  `is_repeat` tinyint(1) DEFAULT '0' COMMENT '0不重复，1重复记录',
  `fail_reason` varchar(5) DEFAULT NULL COMMENT 'NB命令执行失败的原因',
  `send_times` tinyint(1) DEFAULT '1' COMMENT '命令下发的次数',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型，参见字典表',
  `report_time_hex` varchar(32) DEFAULT NULL,
  `help_device_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_id` (`device_id`) USING BTREE,
  KEY `command_id` (`command_id`) USING BTREE,
  KEY `command_type` (`command_type`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Table: lb_nb_data20260421
CREATE TABLE `lb_nb_data20260421` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `msg_type` varchar(255) DEFAULT '' COMMENT '上报/下发',
  `device_id` varchar(64) DEFAULT '' COMMENT '设备id',
  `data_base64` mediumtext COMMENT '数据编码值',
  `data_binary` mediumtext COMMENT '数据十六进制值',
  `data_binary_decrypt` mediumtext COMMENT '报文解密后的数据',
  `crc_origin` int(11) DEFAULT '0' COMMENT '循环校验码十进制值',
  `crc_calculate` int(11) DEFAULT '0' COMMENT 'crc本地计算值',
  `is_data_error` varchar(255) DEFAULT '' COMMENT '正确/错误',
  `create_time` bigint(20) DEFAULT '0' COMMENT '生成时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `receive_time` bigint(20) DEFAULT '0' COMMENT '接收时间',
  `last_china_telecom_response_time` bigint(20) DEFAULT '0' COMMENT '电信上次的响应时间',
  `last_lb_server_response_time` bigint(20) DEFAULT '0' COMMENT '服务器最近的响应时间',
  `command_id` varchar(64) DEFAULT '' COMMENT '电信接口创建的命令主键',
  `command_type` varchar(32) DEFAULT '' COMMENT '数据区自定义消息类型',
  `command_mid` varchar(11) DEFAULT '' COMMENT '命令消息条数编号',
  `status` tinyint(1) DEFAULT '0' COMMENT '0已下发，1执行成功，2执行失败，3超时',
  `event_time` bigint(20) DEFAULT '0' COMMENT '事件时间',
  `is_repeat` tinyint(1) DEFAULT '0' COMMENT '0不重复，1重复记录',
  `fail_reason` varchar(5) DEFAULT NULL COMMENT 'NB命令执行失败的原因',
  `send_times` tinyint(1) DEFAULT '1' COMMENT '命令下发的次数',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型，参见字典表',
  `report_time_hex` varchar(32) DEFAULT NULL,
  `help_device_id` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `device_id` (`device_id`) USING BTREE,
  KEY `command_id` (`command_id`) USING BTREE,
  KEY `command_type` (`command_type`) USING BTREE,
  KEY `device_type` (`device_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Table: lb_nb_data_private
CREATE TABLE `lb_nb_data_private` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `org_id` varchar(64) DEFAULT NULL COMMENT '机构id',
  `message` text COMMENT '消息内容',
  `is_receive` tinyint(4) DEFAULT '0' COMMENT '是否已接收',
  `send_time` bigint(20) DEFAULT NULL COMMENT '发送时间',
  `receive_time` bigint(20) DEFAULT '0' COMMENT '接收时间',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  `message_type` varchar(100) DEFAULT NULL COMMENT '消息类型',
  PRIMARY KEY (`id`),
  KEY `lb_nb_data_private_org_id_IDX` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私有化服务nb数据异常接收记录';


-- Table: lb_nbdata_bak
CREATE TABLE `lb_nbdata_bak` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `msg_type` varchar(255) DEFAULT '' COMMENT '上报/下发',
  `device_id` varchar(64) DEFAULT '' COMMENT '设备id',
  `data_base64` mediumtext COMMENT '数据编码值',
  `data_binary` mediumtext COMMENT '数据十六进制值',
  `data_binary_decrypt` mediumtext COMMENT '报文解密后的数据',
  `crc_origin` int(11) DEFAULT '0' COMMENT '循环校验码十进制值',
  `crc_calculate` int(11) DEFAULT '0' COMMENT 'crc本地计算值',
  `is_data_error` varchar(255) DEFAULT '' COMMENT '正确/错误',
  `create_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '生成时间',
  `update_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `receive_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '接收时间',
  `last_china_telecom_response_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '电信上次的响应时间',
  `last_lb_server_response_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '服务器最近的响应时间',
  `command_id` varchar(64) DEFAULT '' COMMENT '电信接口创建的命令主键',
  `command_type` varchar(32) DEFAULT '' COMMENT '数据区自定义消息类型',
  `command_mid` varchar(11) DEFAULT '' COMMENT '命令消息条数编号',
  `status` tinyint(1) DEFAULT '0' COMMENT '0已下发，1执行成功，2执行失败，3超时',
  `event_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '事件时间',
  `is_repeat` tinyint(1) DEFAULT '0' COMMENT '0不重复，1重复记录',
  `fail_reason` varchar(5) DEFAULT NULL COMMENT 'NB命令执行失败的原因',
  `send_times` tinyint(1) DEFAULT '1' COMMENT '命令下发的次数',
  `device_type` mediumint(9) DEFAULT '0' COMMENT '设备类型，参见字典表',
  `report_time_hex` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `device_id` (`device_id`) USING BTREE,
  KEY `command_id` (`command_id`) USING BTREE,
  KEY `command_type` (`command_type`) USING BTREE,
  KEY `device_type` (`device_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;


-- Table: lb_schedule_task_cluster
CREATE TABLE `lb_schedule_task_cluster` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `task_name` varchar(64) DEFAULT NULL COMMENT '任务名称',
  `execute_status` tinyint(1) DEFAULT '0' COMMENT '执行状态:0未执行,1在执行',
  `execute_ip` varchar(32) DEFAULT NULL COMMENT '执行机器ip',
  `execute_time` bigint(20) DEFAULT '0' COMMENT '最后一次执行时间',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `lb_schedule_task_cluster_task_name_IDX` (`task_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行状态记录表';


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
) ENGINE=InnoDB AUTO_INCREMENT=5202 DEFAULT CHARSET=utf8mb4 COMMENT='分布式UID Generator生成策略依赖表';


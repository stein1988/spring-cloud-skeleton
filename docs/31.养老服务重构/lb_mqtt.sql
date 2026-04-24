-- Database: lb_mqtt
-- Tables only, no data
-- Exported at: 2026-04-21 19:26:09


-- Table: lb_device_position
CREATE TABLE `lb_device_position` (
  `device_position_id` varchar(64) NOT NULL DEFAULT '' COMMENT '位置id',
  `mac` varchar(32) NOT NULL DEFAULT '' COMMENT 'mac',
  `imei` varchar(32) NOT NULL DEFAULT '' COMMENT 'imei',
  `org_position` varchar(64) DEFAULT '' COMMENT '机构位置',
  `position_desc` varchar(64) NOT NULL DEFAULT '' COMMENT '位置叙述',
  `longitude` varchar(32) NOT NULL DEFAULT '' COMMENT '经度',
  `latitude` varchar(32) NOT NULL DEFAULT '' COMMENT '纬度',
  `device_type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '设备类型',
  `org_id` varchar(64) NOT NULL DEFAULT '' COMMENT '机构组织id',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '生产时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `coordsys` tinyint(1) DEFAULT '1' COMMENT '坐标系类型，0：WGS-84，1：百度，2：高德',
  PRIMARY KEY (`device_position_id`),
  KEY `lb_device_position_org_id_IDX` (`org_id`,`mac`,`imei`) USING BTREE,
  KEY `lb_device_position_mac_IDX` (`mac`) USING BTREE,
  KEY `lb_device_position_imei_IDX` (`imei`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备位置信息表';


-- Table: lb_mq_fail
CREATE TABLE `lb_mq_fail` (
  `mq_fail_id` varchar(64) NOT NULL DEFAULT '' COMMENT '主键',
  `type` tinyint(3) DEFAULT '1' COMMENT '类型,1:位置信息',
  `msg` varchar(255) DEFAULT '' COMMENT '消息内容',
  `status` tinyint(3) DEFAULT '1' COMMENT '状态,1:失败未补发,2:已经补发',
  `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`mq_fail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MQTT失败记录表';


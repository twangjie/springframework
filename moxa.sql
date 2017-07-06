/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50535
Source Host           : 127.0.0.1:3306
Source Database       : dzkd

Target Server Type    : MYSQL
Target Server Version : 50535
File Encoding         : 65001

Date: 2017-07-06 11:00:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for moxa
-- ----------------------------
DROP TABLE IF EXISTS `moxa`;
CREATE TABLE `moxa` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) DEFAULT NULL COMMENT '设备名称',
  `address` varchar(100) DEFAULT NULL COMMENT '归属地',
  `ip` varchar(100) DEFAULT NULL COMMENT '串口服务器地址',
  `port` varchar(10) DEFAULT NULL COMMENT '端口号',
  `info` text COMMENT '备注',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 1-连接 2-断开 3-未知',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of moxa
-- ----------------------------

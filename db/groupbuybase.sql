/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : groupbuybase

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 18/06/2025 22:26:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for addresses
-- ----------------------------
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_default`(`is_default` ASC) USING BTREE,
  INDEX `idx_user_default`(`user_id` ASC, `is_default` ASC) USING BTREE,
  CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of addresses
-- ----------------------------
INSERT INTO `addresses` VALUES (1, 2, '测试用户', '13800138001', '北京市', '朝阳区', '望京街道', '望京SOHO T1座1001室', '100102', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (2, 2, '测试用户', '13800138001', '北京市', '海淀区', '中关村街道', '中关村大街1号', '100080', 0, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (3, 7, '普通用户1', '13800138006', '上海市', '浦东新区', '陆家嘴街道', '世纪大道100号', '200120', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (4, 8, '普通用户2', '13800138007', '广东省', '深圳市', '南山区', '科技园南区深南大道9999号', '518057', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (5, 3, '张团长', '13800138002', '北京市', '丰台区', '方庄街道', '方庄芳城园一区15号楼', '100078', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (6, 4, '李团长', '13800138003', '北京市', '昌平区', '回龙观街道', '回龙观西大街118号', '102208', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (7, 5, '王供应商', '13800138004', '北京市', '大兴区', '亦庄开发区', '经海路36号', '100176', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (8, 6, '赵供应商', '13800138005', '天津市', '滨海新区', '开发区', '第三大街99号', '300457', 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `addresses` VALUES (9, 9, '测试用户9', '13900001001', '北京市', '朝阳区', '望京街道', '望京SOHO T1座1001室', '100102', 1, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (10, 10, '测试用户10', '13900001002', '上海市', '浦东新区', '陆家嘴街道', '世纪大道100号', '200120', 1, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (11, 11, '测试用户11', '13900001003', '广东省', '深圳市', '南山区', '科技园南区深南大道9999号', '518057', 1, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (12, 12, '测试用户12', '13900001004', '浙江省', '杭州市', '西湖区', '文三路100号', '310012', 1, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (13, 9, '测试用户9-2', '13900001001', '北京市', '海淀区', '中关村街道', '中关村大街1号', '100080', 0, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (14, 10, '测试用户10-2', '13900001002', '上海市', '徐汇区', '徐家汇街道', '漕溪北路100号', '200030', 0, '2025-06-13 21:42:46', '2025-06-13 21:42:46');
INSERT INTO `addresses` VALUES (15, 27, '红牛', '13122344432', '吉林', '吉林', '二道', '第二条胡同', NULL, 0, '2025-06-15 19:54:01', '2025-06-15 19:54:01');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `level` tinyint NULL DEFAULT 1 COMMENT '分类层级',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '生鲜食品', 0, 1, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (2, '日用百货', 0, 1, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (3, '母婴用品', 0, 1, 3, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (4, '数码家电', 0, 1, 4, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (5, '服装鞋帽', 0, 1, 5, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (6, '家居用品', 0, 1, 6, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (7, '图书文具', 0, 1, 7, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (8, '运动户外', 0, 1, 8, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (9, '蔬菜', 1, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (10, '水果', 1, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (11, '肉类', 1, 2, 3, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (12, '海鲜', 1, 2, 4, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (13, '清洁用品', 2, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (14, '纸品', 2, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (15, '奶粉', 3, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (16, '纸尿裤', 3, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (17, '手机数码', 4, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (18, '家用电器', 4, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (19, '男装', 5, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (20, '女装', 5, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (21, '厨房用品', 6, 2, 1, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `categories` VALUES (22, '卧室用品', 6, 2, 2, NULL, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');

-- ----------------------------
-- Table structure for group_leaders
-- ----------------------------
DROP TABLE IF EXISTS `group_leaders`;
CREATE TABLE `group_leaders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '团长ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请理由',
  `community_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '社区名称',
  `community_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '社区地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述信息',
  `longitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `service_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务区域',
  `commission_rate` decimal(5, 2) NULL DEFAULT 5.00 COMMENT '佣金比例(%)',
  `status` enum('PENDING','APPROVED','REJECTED','SUSPENDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `approve_time` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_location`(`longitude` ASC, `latitude` ASC) USING BTREE,
  CONSTRAINT `group_leaders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团长表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_leaders
-- ----------------------------
INSERT INTO `group_leaders` VALUES (1, 3, '团长1', '13144234456', NULL, NULL, NULL, NULL, NULL, '望京花园社区', '北京市朝阳区望京花园小区', NULL, 116.4851000, 39.9950000, '望京地区', 5.50, 'APPROVED', '2025-06-13 18:24:22', '2024-01-15 10:30:00', '2025-06-13 18:24:22', '2025-06-15 15:51:33');
INSERT INTO `group_leaders` VALUES (2, 4, '团长2', '13144234456', NULL, NULL, NULL, NULL, NULL, '回龙观东区', '北京市昌平区回龙观东区', NULL, 116.3267000, 40.0742000, '回龙观地区', 6.00, 'APPROVED', '2025-06-13 18:24:22', '2024-01-20 14:20:00', '2025-06-13 18:24:22', '2025-06-15 15:51:34');
INSERT INTO `group_leaders` VALUES (3, 7, '团长5', '13144234456', NULL, NULL, NULL, NULL, NULL, '浦东花园', '上海市浦东新区花园小区', NULL, 121.5057000, 31.2453000, '浦东新区', 5.00, 'REJECTED', '2025-06-13 18:24:22', NULL, '2025-06-13 18:24:22', '2025-06-15 16:11:00');
INSERT INTO `group_leaders` VALUES (4, 8, '团长6', '13144234456', NULL, NULL, NULL, NULL, NULL, '南山科技园', '深圳市南山区科技园', NULL, 113.9547000, 22.5431000, '南山区', 5.80, 'APPROVED', '2025-06-13 18:24:22', '2024-02-01 09:15:00', '2025-06-13 18:24:22', '2025-06-15 15:51:35');
INSERT INTO `group_leaders` VALUES (5, 11, '团长3', '13144234456', NULL, NULL, NULL, NULL, NULL, '阳光花园社区', '广东省深圳市南山区阳光花园小区', '负责阳光花园社区的团购配送服务', 114.0579000, 22.5431000, NULL, 6.00, 'APPROVED', '2025-06-13 21:42:46', '2024-01-20 10:30:00', '2025-06-13 21:42:46', '2025-06-15 15:51:35');
INSERT INTO `group_leaders` VALUES (6, 12, '团长4', '13144234456', NULL, NULL, NULL, NULL, NULL, '绿城小区', '浙江省杭州市西湖区绿城小区', '负责绿城小区的团购配送服务', 120.1551000, 30.2741000, NULL, 5.50, 'APPROVED', '2025-06-13 21:42:46', '2024-01-22 14:15:00', '2025-06-13 21:42:46', '2025-06-15 15:51:36');
INSERT INTO `group_leaders` VALUES (8, 2, '张三', '13144445567', '222334556677665567', '长春', '长春市', '测试测试是', '测试测试是测试测试测试测试测试从', '长春长春市', '测试测试是', '测试测试是测试测试测试测试测试从', NULL, NULL, NULL, 5.00, 'APPROVED', '2025-06-15 20:14:25', NULL, '2025-06-15 20:14:25', '2025-06-16 15:38:21');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单详情ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `original_price` decimal(10, 2) NOT NULL COMMENT '原价',
  `group_price` decimal(10, 2) NOT NULL COMMENT '团购价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_order_product`(`order_id` ASC, `product_id` ASC) USING BTREE,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, '新鲜有机白菜', '/images/products/cabbage.jpg', 8.00, 6.50, 2, 13.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (2, 1, 7, '红富士苹果', '/images/products/apple.jpg', 12.00, 9.80, 1, 9.80, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (3, 1, 17, '洗洁精', '/images/products/detergent.jpg', 15.00, 12.00, 1, 12.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (4, 2, 3, '新鲜土豆', '/images/products/potato.jpg', 5.00, 3.80, 5, 19.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (5, 2, 9, '甜橙', '/images/products/orange.jpg', 10.00, 8.00, 2, 16.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (6, 2, 13, '新鲜猪肉', '/images/products/pork.jpg', 25.00, 22.00, 1, 22.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (7, 3, 5, '精选西红柿', '/images/products/tomato.jpg', 9.00, 7.20, 3, 21.60, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (8, 3, 11, '进口奇异果', '/images/products/kiwi.jpg', 18.00, 15.00, 2, 30.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (9, 3, 15, '新鲜牛肉', '/images/products/beef.jpg', 45.00, 38.00, 2, 76.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (10, 4, 2, '精品胡萝卜', '/images/products/carrot.jpg', 6.00, 4.80, 3, 14.40, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (11, 4, 8, '新鲜香蕉', '/images/products/banana.jpg', 8.00, 6.50, 2, 13.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (12, 5, 4, '有机菠菜', '/images/products/spinach.jpg', 7.00, 5.50, 3, 16.50, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (13, 5, 10, '新疆哈密瓜', '/images/products/melon.jpg', 15.00, 12.50, 2, 25.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (14, 5, 18, '洗衣液', '/images/products/laundry.jpg', 25.00, 20.00, 1, 20.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (15, 6, 6, '新鲜黄瓜', '/images/products/cucumber.jpg', 4.50, 3.60, 5, 18.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (16, 6, 12, '新鲜草莓', '/images/products/strawberry.jpg', 25.00, 20.00, 3, 60.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (17, 6, 16, '新鲜羊肉', '/images/products/mutton.jpg', 35.00, 30.00, 3, 90.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (18, 7, 1, '新鲜有机白菜', '/images/products/cabbage.jpg', 8.00, 6.50, 3, 19.50, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (19, 7, 14, '土鸡蛋', '/images/products/eggs.jpg', 18.00, 15.00, 2, 30.00, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (20, 8, 7, '红富士苹果', '/images/products/apple.jpg', 12.00, 9.80, 4, 39.20, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (21, 8, 19, '多功能清洁剂', '/images/products/cleaner.jpg', 18.00, 14.50, 3, 43.50, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (22, 8, 20, '厨房清洁剂', '/images/products/kitchen_cleaner.jpg', 12.00, 9.80, 2, 19.60, '2025-06-13 18:24:22');
INSERT INTO `order_items` VALUES (23, 9, 2, '精品胡萝卜', '/images/products/carrot.jpg', 6.00, 4.80, 3, 14.40, NULL);
INSERT INTO `order_items` VALUES (24, 9, 1, '新鲜有机白菜', '/images/products/cabbage.jpg', 8.00, 6.50, 2, 13.00, NULL);
INSERT INTO `order_items` VALUES (25, 9, 7, '红富士苹果', '/images/products/apple.jpg', 12.00, 9.80, 1, 9.80, NULL);
INSERT INTO `order_items` VALUES (26, 9, 13, '新鲜猪肉', '/images/products/pork.jpg', 25.00, 22.00, 3, 66.00, NULL);
INSERT INTO `order_items` VALUES (59, 27, 2, '精品胡萝卜', '/images/products/carrot.jpg', 6.00, 4.80, 9, 43.20, NULL);
INSERT INTO `order_items` VALUES (60, 27, 1, '新鲜有机白菜', '/images/products/cabbage.jpg', 8.00, 6.50, 9, 58.50, NULL);
INSERT INTO `order_items` VALUES (121, 50, 2, '精品胡萝卜', '/images/products/carrot.jpg', 6.00, 4.80, 6, 28.80, NULL);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `group_leader_id` bigint NULL DEFAULT NULL COMMENT '团长ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `commission_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '佣金金额',
  `status` enum('PENDING_PAYMENT','PAID','CONFIRMED','SHIPPED','DELIVERED','COMPLETED','CANCELLED','REFUNDED','REFUND_PENDING','REFUND_REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `payment_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `delivery_time` timestamp NULL DEFAULT NULL COMMENT '收货时间',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `refund_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款备注',
  `refund_apply_time` timestamp NULL DEFAULT NULL COMMENT '退款申请时间',
  `refund_process_time` timestamp NULL DEFAULT NULL COMMENT '退款处理时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款完成时间',
  `rating` tinyint NULL DEFAULT NULL COMMENT '评分(1-5分)',
  `review_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `review_time` timestamp NULL DEFAULT NULL COMMENT '评价时间',
  `logistics_company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流公司',
  `tracking_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流单号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_group_leader_id`(`group_leader_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_status_time`(`user_id` ASC, `status` ASC, `created_at` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`group_leader_id`) REFERENCES `group_leaders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'GB202401150001', 2, 1, 45.60, 5.60, 40.00, 2.20, 'COMPLETED', '微信支付', '2024-01-15 14:30:00', NULL, NULL, '测试用户', '13800138001', '北京市朝阳区望京SOHO T1座1001室', '请在工作日配送', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (2, 'GB202401160002', 7, 2, 78.50, 8.50, 70.00, 4.20, 'SHIPPED', '支付宝', '2024-01-16 10:15:00', NULL, NULL, '普通用户1', '13800138006', '上海市浦东新区世纪大道100号', '', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (3, 'GB202401170003', 8, 4, 156.80, 16.80, 140.00, 8.12, 'PAID', '微信支付', '2024-01-17 16:45:00', NULL, NULL, '普通用户2', '13800138007', '广东省深圳市南山区科技园南区深南大道9999号', '需要发票', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (4, 'GB202401180004', 2, 1, 32.40, 2.40, 30.00, 1.65, 'DELIVERED', '支付宝', '2024-01-18 09:20:00', NULL, NULL, '测试用户', '13800138001', '北京市海淀区中关村大街1号', '', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (5, 'GB202401190005', 7, 2, 89.60, 9.60, 80.00, 4.80, 'CONFIRMED', '微信支付', '2024-01-19 13:10:00', NULL, NULL, '普通用户1', '13800138006', '上海市浦东新区世纪大道100号', '加急配送', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (6, 'GB202401200006', 8, 4, 234.50, 24.50, 210.00, 12.18, 'PENDING_PAYMENT', '', NULL, NULL, NULL, '普通用户2', '13800138007', '广东省深圳市南山区科技园南区深南大道9999号', '', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (7, 'GB202401210007', 2, 1, 67.20, 7.20, 60.00, 3.30, 'CANCELLED', '', NULL, NULL, NULL, '测试用户', '13800138001', '北京市朝阳区望京SOHO T1座1001室', '临时取消', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (8, 'GB202401220008', 7, 2, 123.80, 13.80, 110.00, 6.60, 'REFUNDED', '微信支付', '2024-01-22 11:30:00', NULL, NULL, '普通用户1', '13800138006', '上海市浦东新区世纪大道100号', '商品有问题', '2025-06-13 18:24:22', '2025-06-13 18:24:22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (9, 'GB202506131932180001', 2, NULL, 103.20, 0.00, 103.20, 0.00, 'PAID', 'alipay', '2025-06-13 20:47:24', NULL, NULL, '测试用户', '13800138001', '北京市 朝阳区 望京街道 望京SOHO T1座1001室', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (27, 'GB202506132056490002', 3, NULL, 101.70, 0.00, 101.70, 0.00, 'PAID', 'bank', '2025-06-13 20:56:55', NULL, NULL, '张团长', '13800138002', '北京市 丰台区 方庄街道 方庄芳城园一区15号楼', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (28, 'GB202406140001', 9, 1, 196.00, 20.00, 176.00, 10.56, 'COMPLETED', '微信支付', '2024-06-14 10:30:00', NULL, NULL, '测试用户9', '13900001001', '北京市朝阳区望京SOHO T1座1001室', '供应商5测试订单1', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (29, 'GB202406140002', 10, 2, 105.00, 10.00, 95.00, 5.70, 'SHIPPED', '支付宝', '2024-06-14 14:20:00', NULL, NULL, '测试用户10', '13900001002', '上海市浦东新区世纪大道100号', '供应商5测试订单2', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (30, 'GB202406140003', 11, 3, 150.00, 15.00, 135.00, 8.10, 'DELIVERED', '微信支付', '2024-06-14 16:45:00', NULL, NULL, '测试用户11', '13900001003', '广东省深圳市南山区科技园南区深南大道9999号', '供应商5测试订单3', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (31, 'GB202406140004', 12, 4, 84.00, 8.00, 76.00, 4.56, 'COMPLETED', '支付宝', '2024-06-14 18:10:00', NULL, NULL, '测试用户12', '13900001004', '浙江省杭州市西湖区文三路100号', '供应商5测试订单4', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (32, 'GB202406140005', 9, 1, 256.00, 25.00, 231.00, 13.86, 'CANCELLED', '微信支付', '2024-06-14 11:15:00', NULL, NULL, '测试用户9', '13900001001', '北京市朝阳区望京SOHO T1座1001室', '供应商6测试订单1', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (33, 'GB202406140006', 10, 2, 183.00, 18.00, 165.00, 9.90, 'SHIPPED', '支付宝', '2024-06-14 15:30:00', NULL, NULL, '测试用户10', '13900001002', '上海市浦东新区世纪大道100号', '供应商6测试订单2', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (34, 'GB202406140007', 11, 3, 110.00, 11.00, 99.00, 5.94, 'DELIVERED', '微信支付', '2024-06-14 17:20:00', NULL, NULL, '测试用户11', '13900001003', '广东省深圳市南山区科技园南区深南大道9999号', '供应商6测试订单3', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (35, 'GB202406140008', 12, 4, 2948.00, 250.00, 2698.00, 161.88, 'PAID', '微信支付', '2024-06-14 12:00:00', NULL, NULL, '测试用户12', '13900001004', '浙江省杭州市西湖区文三路100号', '供应商7测试订单1', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (36, 'GB202406140009', 9, 1, 1568.00, 150.00, 1418.00, 85.08, 'DELIVERED', '支付宝', '2024-06-14 16:00:00', NULL, '2025-06-15 19:51:03', '测试用户9', '13900001001', '北京市朝阳区望京SOHO T1座1001室', '供应商7测试订单2', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (37, 'GB202406140010', 10, 2, 687.00, 60.00, 627.00, 37.62, 'PAID', '微信支付', '2024-06-14 13:30:00', NULL, NULL, '测试用户10', '13900001002', '上海市浦东新区世纪大道100号', '供应商8测试订单1', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (38, 'GB202406140011', 11, 3, 458.00, 40.00, 418.00, 25.08, 'DELIVERED', '支付宝', '2024-06-14 19:15:00', NULL, NULL, '测试用户11', '13900001003', '广东省深圳市南山区科技园南区深南大道9999号', '供应商8测试订单2', '2025-06-13 21:48:51', '2025-06-13 21:48:51', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (50, 'GB202506132202160001', 2, NULL, 28.80, 0.00, 28.80, 0.00, 'PAID', 'wechat', '2025-06-13 22:02:19', NULL, NULL, '测试用户', '13800138001', '北京市 朝阳区 望京街道 望京SOHO T1座1001室', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主图片',
  `images` json NULL COMMENT '商品图片集合',
  `original_price` decimal(10, 2) NOT NULL COMMENT '原价',
  `group_price` decimal(10, 2) NOT NULL COMMENT '团购价',
  `cost_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `min_group_size` int NULL DEFAULT 1 COMMENT '最小成团数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '件' COMMENT '单位',
  `weight` decimal(8, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `shelf_life` int NULL DEFAULT NULL COMMENT '保质期(天)',
  `storage_condition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '储存条件',
  `status` enum('DRAFT','PENDING','APPROVED','REJECTED','ON_SALE','OFF_SALE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT' COMMENT '状态',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '拒绝原因',
  `sales_count` int NULL DEFAULT 0 COMMENT '销量',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_group_price`(`group_price` ASC) USING BTREE,
  INDEX `idx_sales_count`(`sales_count` ASC) USING BTREE,
  INDEX `idx_category_status_price`(`category_id` ASC, `status` ASC, `group_price` ASC) USING BTREE,
  FULLTEXT INDEX `idx_name_desc`(`name`, `description`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 169 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 2, 9, '新鲜有机白菜', '产地直供，新鲜有机白菜，口感清脆甘甜，富含维生素', '/images/products/cabbage.jpg', NULL, 8.00, 6.50, 4.00, 70, 5, '斤', 1.00, 7, '冷藏保存', 'PENDING', NULL, 25, 124, '2025-06-13 18:24:22', '2025-06-16 08:29:24');
INSERT INTO `products` VALUES (2, 1, 9, '精品胡萝卜', '营养丰富的胡萝卜，富含胡萝卜素，适合各种烹饪', '/images/products/carrot.jpg', NULL, 6.00, 4.80, 3.00, 25, 3, '斤', 0.80, 14, '阴凉干燥处', 'PENDING', NULL, 18, 103, '2025-06-13 18:24:22', '2025-06-16 07:44:16');
INSERT INTO `products` VALUES (3, 3, 9, '新鲜土豆', '优质土豆，适合各种烹饪方式，口感绵软香甜', '/images/products/potato.jpg', NULL, 5.00, 3.80, 2.50, 147, 5, '斤', 1.20, 30, '阴凉干燥处', 'ON_SALE', NULL, 42, 200, '2025-06-13 18:24:22', '2025-06-16 08:29:27');
INSERT INTO `products` VALUES (4, 1, 9, '有机菠菜', '新鲜有机菠菜，叶片嫩绿，营养价值高', '/images/products/spinach.jpg', NULL, 7.00, 5.50, 3.50, 52, 3, '斤', 0.50, 3, '冷藏保存', 'ON_SALE', NULL, 15, 88, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (5, 2, 9, '精选西红柿', '自然成熟西红柿，酸甜可口，维生素C丰富', '/images/products/tomato.jpg', NULL, 9.00, 7.20, 4.80, 87, 4, '斤', 0.60, 10, '常温保存', 'ON_SALE', NULL, 33, 156, '2025-06-13 18:24:22', '2025-06-16 08:29:29');
INSERT INTO `products` VALUES (6, 1, 9, '新鲜黄瓜', '脆嫩黄瓜，水分充足，适合生食和烹饪', '/images/products/cucumber.jpg', NULL, 4.50, 3.60, 2.20, 113, 5, '斤', 0.40, 7, '冷藏保存', 'ON_SALE', NULL, 28, 135, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (7, 1, 10, '红富士苹果', '脆甜可口的红富士苹果，产地山东，果形端正', '/images/products/apple.jpg', NULL, 12.00, 9.80, 6.00, 52, 3, '斤', 0.80, 60, '阴凉通风处', 'ON_SALE', NULL, 35, 180, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (8, 1, 10, '新鲜香蕉', '进口香蕉，香甜软糯，富含钾元素', '/images/products/banana.jpg', NULL, 8.00, 6.50, 4.50, 40, 2, '斤', 0.60, 7, '常温保存', 'ON_SALE', NULL, 28, 150, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (9, 4, 10, '甜橙', '汁多味甜的新鲜橙子，维生素C含量丰富', '/images/products/orange.jpg', NULL, 10.00, 8.00, 5.00, 70, 3, '斤', 0.70, 21, '阴凉通风处', 'ON_SALE', NULL, 22, 110, '2025-06-13 18:24:22', '2025-06-16 08:29:32');
INSERT INTO `products` VALUES (10, 1, 10, '新疆哈密瓜', '新疆特产哈密瓜，香甜多汁，口感独特', '/images/products/melon.jpg', NULL, 15.00, 12.50, 8.00, 30, 2, '个', 2.50, 14, '阴凉通风处', 'ON_SALE', NULL, 12, 76, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (11, 3, 10, '进口奇异果', '新西兰进口奇异果，酸甜可口，营养丰富', '/images/products/kiwi.jpg', NULL, 18.00, 15.00, 10.00, 45, 3, '斤', 0.50, 14, '冷藏保存', 'ON_SALE', NULL, 19, 99, '2025-06-13 18:24:22', '2025-06-16 08:29:33');
INSERT INTO `products` VALUES (12, 1, 10, '新鲜草莓', '当季新鲜草莓，香甜多汁，维生素含量高', '/images/products/strawberry.jpg', NULL, 25.00, 20.00, 15.00, 25, 2, '斤', 0.30, 3, '冷藏保存', 'ON_SALE', NULL, 8, 45, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (13, 2, 11, '新鲜猪肉', '优质猪肉，肉质鲜美，适合各种烹饪方式', '/images/products/pork.jpg', NULL, 25.00, 22.00, 18.00, 27, 2, '斤', 1.00, 3, '冷藏保存', 'ON_SALE', NULL, 15, 85, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (14, 4, 11, '土鸡蛋', '农家散养土鸡蛋，营养丰富，蛋黄饱满', '/images/products/eggs.jpg', NULL, 18.00, 15.00, 12.00, 50, 2, '斤', 0.60, 30, '冷藏保存', 'ON_SALE', NULL, 38, 160, '2025-06-13 18:24:22', '2025-06-16 08:29:36');
INSERT INTO `products` VALUES (15, 2, 11, '新鲜牛肉', '优质牛肉，肉质紧实，蛋白质含量高', '/images/products/beef.jpg', NULL, 45.00, 38.00, 30.00, 20, 2, '斤', 1.00, 3, '冷冻保存', 'ON_SALE', NULL, 12, 67, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (16, 2, 11, '新鲜羊肉', '内蒙古羊肉，肉质鲜嫩，无膻味', '/images/products/mutton.jpg', NULL, 35.00, 30.00, 24.00, 15, 2, '斤', 1.00, 3, '冷冻保存', 'ON_SALE', NULL, 8, 42, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (17, 1, 13, '洗洁精', '强效去污洗洁精，温和不伤手，去污力强', '/images/products/detergent.jpg', NULL, 15.00, 12.00, 8.00, 100, 5, '瓶', 0.50, 1095, '常温保存', 'ON_SALE', NULL, 45, 220, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (18, 2, 13, '洗衣液', '深层清洁洗衣液，留香持久，护色护型', '/images/products/laundry.jpg', NULL, 25.00, 20.00, 15.00, 80, 3, '瓶', 1.00, 1095, '常温保存', 'ON_SALE', NULL, 32, 140, '2025-06-13 18:24:22', '2025-06-16 08:29:38');
INSERT INTO `products` VALUES (19, 2, 13, '多功能清洁剂', '家用多功能清洁剂，一瓶多用，清洁力强', '/images/products/cleaner.jpg', NULL, 18.00, 14.50, 10.00, 60, 4, '瓶', 0.60, 1095, '常温保存', 'ON_SALE', NULL, 23, 112, '2025-06-13 18:24:22', '2025-06-16 08:29:40');
INSERT INTO `products` VALUES (20, 1, 13, '厨房清洁剂', '专业厨房清洁剂，去油污效果佳', '/images/products/kitchen_cleaner.jpg', NULL, 12.00, 9.80, 6.50, 90, 5, '瓶', 0.40, 1095, '常温保存', 'ON_SALE', NULL, 28, 134, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `products` VALUES (166, 6, 9, '测试商品1', '测试商品描述1', '/images/products/test1.jpg', NULL, 10.00, 8.00, 5.00, 100, 5, '个', NULL, NULL, NULL, 'OFF_SALE', NULL, 0, 1, '2025-06-16 08:48:50', '2025-06-16 08:48:50');
INSERT INTO `products` VALUES (167, 6, 10, '测试商品2', '测试商品描述2', '/images/products/test2.jpg', NULL, 15.00, 12.00, 8.00, 80, 3, '斤', NULL, NULL, NULL, 'OFF_SALE', NULL, 0, 0, '2025-06-16 08:48:50', '2025-06-16 08:48:50');
INSERT INTO `products` VALUES (168, 6, 11, '测试商品3', '测试商品描述3', '/images/products/test3.jpg', NULL, 25.00, 20.00, 15.00, 50, 2, '份', NULL, NULL, NULL, 'OFF_SALE', NULL, 0, 0, '2025-06-16 08:48:50', '2025-06-16 08:48:50');

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `shopping_cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `shopping_cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------
INSERT INTO `shopping_cart` VALUES (4, 7, 3, 5, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `shopping_cart` VALUES (5, 7, 9, 2, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `shopping_cart` VALUES (6, 7, 17, 1, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `shopping_cart` VALUES (7, 8, 5, 3, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `shopping_cart` VALUES (8, 8, 11, 2, '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `shopping_cart` VALUES (35, 1, 3, 3, '2025-06-17 15:21:01', '2025-06-17 15:21:01');

-- ----------------------------
-- Table structure for suppliers
-- ----------------------------
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司名称',
  `business_license` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照号',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `company_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司地址',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公司描述',
  `status` enum('PENDING','APPROVED','REJECTED','SUSPENDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `approve_time` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_company_name`(`company_name` ASC) USING BTREE,
  CONSTRAINT `suppliers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of suppliers
-- ----------------------------
INSERT INTO `suppliers` VALUES (1, 5, '新鲜果蔬供应有限公司', '91110000MA001234XY', '王经理', '13800001001', '北京市朝阳区果蔬批发市场A区101号', '专业从事新鲜果蔬批发配送，产地直供，品质保证', 'APPROVED', '2025-06-13 18:24:22', '2024-01-10 16:45:00', '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `suppliers` VALUES (2, 6, '优质肉类食品公司', '91110000MA005678ZW', '赵经理', '13800001002', '天津市滨海新区肉类加工园区B栋', '提供优质肉类产品，严格质量控制，冷链配送', 'APPROVED', '2025-06-13 18:24:22', '2024-01-12 11:20:00', '2025-06-13 18:24:22', '2025-06-13 18:24:22');
INSERT INTO `suppliers` VALUES (3, 7, '智能数码科技有限公司', '91110000MA009876AB', '李经理', '13800001003', '深圳市南山区科技园数码大厦C座', '专业销售手机、电脑、智能家电等数码产品，品质保证', 'APPROVED', '2025-06-13 21:18:55', '2024-01-15 09:30:00', '2025-06-13 21:18:55', '2025-06-13 21:18:55');
INSERT INTO `suppliers` VALUES (4, 8, '时尚服饰贸易公司', '91110000MA005432CD', '张经理', '13800001004', '广州市天河区服装批发市场D区', '提供男女装、童装、鞋帽等时尚服饰，款式新颖', 'APPROVED', '2025-06-13 21:18:55', '2024-01-18 14:20:00', '2025-06-13 21:18:55', '2025-06-13 21:18:55');
INSERT INTO `suppliers` VALUES (6, 9, '测试供应商公司', '91110000MA009999XX', '测试用户9', '13900001001', '北京市朝阳区望京SOHO T1座1001室', '测试供应商，用于系统功能测试', 'APPROVED', '2025-06-16 08:48:50', '2024-06-16 08:00:00', '2025-06-16 08:48:50', '2025-06-16 08:48:50');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `role` enum('USER','GROUP_LEADER','SUPPLIER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT '角色',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138000', 'admin@groupbuy.com', '系统管理员', NULL, 1, '1985-01-15', 1, 'ADMIN', '2025-06-13 18:24:22', '2025-06-15 15:39:57');
INSERT INTO `users` VALUES (2, 'test', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138001', 'test@groupbuy.com', '测试用户', NULL, 2, '1990-05-20', 1, 'USER', '2025-06-13 18:24:22', '2025-06-15 15:37:54');
INSERT INTO `users` VALUES (3, 'leader1', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138002', 'leader1@groupbuy.com', '张团长', NULL, 1, '1988-03-10', 1, 'GROUP_LEADER', '2025-06-13 18:24:22', '2025-06-15 15:39:59');
INSERT INTO `users` VALUES (4, 'leader2', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138003', 'leader2@groupbuy.com', '李团长', NULL, 2, '1992-07-25', 1, 'GROUP_LEADER', '2025-06-13 18:24:22', '2025-06-15 15:40:00');
INSERT INTO `users` VALUES (5, 'supplier1', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138004', 'supplier1@groupbuy.com', '王供应商', NULL, 1, '1980-12-08', 1, 'SUPPLIER', '2025-06-13 18:24:22', '2025-06-15 15:40:01');
INSERT INTO `users` VALUES (6, 'supplier2', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138005', 'supplier2@groupbuy.com', '赵供应商', NULL, 2, '1983-09-18', 1, 'SUPPLIER', '2025-06-13 18:24:22', '2025-06-15 15:40:01');
INSERT INTO `users` VALUES (7, 'user1', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138006', 'user1@groupbuy.com', '普通用户1', NULL, 1, '1995-11-30', 1, 'USER', '2025-06-13 18:24:22', '2025-06-15 15:40:02');
INSERT INTO `users` VALUES (8, 'user2', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800138007', 'user2@groupbuy.com', '普通用户2', NULL, 2, '1993-04-12', 1, 'USER', '2025-06-13 18:24:22', '2025-06-15 15:40:03');
INSERT INTO `users` VALUES (9, 'supplier7', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800001003', 'supplier7@example.com', '李经理', NULL, 0, NULL, 1, 'SUPPLIER', '2025-06-13 21:28:18', '2025-06-15 15:40:04');
INSERT INTO `users` VALUES (10, 'supplier8', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13800001004', 'supplier8@example.com', '张经理', NULL, 0, NULL, 1, 'SUPPLIER', '2025-06-13 21:28:18', '2025-06-15 15:40:04');
INSERT INTO `users` VALUES (11, 'user9', '', '13900001001', 'user9@example.com', '测试用户19', NULL, 0, NULL, 1, 'USER', '2025-06-13 21:42:46', '2025-06-16 05:41:23');
INSERT INTO `users` VALUES (12, 'user10', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900001002', 'user10@example.com', '测试用户10', NULL, 0, NULL, 1, 'USER', '2025-06-13 21:42:46', '2025-06-15 15:40:06');
INSERT INTO `users` VALUES (13, 'user11', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900001003', 'user11@example.com', '测试用户11', NULL, 0, NULL, 1, 'USER', '2025-06-13 21:42:46', '2025-06-15 15:40:07');
INSERT INTO `users` VALUES (14, 'user12', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900001004', 'user12@example.com', '测试用户12', NULL, 0, NULL, 1, 'USER', '2025-06-13 21:42:46', '2025-06-15 15:40:08');
INSERT INTO `users` VALUES (15, 'leader3', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900002001', 'leader3@example.com', '团长3', NULL, 0, NULL, 1, 'GROUP_LEADER', '2025-06-13 21:42:46', '2025-06-15 15:40:09');
INSERT INTO `users` VALUES (16, 'leader4', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900002002', 'leader4@example.com', '团长4', NULL, 0, NULL, 1, 'GROUP_LEADER', '2025-06-13 21:42:46', '2025-06-15 15:40:10');
INSERT INTO `users` VALUES (17, 'admin2', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13900003001', 'admin2@example.com', '管理员2', NULL, 0, NULL, 1, 'ADMIN', '2025-06-13 21:42:46', '2025-06-15 15:40:10');
INSERT INTO `users` VALUES (18, 'usertest', '$2a$10$EtvDcEysjbrGYxfn6GqiQOYbH/HBV8QhMdE9iayHdOcoYdDoVC.1a', '13144335567', '789987764@qq.om', '刘关张', NULL, 0, NULL, 1, 'USER', NULL, '2025-06-16 08:29:08');
INSERT INTO `users` VALUES (27, 'testuser2', '$2a$10$T.KQ0lRclPCfS/igQK4P7.IqbPt7jjo95Rm4BzjVewba0qqfYxpDO', '13144323345', 'qqeerer@qq.com', '红牛', NULL, 0, NULL, 1, 'USER', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;

# 社区团购系统

一个基于 Spring Boot + Vue 3 的现代化社区团购平台，软件开发课程的大作业，支持用户购买、团长管理、供应商入驻和管理员后台等完整功能。

## 🚀 项目简介

社区团购系统是一个完整的电商解决方案，专为社区团购业务设计。系统支持多角色用户（普通用户、团长、供应商、管理员），提供商品管理、订单处理、支付集成、数据统计等核心功能。

### 主要特点

- 🏪 **多角色支持**：用户、团长、供应商、管理员四种角色
- 📱 **响应式设计**：支持PC端和移动端访问
- 🔐 **安全认证**：JWT token认证，角色权限控制
- 💰 **订单管理**：完整的订单流程，支持多种支付方式
- 📊 **数据统计**：丰富的数据分析和报表功能
- 🚚 **物流管理**：地址管理，配送状态跟踪

## 🛠 技术栈

### 后端技术

- **框架**: Spring Boot 3.1.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: MyBatis Plus 3.5.5
- **安全**: Spring Security + JWT
- **构建工具**: Maven
- **Java版本**: JDK 21

### 前端技术

- **框架**: Vue 3.3.4
- **路由**: Vue Router 4.2.4
- **状态管理**: Pinia 2.1.6
- **UI组件**: Element Plus 2.3.8
- **HTTP客户端**: Axios 1.4.0
- **构建工具**: Vite 4.4.5

### 开发工具

- **版本控制**: Git
- **API测试**: 支持Postman/Apifox
- **数据库管理**: Navicat/DataGrip

## ✨ 功能特性

### 用户端功能

- ✅ 用户注册/登录
- ✅ 商品浏览/搜索
- ✅ 购物车管理
- ✅ 订单创建/支付
- ✅ 地址管理
- ✅ 订单历史查看
- ✅ 个人信息管理

### 团长功能

- ✅ 团长申请/审核
- ✅ 社区管理
- ✅ 订单管理
- ✅ 佣金统计
- ✅ 配送管理

### 供应商功能

- ✅ 供应商入驻
- ✅ 商品发布/管理
- ✅ 订单处理
- ✅ 库存管理
- ✅ 销售统计

### 管理员功能

- ✅ 用户管理
- ✅ 商品审核
- ✅ 订单管理
- ✅ 分类管理
- ✅ 团长审核
- ✅ 数据统计
- ✅ 系统设置

## 🏗 系统架构

```
社区团购系统
├── 前端 (Vue 3 + Element Plus)
│   ├── 用户端界面
│   ├── 管理后台
│   └── 供应商后台
├── 后端 (Spring Boot)
│   ├── 控制层 (Controller)
│   ├── 服务层 (Service)
│   ├── 数据访问层 (Mapper)
│   └── 实体层 (Entity)
├── 数据库 (MySQL)
│   ├── 用户相关表
│   ├── 商品相关表
│   ├── 订单相关表
│   └── 系统配置表
└── 缓存 (Redis)
    ├── 用户会话
    ├── 商品缓存
    └── 系统配置


### 环境要求

- JDK 21+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+


部分截图：
![image](https://github.com/user-attachments/assets/d47b9b6c-8417-450a-bb79-831a75133d3d)
![image](https://github.com/user-attachments/assets/ed7b8b6d-9557-429d-ad21-960bbcbf807e)
![image](https://github.com/user-attachments/assets/a58bf545-24ff-42ab-9351-96474106e5ca)
![image](https://github.com/user-attachments/assets/33742c76-ed15-49ed-ac39-56e368520a57)
![image](https://github.com/user-attachments/assets/9891b515-6c30-40d5-a40c-e3e8fe02cfc6)
![image](https://github.com/user-attachments/assets/05e7a44a-d0d8-473c-b01b-34e3527781f4)

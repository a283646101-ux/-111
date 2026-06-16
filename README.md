# 中州养老管理系统 — 后端服务 🏥

> 为养老院量身定制的智慧养老管理平台后端服务，覆盖从参观到退住的完整业务生命周期。

---

## 📋 项目简介

中州养老管理系统是一个面向养老院的 **全生命周期管理平台**，涵盖来访参观、入住办理、在住服务、退住办理等核心业务流程。系统采用 **管理后台 + 家属小程序** 双端架构，满足养老院日常运营管理和家属远程关怀的双重需求。

团队 4 人，历时 5 个月从零交付。

---

## 🛠 技术栈

| 技术 | 用途 |
|------|------|
| **Spring Boot 2.7.4** | 核心框架，快速搭建 Web 项目 |
| **MyBatis** | 持久层框架，数据库对象关系映射 |
| **MySQL** | 关系型数据库，业务数据存储 |
| **Redis** | 缓存中间件，热点数据缓存加速 |
| **Druid** | 数据库连接池，连接管理与监控 |
| **Lombok** | 代码简化工具，自动生成 Getter/Setter 等 |
| **Swagger / Knife4j** | API 文档自动生成与在线调试 |
| **JWT** | 身份认证，无状态 Token 鉴权 |
| **WebSocket** | 实时通信，设备告警推送 |
| **华为云 IoT** | 物联网设备接入与数据采集 |
| **阿里云 OSS** | 文件与图片云存储 |
| **Maven** | 项目构建与依赖管理 |

---

## 📁 项目结构（多模块架构）

```
zzyl/                          # 项目根目录
├── zzyl-common/               # 公共模块
│   ├── base/                  # 基础类（BaseEntity、BaseDto、BaseVo）
│   ├── config/                # 配置类（MyBatis、Swagger、OSS 等）
│   ├── constant/              # 常量定义
│   ├── enums/                 # 枚举类
│   ├── exception/             # 异常处理
│   ├── utils/                 # 工具类（JwtUtil、StringUtils 等）
│   └── vo/                    # 视图对象
├── zzyl-security/             # 安全模块
├── zzyl-service/              # 业务模块（核心业务逻辑）
│   ├── entity/                # 实体类（对应数据库表）
│   ├── mapper/                # 数据访问层
│   ├── service/               # 业务逻辑层
│   └── vo/                    # 业务视图对象
├── zzyl-web/                  # Web 模块（控制器 + 启动类）
│   ├── controller/            # 控制器（接收 HTTP 请求）
│   │   ├── customer/          # C 端（家属端）接口
│   │   ├── BedController.java # 床位管理
│   │   ├── ElderController.java # 老人管理
│   │   └── LoginController.java # 登录认证
│   └── ZzylApplication.java   # 启动类
├── springcache-demo/          # 缓存演示模块
├── mybatis-gen/               # MyBatis 代码生成器
├── docs/                      # 项目文档
├── scripts/                   # 部署脚本
├── skills/                    # 技能相关
└── pom.xml                    # 父工程 Maven 配置
```

### 三层架构设计

```
用户请求 → Controller（控制器）→ Service（业务层）→ Mapper（数据层）→ 数据库
                ↓
            返回 JSON 结果给前端
```

**优点：** 职责清晰、便于维护、支持多人协作、代码复用性高。

---

## 🚀 快速启动

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis（可选，缓存功能）

### 启动步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS zzyl CHARACTER SET utf8mb4;"

# 2. 导入 SQL（如有）
# mysql -u root -p zzyl < zzyl.sql

# 3. 修改配置
# 编辑 zzyl-web/src/main/resources/application.yml
# 修改数据库连接、JWT 密钥、OSS 配置等

# 4. 编译打包
cd zzyl && mvn clean package -DskipTests

# 5. 启动服务
java -jar zzyl-web/target/zzyl-web.jar
```

启动后访问 API 文档：`http://localhost:9995/doc.html`

---

## 🔧 核心功能模块

| 模块 | 说明 |
|------|------|
| **来访管理** | 参观预约、来访登记、访客追踪 |
| **入住管理** | 老人信息录入、床位分配、合同签订 |
| **在住管理** | 日常照护、健康监测、护理记录 |
| **服务管理** | 护理项目管理、排班、服务计费 |
| **财务管理** | 费用缴纳、账单生成、收支统计 |
| **退住管理** | 退住申请、费用结算、床位释放 |
| **设备监测** | IoT 设备接入、异常告警、实时推送 |
| **权限管理** | RBAC 多角色权限、JWT 无状态认证 |

---

## 📊 项目亮点

### 1. RBAC 权限系统
多角色（管理员、护理员、家属）细粒度权限控制，采用 JWT 无状态认证，权限变更即时生效。

### 2. IoT 设备接入
对接华为云 IoT 平台，接入睡眠监测带、烟雾报警器、定位手表等多设备，异常数据通过 WebSocket 实时推送（端到端延迟 < 1s）。

### 3. 缓存优化
Spring Cache + Redis 两级缓存策略，家属端老人信息查询响应时间从 450ms 降至 80ms（提升 82%）。

### 4. 代码规范
- RESTful API 接口设计
- Entity / DTO / VO 三层分离
- Swagger 自动生成接口文档

---

## 📝 学习资料

- [`项目学习.md`](./项目学习.md) — 项目详解与开发指南（含三层架构解析、注解说明、请求流程示例）

---

## 🏆 成果

- 后台核心接口 QPS 200+
- 数据录入错误率从 15% 降至 1% 以下
- 系统按期交付并投入内部试用

---

> *本项目为 Java 后端学习项目，基于 RuoYi 框架二次开发，用于学习 Spring Boot、MyBatis 等主流 Java 技术栈。*

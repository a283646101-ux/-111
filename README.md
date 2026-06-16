# 中州养老管理系统 — 后端服务 🏥

> 智慧养老全生命周期管理平台 · Spring Boot 多模块企业级项目

---

## 📋 项目概述

中州养老管理系统是一套面向养老院的 **全生命周期管理平台**，覆盖 **来访参观 → 入住办理 → 在住服务 → 退住办理** 等核心业务闭环。采用 **管理后台 + 家属小程序** 双端架构，融合 IoT 物联网设备实时监测、WebSocket 主动推送告警、Redis 缓存加速等企业级技术方案。

- **团队规模**：4 人 | **研发周期**：5 个月 | **状态**：已交付内部试用

---

## ⚙️ 技术架构

### 核心依赖

| 技术 | 版本 | 用途 | 面试高频考点 |
|------|------|------|-------------|
| Spring Boot | 2.7.4 | 自动配置 / IoC 容器 / 约定优于配置 | 启动原理、自动配置源码 |
| MyBatis + Spring Boot Starter | 2.2.0 | 持久层 ORM + 动态 SQL | #{} 和 ${} 区别、一级/二级缓存 |
| MySQL 8.0 + Druid | 8.0.19 / 1.2.1 | 关系型数据库 + 连接池监控 | 索引优化、慢查询、Druid 监控 |
| Redis + Redisson | - / 3.11.2 | 缓存中间件 + 分布式锁 | 缓存穿透/雪崩/击穿、Redisson 锁原理 |
| JWT (jjwt + java-jwt) | 0.9.1 / 3.8.1 | 无状态身份认证 | Token 结构、过期刷新、签名算法 |
| WebSocket (Spring原生) | - | 实时双向通信 | 与 HTTP 区别、心跳机制、集群方案 |
| Knife4j (Swagger) | 3.0.3 | API 文档自动生成与在线调试 | Swagger 注解、分组配置 |
| PageHelper Starter | 1.3.0 | MyBatis 物理分页插件 | 分页原理、方言适配 |
| Hutool | 5.8.0.M3 | Java 工具集（BeanUtil/JSON/Bcrypt） |  |
| Lombok | 1.18.38 | 编译期代码生成 | @Data/@Builder 原理 |
| 华为云 IoT + 阿里云 OSS | - | 设备接入 + 文件云存储 | IoT 协议 MQTT/CoAP、OSS 签名 URL |
| BCrypt | - | 密码哈希加盐 | 与 MD5 区别、彩虹表防御 |
| Jackson + JSR310 | - | JSON 序列化 / LocalDateTime | Long 精度丢失、日期格式化 |
| XXL-Job | 2.3.0 | 分布式任务调度 | 分片策略、失败重试、路由策略 |
| Orika | 1.5.4 | 对象深拷贝 | 与 BeanUtils 区别、性能对比 |
| SnowflakeIdWorker | 自实现 | 分布式全局唯一 ID | 时钟回拨、位运算、Sequence |

### 模块依赖关系

```
zzyl (父工程 - Maven 多模块聚合)
├── zzyl-common          ← 公共基础层（工具类/配置/异常/常量/拦截器）
├── zzyl-security        ← 安全认证层（JWT / RBAC / 登录 / 菜单/角色/权限）
├── zzyl-service         ← 核心业务层（实体/Service/Mapper/VO/DTO）
└── zzyl-web             ← Web表现层（Controller / WebSocket / 启动类 / 定时任务）
```

---

## 🏗️ 架构亮点分析

### 1. 多模块 Maven 分层架构（DDD 分层思想）

```
┌─────────────────────────────────────────────────────────┐
│                    zzyl-web (Controller)                 │
│   接收 HTTP 请求、参数校验、WebSocket、定时任务        │
├─────────────────────────────────────────────────────────┤
│                 zzyl-service (Service)                   │
│    核心业务逻辑、事务管理、DTO/VO 转换、数据校验       │
├─────────────────────────────────────────────────────────┤
│                zzyl-security (Security)                  │
│       JWT 认证、RBAC 权限控制、登录/角色/资源管理       │
├─────────────────────────────────────────────────────────┤
│             zzyl-common (Common Infrastructure)          │
│   统一返回模型、全局异常处理、工具类、配置、拦截器     │
└─────────────────────────────────────────────────────────┘
```

**面试考点**：为什么用多模块？模块间依赖怎么管理？为什么要分层？（单一职责 / 可测试性 / 可维护性）

### 2. 统一返回模型 + 全局异常处理

`ResponseResult<T>` 统一了全系统响应格式，返回体包含 `code` / `msg` / `data` / `operationTime`。

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... },
  "operationTime": "2024-01-01 12:00:00"
}
```

`GlobalExceptionHandler` 使用 `@RestControllerAdvice` 全局拦截以下异常：
- **BaseException**：自定义业务异常，携带枚举错误码 `BasicEnum`
- **MethodArgumentNotValidException**：`@Validated` 参数校验失败 → 精准字段错误提示
- **BindException**：表单数据绑定失败
- **ConstraintViolationException**：方法级别参数约束违反
- **HttpMessageNotReadableException**：请求体 JSON 格式错误
- **RuntimeException / Exception**：兜底异常

**面试考点**：`@RestControllerAdvice` 与 `@ControllerAdvice` 区别、全局异常如何优雅处理、业务异常与系统异常的设计

### 3. JWT 无状态认证 + RBAC 权限模型（⭐ 核心亮点）

**用户认证流程（LoginServiceImpl）：**
1. 用户名查询用户 → 判空 / 数据状态
2. BCrypt 校验密码（`BCrypt.checkpw()`）
3. 查询用户资源权限列表 → 合并公共访问 URL → 去重
4. 构建 JWT Claims，存入用户信息 JSON
5. Token 过期前写入 Redis：`PUBLIC_ACCESS_URLS:{userId}` → 权限路径列表
6. 返回 `UserVo`（含 token）

**Token 拦截器（UserTokenInterceptor）：**
1. 从请求头解析 Token（支持 `Authorization: Bearer xxx` 或 `token` 头）
2. JWT 解析 → 获取 `currentUser` JSON → 反序列化 `UserVo`
3. `UserThreadLocal.setSubject()` + `UserThreadLocal.set()` 存入当前用户
4. 从 Redis 加载用户权限列表 → AntPathMatcher 路径匹配 → 鉴权
5. `afterCompletion` 中清除 ThreadLocal（防止内存泄漏）

**权限规则格式**：`GET/user/*` 或 `POST/user/**`

**面试考点**：
- JWT 结构（Header.Payload.Signature）和 signature 算法（HS256）
- JWT 和 Session 的区别？无状态认证优缺点？
- Token 过期如何处理？刷新 token 方案？
- RBAC 模型如何扩展为 ABAC？
- AntPathMatcher 路径匹配规则
- ThreadLocal 原理、内存泄漏风险及解决方案

### 4. MyBatis 拦截器实现字段自动填充（AutoFillInterceptor）

基于 MyBatis 插件机制，拦截 `Executor.update()` 方法，自动注入审计字段：

| SQL 操作 | 自动填充字段 |
|---------|-------------|
| INSERT | `createBy` / `createTime` / `updateTime` |
| UPDATE | `updateBy` / `updateTime` |
| 批量 INSERT | 遍历 List 逐条填充 |

用户 ID 从 `UserThreadLocal`（ThreadLocal 上下文）获取，支持管理端和客户端双通道。

**面试考点**：MyBatis 四大核心对象、Interceptor 插件原理、@Intercepts/@Signature 配置、MetaObject 反射

### 5. WebSocket 实时通信架构（自研组件，体现工程能力）

#### 架构组件

| 组件 | 职责 |
|------|------|
| `WebSocketConfig` | WebSocket 端点注册 + 容器参数（消息大小/空闲超时） |
| `WebSocketAuthHandshakeInterceptor` | 握手阶段 JWT 鉴权 + 用户身份注入 + Room 分配 |
| `RealtimeWebSocketHandler` | 消息收发核心处理器（文本/二进制/Pong） |
| `WsSessionRegistry` | 连接管理注册表（多级索引：SessionId / UserId / Room） |
| `WsMessageRouter` | 消息路由（单播/组播/广播） |
| `WsHeartbeatTask` | 定时 Ping + 空闲连接清理（@Scheduled） |
| `WsMetricsService` | 连接数/消息量/延迟/错误数 实时统计 |
| `WsMessage` / `WsBinaryEnvelope` | 消息协议（traceId/timestamp/type/metadata） |

#### 消息协议

| 消息类型 | 说明 |
|---------|------|
| `HEARTBEAT` | 心跳探测（15s 间隔，Ping/Pong） |
| `AUTH` | 身份确认 |
| `SUBSCRIBE / UNSUBSCRIBE` | Room 订阅/退订 |
| `ROOM` | 房间内组播 |
| `DIRECT` | 点对点单播 |
| `BROADCAST` | 全连接广播 |
| `ACK` | 确认回执 |
| `ERROR` | 错误通知 |

#### Session 多级索引
```
ConcurrentHashMap<SessionId, WsClientSession>
ConcurrentHashMap<UserId, Set<SessionId>>
ConcurrentHashMap<Room, Set<SessionId>>
```
支持：按用户查会话 / 按房间查会话 / 遍历全部会话

#### 空闲连接回收
定时扫描所有 Session，`lastSeenAt` 超过 `idleTimeoutMs`（默认 60s）的连接自动关闭。

**面试考点**：
- WebSocket 与 HTTP 长轮询/SSE 对比
- WebSocket 握手过程（101 Switching Protocols）
- 分布式 WebSocket 方案（Redis Pub/Sub / MQ 广播）
- ConcurrentHashMap 原理（分段锁/CAS）
- @Scheduled 定时任务原理（ThreadPoolTaskScheduler）
- AtomicLong vs LongAdder 选择（高并发计数）

### 6. Redis 缓存配置（Spring Cache 注解驱动）

```java
@Configuration
@EnableCaching
public class CacheConfig {
    // RedisCacheManager 配置
    // - TTL: 1小时
    // - Key序列化: StringRedisSerializer
    // - Value序列化: GenericJackson2JsonRedisSerializer
    // - 禁止缓存 null 值
    // - 缓存前缀: "zzyl:"
    // - 事务感知: transactionAware()
}
```

**面试考点**：
- `@Cacheable` / `@CachePut` / `@CacheEvict` 区别
- 缓存穿透/雪崩/击穿的解决方案
- GenericJackson2JsonRedisSerializer 的类信息存储
- 缓存前缀的作用（命名空间隔离）

### 7. Jackson 全局序列化配置

```java
// Long → String（解决 JS 精度丢失）
// BigInteger → String
// LocalDateTime → "yyyy-MM-dd HH:mm:ss"
// LocalDate → "yyyy-MM-dd"
// LocalTime → "HH:mm:ss"
```

**面试考点**：Long 在前端丢失精度原因、自定义序列化器、JSR310 时间序列化

### 8. 雪花算法（SnowflakeIdWorker，分布式 ID 生成）

分布式全局唯一 ID，1bit(符号) + 41bit(时间戳) + 5bit(数据中心) + 5bit(机器) + 12bit(序列号)。

**面试考点**：时钟回拨问题、位运算实现、QPS 推算（理论 409.6万/秒）

---

## 📊 项目成果与量化指标

| 指标 | 优化前 | 优化后 | 提升幅度 |
|------|--------|--------|---------|
| 家属端信息查询响应时间 | 450ms | 80ms | **↑ 82%** |
| 核心接口吞吐量 | - | QPS 200+ | 支撑日常运营 |
| 数据录入错误率 | 15% | < 1% | **↓ 93%** |
| WebSocket 消息延迟 | - | P99 < 50ms | 实时推送保障 |
| 项目交付周期 | - | 5 个月 | 按期交付 |

---

## 🔧 核心模块功能矩阵

| 模块 | 核心实体 | 关键接口 | 数据流向 |
|------|---------|---------|---------|
| **来访管理** | Visit | CRUD + 取消 + 分页 | Controller → Service → Mapper → MySQL |
| **床位管理** | Bed ↔ Room ↔ Floor | 嵌套查询 / 状态变更 | 多表联查 |
| **老人管理** | Elder(公民信息) | 身份证查重 / 床位绑定 | BeanUtil 拷贝 Entity→VO |
| **合同管理** | Contract | 入住/退住/续签 | 事务管理 |
| **护理项目** | NursingProject | 服务计划 / 执行记录 | 业务编排 |
| **IoT 监测** | DeviceData / AlertRule | WebSocket 实时推送 | 华为云 → MQ → WebSocket |
| **权限管理** | User / Role / Resource | RBAC 动态路由 | Redis 缓存权限路径 |
| **定时任务** | SystemTask | 日统计 / 健康检查 | @Scheduled |

---

## 🚀 快速启动

### 环境要求
- JDK 11+ / Maven 3.6+ / MySQL 5.7+ / Redis

### 启动步骤

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS zzyl CHARACTER SET utf8mb4;"

# 2. 修改配置
# 编辑各模块 resources 下 application.yml (数据库/JWT/Redis/IoT/OSS)

# 3. 编译打包
cd zzyl && mvn clean package -DskipTests

# 4. 启动服务
java -jar zzyl-web/target/zzyl-web.jar
```

启动后访问 API 文档：**[http://localhost:9995/doc.html](http://localhost:9995/doc.html)**（Knife4j 增强版）

---

## 📚 面试备战：常见技术追问

### Spring Boot 相关
- Spring Boot 自动配置原理？`@EnableAutoConfiguration` 如何加载 `spring.factories`？
- Spring IoC 容器启动流程？Bean 的生命周期？
- `@ConditionalOnMissingBean` 等条件注解的作用？
- Spring Boot 2.7.x 与 3.x 的差异？

### MyBatis 相关
- MyBatis 一级缓存和二级缓存区别？失效场景？
- `#{}` 和 `${}` 的区别？SQL 注入防护？
- MyBatis 插件原理？分页插件怎么实现的？
- MyBatis 的 `ResultMap` 高级映射（association/collection）？

### MySQL 相关
- 索引最左前缀原则？联合索引失效场景？
- MySQL 事务隔离级别？MVCC 实现原理？
- 慢查询优化思路？Explain 分析执行计划？

### Redis 相关
- Redis 缓存穿透/雪崩/击穿的区别和解决方案
- Redisson 分布式锁原理？WatchDog 机制？
- Redis 过期策略？内存淘汰策略？

### JWT & 安全
- JWT 的三段结构和签名过程？
- Token 泄漏怎么办？黑名单机制？
- RBAC 与 ABAC 区别？百万级权限怎么做？

### WebSocket 相关
- WebSocket 握手流程（101 状态码）？
- 分布式环境下 WebSocket 怎么做？（Redis Pub/Sub 或 MQ）
- WebSocket 心跳机制为什么必要？

### 性能优化
- 从 450ms 到 80ms——Redis 缓存优化的完整思路？
- 数据库连接池参数怎么调优（Druid）？
- WebSocket 百万连接怎么玩？

---

## 📁 项目文件结构

```
zzyl/
├── pom.xml                     # Maven 父工程 (多模块聚合)
├── zzyl-common/                # 公共基础模块
│   ├── base/                   # 基类 (BaseEntity/Dto/Vo、ResponseResult、PageResponse)
│   ├── config/                 # 自动配置 (MyBatis/Swagger/Redis/OSS/JSON)
│   ├── constant/               # 常量定义 (缓存/状态/用户)
│   ├── enums/                  # 枚举 (BasicEnum 错误码)
│   ├── exception/              # 全局异常处理 (GlobalExceptionHandler)
│   ├── intercept/              # 拦截器 (AutoFillInterceptor/UserTokenInterceptor)
│   ├── properties/             # 配置属性类 (JWT/OSS/Swagger/微信)
│   └── utils/                  # 工具类 (JwtUtil/SnowflakeIdWorker/ThreadLocal)
├── zzyl-security/              # 安全认证模块
│   ├── controller/             # 登录/用户/角色/部门/菜单/岗位
│   ├── service/                # 登录逻辑 (BCrypt + JWT + Redis 权限缓存)
│   └── mapper/                 # 权限相关 Mapper + XML
├── zzyl-service/               # 核心业务模块
│   ├── entity/                 # 领域实体 (Bed/Elder/Room/Floor/Contract/Visit)
│   ├── dto/                    # 数据传输对象 (请求参数)
│   ├── mapper/                 # 数据访问层 (MyBatis Mapper + XML)
│   ├── service/                # 业务接口 + 实现
│   └── vo/                     # 视图对象 (返回给前端的响应)
├── zzyl-web/                   # Web 表现模块
│   ├── controller/             # RESTful Controllers (床位/老人/来访/合同/护理)
│   ├── controller/customer/    # C 端 (家属端微信小程序) 接口
│   ├── websocket/              # WebSocket 自研组件 (全链路: 认证→路由→广播)
│   ├── task/                   # 定时任务 (日统计/健康检查)
│   └── ZzylApplication.java   # Spring Boot 启动类
├── springcache-demo/           # Redis 缓存 Demo (演示 Spring Cache 用法)
├── mybatis-gen/                # MyBatis 代码生成器
└── 项目学习.md                 # 项目开发学习指南
```

---

> *本项目为 Java 后端学习型企业级项目，基于 RuoYi 框架二次开发，沉淀了 Spring Boot 多模块架构、JWT + RBAC 权限、WebSocket 实时通信、MyBatis 插件、Redis 缓存等主流技术栈的实战经验。*
>
> GitHub：[https://github.com/a283646101-ux/-111](https://github.com/a283646101-ux/-111)

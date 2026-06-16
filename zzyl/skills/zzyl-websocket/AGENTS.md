# 全局工作协议（code-Team）

## 编码风格
- 所有 Java/Kotlin 项目必须使用 Lombok + Spring Boot 3.x
- 异常统一使用 @ExceptionHandler + 自定义 BusinessException
- 日志必须使用 SLF4J + MDC 链路追踪

## 工作流程
- 任何新功能必须先输出架构图
- 生成代码后自动运行测试
- 每次修改前询问是否要创建新分支
- 永远使用最新稳定版依赖

## 审批规则
- 涉及生产环境改动必须二次确认
- 高并发任务必须包含压测报告
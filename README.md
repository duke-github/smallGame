# 🚀 SmallGame - 分布式游戏服务端框架

一个基于 **Java** 构建的高性能游戏服务端框架，支持 **Actor 模型、分片处理、并发调度**，适用于中大型在线游戏后端开发。

---

## ✨ 特性

- 🧩 **Actor 模型**
  - 轻量级 Actor 设计
  - 消息驱动，避免共享状态
  - 易扩展的 Actor 工厂机制

- ⚡ **高并发处理**
  - 基于线程池 + 无锁队列
  - 支持高吞吐消息调度
  - 可自定义调度策略

- 🧠 **分片架构（Shard）**
  - 按业务或玩家 ID 分片
  - 降低锁竞争
  - 提升系统扩展性

- 🔌 **可插拔调度策略**
  - 轮询（Round Robin）
  - 哈希分发（Hash）
  - 空闲优先（Idle First）

- 🛠️ **Spring 集成**
  - 支持依赖注入
  - 生命周期管理清晰

---

## 📦 项目结构

```
smallGame/
├── actor/          # Actor 核心实现
├── shard/          # 分片处理逻辑
├── dispatcher/     # 调度策略
├── factory/        # Actor 工厂
├── config/         # 配置类
└── main/           # 启动入口
```

---

## 🧪 核心设计

### Actor 模型

每个 Actor：

- 拥有独立消息队列
- 单线程处理（避免锁）
- 通过消息通信

```java
actor.tell(message);
```

---

### 分片（Shard）

```
用户ID -> Hash -> Shard -> Actor
```

优势：

- 数据局部性好
- 降低竞争
- 易水平扩展

---

### 调度策略

| 策略 | 说明 |
|------|------|
| RoundRobin | 轮询分发 |
| Hash | 按 Key 分配 |
| IdleFirst | 优先空闲线程 |

---

## 🚀 快速开始

### 1️⃣ 克隆项目

```bash
git clone https://github.com/duke-github/smallGame.git
```

---

### 2️⃣ 启动项目

```bash
mvn spring-boot:run
```

或

```bash
java -jar target/smallGame.jar
```

---

### 3️⃣ 示例代码

```java
Actor actor = actorFactory.create("playerActor");
actor.tell(new LoginMessage(playerId));
```

---

## ⚙️ 配置说明

```yaml
shard:
  count: 16

dispatcher:
  type: hash
  threadPoolSize: 8
```

---

## 📈 性能建议

- Shard 数量 ≈ CPU 核数 * 2
- Actor 避免阻塞操作（IO/锁）
- 使用无锁结构（ConcurrentLinkedQueue）

---

## 🧩 适用场景

- 棋牌游戏
- MMO 游戏后端
- 实时对战服务
- 高并发业务系统

---

## 🛣️ 未来规划

- [ ] 分布式 Shard（跨节点）
- [ ] Actor 持久化
- [ ] 集群通信（Netty）
- [ ] 热更新支持

---

## 🤝 贡献

欢迎提交 PR 或 Issue，一起完善这个项目！

---

## 📄 License

MIT License

---

## 👨‍💻 作者

- duke

---

## ⭐ 支持一下

如果这个项目对你有帮助，欢迎点个 ⭐

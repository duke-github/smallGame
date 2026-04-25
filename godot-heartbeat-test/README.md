## Godot 心跳协议测试工程

这个工程用于连接本仓库 `actor` 模块的 Netty TCP 服务，并按协议发送/解析心跳包。

### 服务端协议（与当前代码一致）

- **传输**: TCP (`netty.port` 默认 `9090`)
- **帧格式**: `length(int32 BE)` + `msgId(int32 BE)` + `body(bytes)`
  - `length = 4 + body.length`（`4` 表示 `msgId` 的长度）
  - `msgId = 2` 表示 `HEART_BEAT`
- **body**: JSON UTF-8
  - 请求: `{ "clientTime": long, "serverTime": 0 }`
  - 响应: 服务端会回写同一个对象并填充 `serverTime`

### 运行方式

1. 启动 Java 服务端（Spring Boot）。
2. 打开 Godot 4.x，导入本目录 `godot-heartbeat-test/` 为工程。
3. 运行后点击 **Connect**，再点击 **Send Heartbeat** 或 **Start Auto**。

### 你会在日志里看到

- `-> HEART_BEAT msgId=2 clientTime=...`
- `<- msgId=2 body={"clientTime":...,"serverTime":...}`


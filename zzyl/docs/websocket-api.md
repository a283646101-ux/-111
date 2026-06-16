# ZZYL WebSocket Real-time API

## 1. Server Endpoint
- URL: `ws://{host}:{port}/ws/realtime`
- Auth: JWT required (`token` query param or `Authorization: Bearer <token>`)
- Optional query: `rooms=roomA,roomB` (auto subscribe after connect)

## 2. Standard Text Protocol
Text frames use JSON:

```json
{
  "traceId": "uuid",
  "type": "SUBSCRIBE",
  "timestamp": 1742976000000,
  "from": "senderUserId",
  "to": "targetUserId",
  "room": "room-1",
  "codec": "text/plain",
  "content": "hello",
  "metadata": {
    "bizType": "alert"
  }
}
```

### type values
- `AUTH`: auth check/refresh ack
- `SUBSCRIBE`: join room
- `UNSUBSCRIBE`: leave room
- `BROADCAST`: send to all online sessions
- `ROOM`: send to all sessions in one room
- `DIRECT`: send to one user (all sessions of that user)
- `HEARTBEAT`: app heartbeat
- `ACK`: server ack
- `ERROR`: protocol/business error
- `SYSTEM`: server system push

## 3. Binary Protocol
Binary frame layout:
1. First 4 bytes: header JSON length (`int32`, big-endian)
2. Header JSON bytes (`WsBinaryEnvelope` fields except payload)
3. Payload bytes (`application/octet-stream` etc.)

Header sample:

```json
{
  "type": "DIRECT",
  "traceId": "abc",
  "to": "1001",
  "room": "ward-3",
  "contentType": "application/octet-stream",
  "timestamp": 1742976000000
}
```

## 4. Room/Channel Isolation
- Room mapping is maintained on server side.
- A client only receives room messages for rooms it has subscribed to.
- Unsubscribe immediately removes delivery routing.

## 5. Reconnect Strategy
- Client side uses exponential backoff (`1s, 2s, 4s ... max 30s`).
- Reconnect can carry `rooms` query parameter to restore subscriptions.
- Server heartbeat + idle session cleanup avoids zombie connections.

## 6. HTTP Admin Push API
Base path: `/ws/admin`

### 6.1 POST `/ws/admin/broadcast`
Push to all online sessions.

### 6.2 POST `/ws/admin/room`
Push to one room.

### 6.3 POST `/ws/admin/push`
Push to one user.

Request body:

```json
{
  "userId": "1001",
  "room": "ward-3",
  "content": "alert message",
  "codec": "application/json",
  "metadata": {
    "level": "HIGH"
  }
}
```

### 6.4 GET `/ws/admin/stats`
Returns connection/throughput/latency snapshot.

## 7. Front-end Integration Demo
- URL: `http://{host}:{port}/ws-demo/index.html`
- Features:
  - connection state monitor
  - auto reconnect
  - heartbeat
  - room subscribe/unsubscribe
  - broadcast/direct/binary send
  - server stats query

## 8. Test Suite
- Unit tests:
  - `WsBinaryEnvelopeTest`
  - `WsSessionRegistryTest`
  - `WsMessageRouterTest`

Run:

```bash
mvn -pl zzyl-web -DskipTests=false test
```

## 9. Load Test
Script:
- `scripts/ws-load-test.js`
- `scripts/ws-load-test.ps1`

Prerequisite:

```bash
npm i ws
```

Example:

```powershell
.\scripts\ws-load-test.ps1 -Token "<JWT>" -Clients 1000 -DurationSec 120 -SendIntervalMs 1000
```

Acceptance check (from script):
- P95 latency `< 100ms`
- memory leak rate `< 0.1%`

## 10. Production Tuning Checklist
- Run with G1GC and fixed heap (for stable latency).
- Keep `heartbeat-interval-ms` between `10s-20s`.
- Set `idle-timeout-ms` to `3x-4x` heartbeat interval.
- Use API gateway rate limit for `/ws/realtime`.
- Expose `/ws/admin/stats` to Prometheus scraper (or bridge to Micrometer).
- For multi-node cluster, move room/user routing to Redis pub/sub + shared registry.

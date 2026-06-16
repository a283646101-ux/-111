---
name: zzyl-websocket
description: Design, implement, and troubleshoot WebSocket real-time communication in the ZZYL Java Spring Boot project. Use when working on connection lifecycle management, JWT-authenticated handshake, room/channel routing, heartbeat and reconnection, text/binary protocol handling, server push APIs, and websocket load/performance validation.
---

# ZZYL WebSocket Skill

## Follow this workflow

1. Inspect `zzyl-web/src/main/java/com/zzyl/websocket` and confirm handler, routing, session registry, and auth interceptor architecture.
2. Validate protocol compatibility before changes:
   - Keep JSON message envelope fields stable (`traceId`, `type`, `timestamp`, `from`, `to`, `room`, `codec`, `content`, `metadata`).
   - Keep binary frame format stable (`int32 headerLength + headerJson + payload`).
3. Reuse existing JWT parsing (`com.zzyl.utils.JwtUtil`) and JWT config (`zzyl.framework.jwt.base64-encoded-secret-key`) for handshake identity.
4. Preserve room isolation rules:
   - Deliver `ROOM` messages only to subscribed sessions.
   - Deliver `DIRECT` messages to all sessions under target user ID.
5. Preserve reliability controls:
   - Keep heartbeat ping schedule enabled.
   - Keep idle-session cleanup enabled.
   - Keep reconnect behavior in frontend demo script.
6. Expose operational visibility through `/ws/admin/stats` and keep metrics fields backward-compatible where possible.
7. Run Maven compile/test checks after change:
   - `mvn -pl zzyl-web -am test`
8. If performance is involved, run load script:
   - `node scripts/ws-load-test.js`
   - Evaluate P95 latency and memory leak rate against target thresholds.

## File map

- Backend WebSocket core: `zzyl-web/src/main/java/com/zzyl/websocket`
- Runtime config: `zzyl-web/src/main/resources/application.yml`
- Frontend demo: `zzyl-web/src/main/resources/static/ws-demo`
- API/usage docs: `docs/websocket-api.md`
- Load test scripts: `scripts/ws-load-test.js`, `scripts/ws-load-test.ps1`

## Guardrails

- Keep authentication mandatory for `/ws/realtime`.
- Do not break existing HTTP APIs while changing websocket internals.
- Avoid changing protocol field names without updating `docs/websocket-api.md` and demo client.

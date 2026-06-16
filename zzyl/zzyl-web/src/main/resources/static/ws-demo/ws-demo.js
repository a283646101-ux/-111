(function () {
    const stateEl = document.getElementById("status");
    const logEl = document.getElementById("log");
    const statsEl = document.getElementById("stats");
    const tokenEl = document.getElementById("token");
    const roomsEl = document.getElementById("rooms");
    const roomEl = document.getElementById("room");
    const toUserEl = document.getElementById("toUser");
    const contentEl = document.getElementById("content");

    let ws = null;
    let reconnectAttempt = 0;
    let manualClose = false;
    let heartbeatTimer = null;

    function log(msg, obj) {
        const time = new Date().toISOString();
        logEl.textContent += `[${time}] ${msg}`;
        if (obj !== undefined) {
            logEl.textContent += ` ${typeof obj === "string" ? obj : JSON.stringify(obj)}`;
        }
        logEl.textContent += "\n";
        logEl.scrollTop = logEl.scrollHeight;
    }

    function setStatus(text, cls) {
        stateEl.className = cls;
        stateEl.textContent = text;
    }

    function wsUrl() {
        const protocol = location.protocol === "https:" ? "wss" : "ws";
        const token = encodeURIComponent(tokenEl.value.trim());
        const rooms = encodeURIComponent(roomsEl.value.trim());
        return `${protocol}://${location.host}/ws/realtime?token=${token}&rooms=${rooms}`;
    }

    function sendJson(type, extra) {
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            log("skip send, websocket not open");
            return;
        }
        const payload = Object.assign({
            type,
            timestamp: Date.now(),
            content: contentEl.value || ""
        }, extra || {});
        ws.send(JSON.stringify(payload));
    }

    function sendBinary() {
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            return;
        }
        const textEncoder = new TextEncoder();
        const body = textEncoder.encode(contentEl.value || "binary-payload");
        const header = {
            type: "DIRECT",
            to: toUserEl.value.trim(),
            room: roomEl.value.trim(),
            timestamp: Date.now(),
            contentType: "application/octet-stream"
        };
        const headerBytes = textEncoder.encode(JSON.stringify(header));
        const data = new Uint8Array(4 + headerBytes.length + body.length);
        const view = new DataView(data.buffer);
        view.setInt32(0, headerBytes.length);
        data.set(headerBytes, 4);
        data.set(body, 4 + headerBytes.length);
        ws.send(data);
    }

    function startHeartbeat() {
        stopHeartbeat();
        heartbeatTimer = setInterval(() => sendJson("HEARTBEAT"), 10000);
    }

    function stopHeartbeat() {
        if (heartbeatTimer) {
            clearInterval(heartbeatTimer);
            heartbeatTimer = null;
        }
    }

    function connect() {
        if (!tokenEl.value.trim()) {
            log("token 不能为空");
            return;
        }
        manualClose = false;
        setStatus("CONNECTING", "warn");
        ws = new WebSocket(wsUrl());
        ws.binaryType = "arraybuffer";

        ws.onopen = function () {
            reconnectAttempt = 0;
            setStatus("CONNECTED", "ok");
            startHeartbeat();
            log("websocket connected");
        };
        ws.onmessage = function (event) {
            if (typeof event.data === "string") {
                try {
                    log("recv text", JSON.parse(event.data));
                } catch (e) {
                    log("recv text raw", event.data);
                }
            } else {
                log("recv binary", `size=${event.data.byteLength}`);
            }
        };
        ws.onerror = function (event) {
            log("websocket error", event.message || "unknown");
        };
        ws.onclose = function (event) {
            stopHeartbeat();
            setStatus("DISCONNECTED", "err");
            log("websocket closed", `${event.code}/${event.reason}`);
            if (!manualClose) {
                reconnect();
            }
        };
    }

    function reconnect() {
        reconnectAttempt += 1;
        const wait = Math.min(30000, Math.pow(2, reconnectAttempt) * 1000);
        log("schedule reconnect", `${wait}ms`);
        setTimeout(connect, wait);
    }

    function disconnect() {
        manualClose = true;
        stopHeartbeat();
        if (ws) {
            ws.close(1000, "manual-close");
        }
    }

    async function loadStats() {
        const response = await fetch("/ws/admin/stats", {
            headers: {
                Authorization: `Bearer ${tokenEl.value.trim()}`
            }
        });
        const data = await response.json();
        statsEl.textContent = JSON.stringify(data, null, 2);
    }

    document.getElementById("connectBtn").onclick = connect;
    document.getElementById("disconnectBtn").onclick = disconnect;
    document.getElementById("joinBtn").onclick = () => sendJson("SUBSCRIBE", { room: roomEl.value.trim() });
    document.getElementById("leaveBtn").onclick = () => sendJson("UNSUBSCRIBE", { room: roomEl.value.trim() });
    document.getElementById("sendBroadcast").onclick = () => sendJson("BROADCAST");
    document.getElementById("sendRoom").onclick = () => sendJson("ROOM", { room: roomEl.value.trim() });
    document.getElementById("sendDirect").onclick = () => sendJson("DIRECT", { to: toUserEl.value.trim() });
    document.getElementById("sendBinary").onclick = sendBinary;
    document.getElementById("sendHeartbeat").onclick = () => sendJson("HEARTBEAT");
    document.getElementById("statsBtn").onclick = () => loadStats().catch(err => log("load stats failed", err.message));
})();

/* eslint-disable no-console */
const WebSocket = require("ws");

const config = {
  url: process.env.WS_URL || "ws://127.0.0.1:9995/ws/realtime",
  token: process.env.WS_TOKEN || "",
  clients: Number(process.env.WS_CLIENTS || 500),
  durationSec: Number(process.env.WS_DURATION_SEC || 60),
  room: process.env.WS_ROOM || "load-test",
  sendIntervalMs: Number(process.env.WS_SEND_INTERVAL_MS || 1000),
};

if (!config.token) {
  console.error("WS_TOKEN is required");
  process.exit(1);
}

const latencies = [];
let sent = 0;
let received = 0;
let errors = 0;
let connected = 0;
let disconnected = 0;
let reconnect = 0;
const startMem = process.memoryUsage().heapUsed;

function percentile(values, p) {
  if (!values.length) return 0;
  const sorted = values.slice().sort((a, b) => a - b);
  const idx = Math.min(sorted.length - 1, Math.floor((p / 100) * sorted.length));
  return sorted[idx];
}

function createClient(index) {
  const url = `${config.url}?token=${encodeURIComponent(config.token)}&rooms=${config.room}`;
  const socket = new WebSocket(url);
  let timer = null;

  socket.on("open", () => {
    connected += 1;
    socket.send(JSON.stringify({ type: "SUBSCRIBE", room: config.room, timestamp: Date.now() }));
    timer = setInterval(() => {
      const now = Date.now();
      const msg = {
        type: "ROOM",
        room: config.room,
        timestamp: now,
        content: `client-${index}-${now}`,
      };
      sent += 1;
      socket.send(JSON.stringify(msg));
    }, config.sendIntervalMs);
  });

  socket.on("message", (data) => {
    try {
      const msg = JSON.parse(data.toString());
      if (msg.timestamp) {
        latencies.push(Date.now() - msg.timestamp);
      }
      received += 1;
    } catch (e) {
      errors += 1;
    }
  });

  socket.on("error", () => {
    errors += 1;
  });

  socket.on("close", () => {
    disconnected += 1;
    if (timer) clearInterval(timer);
  });

  return {
    close() {
      if (socket.readyState === WebSocket.OPEN) {
        socket.close(1000, "load-test-stop");
      } else if (socket.readyState === WebSocket.CLOSED) {
        reconnect += 1;
      }
      if (timer) clearInterval(timer);
    },
  };
}

async function run() {
  console.log("start load test", config);
  const clients = Array.from({ length: config.clients }, (_, i) => createClient(i + 1));
  await new Promise((resolve) => setTimeout(resolve, config.durationSec * 1000));
  clients.forEach((c) => c.close());
  await new Promise((resolve) => setTimeout(resolve, 2000));

  const endMem = process.memoryUsage().heapUsed;
  const leakRate = ((endMem - startMem) / Math.max(startMem, 1)) * 100;

  const summary = {
    clients: config.clients,
    connected,
    disconnected,
    reconnect,
    sent,
    received,
    errors,
    latencyAvgMs: latencies.length ? Math.round(latencies.reduce((a, b) => a + b, 0) / latencies.length) : 0,
    latencyP95Ms: Math.round(percentile(latencies, 95)),
    latencyP99Ms: Math.round(percentile(latencies, 99)),
    memoryLeakRatePercent: Number(leakRate.toFixed(4)),
  };
  console.log("load test summary");
  console.table(summary);

  if (summary.latencyP95Ms > 100) {
    console.error("FAIL: latency P95 exceeded 100ms");
    process.exitCode = 2;
  }
  if (summary.memoryLeakRatePercent > 0.1) {
    console.error("FAIL: memory leak rate exceeded 0.1%");
    process.exitCode = 3;
  }
}

run().catch((err) => {
  console.error(err);
  process.exit(1);
});

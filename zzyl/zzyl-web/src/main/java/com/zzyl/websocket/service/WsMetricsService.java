package com.zzyl.websocket.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Service
public class WsMetricsService {

    private final AtomicLong online = new AtomicLong();
    private final LongAdder connectTotal = new LongAdder();
    private final LongAdder disconnectTotal = new LongAdder();
    private final LongAdder inTextTotal = new LongAdder();
    private final LongAdder inBinaryTotal = new LongAdder();
    private final LongAdder outTextTotal = new LongAdder();
    private final LongAdder outBinaryTotal = new LongAdder();
    private final LongAdder errorTotal = new LongAdder();
    private final LongAdder latencyCount = new LongAdder();
    private final LongAdder latencyTotalMs = new LongAdder();
    private final AtomicLong latencyMaxMs = new AtomicLong();

    public void onConnect() {
        connectTotal.increment();
        online.incrementAndGet();
    }

    public void onDisconnect() {
        disconnectTotal.increment();
        online.decrementAndGet();
    }

    public void onInText() {
        inTextTotal.increment();
    }

    public void onInBinary() {
        inBinaryTotal.increment();
    }

    public void onOutText() {
        outTextTotal.increment();
    }

    public void onOutBinary() {
        outBinaryTotal.increment();
    }

    public void onError() {
        errorTotal.increment();
    }

    public void recordLatency(long latencyMs) {
        if (latencyMs < 0) {
            return;
        }
        latencyCount.increment();
        latencyTotalMs.add(latencyMs);
        latencyMaxMs.updateAndGet(old -> Math.max(old, latencyMs));
    }

    public Map<String, Object> snapshot() {
        Map<String, Object> map = new LinkedHashMap<>();
        long count = latencyCount.sum();
        long total = latencyTotalMs.sum();
        map.put("online", online.get());
        map.put("connectTotal", connectTotal.sum());
        map.put("disconnectTotal", disconnectTotal.sum());
        map.put("inTextTotal", inTextTotal.sum());
        map.put("inBinaryTotal", inBinaryTotal.sum());
        map.put("outTextTotal", outTextTotal.sum());
        map.put("outBinaryTotal", outBinaryTotal.sum());
        map.put("errorTotal", errorTotal.sum());
        map.put("latencyAvgMs", count == 0 ? 0 : total / count);
        map.put("latencyMaxMs", latencyMaxMs.get());
        return map;
    }
}

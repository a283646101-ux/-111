package com.zzyl.websocket.protocol;

public enum WsMessageType {
    AUTH,
    SUBSCRIBE,
    UNSUBSCRIBE,
    BROADCAST,
    DIRECT,
    ROOM,
    HEARTBEAT,
    ACK,
    ERROR,
    SYSTEM
}

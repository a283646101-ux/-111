package com.zzyl.websocket.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage {

    private String traceId = UUID.randomUUID().toString();

    private WsMessageType type;

    private Long timestamp = System.currentTimeMillis();

    private String from;

    private String to;

    private String room;

    private String codec = "text/plain";

    private String content;

    private Map<String, Object> metadata = new HashMap<>();

    public static WsMessage ack(String content) {
        WsMessage message = new WsMessage();
        message.setType(WsMessageType.ACK);
        message.setContent(content);
        return message;
    }

    public static WsMessage error(String content) {
        WsMessage message = new WsMessage();
        message.setType(WsMessageType.ERROR);
        message.setContent(content);
        return message;
    }
}

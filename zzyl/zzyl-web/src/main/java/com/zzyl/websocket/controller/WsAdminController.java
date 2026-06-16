package com.zzyl.websocket.controller;

import com.zzyl.base.ResponseResult;
import com.zzyl.websocket.dto.WsPushRequest;
import com.zzyl.websocket.protocol.WsMessage;
import com.zzyl.websocket.protocol.WsMessageType;
import com.zzyl.websocket.service.WsMessageRouter;
import com.zzyl.websocket.service.WsMetricsService;
import com.zzyl.websocket.session.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/ws/admin")
@RequiredArgsConstructor
public class WsAdminController {

    private final WsMessageRouter messageRouter;
    private final WsSessionRegistry sessionRegistry;
    private final WsMetricsService metricsService;

    @PostMapping("/broadcast")
    public ResponseResult<Map<String, Object>> broadcast(@RequestBody WsPushRequest request) {
        WsMessage message = new WsMessage();
        message.setType(WsMessageType.BROADCAST);
        message.setContent(request.getContent());
        message.setCodec(request.getCodec());
        message.setMetadata(request.getMetadata());
        int delivered = messageRouter.broadcast(message);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("delivered", delivered);
        return ResponseResult.success(data);
    }

    @PostMapping("/room")
    public ResponseResult<Map<String, Object>> roomBroadcast(@RequestBody WsPushRequest request) {
        WsMessage message = new WsMessage();
        message.setType(WsMessageType.ROOM);
        message.setRoom(request.getRoom());
        message.setContent(request.getContent());
        message.setCodec(request.getCodec());
        message.setMetadata(request.getMetadata());
        int delivered = messageRouter.broadcastRoom(request.getRoom(), message);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("delivered", delivered);
        return ResponseResult.success(data);
    }

    @PostMapping("/push")
    public ResponseResult<Map<String, Object>> push(@RequestBody WsPushRequest request) {
        WsMessage message = new WsMessage();
        message.setType(WsMessageType.DIRECT);
        message.setTo(request.getUserId());
        message.setContent(request.getContent());
        message.setCodec(request.getCodec());
        message.setMetadata(request.getMetadata());
        int delivered = messageRouter.direct(request.getUserId(), message);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("delivered", delivered);
        return ResponseResult.success(data);
    }

    @GetMapping("/stats")
    public ResponseResult<Map<String, Object>> stats() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("registry", sessionRegistry.snapshot());
        data.put("metrics", metricsService.snapshot());
        return ResponseResult.success(data);
    }
}

package com.zzyl.websocket.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WsPushRequest {

    private String userId;

    private String room;

    private String content;

    private String codec = "text/plain";

    private Map<String, Object> metadata = new HashMap<>();
}

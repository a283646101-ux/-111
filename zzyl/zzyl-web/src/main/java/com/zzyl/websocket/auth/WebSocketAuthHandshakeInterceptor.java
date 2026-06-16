package com.zzyl.websocket.auth;

import com.alibaba.fastjson.JSONObject;
import com.zzyl.properties.JwtTokenManagerProperties;
import com.zzyl.utils.JwtUtil;
import com.zzyl.websocket.WsConstants;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenManagerProperties jwtTokenManagerProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = resolveToken(servletRequest);
        if (!StringUtils.hasText(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        String userId = resolveUserId(token);
        if (!StringUtils.hasText(userId)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        attributes.put(WsConstants.ATTR_USER_ID, userId);
        attributes.put(WsConstants.ATTR_CLIENT_ID, UUID.randomUUID().toString());
        attributes.put(WsConstants.ATTR_ROOMS, resolveRooms(servletRequest));
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader)) {
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring("Bearer ".length());
            }
            return authHeader;
        }
        return request.getHeader("token");
    }

    private String resolveUserId(String token) {
        try {
            Claims claims = JwtUtil.parseJWT(jwtTokenManagerProperties.getBase64EncodedSecretKey(), token);
            Object currentUser = claims.get("currentUser");
            if (currentUser != null) {
                JSONObject object = JSONObject.parseObject(String.valueOf(currentUser));
                Object id = object.get("id");
                if (id != null) {
                    return String.valueOf(id);
                }
            }
            Object username = claims.get("username");
            if (username != null) {
                return String.valueOf(username);
            }
            Object sub = claims.getSubject();
            if (sub != null) {
                return String.valueOf(sub);
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private List<String> resolveRooms(HttpServletRequest request) {
        String rooms = request.getParameter("rooms");
        if (!StringUtils.hasText(rooms)) {
            return Collections.emptyList();
        }
        return Arrays.asList(rooms.split(","));
    }
}

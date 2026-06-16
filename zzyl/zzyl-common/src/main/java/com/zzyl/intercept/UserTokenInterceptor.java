package com.zzyl.intercept;

import com.alibaba.fastjson.JSONObject;
import com.zzyl.constant.CacheConstants;
import com.zzyl.enums.BasicEnum;
import com.zzyl.exception.BaseException;
import com.zzyl.properties.JwtTokenManagerProperties;
import com.zzyl.utils.JwtUtil;
import com.zzyl.utils.ObjectUtil;
import com.zzyl.utils.UserThreadLocal;
import com.zzyl.vo.UserVo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenManagerProperties jwtTokenManagerProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getToken(request);
        if (ObjectUtil.isEmpty(token)) {
            throw new BaseException(BasicEnum.LOGIN_LOSE_EFFICACY);
        }
        String currentUserJson = parseCurrentUser(token);
        UserVo userVo = JSONObject.parseObject(currentUserJson, UserVo.class);
        if (ObjectUtil.isEmpty(userVo) || ObjectUtil.isEmpty(userVo.getId())) {
            throw new BaseException(BasicEnum.LOGIN_LOSE_EFFICACY);
        }

        UserThreadLocal.setSubject(currentUserJson);
        UserThreadLocal.set(userVo.getId());
        List<String> allowedRules = loadAllowedRules(userVo.getId());
        if (ObjectUtil.isEmpty(allowedRules)) {
            throw new BaseException(BasicEnum.SECURITY_ACCESSDENIED_FAIL);
        }
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        boolean matched = allowedRules.stream().anyMatch(rule -> matchRule(rule, method, requestUri));
        if (!matched) {
            throw new BaseException(BasicEnum.SECURITY_ACCESSDENIED_FAIL);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserThreadLocal.remove();
        UserThreadLocal.removeSubject();
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (ObjectUtil.isNotEmpty(authorization)) {
            String bearer = "Bearer ";
            if (authorization.startsWith(bearer)) {
                return authorization.substring(bearer.length());
            }
            return authorization;
        }
        return request.getHeader("token");
    }

    private String parseCurrentUser(String token) {
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(jwtTokenManagerProperties.getBase64EncodedSecretKey(), token);
        } catch (Exception e) {
            throw new BaseException(BasicEnum.LOGIN_LOSE_EFFICACY);
        }
        Object currentUser = claims.get("currentUser");
        if (ObjectUtil.isEmpty(currentUser)) {
            throw new BaseException(BasicEnum.LOGIN_LOSE_EFFICACY);
        }
        return String.valueOf(currentUser);
    }

    private List<String> loadAllowedRules(Long userId) {
        String key = CacheConstants.PUBLIC_ACCESS_URLS + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (ObjectUtil.isEmpty(json)) {
            return Collections.emptyList();
        }
        return JSONObject.parseArray(json, String.class);
    }

    private boolean matchRule(String rule, String method, String requestUri) {
        if (ObjectUtil.isEmpty(rule)) {
            return false;
        }
        int idx = rule.indexOf("/");
        if (idx <= 0) {
            return ANT_PATH_MATCHER.match(rule, requestUri);
        }
        String ruleMethod = rule.substring(0, idx);
        String rulePath = rule.substring(idx);
        if (!method.equalsIgnoreCase(ruleMethod)) {
            return false;
        }
        return ANT_PATH_MATCHER.match(rulePath, requestUri);
    }
}

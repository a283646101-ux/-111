package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONUtil;
import com.zzyl.constant.CacheConstants;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.LoginDto;
import com.zzyl.entity.Resource;
import com.zzyl.entity.User;
import com.zzyl.enums.BasicEnum;
import com.zzyl.exception.BaseException;
import com.zzyl.mapper.ResourceMapper;
import com.zzyl.mapper.UserMapper;
import com.zzyl.properties.JwtTokenManagerProperties;
import com.zzyl.properties.SecurityConfigProperties;
import com.zzyl.service.LoginService;
import com.zzyl.utils.JwtUtil;
import com.zzyl.utils.ObjectUtil;
import com.zzyl.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenManagerProperties jwtTokenManagerProperties;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private SecurityConfigProperties securityConfigProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @Override
    public UserVo login(LoginDto loginDto) {
        User user = userMapper.selectByUsername(loginDto.getUsername());
        if (ObjectUtil.isEmpty(user) || SuperConstant.DATA_STATE_1.equals(user.getDataState())) {
            throw new BaseException(BasicEnum.LOGIN_FAIL);
        }
        if (!checkPassword(loginDto.getPassword(), user.getPassword())) {
            throw new BaseException(BasicEnum.LOGIN_FAIL);
        }

        UserVo userVo = BeanUtil.toBean(user, UserVo.class);
        userVo.setPassword("");

        List<Resource> resourceList = resourceMapper.selectListByUserId(userVo.getId());
        List<String> roleUrlList = resourceList.stream()
                .map(Resource::getRequestPath)
                .filter(ObjectUtil::isNotEmpty)
                .collect(Collectors.toList());
        List<String> publicAccessUrls = securityConfigProperties.getPublicAccessUrls();
        if (ObjectUtil.isEmpty(publicAccessUrls)) {
            publicAccessUrls = new ArrayList<>();
        }
        LinkedHashSet<String> mergedUrlSet = new LinkedHashSet<>();
        mergedUrlSet.addAll(roleUrlList);
        mergedUrlSet.addAll(publicAccessUrls);
        List<String> urlList = new ArrayList<>(mergedUrlSet);

        Map<String, Object> claims = new HashMap<>();
        claims.put("currentUser", JSONUtil.toJsonStr(userVo));
        String token = JwtUtil.createJWT(jwtTokenManagerProperties.getBase64EncodedSecretKey(),
                jwtTokenManagerProperties.getTtl(), claims);
        int ttl = jwtTokenManagerProperties.getTtl() / 1000;
        redisTemplate.opsForValue().set(CacheConstants.PUBLIC_ACCESS_URLS + userVo.getId(),
                JSONUtil.toJsonStr(urlList), ttl, TimeUnit.SECONDS);
        userVo.setUserToken(token);
        return userVo;
    }

    private boolean checkPassword(String password, String dbPassword) {
        return BCrypt.checkpw(password, dbPassword);
    }
}

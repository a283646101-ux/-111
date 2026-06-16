package com.zzyl.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.dto.WechatLoginDto;
import com.zzyl.entity.Member;
import com.zzyl.properties.WechatProperties;
import com.zzyl.service.MemberService;
import com.zzyl.utils.JwtUtil;
import com.zzyl.vo.WechatLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录控制器
 */
@RestController
@RequestMapping("/customer/wechat")
@Api(tags = "微信登录相关接口")
@Slf4j
public class WechatLoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private WechatProperties wechatProperties;

    @Value("${zzyl.framework.jwt.base64-encoded-secret-key}")
    private String secretKey;

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";

    /**
     * 微信登录
     * @param wechatLoginDto 微信登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public WechatLoginVo login(@RequestBody WechatLoginDto wechatLoginDto) {
        log.info("微信登录请求：{}", wechatLoginDto);

        String openId = getOpenId(wechatLoginDto.getCode());
        log.info("获取到openId：{}", openId);

        Member member = memberService.getByOpenId(openId);
        WechatLoginVo wechatLoginVo = new WechatLoginVo();
        wechatLoginVo.setOpenId(openId);

        if (member == null) {
            member = new Member();
            member.setOpenId(openId);
            member.setName(wechatLoginDto.getNickName());
            member.setAvatar(wechatLoginDto.getAvatarUrl());
            member.setGender(wechatLoginDto.getGender());
            member.setCreateTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            member.setCreateBy(1L);
            memberService.add(member);

            wechatLoginVo.setId(member.getId());
            wechatLoginVo.setName(member.getName());
            wechatLoginVo.setAvatar(member.getAvatar());
            wechatLoginVo.setGender(member.getGender());
            wechatLoginVo.setIsNew(true);
        } else {
            wechatLoginVo.setId(member.getId());
            wechatLoginVo.setPhone(member.getPhone());
            wechatLoginVo.setName(member.getName());
            wechatLoginVo.setAvatar(member.getAvatar());
            wechatLoginVo.setGender(member.getGender());
            wechatLoginVo.setIsNew(false);
        }

        String token = generateToken(member.getId(), openId);
        wechatLoginVo.setToken(token);

        log.info("微信登录成功，返回结果：{}", wechatLoginVo);
        return wechatLoginVo;
    }

    /**
     * 通过code获取微信openId
     * @param code 微信授权码
     * @return openId
     */
    private String getOpenId(String code) {
        String url = JSCODE2SESSION_URL
                .replaceFirst("\\{\\}", wechatProperties.getAppid())
                .replaceFirst("\\{\\}", wechatProperties.getSecret())
                .replaceFirst("\\{\\}", code);

        String result = HttpUtil.get(url);
        log.info("微信接口返回：{}", result);

        JSONObject jsonObject = JSONUtil.parseObj(result);
        String openId = jsonObject.getStr("openid");

        if (openId == null) {
            String errcode = jsonObject.getStr("errcode");
            String errmsg = jsonObject.getStr("errmsg");
            log.error("获取openId失败，errcode：{}，errmsg：{}", errcode, errmsg);
            throw new RuntimeException("微信登录失败：" + errmsg);
        }

        return openId;
    }

    /**
     * 生成JWT令牌
     * @param memberId 会员ID
     * @param openId 微信openId
     * @return JWT令牌
     */
    private String generateToken(Long memberId, String openId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", memberId);
        claims.put("openId", openId);
        return JwtUtil.createJWT(secretKey, 24, claims);
    }
}

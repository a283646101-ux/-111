package com.zzyl.controller.customer;

import com.zzyl.base.ResponseResult;
import com.zzyl.dto.WechatLoginDto;
import com.zzyl.vo.WechatLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户管理
 */
@Slf4j
@Api(tags = "客户管理")
@RestController
@RequestMapping("/customer/user")
public class CustomerUserController {

    /**
     * C端用户登录--微信登录
     * 
     * @param wechatLoginDto 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    @ApiOperation("小程序端登录")
    public ResponseResult<WechatLoginVo> login(@RequestBody WechatLoginDto wechatLoginDto) {

        return ResponseResult.success();
    }

}
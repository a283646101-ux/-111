package com.zzyl.service;

import com.zzyl.dto.LoginDto;
import com.zzyl.vo.UserVo;

/**
 * @author sjqn
 */
public interface LoginService {

    /**
     * 后台用户登录
     * @param loginDto
     * @return
     */
    UserVo login(LoginDto loginDto);
}
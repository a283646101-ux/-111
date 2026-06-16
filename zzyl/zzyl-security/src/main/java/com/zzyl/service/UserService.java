package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.UserDto;
import com.zzyl.vo.UserVo;

import java.util.Set;

public interface UserService {

    PageResponse<UserVo> findUserPage(UserDto userDto, int pageNum, int pageSize);

    void createUser(UserDto userDto);

    Boolean updateUser(UserDto userDto);

    void isEnable(UserDto userDto);

    int deleteUserById(Long userId);

    Set<String> findCheckedRoleIds(Long userId);
}

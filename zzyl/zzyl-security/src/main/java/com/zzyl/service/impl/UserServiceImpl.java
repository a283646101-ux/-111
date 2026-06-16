package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zzyl.base.PageResponse;
import com.zzyl.constant.CacheConstants;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.UserDto;
import com.zzyl.entity.User;
import com.zzyl.entity.UserRole;
import com.zzyl.mapper.UserMapper;
import com.zzyl.mapper.UserRoleMapper;
import com.zzyl.service.UserService;
import com.zzyl.utils.EmptyUtil;
import com.zzyl.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageResponse<UserVo> findUserPage(UserDto userDto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<List<User>> page = userMapper.selectPage(userDto);
        PageResponse<UserVo> pageResponse = PageResponse.of(page, UserVo.class);
        if (EmptyUtil.isNullOrEmpty(pageResponse.getRecords())) {
            return pageResponse;
        }
        pageResponse.getRecords().forEach(userVo -> {
            Set<String> checkedRoleIds = findCheckedRoleIds(userVo.getId());
            if (!EmptyUtil.isNullOrEmpty(checkedRoleIds)) {
                userVo.setCheckedIds(checkedRoleIds.toArray(new String[0]));
            }
        });
        return pageResponse;
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.USER, allEntries = true)
    public void createUser(UserDto userDto) {
        User user = BeanUtil.toBean(userDto, User.class);
        if (ObjectUtil.isEmpty(user.getUserType())) {
            user.setUserType("0");
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword("123456");
        }
        if (ObjectUtil.isEmpty(user.getDataState())) {
            user.setDataState(SuperConstant.DATA_STATE_0);
        }
        userMapper.insertSelective(user);
        saveUserRoleRelation(user.getId(), userDto.getCheckedIds());
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.USER, key = "#userDto.id")
    public Boolean updateUser(UserDto userDto) {
        User user = BeanUtil.toBean(userDto, User.class);
        userMapper.updateByPrimaryKeySelective(user);
        saveUserRoleRelation(user.getId(), userDto.getCheckedIds());
        return true;
    }

    @Override
    @CacheEvict(value = CacheConstants.USER, key = "#userDto.id")
    public void isEnable(UserDto userDto) {
        User user = BeanUtil.toBean(userDto, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.USER, key = "#userId")
    public int deleteUserById(Long userId) {
        userRoleMapper.deleteByUserId(userId);
        return userMapper.deleteByPrimaryKey(userId);
    }

    @Override
    @Cacheable(value = CacheConstants.USER_ROLE, key = "#userId")
    public Set<String> findCheckedRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    private void saveUserRoleRelation(Long userId, String[] checkedIds) {
        userRoleMapper.deleteByUserId(userId);
        if (EmptyUtil.isNullOrEmpty(checkedIds)) {
            return;
        }
        List<UserRole> userRoleList = Arrays.stream(checkedIds)
                .filter(ObjectUtil::isNotEmpty)
                .map(roleId -> UserRole.builder()
                        .userId(userId)
                        .roleId(Long.valueOf(roleId))
                        .dataState(SuperConstant.DATA_STATE_0)
                        .build())
                .collect(Collectors.toList());
        if (EmptyUtil.isNullOrEmpty(userRoleList)) {
            return;
        }
        userRoleMapper.batchInsert(userRoleList);
    }
}

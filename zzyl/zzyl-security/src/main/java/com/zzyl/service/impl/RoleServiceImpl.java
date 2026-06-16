package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zzyl.base.PageResponse;
import com.zzyl.constant.CacheConstants;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.RoleDto;
import com.zzyl.entity.Role;
import com.zzyl.entity.RoleResource;
import com.zzyl.mapper.RoleMapper;
import com.zzyl.mapper.RoleResourceMapper;
import com.zzyl.service.RoleService;
import com.zzyl.utils.EmptyUtil;
import com.zzyl.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public PageResponse<RoleVo> findRolePage(RoleDto roleDto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<List<Role>> page = roleMapper.selectPage(roleDto);
        return PageResponse.of(page, RoleVo.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.ROLE, allEntries = true)
    public void createRole(RoleDto roleDto) {
        Role role = BeanUtil.toBean(roleDto, Role.class);
        roleMapper.insert(role);
    }

    @Override
    @Cacheable(value = CacheConstants.RESOURCE, key = "#roleId")
    public Set<String> findCheckedResources(Long roleId) {
        return roleResourceMapper.selectResourceNoByRoleId(roleId);
    }

    @Override
    @CacheEvict(value = CacheConstants.ROLE, key = "#roleDto.id")
    public Boolean updateRole(RoleDto roleDto) {
        Role role = BeanUtil.toBean(roleDto, Role.class);
        roleMapper.updateByPrimaryKeySelective(role);
        if (ObjectUtil.isEmpty(roleDto.getCheckedResourceNos())) {
            return true;
        }

        roleResourceMapper.deleteRoleResourceByRoleId(role.getId());
        List<RoleResource> roleResourceList = Lists.newArrayList();
        Arrays.asList(roleDto.getCheckedResourceNos()).forEach(n -> {
            RoleResource roleResource = RoleResource.builder()
                    .roleId(role.getId())
                    .resourceNo(n)
                    .dataState(SuperConstant.DATA_STATE_0)
                    .build();
            roleResourceList.add(roleResource);
        });
        if (EmptyUtil.isNullOrEmpty(roleResourceList)) {
            return true;
        }
        roleResourceMapper.batchInsert(roleResourceList);
        return true;
    }

    @Override
    @CacheEvict(value = CacheConstants.ROLE, key = "#roleId")
    public int deleteRoleById(Long roleId) {
        roleResourceMapper.deleteRoleResourceByRoleId(roleId);
        return roleMapper.deleteByPrimaryKey(roleId);
    }
}

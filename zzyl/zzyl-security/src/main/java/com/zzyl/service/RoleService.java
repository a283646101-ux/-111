package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.RoleDto;
import com.zzyl.vo.RoleVo;
import java.util.Set;

/**
 * 角色表服务类
 */
public interface RoleService {

    /**
     *  多条件查询角色表分页列表
     * @param roleDto 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<ResourceVo>
     */
    PageResponse<RoleVo> findRolePage(RoleDto roleDto, int pageNum, int pageSize);

    /**
     *  创建角色
     * @param roleDto 对象信息
     */
    void createRole(RoleDto roleDto);

    /**
     * 根据角色id查询资源列表
     * @param roleId 角色id
     * @return 资源编号集合
     */
    Set<String> findCheckedResources(Long roleId);

    /**
     *  修改角色表
     * @param roleDto 对象信息
     * @return Boolean
     */
    Boolean updateRole(RoleDto roleDto);

    /**
     * 删除角色
     * @param roleId 角色id
     * @return 删除结果
     */
    int deleteRoleById(Long roleId);
}

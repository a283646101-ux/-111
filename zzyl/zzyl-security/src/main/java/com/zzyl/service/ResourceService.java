package com.zzyl.service;

import com.zzyl.dto.ResourceDto;
import com.zzyl.vo.MenuVo;
import com.zzyl.vo.ResourceVo;
import com.zzyl.vo.TreeVo;

import java.util.List;

public interface ResourceService {
    /**
     * 多条件查询资源列表
     * @param resourceDto
     * @return
     */
    List<ResourceVo> findResourceList(ResourceDto resourceDto);

    /**
     * 资源树形
     * @param resourceDto
     * @return
     */
    TreeVo resourceTreeVo(ResourceDto resourceDto);

    /**
     * 添加资源菜单
     * @param resourceDto
     */
    void createResource(ResourceDto resourceDto);

    /**
     * 根据用户id查询对应的资源数据
     * @param userId 用户id
     * @return 菜单列表
     */
    List<MenuVo> menus(Long userId);
}

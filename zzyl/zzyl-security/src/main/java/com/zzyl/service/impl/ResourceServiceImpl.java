package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.ResourceDto;
import com.zzyl.entity.Resource;
import com.zzyl.enums.BasicEnum;
import com.zzyl.exception.BaseException;
import com.zzyl.mapper.ResourceMapper;
import com.zzyl.service.ResourceService;
import com.zzyl.utils.EmptyUtil;
import com.zzyl.utils.NoProcessing;
import com.zzyl.utils.StringUtils;
import com.zzyl.vo.MenuMetaVo;
import com.zzyl.vo.MenuVo;
import com.zzyl.vo.ResourceVo;
import com.zzyl.vo.TreeItemVo;
import com.zzyl.vo.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
    /**
     * 多条件查询资源列表
     * @param resourceDto
     * @return
     */
    @Override
    public List<ResourceVo> findResourceList(ResourceDto resourceDto) {
        List<Resource> resourceList = resourceMapper.selectList(resourceDto);
        return BeanUtil.copyToList(resourceList, ResourceVo.class);
    }

    /**
     * 树形结构
     * @param resourceDto
     * @return
     */
    @Override
    public TreeVo resourceTreeVo(ResourceDto resourceDto) {
        //构造查询条件
        ResourceDto dto = ResourceDto.builder()
                .dataState(SuperConstant.DATA_STATE_0)
                .parentResourceNo(NoProcessing.processString(SuperConstant.ROOT_PARENT_ID))
                .resourceType(SuperConstant.MENU).build();
        //查询所有资源数据
        List<Resource> resourceList = resourceMapper.selectList(dto);
        if(EmptyUtil.isNullOrEmpty(resourceList)){
            throw new RuntimeException("资源信息未定义");
        }
        //没有根节点，构建根节点
        Resource rootResource = new Resource();
        rootResource.setResourceNo(SuperConstant.ROOT_PARENT_ID);
        rootResource.setResourceName("智慧养老院");

        //返回的树形集合
        List<TreeItemVo> itemVos = new ArrayList<>();

        //使用递归构建树形结构
        recursionTreeItem(itemVos,rootResource,resourceList);

        return TreeVo.builder().items(itemVos).build();
    }



    /**
     * 使用递归构建树形结构
     * @param itemVos
     * @param rootResource
     * @param resourceList
     */
    private void recursionTreeItem(List<TreeItemVo> itemVos, Resource rootResource, List<Resource> resourceList) {
        //构建每个资源的属性
        TreeItemVo treeItemVo = TreeItemVo.builder()
                .id(rootResource.getResourceNo())
                .label(rootResource.getResourceName()).build();
        //获取当前资源下的子资源
        List<Resource> childrenResourceList = resourceList.stream()
                .filter(n -> n.getParentResourceNo().equals(rootResource.getResourceNo()))
                .collect(Collectors.toList());
        //判断子资源是否为空
        if(!EmptyUtil.isNullOrEmpty(childrenResourceList)){
            List<TreeItemVo> listChildren = new ArrayList<>();
            //构建子资源
            childrenResourceList.forEach(resource -> {
                recursionTreeItem(listChildren,resource,resourceList);
            });
            treeItemVo.setChildren(listChildren);
        }

        //添加到集合
        itemVos.add(treeItemVo);

    }

    /**
     * 添加资源
     * @param resourceDto
     */
    @Override
    public void createResource(ResourceDto resourceDto) {
        //属性拷贝
        Resource resource = BeanUtil.toBean(resourceDto, Resource.class);
        //查询父资源
        Resource parentResource = resourceMapper.selectByResourceNo(resourceDto.getParentResourceNo());
        resource.setDataState(parentResource.getDataState());
        boolean isIgnore = true;
        //判断是否是按钮，如果是按钮，则不限制层级
        if(StringUtils.isNotEmpty(resourceDto.getResourceType())
            && resourceDto.getResourceType().equals(SuperConstant.BUTTON)){
            isIgnore = false;
        }
        //创建当前资源的编号
        String resourceNo = createResourceNo(resourceDto.getParentResourceNo(),isIgnore);
        resource.setResourceNo(resourceNo);
        resourceMapper.insert(resource);

    }

    @Override
    public List<MenuVo> menus(Long userId) {
        List<MenuVo> menuVoList = resourceMapper.findListByUserId(userId);
        if (EmptyUtil.isNullOrEmpty(menuVoList)) {
            throw new BaseException(BasicEnum.USER_ROLE_AND_MENU_EMPTY);
        }
        Map<String, List<MenuVo>> parentRNoMap = menuVoList.stream()
                .collect(Collectors.groupingBy(MenuVo::getParentResourceNo));
        menuVoList.forEach(menuVo -> {
            menuVo.setMeta(MenuMetaVo.builder().title(menuVo.getName()).build());
            menuVo.setRedirect("/" + menuVo.getName());
            List<MenuVo> children = parentRNoMap.get(menuVo.getResourceNo());
            if (!EmptyUtil.isNullOrEmpty(children)) {
                children.forEach(m -> {
                    m.setMeta(MenuMetaVo.builder().title(m.getName()).build());
                    m.setRedirect(m.getName());
                });
                menuVo.setChildren(children);
            }
        });
        List<MenuVo> rootMenus = parentRNoMap.get(SuperConstant.ROOT_PARENT_ID);
        if (EmptyUtil.isNullOrEmpty(rootMenus)) {
            return new ArrayList<>();
        }
        return rootMenus;
    }

    /**
     * 创建资源编号
     * @param parentResourceNo
     * @param isIgnore
     * @return
     */
    private String createResourceNo(String parentResourceNo, boolean isIgnore) {
        //100 001 000 000 000
        //100 001 001 000 000
        //100 001 001 001 000
        //100 001 001 001 001 001
        //判断资源编号是否大于三级
        if(isIgnore && NoProcessing.processString(parentResourceNo).length() / 3 >= 5){
            throw new BaseException(BasicEnum.RESOURCE_DEPTH_UPPER_LIMIT);
        }
        //根据父资源编号查询子资源
        ResourceDto dto = ResourceDto.builder().parentResourceNo(parentResourceNo).build();
        List<Resource> resourceList = resourceMapper.selectList(dto);
        if(EmptyUtil.isNullOrEmpty(resourceList)){
            //无下属节点，创建新的节点编号
            //100 001 001 001 000 -->100 001 001 001 001
            return NoProcessing.createNo(parentResourceNo,false);
        }else{
            //有下属节点，在已有节点上追加
            //100 001 001 001 000 -->100 001 001 001 003 -->100 001 001 001 004
            //先获取已有节点的最大值
            Long maxNo = resourceList.stream()
                    .map(resource -> {
                        return Long.valueOf(resource.getResourceNo());
                    }).max(Comparator.comparing(i -> i)).get();
            return NoProcessing.createNo(String.valueOf(maxNo),true);
        }

    }

}

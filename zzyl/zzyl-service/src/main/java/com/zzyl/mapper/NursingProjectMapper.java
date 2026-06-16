package com.zzyl.mapper;

import com.github.pagehelper.Page;
import com.zzyl.entity.NursingProject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理项目Mapper接口
 */
@Mapper
public interface NursingProjectMapper {

    Page<NursingProject> selectByPage(String name, Integer status);

    void insert(NursingProject nursingProject);
// 根据id查询护理项目
    NursingProject selectById(Long id);

    void update(NursingProject nursingProject);
}
package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.NursingProjectDto;
import com.zzyl.vo.NursingProjectVo;

/**
 * 护理项目service接口
 */
public interface NursingProjectService {
    /**
     * 分页查询护理项目
     * 
     * @param name
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResponse<NursingProjectVo> getByPage(String name, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 新增护理项目
     * 
     * @param nursingProjectDTO
     */
    void add(NursingProjectDto nursingProjectDTO);

    /**
     * 根据id查询护理项目
     * 
     * @param id
     * @return
     */
    NursingProjectVo getById(Long id);

    /**
     * 修改护理项目
     * 
     * @param nursingProjectDTO
     */
    void update(NursingProjectDto nursingProjectDTO);
}

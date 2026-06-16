package com.zzyl.mapper;

import com.github.pagehelper.Page;
import com.zzyl.dto.AlertRuleDto;
import com.zzyl.entity.AlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlertRuleMapper {

    int insert(AlertRule alertRule);

    int updateById(AlertRule alertRule);

    int deleteById(Long id);

    AlertRule selectById(Long id);

    Page<AlertRule> selectByPage(@Param("dto") AlertRuleDto dto);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

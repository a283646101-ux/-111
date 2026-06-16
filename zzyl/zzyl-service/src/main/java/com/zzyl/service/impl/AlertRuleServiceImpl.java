package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zzyl.base.PageResponse;
import com.zzyl.dto.AlertRuleDto;
import com.zzyl.entity.AlertRule;
import com.zzyl.mapper.AlertRuleMapper;
import com.zzyl.service.AlertRuleService;
import com.zzyl.vo.AlertRuleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertRuleServiceImpl implements AlertRuleService {

    @Autowired
    private AlertRuleMapper alertRuleMapper;

    @Override
    public void create(AlertRuleDto dto) {
        AlertRule alertRule = new AlertRule();
        BeanUtils.copyProperties(dto, alertRule);
        if (alertRule.getStatus() == null) {
            alertRule.setStatus(0);
        }
        alertRuleMapper.insert(alertRule);
    }

    @Override
    public void update(Long id, AlertRuleDto dto) {
        AlertRule alertRule = new AlertRule();
        BeanUtils.copyProperties(dto, alertRule);
        alertRule.setId(id);
        alertRuleMapper.updateById(alertRule);
    }

    @Override
    public void delete(Long id) {
        alertRuleMapper.deleteById(id);
    }

    @Override
    public AlertRuleVo read(Long id) {
        AlertRule alertRule = alertRuleMapper.selectById(id);
        return BeanUtil.toBean(alertRule, AlertRuleVo.class);
    }

    @Override
    public PageResponse<AlertRuleVo> getPage(AlertRuleDto dto, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<AlertRule> page = alertRuleMapper.selectByPage(dto);
        return PageResponse.of(page, AlertRuleVo.class);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        alertRuleMapper.updateStatus(id, status);
    }
}

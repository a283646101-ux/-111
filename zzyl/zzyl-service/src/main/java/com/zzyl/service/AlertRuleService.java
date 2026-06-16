package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.AlertRuleDto;
import com.zzyl.vo.AlertRuleVo;

public interface AlertRuleService {

    void create(AlertRuleDto dto);

    void update(Long id, AlertRuleDto dto);

    void delete(Long id);

    AlertRuleVo read(Long id);

    PageResponse<AlertRuleVo> getPage(AlertRuleDto dto, Integer pageNum, Integer pageSize);

    void updateStatus(Long id, Integer status);
}

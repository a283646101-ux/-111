package com.zzyl.controller;

import com.zzyl.base.PageResponse;
import com.zzyl.base.ResponseResult;
import com.zzyl.dto.AlertRuleDto;
import com.zzyl.service.AlertRuleService;
import com.zzyl.vo.AlertRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alert-rule")
@Api(tags = "报警规则")
public class AlertRuleController extends BaseController {

    @Autowired
    private AlertRuleService alertRuleService;

    @PostMapping("/create")
    @ApiOperation("新增报警规则")
    public ResponseResult create(@RequestBody AlertRuleDto dto) {
        alertRuleService.create(dto);
        return success();
    }

    @PutMapping("/update/{id}")
    @ApiOperation("编辑报警规则")
    public ResponseResult update(@PathVariable Long id, @RequestBody AlertRuleDto dto) {
        alertRuleService.update(id, dto);
        return success();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除报警规则")
    public ResponseResult delete(@PathVariable Long id) {
        alertRuleService.delete(id);
        return success();
    }

    @GetMapping("/read/{id}")
    @ApiOperation("查询报警规则详情")
    public ResponseResult<AlertRuleVo> read(@PathVariable Long id) {
        return success(alertRuleService.read(id));
    }

    @GetMapping("/get-page")
    @ApiOperation("分页查询报警规则")
    public ResponseResult<PageResponse<AlertRuleVo>> getPage(
            AlertRuleDto dto,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(alertRuleService.getPage(dto, pageNum, pageSize));
    }

    @PutMapping("/status/{id}/{status}")
    @ApiOperation("启用禁用报警规则")
    public ResponseResult updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        alertRuleService.updateStatus(id, status);
        return success();
    }
}

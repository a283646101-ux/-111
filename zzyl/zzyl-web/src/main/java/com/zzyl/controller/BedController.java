package com.zzyl.controller;

import com.zzyl.base.ResponseResult;
import com.zzyl.dto.BedDto;
import com.zzyl.service.BedService;
import com.zzyl.vo.BedVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bed")
@Api(tags = "床位管理相关接口")
public class BedController extends BaseController {

    @Autowired
    private BedService bedService;

    @GetMapping("/read/room/{roomId}")
    @ApiOperation(value = "根据房间id查询床位", notes = "传入房间id")
    public ResponseResult<List<BedVo>> readBedByRoomId(
            @ApiParam(value = "房间ID", required = true) @PathVariable("roomId") Long roomId) {
        List<BedVo> beds = bedService.getBedsByRoomId(roomId);
        return success(beds);
    }

    @PostMapping("/create")
    @ApiOperation(value = "新增床位", notes = "传入床位信息,包括床位号和所属房间号")
    public ResponseResult createBed(@RequestBody BedDto bedDto) {
        bedService.addBed(bedDto);
        return success();
    }

    // ApiParam的required属性，表示该参数是否为必填项
    @GetMapping("/read/{id}")
    @ApiOperation(value = "根据id查询床位", notes = "传入床位id")
    public ResponseResult<BedVo> readBed(@ApiParam(value = "床位ID", required = true) @PathVariable("id") Long id) {
        return success(bedService.getById(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新床位", notes = "传入床位信息,包括床位号和所属房间号")
    public ResponseResult updateBed(@ApiParam(value = "床位信息", required = true) @RequestBody BedDto bedDto) {
        bedService.updateBed(bedDto);
        return success();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除床位", notes = "传入床位id")
    public ResponseResult delBed(@ApiParam(value = "床位ID", required = true) @PathVariable("id") Long id) {
        bedService.delBed(id);
        return success();
    }
}

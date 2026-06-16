package com.zzyl.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zzyl.base.PageResponse;
import com.zzyl.base.ResponseResult;
import com.zzyl.dto.UserDto;
import com.zzyl.service.UserService;
import com.zzyl.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation(value = "用户分页", notes = "用户分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userDto", value = "用户DTO对象", required = true, dataType = "UserDto"),
            @ApiImplicitParam(paramType = "path", name = "pageNum", value = "页码", example = "1", dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "每页条数", example = "10", dataType = "Integer")
    })
    @ApiOperationSupport(includeParameters = {"userDto.username", "userDto.nickName", "userDto.mobile", "userDto.dataState", "userDto.deptNo"})
    public ResponseResult<PageResponse<UserVo>> findUserPage(@RequestBody UserDto userDto,
                                                             @PathVariable("pageNum") int pageNum,
                                                             @PathVariable("pageSize") int pageSize) {
        return ResponseResult.success(userService.findUserPage(userDto, pageNum, pageSize));
    }

    @PutMapping
    @ApiOperation(value = "用户添加", notes = "用户添加")
    @ApiImplicitParam(name = "userDto", value = "用户DTO对象", required = true, dataType = "UserDto")
    @ApiOperationSupport(includeParameters = {"userDto.username", "userDto.nickName", "userDto.realName", "userDto.mobile", "userDto.email", "userDto.sex", "userDto.dataState", "userDto.deptNo", "userDto.postNo", "userDto.checkedIds"})
    public ResponseResult<UserVo> createUser(@RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return ResponseResult.success();
    }

    @PatchMapping
    @ApiOperation(value = "用户修改", notes = "用户修改")
    @ApiImplicitParam(name = "userDto", value = "用户DTO对象", required = true, dataType = "UserDto")
    @ApiOperationSupport(includeParameters = {"userDto.id", "userDto.username", "userDto.nickName", "userDto.realName", "userDto.mobile", "userDto.email", "userDto.sex", "userDto.dataState", "userDto.deptNo", "userDto.postNo", "userDto.checkedIds"})
    public ResponseResult<Boolean> updateUser(@RequestBody UserDto userDto) {
        return ResponseResult.success(userService.updateUser(userDto));
    }

    @PatchMapping("/is_enable")
    @ApiOperation(value = "启用-禁用", notes = "启用-禁用")
    @ApiImplicitParam(name = "userDto", value = "用户DTO对象", required = true, dataType = "UserDto")
    @ApiOperationSupport(includeParameters = {"userDto.id", "userDto.dataState"})
    public ResponseResult<Boolean> isEnable(@RequestBody UserDto userDto) {
        userService.isEnable(userDto);
        return ResponseResult.success();
    }

    @GetMapping("/find-checked-roles/{userId}")
    @ApiOperation(value = "根据用户查询选中的角色数据")
    public ResponseResult<Set<String>> findCheckedRoles(@PathVariable("userId") Long userId) {
        return ResponseResult.success(userService.findCheckedRoleIds(userId));
    }

    @DeleteMapping("/{userId}")
    @ApiOperation("删除用户")
    public ResponseResult<Integer> remove(@PathVariable("userId") Long userId) {
        return ResponseResult.success(userService.deleteUserById(userId));
    }
}

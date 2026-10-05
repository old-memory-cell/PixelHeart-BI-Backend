package com.zzsd.inolibi.controller;


import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.User;
import com.zzsd.inolibi.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 前端控制器
 *
 * @author inoli
 * @since 2026-10-05
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理")
public class UserController {

    @Resource
    private IUserService userService;

    @GetMapping("/list")
    @Operation(summary = "用户列表")
    public Result<List<User>> list() {
        List<User> list = userService.list();
        return Result.success(list);
    }

}

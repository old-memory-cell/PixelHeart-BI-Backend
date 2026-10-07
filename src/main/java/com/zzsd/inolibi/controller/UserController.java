package com.zzsd.inolibi.controller;


import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.user.User;
import com.zzsd.inolibi.entity.user.dto.UserLoginDto;
import com.zzsd.inolibi.entity.user.dto.UserRegisterDto;
import com.zzsd.inolibi.entity.user.vo.CurrentUserVo;
import com.zzsd.inolibi.entity.user.vo.LoginInfo;
import com.zzsd.inolibi.service.IUserService;
import com.zzsd.inolibi.utils.CurrentHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 用户注册接口
     *
     * @param userRegisterDto 用户注册实体
     * @return 用户id
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Object> register(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.register(userRegisterDto);
    }


    /*
     * 登录功能
     * */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@RequestBody UserLoginDto userLoginDto) {
        LoginInfo loginInfo = userService.login(userLoginDto);
        if (loginInfo != null) {
            return Result.success(loginInfo);
        }
        return Result.error("用户名或密码错误");
    }


    /**
     * 获取当前登录用户信息
     * 前端登录后携带 token 请求头调用，返回不含密码的用户信息
     *
     * @return 当前登录用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户")
    public Result<CurrentUserVo> current() {
        Long userId = CurrentHolder.getCurrentId();
        CurrentUserVo currentUserVo = userService.getCurrentUser(userId);
        return Result.success(currentUserVo);
    }

    /**
     * 用户退出登录状态
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result logout(HttpServletRequest request) throws Exception {
        // 用户退出，清楚jwt
        userService.logout(request);
        return Result.success();
    }


}

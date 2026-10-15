package com.zzsd.inolibi.controller;


import cn.hutool.core.bean.BeanUtil;
import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.page.PageResult;
import com.zzsd.inolibi.entity.user.User;
import com.zzsd.inolibi.entity.user.dto.UserLoginDto;
import com.zzsd.inolibi.entity.user.dto.UserQueryDto;
import com.zzsd.inolibi.entity.user.dto.UserRegisterDto;
import com.zzsd.inolibi.entity.user.vo.CurrentUserVo;
import com.zzsd.inolibi.entity.user.vo.LoginInfo;
import com.zzsd.inolibi.entity.user.vo.UserQueryVo;
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


    /**
     * 用户登录接口
     *
     * @param userLoginDto 用户登录实体
     * @return 登录信息
     */
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
     *
     * @return 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result logout(HttpServletRequest request) throws Exception {
        // 用户退出，清楚jwt
        userService.logout(request);
        return Result.success();
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result updateUser(@RequestBody CurrentUserVo user) {
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 查询用户列表 仅限管理员
     *
     * @return 用户列表
     */
    @PostMapping("/queryList")
    @Operation(summary = "仅限管理员查询用户列表")
    public Result<PageResult<UserQueryVo>> queryList(@RequestBody UserQueryDto userQueryDto) {
        PageResult<UserQueryVo> result = userService.queryList(userQueryDto);
        return Result.success(result);
    }

    /**
     * 根据用户id查询用户信息，仅管理员可查询
     *
     * @param id 要查询的用户id
     * @return 用户信息
     */
    @GetMapping("/queryUserById/{id}")
    @Operation(summary = "根据用户id查询用户信息，仅管理员可查询")
    public Result<UserQueryVo> queryUserById(@PathVariable Long id) {
        // 1. 鉴权
        Long currentId = CurrentHolder.getCurrentId();
        User currentUser = userService.getById(currentId);
        if (currentUser == null || !"admin".equals(currentUser.getUserRole())) {
            return Result.error("无权限");
        }

        // 2. 查询用户信息
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        UserQueryVo userQueryVo = BeanUtil.copyProperties(user, UserQueryVo.class);
        return Result.success(userQueryVo);
    }

    /**
     * 删除用户，仅管理员
     * @param id 要删除的用户id
     * @return Result
     */
    @PostMapping("/deleteById/{id}")
    @Operation(summary = "删除用户,仅管理员")
    public Result deleteById(@PathVariable Long id) {
        // 1. 鉴权
        Long currentId = CurrentHolder.getCurrentId();
        User currentUser = userService.getById(currentId);
        if (currentUser == null || !"admin".equals(currentUser.getUserRole())) {
            return Result.error("无权限");
        }

        // 2. 删除用户
        boolean b = userService.removeById(id);
        if (b) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

}

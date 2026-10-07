package com.zzsd.inolibi.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.user.User;
import com.zzsd.inolibi.entity.user.dto.UserLoginDto;
import com.zzsd.inolibi.entity.user.dto.UserRegisterDto;
import com.zzsd.inolibi.entity.user.vo.CurrentUserVo;
import com.zzsd.inolibi.entity.user.vo.LoginInfo;
import jakarta.servlet.http.HttpServletRequest;


/**
 * 用户 服务类
 *
 * @author inoli
 * @since 2026-10-05
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDto 注册实体
     * @return 用户id
     */
    Result<Object> register(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     *
     * @param userLoginDto 登录实体
     * @return 登录信息
     */
    LoginInfo login(UserLoginDto userLoginDto);

    /**
     * 根据id获取当前登录用户信息
     *
     * @param userId 用户id
     * @return 当前用户信息（不含密码等敏感字段）
     */
    CurrentUserVo getCurrentUser(Long userId);

    /**
     * 用户的退出
     * @param request 请求
     */
    void logout(HttpServletRequest request) throws Exception;
}

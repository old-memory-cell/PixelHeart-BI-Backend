package com.zzsd.inolibi.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.user.User;
import com.zzsd.inolibi.entity.user.dto.UserLoginDto;
import com.zzsd.inolibi.entity.user.dto.UserRegisterDto;
import com.zzsd.inolibi.entity.user.vo.LoginInfo;
import com.zzsd.inolibi.mapper.UserMapper;
import com.zzsd.inolibi.service.IUserService;

import com.zzsd.inolibi.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 用户注册逻辑
     *
     * @param userRegisterDto 注册实体
     * @return 用户注册id
     */
    @Override
    public Result<Object> register(UserRegisterDto userRegisterDto) {
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        String checkPassword = userRegisterDto.getCheckPassword();

        // 1. 校验
        if (ObjUtil.hasEmpty(userAccount, userPassword, checkPassword) || ObjUtil.hasNull(userAccount, userPassword, checkPassword)) {
            throw new RuntimeException("用户名或密码为空");
        }

        // 用户账号长度不能小于4 密码不能小于8
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new RuntimeException("用户名或密码长度不能小于4或8");
        }

        // 密码不相同
        if (!userPassword.equals(checkPassword)) {
            throw new RuntimeException("两次密码不一致");
        }

        // 不能包含特殊字符
        if (!ReUtil.isMatch("^[a-zA-Z0-9]+$", userAccount)) {
            throw new RuntimeException("用户名不能包含特殊字符");
        }

        // 不能有重复账户
        long count = count(Wrappers.<User>lambdaQuery().eq(User::getUserAccount, userAccount));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 对密码进行加密
        // 2. 加密
        String hashpw = BCrypt.hashpw(userPassword, BCrypt.gensalt());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashpw);
        if (!save(user)) {
            throw new RuntimeException("注册失败");
        }
        return Result.success(user.getId());
    }

    /**
     * 用户登录逻辑
     *
     * @param userLoginDto 登录实体
     * @return 登录信息
     */
    @Override
    public LoginInfo login(UserLoginDto userLoginDto) {
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();


        // 1. 校验
        if (ObjUtil.hasEmpty(userAccount, userPassword) || ObjUtil.hasNull(userAccount, userPassword)) {
            throw new RuntimeException("用户名或密码为空");
        }

        // 用户账号长度不能小于4 密码不能小于8
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new RuntimeException("用户名或密码长度不能小于4或8");
        }

        // 2.查询用户
        User user = getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUserAccount, userAccount)
        );

        // 校验用户的密码是否正确
        if (user == null || !BCrypt.checkpw(userPassword, user.getUserPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        //生成token
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", user.getId());
        dataMap.put("userAccount", user.getUserAccount());
        String token = JwtUtils.generateJwt(dataMap);

        return new LoginInfo(user.getId(), user.getUserAccount(), token);
    }
}

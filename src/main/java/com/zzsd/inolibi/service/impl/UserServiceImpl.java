package com.zzsd.inolibi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.page.PageResult;
import com.zzsd.inolibi.entity.user.User;
import com.zzsd.inolibi.entity.user.dto.UserLoginDto;
import com.zzsd.inolibi.entity.user.dto.UserQueryDto;
import com.zzsd.inolibi.entity.user.dto.UserRegisterDto;
import com.zzsd.inolibi.entity.user.vo.CurrentUserVo;
import com.zzsd.inolibi.entity.user.vo.LoginInfo;
import com.zzsd.inolibi.entity.user.vo.UserQueryVo;
import com.zzsd.inolibi.mapper.UserMapper;
import com.zzsd.inolibi.service.IUserService;
import com.zzsd.inolibi.utils.CurrentHolder;
import com.zzsd.inolibi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    public final StringRedisTemplate stringRedisTemplate;

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

    /**
     * 根据id获取当前登录用户信息
     *
     * @param userId 用户id
     * @return 当前用户信息（不含密码等敏感字段）
     */
    @Override
    public CurrentUserVo getCurrentUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 拷贝属性，忽略密码、逻辑删除等敏感/无用字段
        CurrentUserVo currentUserVo = new CurrentUserVo();
        BeanUtil.copyProperties(user, currentUserVo);
        return currentUserVo;
    }

    /**
     * 用户退出登录状态
     *
     * @param request 请求
     */
    @Override
    public void logout(HttpServletRequest request) throws Exception {
        // 1. 从 header 中获取 token（注意：名字必须和拦截器里一致，叫 "token"）
        String token = request.getHeader("token");

        if (token != null && !token.isEmpty()) {
            // 2. 获取 token 的过期时间
            Claims claims = JwtUtils.parseJWT(token);
            Date expiration = claims.getExpiration();

            // 3. 计算剩余有效时间
            long ttl = expiration.getTime() - System.currentTimeMillis();

            // 4. 存入 Redis 黑名单（设置与 token 相同的过期时间）
            if (ttl > 0) {
                stringRedisTemplate.opsForValue().set("jwt:blacklist:" + token, "logout", ttl, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 更新用户
     *
     * @param user 用户更新
     */
//    @Override
//    public void updateUser(CurrentUserVo user) {
//        Long currentId = CurrentHolder.getCurrentId();
//        if (currentId == null) throw new RuntimeException("用户未登录");
//        if (user.getId() != null && !currentId.equals(user.getId())) throw new RuntimeException("只能修改本人信息");
//
//        User current = getById(currentId);
//        LambdaUpdateWrapper<User> w = Wrappers.<User>lambdaUpdate()
//                .eq(User::getId, currentId)
//                .set(User::getUpdateTime, LocalDateTime.now())
//                .set(user.getUserName() != null, User::getUserName, user.getUserName())
//                .set(user.getUserAvatar() != null, User::getUserAvatar, user.getUserAvatar());
//
//        String target = user.getUserRole();
//        if (target != null && !target.equals(current.getUserRole())) {
//            if (!"admin".equals(current.getUserRole())) throw new RuntimeException("无权限修改用户角色");
//            w.set(User::getUserRole, target);
//        }
//        if (!update(null, w)) throw new RuntimeException("用户信息未发生变化或更新失败");
//    }
    @Override
    public void updateUser(CurrentUserVo user) {
        Long currentId = CurrentHolder.getCurrentId();
        if (currentId == null) throw new RuntimeException("用户未登录");

        User userInDb = getById(currentId);
        if (userInDb == null) throw new RuntimeException("用户不存在");

        boolean isAdmin = "admin".equals(userInDb.getUserRole());
        Long targetId = user.getId() == null ? currentId : user.getId();   // 默认本人
        if (!isAdmin && !currentId.equals(targetId)) {
            throw new RuntimeException("只能修改本人信息");   // 授权用异常，不用 WHERE
        }

        LambdaUpdateWrapper<User> w = Wrappers.<User>lambdaUpdate()
                .eq(User::getId, targetId)                                  // ← WHERE 永远存在
                .set(ObjectUtil.isNotEmpty(user.getUserName()), User::getUserName, user.getUserName())
                .set(ObjectUtil.isNotEmpty(user.getUserAvatar()), User::getUserAvatar, user.getUserAvatar())
                .set(User::getUpdateTime, LocalDateTime.now());

        if (isAdmin && ObjectUtil.isNotEmpty(user.getUserRole())) {          // ← 值也要判空
            if (!"user".equals(user.getUserRole()) && !"admin".equals(user.getUserRole())) {
                throw new RuntimeException("角色不合法");
            }
            w.set(User::getUserRole, user.getUserRole());
        }
        if (!update(null, w)) throw new RuntimeException("更新失败");
    }

    /**
     * 查询用户的方法
     *
     * @param userQueryDto 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<UserQueryVo> queryList(UserQueryDto userQueryDto) {
        // 判断是否是管理员
        Long currentId = CurrentHolder.getCurrentId();
        User currentUser = getById(currentId);
        boolean isAdmin = "admin".equals(currentUser.getUserRole());
        if (!isAdmin) {
            // 不是管理员直接抛异常
            throw new RuntimeException("无权限查询用户");
        }

        // 构建查询条件
        Page<User> page = new Page<>(userQueryDto.getPageNum(), userQueryDto.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .like(ObjectUtil.isNotEmpty(userQueryDto.getUserName()), User::getUserName, userQueryDto.getUserName())
                .eq(ObjectUtil.isNotEmpty(userQueryDto.getUserRole()), User::getUserRole, userQueryDto.getUserRole())
                .eq(ObjectUtil.isNotEmpty(userQueryDto.getUserAccount()),User::getUserAccount , userQueryDto.getUserAccount())
                .orderByDesc(User::getCreateTime);
        // 查询结果
        Page<User> userPage = page(page, queryWrapper);
        log.info("查询到对象：{}", userPage);
        return new PageResult<>(userPage.getTotal(),
                userPage.getRecords().stream()
                        .map(user -> BeanUtil.copyProperties(user, UserQueryVo.class))
                        .toList()
        );
    }

}

package com.zzsd.inolibi.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.entity.User;
import com.zzsd.inolibi.mapper.UserMapper;
import com.zzsd.inolibi.service.IUserService;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author inoli
 * @since 2026-10-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}

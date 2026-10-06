package com.zzsd.inolibi.entity.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private Long id;
    private String userAccount;
    private String token;
}

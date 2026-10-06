package com.zzsd.inolibi.entity.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录实体
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@Schema(name = "User", description = "用户注册实体")
public class UserLoginDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    @Schema(description = "账号")
    private String userAccount;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String userPassword;

}
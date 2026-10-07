package com.zzsd.inolibi.entity.user.vo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 当前登录用户信息（不含密码等敏感字段）
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@Schema(name = "CurrentUserVo", description = "当前登录用户信息")
@JsonPropertyOrder({
        "id", "userRole", "userName", "userAccount",
        "userAvatar", "createTime"
})
public class CurrentUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 用户角色：user/admin
     */
    @Schema(description = "用户角色：user/admin")
    private String userRole;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

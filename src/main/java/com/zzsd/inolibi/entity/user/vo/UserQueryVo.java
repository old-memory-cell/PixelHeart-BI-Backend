package com.zzsd.inolibi.entity.user.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户查询返回实体
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@Schema(name = "User", description = "用户查询返回实体")
public class UserQueryVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 5694494264287305908L;

    /**
     * id
     */
    @Schema(description = "id")
    @TableField("id")
    private Long id;

    /**
     * 用户角色：user/admin
     */
    @Schema(description = "用户角色：user/admin")
    @TableField("user_role")
    private String userRole;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @TableField("user_name")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "账号")
    @TableField("user_account")
    private String userAccount;


    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    @TableField("user_avatar")
    private String userAvatar;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

}

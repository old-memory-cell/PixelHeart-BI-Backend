package com.zzsd.inolibi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@TableName("user")
@Schema(name = "User", description = "用户")
@JsonPropertyOrder({
        "id", "userRole", "userName", "userAccount",
        "userPassword", "userAvatar",
        "createTime", "updateTime", "isDelete"
})
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
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
     * 密码
     */
    @Schema(description = "密码")
    @TableField("user_password")
    private String userPassword;

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

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    @TableField("is_delete")
    private Integer isDelete;
}
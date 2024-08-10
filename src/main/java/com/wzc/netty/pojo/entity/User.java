package com.wzc.netty.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName user
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user")
public class User implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 
     */
    private String openId;

    /**
     * IP详情
     */
    private String ipInfo;


    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 登录状态 1:在线 0:离线
     */
    private Integer status;

    /**
     * 上线时间
     */
    private LocalDateTime onlineTime;

    /**
     * 离线时间
     */
    private LocalDateTime offlineTime;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：1:删除；0:未删除
     */
    @TableLogic
    private Boolean delFlag;


}
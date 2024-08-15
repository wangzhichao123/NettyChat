package com.wzc.netty.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user_relationship")
public class UserRelationship {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 发送方用户ID
     */
    private String userFromId;

    /**
     * 接收方用户ID
     */
    private String userToId;

    /**
     * 好友申请状态: 1-待验证, 2-已验证, 3-已拒绝
     */
    private Integer status;

    /**
     * 添加好友时间
     */
    private LocalDateTime addTime;

    /**
     * 删除好友时间
     */
    private LocalDateTime delTime;

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
     * 逻辑删除：1：删除；0:未删除
     */
    @TableLogic
    private Boolean delFlag;
}


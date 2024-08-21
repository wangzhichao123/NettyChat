package com.wzc.netty.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.lettuce.core.resource.Delay;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName message
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="message")
public class Message implements Serializable{

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送用户ID
     */
    private String userFromId;

    /**
     * 接收用户ID
     */
    private String userToId;

    /**
     * 发送消息类型 1：私聊消息 2：群组消息
     */
    private Integer messageType;


    /**
     * 消息类型 1：文本消息和emoji消息 2：图片消息 3：文件消息 4：语音消息 5：视频消息
     */
    private Integer sendMessageType;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 消息内容
     */
    private String sendMessageContent;

    /**
     * 消息状态 1：消息初始化；2：消息发送中；3：消息发送成功；4：消息发送失败；5：消息撤回
     */
    private Integer messageStatus;

    /**
     * 消息发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 消息接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：1：删除；0:未删除
     */
    @TableLogic
    private Boolean delFlag;


}
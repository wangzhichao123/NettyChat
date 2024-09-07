package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    @ApiModelProperty("发送用户ID")
    private String userFromId;

    @ApiModelProperty("接收用户ID")
    private String userToId;

    @ApiModelProperty("消息ID")
    private String messageId;

    @ApiModelProperty("群组ID")
    private String groupId;

    @ApiModelProperty("展示状态 0：不能展示 1：可能展示")
    private Integer displayStatus;

    @ApiModelProperty("消息类型 1：私聊消息 2：群组消息")
    private Integer messageType;

    @ApiModelProperty("发送消息类型 1：文本消息和Emoji-表情包消息 2：图片/图片表情包消息 3：文件消息 4：语音消息 5：视频消息")
    private Integer sendMessageType;

    @ApiModelProperty(name = "sendMessageContent", value = "发送消息内容", required = true, dataType = "String")
    private String sendMessageContent;

    @ApiModelProperty(name = "sendMessageFile", value = "发送的文件", dataType = "MultipartFile")
    private MultipartFile sendFile;

    @ApiModelProperty("发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty("接收时间")
    private LocalDateTime receiveTime;


}

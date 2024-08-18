package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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

    @ApiModelProperty("发送消息ACK")
    private Long sendMessageAck;

    @ApiModelProperty("接收消息ACK")
    private Long receiveMessageAck;

    @ApiModelProperty("消息是否可以展示：1：展示 0：不展示")
    private Integer sendDisplay;

    @ApiModelProperty("消息是否可以展示：1：展示 0：不展示")
    private Integer receiveDisplay;

    @ApiModelProperty("发送消息类型 1：私聊消息 2：群组消息")
    private Integer MessageType;

    @ApiModelProperty("消息类型 1：文本消息和Emoji-表情包消息 2：图片/图片表情包消息 3：文件消息 4：语音消息 5：视频消息 ")
    private Integer sendMessageType;

    @ApiModelProperty(name = "sendMessageContent", value = "发送消息内容", required = true, dataType = "String")
    private String sendMessageContent;

    @ApiModelProperty(name = "sendMessageFile", value = "发送的文件", dataType = "MultipartFile")
    private MultipartFile sendMessageFile;

    @ApiModelProperty("发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty("接收时间")
    private LocalDateTime receiveTime;

}

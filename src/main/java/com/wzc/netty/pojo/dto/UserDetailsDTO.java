package com.wzc.netty.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO{

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("登录类型")
    private Integer loginType;

    @ApiModelProperty("IP详情")
    private String ipInfo;

    @ApiModelProperty("在线状态 1:在线 0:离线")
    private Integer status;

    @ApiModelProperty("用户头像")
    private String userAvatar;

    @ApiModelProperty("token")
    private String token;


}

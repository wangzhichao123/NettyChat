package com.wzc.netty.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "UserFriendsInfoVo")
public class UserFriendsInfoVo {

    @ApiModelProperty(name = "排序ID")
    private Long id;

    @ApiModelProperty(name = "用户ID")
    private String userId;

    @ApiModelProperty(name = "用户昵称")
    private String nickname;

    @ApiModelProperty(name = "用户头像")
    private String userAvatar;

    @ApiModelProperty(name = "登录状态")
    private Integer status;
}

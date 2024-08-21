package com.wzc.netty.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.dto.ChatMessageDTO;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "消息接口")
@RestController
@RequestMapping("/api/msg")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/friend")
    @ApiOperation(value = "好友消息")
    public R<Page<ChatMessageDTO>> getUserFriendMessage(
            @RequestParam("userFromId") String userFromId,
            @RequestParam("userToId") String userToId) {
        return R.ok(messageService.getUserFriendMessage(userFromId, userToId));
    }

    @PostMapping("/group")
    @ApiOperation(value = "群组消息")
    public R<Page<ChatMessageDTO>> getUserGroupMessage(@RequestParam("userFromId") String userFromId, @RequestParam("groupId") String groupId) {
        return R.ok(messageService.getUserGroupMessage(userFromId, groupId));
    }

//    @PostMapping("/relationship")
//    @ApiOperation(value = "最新一条消息")
//    public R<Page<UserFriendsInfoVo>> getUserFriendList(@RequestParam("userId") String userId) {
//        return R.ok(messageService.getUserFriendListByUserId(userId));
//    }
}

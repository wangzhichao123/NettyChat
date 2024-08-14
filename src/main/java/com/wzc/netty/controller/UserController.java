package com.wzc.netty.controller;

import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;
import com.wzc.netty.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;


@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/relationship")
    @ApiOperation(value = "获取用户好友列表")
    public R<List<UserFriendsInfoVo>> getUserFriendList(@RequestParam("userId") @Valid @NotBlank String userId) {
        return R.ok(userService.getUserFriendListByUserId(userId));
    }

    @PostMapping("/search/relationship")
    @ApiOperation(value = "获取搜索用户信息")
    public R<UserSearchInfoVo> getSearchUserInfo(@RequestParam("userId") @Valid @NotBlank String userId) {
        return R.ok(userService.getSearchUserInfoByUserId(userId));
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加用户")
    public R<Boolean> addUser(@RequestParam("userFromId") @Valid @NotBlank String userFromId,
                              @RequestParam("userToId") @Valid @NotBlank String userToId) {
        return R.ok(userService.addUser(userFromId, userToId));
    }


}

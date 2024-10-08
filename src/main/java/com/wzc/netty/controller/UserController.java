package com.wzc.netty.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzc.netty.pojo.R;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;
import com.wzc.netty.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/relationship")
    @ApiOperation(value = "好友列表")
    public R<Page<UserFriendsInfoVo>> getUserFriendList(@RequestParam("userId") String userId) {
        return R.ok(userService.getUserFriendListByUserId(userId));
    }

    @PostMapping("/relationship/cursor")
    @ApiOperation(value = "游标好友列表")
    public R<Page<UserFriendsInfoVo>> getCursorUserFriendList(@RequestParam("userId") String userId, @RequestParam("id") Long id) {
        return R.ok(userService.getCursorUserFriendList(userId, id));
    }

    @PostMapping("/friend/application")
    @ApiOperation(value = "好友申请列表")
    public R<Page<UserFriendsInfoVo>> getUserApplicationList(@RequestParam("userId") String userId) {
        return R.ok(userService.getUserApplicationList(userId));
    }

    @PostMapping("/friend/application/cursor")
    @ApiOperation(value = "游标好友申请列表")
    public R<Page<UserFriendsInfoVo>> getCursorUserApplicationList(@RequestParam("userId") String userId, @RequestParam("id") Long id) {
        return R.ok(userService.getCursorUserApplicationList(userId, id));
    }

    @PostMapping("/search/relationship")
    @ApiOperation(value = "获取搜索用户信息")
    public R<UserSearchInfoVo> getSearchUserInfo(@RequestParam("userId") String userId) {
        return R.ok(userService.getSearchUserInfoByUserId(userId));
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加好友")
    public R<Boolean> addUser(@RequestParam("userFromId") String userFromId,
                              @RequestParam("userToId") String userToId) {
        return R.ok(userService.addFriend(userFromId, userToId));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除好友")
    public R<Boolean> deleteUser(@RequestParam("userFromId") String userFromId,
                                 @RequestParam("userToId") String userToId) {
        return R.ok(userService.delFriend(userFromId, userToId));
    }
    @PostMapping("/approve")
    @ApiOperation(value = "通过/拒绝好友申请信息")
    public R<Boolean> approveUser(@RequestParam("userFromId") String userFromId,
                                  @RequestParam("userToId") String userToId,
                                  @RequestParam("code") Integer code) {
        return R.ok(userService.approveOrRejectUser(userFromId, userToId, code));
    }

}

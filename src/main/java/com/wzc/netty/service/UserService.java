package com.wzc.netty.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;

import java.util.List;

/**
 * @author wzc
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-07-11 23:15:06
 */
public interface UserService extends IService<User> {

    Page<UserFriendsInfoVo> getUserFriendListByUserId(String userId);

    UserSearchInfoVo getSearchUserInfoByUserId(String userId);

    Boolean addFriend(String userFromId, String userToId);

    Boolean delFriend(String userFromId, String userToId);

    Boolean approveOrRejectUser(String userFromId, String userToId, Integer code);

    Page<UserFriendsInfoVo> getUserApplicationList(String userId);

    Page<UserFriendsInfoVo> getCursorUserApplicationList(String userId, Long id);

    Page<UserFriendsInfoVo> getCursorUserFriendList(String userId, Long id);
}

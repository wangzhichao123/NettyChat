package com.wzc.netty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;

import java.util.List;

/**
* @author wzc
* @description 针对表【user】的数据库操作Service
* @createDate 2024-07-11 23:15:06
*/
public interface UserService extends IService<User> {

    List<UserFriendsInfoVo> getFriendsByUserId(String userId);
}

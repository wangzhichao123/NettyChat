package com.wzc.netty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzc.netty.pojo.entity.User;
import com.wzc.netty.pojo.vo.UserFriendsInfoVo;
import com.wzc.netty.pojo.vo.UserSearchInfoVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wzc
 * @description 针对表【user】的数据库操作Mapper
 * @createDate 2024-07-11 23:15:06
 * @Entity generator.domain.User
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 保存用户登录状态
     * @param userId
     * @param status
     * @param onlineTime
     * @param offlineTime
     */
    void saveUserLoginStatus(@Param("userId") String userId, @Param("status") Integer status, @Param("onlineTime") LocalDateTime onlineTime, @Param("offlineTime") LocalDateTime offlineTime);

    /**
     * 根据用户ID查询用户
     * @param userId
     * @return
     */
    User queryUserByUserId(@Param("userId") String userId);

    /**
     * 获取用户好友列表
     * @param userId
     * @return
     */

    Page<UserFriendsInfoVo> getUserFriendListByUserId(Page<UserFriendsInfoVo> page,
                                                      @Param("userId") String userId,
                                                      @Param("status") Integer status);

    /**
     * 获取搜索用户信息
     * @param userId
     * @return
     */
    UserSearchInfoVo getSearchUserInfoByUserId(@Param("userId") String userId);

    /**
     * 获取好友申请列表
     * @param page
     * @param userId
     * @param status
     * @return
     */
    Page<UserFriendsInfoVo> getUserApplicationList(Page<UserFriendsInfoVo> page,
                                                   @Param("userId") String userId,
                                                   @Param("status") Integer status);

    /**
     * 获取游标好友申请列表
     * @param page
     * @param userId
     * @param status
     * @return
     */
    Page<UserFriendsInfoVo> getCursorUserApplicationList(Page<UserFriendsInfoVo> page,
                                                         @Param("userId") String userId,
                                                         @Param("id") Long id,
                                                         @Param("status") Integer status);
}





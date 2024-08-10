package com.wzc.netty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzc.netty.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

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
     * @param code
     * @param onlineTime
     * @param offlineTime
     */
    void saveUserLoginStatus(@Param("userId") String userId, @Param("code") Integer code, @Param("onlineTime") LocalDateTime onlineTime, @Param("offlineTime") LocalDateTime offlineTime);

    /**
     * 根据用户ID查询用户
     * @param userId
     * @return
     */
    User queryUserByUserId(@Param("userId") String userId);
}





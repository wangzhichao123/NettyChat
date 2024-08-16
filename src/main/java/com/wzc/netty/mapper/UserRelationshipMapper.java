package com.wzc.netty.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzc.netty.pojo.entity.UserRelationship;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserRelationshipMapper extends BaseMapper<UserRelationship> {

    /**
     * 查询用户关系
     * @param userFromId
     * @param userToId
     * @param validCodeList
     * @return
     */
    UserRelationship queryUserRelationship(@Param("userFromId") String userFromId, @Param("userToId") String userToId, @Param("validCodeList") List<Integer> validCodeList);

}

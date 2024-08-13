package com.wzc.netty.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzc.netty.pojo.entity.Message;
import org.apache.ibatis.annotations.Param;

/**
* @author wzc
* @description 针对表【message】的数据库操作Mapper
* @createDate 2024-07-11 23:15:06
* @Entity generator.domain.Message
*/
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 更新消息状态
     * @param messageId
     * @param messageStatus
     * @return
     */
    boolean updateMessageStatus(@Param("messageId") String messageId, @Param("messageStatus") Integer messageStatus);

    /**
     * 查询私聊消息
     * @param messageId
     * @param userFromId
     * @param userToId
     * @return
     */
    Message queryPrivateMessage(@Param("messageId") String messageId, @Param("userFromId") String userFromId, @Param("userToId") String userToId);

    /**
     * 查询群聊消息
     * @param messageId
     * @param userFromId
     * @param groupId
     * @return
     */
    Message queryGroupMessage(@Param("messageId") String messageId, @Param("userFromId") String userFromId, @Param("groupId") String groupId);
}





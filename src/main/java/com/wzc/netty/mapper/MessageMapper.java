package com.wzc.netty.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzc.netty.pojo.entity.Message;

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
     * @param status
     * @return
     */
    boolean updateMessageStatus(String messageId, Integer status);

    /**
     * 查询私聊消息
     * @param messageId
     * @param userFromId
     * @param userToId
     * @return
     */
    Message queryPrivateMessage(String messageId, String userFromId, String userToId);

    /**
     * 查询群聊消息
     * @param messageId
     * @param userFromId
     * @param groupId
     * @return
     */
    Message queryGroupMessage(String messageId, String userFromId, String groupId);
}





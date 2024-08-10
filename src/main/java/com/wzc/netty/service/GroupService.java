package com.wzc.netty.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wzc.netty.pojo.entity.Group;

/**
* @author wzc
* @description 针对表【group】的数据库操作Service
* @createDate 2024-07-11 23:15:06
*/
public interface GroupService extends IService<Group> {

    void createChatRoom(String chatRoomName);

}

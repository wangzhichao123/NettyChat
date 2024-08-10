package com.wzc.netty.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzc.netty.mapper.MessageMapper;
import com.wzc.netty.pojo.entity.Message;
import com.wzc.netty.service.MessageService;
import org.springframework.stereotype.Service;

/**
* @author wzc
* @description 针对表【message】的数据库操作Service实现
* @createDate 2024-07-11 23:15:06
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

}





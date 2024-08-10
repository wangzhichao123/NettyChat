package com.wzc.netty;

import cn.hutool.core.lang.UUID;
import com.wzc.netty.mapper.MessageMapper;
import com.wzc.netty.pojo.entity.Message;
import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;
import net.fellbaum.jemoji.IndexedEmoji;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class NettyDemoApplicationTests {

    @Resource
    private MessageMapper messageMapper;

    @Test
    void contextLoads() {

        boolean b = StringUtils.hasText("   ");
        System.out.println(b);
        //模拟从前端获得的密码
        String password = "123456";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String newPassword = bCryptPasswordEncoder.encode(password);
        System.out.println("加密的密码为: "+newPassword);
        boolean same_password_result = bCryptPasswordEncoder.matches(password,newPassword);
        //返回true
        System.out.println("相同代码对比: "+same_password_result);
        boolean other_password_result = bCryptPasswordEncoder.matches("1234456",newPassword);
        //返回false
        System.out.println("其他密码对比: " + other_password_result);

    }

    @Test
    void test() {
        String sss = "我今天挺好的dsada111\uD83D\uDE031111111";
//        Optional<Emoji> emoji = EmojiManager.getEmoji(sss);
//        List<IndexedEmoji> indexedEmojis = EmojiManager.extractEmojisInOrderWithIndex(sss);
//        for (int i = 0; i < indexedEmojis.size(); i++) {
//            int charIndex = indexedEmojis.get(i).getCharIndex();
//            String s = indexedEmojis.get(i).getEmoji().getEmoji();
//            System.out.println(charIndex+" "+s);
//        }
//        if(emoji.isPresent()){
//            String content = emoji.get().getEmoji();
//            Message message = new Message();
//            message.setMessageContent(content);
//            messageMapper.insert(message);
//            System.out.println(content);
//        }
        Message message = new Message();
        message.setMessageContent(sss);
        messageMapper.insert(message);

        String s = UUID.randomUUID().toString();
        System.out.println(s);
    }

}

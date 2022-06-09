package com.nowcoder.community;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelect() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }

        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
        List<Message> letters = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : letters) {
            System.out.println(message);
        }

        int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println(i1);

        int i2 = messageMapper.selectLetterUnreadCount(111, "111_131");
        System.out.println(i2);
    }
}

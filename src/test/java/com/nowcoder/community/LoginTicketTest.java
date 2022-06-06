package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketTest {

    @Autowired
    private LoginTicketMapper mapper;

    @Test
    public void insertTest() {
        LoginTicket ticket = new LoginTicket();
        ticket.setTicket("2edfrdvbgfgc");
        ticket.setUserId(12);
        ticket.setExpired(new Date());
        ticket.setStatus(0);
        mapper.insertLoginTicket(ticket);
    }

    @Test
    public void updateTest() {
        mapper.updateStatus("2edfrdvbgfgc", 0);
        LoginTicket ticket = mapper.selectByTicket("2edfrdvbgfgc");
        System.out.println(ticket);
    }
}

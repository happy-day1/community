package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient client;

    @Autowired
    private TemplateEngine engine;

    @Test
    public void testTestMail() {
        client.sendMail("18268301639@163.com", "test", "hello");
    }

    @Test
    public void testHtml() {
        Context context = new Context();
        context.setVariable("username", "Lily");
        String process = engine.process("/mail/demo", context);
        System.out.println(process);
        client.sendMail("18268301639@163.com", "Html", process);
    }
}

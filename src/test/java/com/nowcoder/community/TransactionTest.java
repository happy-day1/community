package com.nowcoder.community;

import com.nowcoder.community.service.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {

    @Autowired
    private DemoService demoService;

    @Test
    public void testSave1() {
        System.out.println(demoService.save1());
    }
}

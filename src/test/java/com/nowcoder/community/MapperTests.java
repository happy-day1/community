package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper postMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void insertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.noecoder.com/101.png");
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void updateTest() {
        int i = userMapper.updateUserStatus(150, 1);
        System.out.println(i);
        int abc = userMapper.updateUserPassword(150, "abc");
        System.out.println(abc);
        userMapper.updateUserHeaderUrl(150,"http://www.noecoder.com/1.png");
    }

    @Test
    public void discussPostsTest() {
        List<DiscussPost> discussPosts = postMapper.selectDiscussPosts(149, 0, 10, 0);
        for (DiscussPost discussPost: discussPosts) {
            System.out.println(discussPost);
        }
        int rows = postMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void test() {
        List<int[]> l = new ArrayList<int[]>();
        l.add(new int[] {1,2});
        int[][] ints = l.toArray(new int[l.size()][2]);
        System.out.println(Arrays.toString(ints));
    }

    @Test
    public void deleteTest() {
        int i = userMapper.deleteUser(151);
        System.out.println(i);
    }

}

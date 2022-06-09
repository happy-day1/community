package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (user==null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMassage", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMassage", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMassage", "邮箱不能为空");
            return map;
        }

        // 验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u!=null) {
            map.put("usernameMassage", "该账号已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u!=null) {
            map.put("emailMassage", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/community/activation/101/code
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url", url);
        String process = engine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", process);
        return map;
    }

    public int activation(int id, String activationCode) {
        User user = userMapper.selectById(id);
        if (user.getStatus()==1) {
            return ACTIVATION_REPEAT;
        } else {
            if (user.getActivationCode().equals(activationCode)) {
                userMapper.updateUserStatus(id, 1);
                return ACTIVATION_SUCCESS;
            } else {
                return ACTIVATION_FAIL;
            }
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSecond) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user==null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if (user.getStatus()==0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        // 验证密码
        String encode = CommunityUtil.md5(password + user.getSalt());
        if (!encode.equals(user.getPassword())) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        // 生成登录凭证
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId());
        ticket.setTicket(CommunityUtil.generateUUID());
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis()+expiredSecond*1000));
        loginTicketMapper.insertLoginTicket(ticket);
        map.put("ticket", ticket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeaderUrl(int userId, String headUrl) {
        return userMapper.updateUserHeaderUrl(userId, headUrl);
    }

    public int updatePassword(int userId, String password) {
        return userMapper.updateUserPassword(userId, password);
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
}

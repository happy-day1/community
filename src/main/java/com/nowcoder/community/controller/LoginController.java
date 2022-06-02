package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> register = userService.register(user);
        if (register==null || register.isEmpty()) {
            model.addAttribute("msg", "注册成功，已向你的邮箱发送一封激活邮件，请点击激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", register.get("usernameMassage"));
            model.addAttribute("passwordMsg", register.get("passwordMassage"));
            model.addAttribute("emailMsg", register.get("emailMassage"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(
            Model model,
            @PathVariable("userId") int userId,
            @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result==ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功");
            model.addAttribute("target", "/login");
        } else if (result==ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }
}

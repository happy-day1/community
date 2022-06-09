package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 在spring mvc中，controller方法调用前，spring mvc会自动实例化Model和Page
        // 所以在thymeleaf中可以直接访问呢Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPostList = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussesPostMap = new ArrayList<Map<String, Object>>();
        if (discussPostList!=null) {
            for (DiscussPost discussPost : discussPostList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                discussesPostMap.add(map);
            }
        }
        model.addAttribute("discussPosts", discussesPostMap);
        return "index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

}

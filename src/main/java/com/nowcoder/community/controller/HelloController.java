package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello() {
        return "hello spring boot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        System.out.println(request.getParameter("code"));
        response.setContentType("text.html;charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求
    // /student?current=1&limit=20
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some student";
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getAStudent(
            @PathVariable("id") int id) {
        System.out.println(id);
        return "some student";
    }

    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 30);
        // 模板放置在templates目录下，但在setViewName从templates的下级目录开始写，并且省略模板的.html格式。
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchools(Model model) {
        model.addAttribute("name", "PKU");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    // 响应JSON数据（异步请求）
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<String, Object>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }
}

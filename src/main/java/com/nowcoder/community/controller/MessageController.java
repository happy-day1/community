package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder holder;

    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = holder.getUser();

        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 查询会话列表
        List<Message> conversationList =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<Map<String, Object>>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                int targetId = (user.getId()== message.getFromId())? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  Page page, Model model) {
        User user = holder.getUser();
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        // 得到私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        // 查询私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (ids != null && !ids.isEmpty()) {
            messageService.setMessageRead(ids);
        }
        return "/site/letter-detail";
    }

    // 得到一个会话中所有未读的消息的id
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<Integer>();
        if (letterList!=null) {
            for (Message message : letterList) {
                if ((message.getStatus()==0) && (message.getToId()==holder.getUser().getId())) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    public User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);

        if (holder.getUser().getId()==d0) {
            return userService.findUserById(d1);
        }
        return userService.findUserById(d0);
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = userService.findUserByName(toName);
        if (toUser==null) {
            return CommunityUtil.getJSONString(1, "用户不存在");
        }
        Message message = new Message();
        message.setContent(content);
        message.setFromId(holder.getUser().getId());
        message.setToId(toUser.getId());
        StringBuffer buffer = new StringBuffer();
        if (message.getFromId()<message.getToId()) {
            buffer.append(message.getFromId());
            buffer.append("_");
            buffer.append(message.getToId());
        } else {
            buffer.append(message.getToId());
            buffer.append("_");
            buffer.append(message.getFromId());
        }
        String conversationId = buffer.toString();
        message.setConversationId(conversationId);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = holder.getUser();

        String[] topics = new String[] {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW};
        String[] modelNames = new String[] {"commentNotice", "likeNotice", "followNotice"};

        for (int i=0; i<topics.length; i++) {
            Map<String, Object> messageVO = new HashMap<String, Object>();
            Message latestComment = messageService.findLatestNotice(user.getId(), topics[i]);
            if (latestComment!=null) {
                messageVO.put("message", latestComment);
                String content = HtmlUtils.htmlUnescape(latestComment.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
                messageVO.put("entityType", data.get("entityType"));
                messageVO.put("entityId", data.get("entityId"));
                messageVO.put("postId", data.get("postId"));
                int count = messageService.findNoticeCount(user.getId(), topics[i]);
                messageVO.put("count", count);
                int unread = messageService.findUnreadCount(user.getId(), topics[i]);
                messageVO.put("unread", unread);
            }
            model.addAttribute(modelNames[i], messageVO);
        }
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = holder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<Map<String, Object>>();
        if (noticeList != null) {
            for (Message message : noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", message);
                String content = HtmlUtils.htmlUnescape(message.getContent());
                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(message.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (ids != null) {
            messageService.setMessageRead(ids);
        }
        return "/site/notice-detail";
    }
}

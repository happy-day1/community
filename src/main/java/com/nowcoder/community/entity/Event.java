package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    // 主题类型
    private String topic;
    // 触发事件的用户
    private int userId;
    // 实体类型
    private int entityType;
    // 实体的id
    private int entityId;
    // 实体对应的userId，如果实体是帖子或评论，对应为帖子或评论的创造者，实体是用户，对应是该用户的id。
    private int entityUserId;

    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}

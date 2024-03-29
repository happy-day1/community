package com.nowcoder.community.util;

public interface CommunityConstant {

    // 激活成功
    int ACTIVATION_SUCCESS = 0;
    // 重复激活
    int ACTIVATION_REPEAT = 1;
    // 激活失败
    int ACTIVATION_FAIL = 2;

    // 默认状态的登录凭证超时时间
    int DEFAULT_EXPIRED_SECOND = 3600*12;

    // 记住我状态下的登录凭证超时时间
    int REMEMBER_EXPIRED_SECOND = 3600*24*15;

    //实体类型：帖子，评论
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;

    // 事件主题
    // 主题：评论
    String TOPIC_COMMENT = "comment";
    // 主题：点赞
    String TOPIC_LIKE = "like";
    // 主题：关注
    String TOPIC_FOLLOW = "follow";
    // 主题：发帖
    String TOPIC_PUBLISH = "publish";
    // 主题：删帖
    String TOPIC_DELETE = "delete";

    //系统用户的ID
    int SYSTEM_USER_ID = 1;

    // 权限：普通用户
    String AUTHORITY_USER = "user";
    // 权限：版主
    String AUTHORITY_MODERATOR = "moderator";
    // 权限：管理员
    String AUTHORITY_ADMIN = "admin";
}

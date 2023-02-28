package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPILT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_CAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";

    // 某个实体的赞
    // like:entity:entityType:entityId->Set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE+SPILT+entityType+SPILT+entityId;
    }

    // 某一个用户收到的赞
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE+SPILT+userId;
    }

    // 某一个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int entityType, int userId) {
        return PREFIX_FOLLOWEE+SPILT+userId+SPILT+entityType;
    }

    // 某一个用户拥有的粉丝
    // follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER+SPILT+entityType+SPILT+entityId;
    }

    public static String getCaptchaKey(String owner) {
        return PREFIX_CAPTCHA+SPILT+owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET+SPILT+ticket;
    }

    public static String getUserKey(int userId) {
        return PREFIX_USER+SPILT+userId;
    }

    // 单日UV
    public static String getUVKey(String data) {
        return PREFIX_UV+SPILT+data;
    }

    // 区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV+SPILT+startDate+SPILT+endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU+SPILT+date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU+SPILT+startDate+SPILT+endDate;
    }
    // 返回统计帖子分数
    public static String getPostScoreKey() {
        return PREFIX_POST + SPILT + "score";
    }

}

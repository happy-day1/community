package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    // 关注功能
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    // 查询某一个用户关注实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String foloweeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().zCard(foloweeKey);
    }

    // 查询某一个实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String foloweeKey = RedisKeyUtil.getFolloweeKey(entityType, userId);
        return redisTemplate.opsForZSet().score(foloweeKey, entityId)!=null ? true : false;
    }

    // 查询当前用户关注的人
    public List<Map<String, Object>> findFollowee(int userId, int offset, int limit) {
        String foloweeKey = RedisKeyUtil.getFolloweeKey(ENTITY_TYPE_USER, userId);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(foloweeKey, offset, offset + limit - 1);
        if (followeeIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : followeeIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            Double score = redisTemplate.opsForZSet().score(foloweeKey, id);
            map.put("user", user);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 查询某一个用户的粉丝
    public List<Map<String, Object>> findFollower(int userId, int offset, int limit) {
        String folowerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(folowerKey, offset, offset + limit - 1);
        if (followerIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : followerIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            Double score = redisTemplate.opsForZSet().score(folowerKey, id);
            map.put("user", user);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}

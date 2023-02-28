package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    // @Param注解用于给参数取别名
    // 如果方法只有一个参数，并且在<if>里使用，必须取别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPostRows(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateDiscussPostCount(int id, int commentCount);

    int updateDiscussPostType(int id, int type);

    int updateDiscussPostStatus(int id, int status);

    int updateDiscussPostScore(int id, double score);
}

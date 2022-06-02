package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateUserStatus(int id, int status);

    int updateUserHeaderUrl(int id, String headerUrl);

    int updateUserPassword(int id, String password);

    int deleteUser(int id);
}

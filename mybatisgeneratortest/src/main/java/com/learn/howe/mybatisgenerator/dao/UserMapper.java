package com.learn.howe.mybatisgenerator.dao;

import com.learn.howe.mybatisgenerator.model.User;
import com.learn.howe.mybatisgenerator.model.UserExample;
import java.util.List;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);
}
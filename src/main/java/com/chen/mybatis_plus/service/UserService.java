package com.chen.mybatis_plus.service;

import com.chen.mybatis_plus.model.User;

import java.util.List;

public interface UserService {
    List<User> select();
    boolean insert1(User user);
    int update(User user);
    int delete(User user);
    List<User> selectWrapper();
    List<User> selectPage();
}

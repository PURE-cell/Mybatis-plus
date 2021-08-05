package com.chen.mybatis_plus.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.mybatis_plus.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao extends BaseMapper<User> {
    boolean insert1(@Param("user") User user);
}

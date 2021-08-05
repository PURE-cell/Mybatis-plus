package com.chen.mybatis_plus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.model.User;
import com.chen.mybatis_plus.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public List<User> select() {
        return userDao.selectList(null);
    }

    @Override
    public boolean insert1(User user) {
        return userDao.insert1(user);
    }

    @Override
    public int update(User user) {
        return userDao.updateById(user);
    }

    @Override
    public int delete(User user) {
        if (StringUtils.isNotEmpty(user.getId())){
            userDao.deleteById(user);
            return 1;
        }
        return 0;
    }

    @Override
    public List<User> selectWrapper() {
        //查询姓名不为空,邮箱不为空,年龄大于18
        QueryWrapper<User> lq = new QueryWrapper<>();
//        lq.isNotNull("name").isNotNull("email").gt("age", 18);
//        lq.eq("age", 18);
//        lq.between("age", 20, 24);
//        lq.like("name", "il");
//        lq.likeLeft("name", "Bil");
//        lq.likeRight("name", "Bil");
//        lq.in("age", 18,20,23);
//        lq.inSql("id", "select uid from role,user where user.id = role.uid ");
//        lq.select("count(*)").groupBy("name");
//        lq.select("name,count(name)").groupBy("name").having("count(*) = 2");
//        lq.func(i -> {
//           if (true){
//               lq.select("name");
//           }else {
//               lq.eq("name", "Tom");
//           }
//        });
//        lq.gt("age", 22).or().lt("age", 12);
//        lq.exists("select id from user where age = 18");
        return userDao.selectList(lq);
    }

    @Override
    public List<User> selectPage() {
        //参数一： 当前页数
        //参数二： 每页多少条数据
        Page<User> page = new Page<>(1, 3);
        userDao.selectPage(page, null);
        List<User> pageRecords = page.getRecords();
        return pageRecords;
    }

}

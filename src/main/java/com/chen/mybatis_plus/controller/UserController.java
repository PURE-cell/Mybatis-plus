package com.chen.mybatis_plus.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chen.mybatis_plus.common.Response;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.model.User;
import com.chen.mybatis_plus.service.UserService;
import com.chen.mybatis_plus.service.impl.WebSocketServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@Api(description = "用户管理操作")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserDao userDao;

    @Resource
    private WebSocketServiceImpl webSocketService;

    /*=====================mybatis-plus实现增删查改======================*/
    @ApiOperation(value = "获取用户列表", notes = "获取用户所有数据")
    @GetMapping("/select")
    public Response<List<User>> select(){
        List<User> userInfo = userService.select();
        /*=====================List数据======================*/
        log.info(String.valueOf(userInfo));
        /*=====================Map数据======================*/
        Map check = new HashMap();
        userInfo.forEach(info -> {
            check.put("name", info.getName());
            check.put("age", info.getAge());
            check.put("email", info.getEmail());

        });
        log.info("/*=====================Map数据======================*/");
        check.forEach((k,v) -> log.info("key:value = " + k + ":" + v));
        log.info(String.valueOf(check));
        return new Response<List<User>>(userInfo);
    }

    @ApiOperation(value = "插入用户数据", notes = "将用户数据插入数据库表user中")
    @PostMapping("/insert")
    public Response insert(@RequestBody JSONObject res){
//        String name = res.getString("name");
//        String age = res.getString("age");
//        String email = res.getString("email");
        User user = JSONObject.toJavaObject(res,User.class);
//        User user = new User();
//        user.setName(name);
//        user.setAge(Integer.parseInt(age));
//        user.setEmail(email);
//        if (userService.insert1(user)){
//            return new Response();
//        }
        if (userDao.insert(user) != 0){
            /*=====================websocket推送信息的使用======================*/
            try {
                webSocketService.sendMessage("新增个人信息成功！");
            }catch (Exception e){
                e.printStackTrace();
            }
            return new Response();
        }
        return new Response("更改失败!");
    }

    @ApiOperation(value = "更新用户数据", notes = "根据用户Id更新用户数据到数据库表user中")
    @PostMapping("/update")
    public Response update(@RequestBody JSONObject res){
        User user = JSONObject.toJavaObject(res, User.class);
        int update = userService.update(user);
        log.info(String.valueOf(update));
        return new Response();
    }

    @ApiOperation(value = "删除用户数据", notes = "")
    @DeleteMapping("/delete")
    public Response delete(@ApiParam(value = "需要删除的数据") @RequestBody JSONObject res){
        User user = JSONObject.toJavaObject(res, User.class);
        int i = userService.delete(user);
        if ( i == 1){
            return new  Response();
        }
        return new Response("删除失败!");
    }

    /*========================条件构造器的使用==========================*/
    @ApiOperation(value = "条件构造器的使用")
    @GetMapping("/selectWrapper")
    public Response selectWrapper(){
        List<User> userList = userService.selectWrapper();
        return new Response(userList);
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/selectPage")
    public Response selectPage(){
        List<User> userList = userService.selectPage();
        return new Response(userList);
    }


}

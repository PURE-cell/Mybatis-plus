package com.chen.mybatis_plus.controller;

import com.alibaba.fastjson.JSONObject;
import com.chen.mybatis_plus.common.Response;
import com.chen.mybatis_plus.model.User;
import com.chen.mybatis_plus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /*=====================mybatis-plus实现增删查改======================*/
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
        if (userService.insert1(user)){
            return new Response();
        }
        return new Response("更改失败!");
    }

    @PostMapping("/update")
    public Response update(@RequestBody JSONObject res){
        User user = JSONObject.toJavaObject(res, User.class);
        int update = userService.update(user);
        log.info(String.valueOf(update));
        return new Response();
    }

    @DeleteMapping("/delete")
    public Response delete(@RequestBody JSONObject res){
        User user = JSONObject.toJavaObject(res, User.class);
        int i = userService.delete(user);
        if ( i == 1){
            return new  Response();
        }
        return new Response("删除失败!");
    }

    /*========================条件构造器的使用==========================*/
    @GetMapping("/selectWrapper")
    public Response selectWrapper(){
        List<User> userList = userService.selectWrapper();
        return new Response(userList);
    }

    @GetMapping("/selectPage")
    public Response selectPage(){
        List<User> userList = userService.selectPage();
        return new Response(userList);
    }

}

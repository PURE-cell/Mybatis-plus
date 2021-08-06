package com.chen.mybatis_plus;

import cn.hutool.json.JSONUtil;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.model.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class MybatisPlusApplicationTests {
    @Autowired
    private UserDao userDao;

    @Test
    void contextLoads() {
        String a = " ";
        System.out.println(a);
        System.out.println(StringUtils.isNotEmpty(a));
        System.out.println(StringUtils.isNotBlank(a));
    }

    @Test
    void test1(){
        List<User> user = userDao.selectList(null);
        String[] split = user.toString().split(",");    //分割逗号,去除逗号
        for (String s : split) {
            System.out.println(s);
        }
        System.out.println("===========================================");
        user.forEach(System.out::println);
    }

    @Test
    void test2(){
        /*=====================trim()去除字符串两端的空格======================*/
        /*=====================测试String.trim()======================*/
        String str1 = "abc ";
        String str2 = "abc";
        String str3 = "abc";
        System.out.println(str1 == str2);
        str1 = str1.trim();
        System.out.println(str1.equals(str2));
        System.out.println(str2.equals(str3));
    }

    // 5 2 8 1 2 19 15 14 10 9
    /**
     *  @Author: chenchao
     *  @Date: 2021/8/6 11:17
     *  @Description: 冒泡排序算法
     */
    @Test
    void bubbleSort(){
        int []array = new int[10];
        Scanner arr = new Scanner(System.in);
        System.out.println("请输入10个数据:");
        for (int i = 0; i < array.length; i++) {
            array[i] = arr.nextInt();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length-i-1; j++) {
                int temp;
                if (array[j] > array[j+1]){
                    temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            System.out.print(array[i] + "\t");
        }
    }
    // 5 2 8 1 2 19 15 14 10 9
    /**
     *  @Author: chenchao
     *  @Date: 2021/8/6 11:36
     *  @Description: 选择排序算法
     */
    @Test
    void test(){
        int []array = new int[10];
        Scanner arr = new Scanner(System.in);
        System.out.println("请输入10个数据:");
        for (int i = 0; i < array.length; i++) {
            array[i] = arr.nextInt();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = i; j < array.length; j++) {
                int temp;
                if (array[i] > array[j]){
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            System.out.print(array[i] + "\t");
        }
    }

}

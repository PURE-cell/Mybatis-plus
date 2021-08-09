package com.chen.mybatis_plus;

import com.alibaba.fastjson.JSONObject;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class MybatisPlusApplicationTests {
    @Resource
    private UserDao userDao;

    @Test
    void contextLoads() {
        String a = " ";
        System.out.println(a);
        System.out.println(StringUtils.isNotEmpty(a));
        System.out.println(StringUtils.isNotBlank(a));
    }

    @Test
    void test1() {
        List<User> user = userDao.selectList(null);
        String[] split = user.toString().split(",");    //分割逗号,去除逗号
        for (String s : split) {
            System.out.println(s);
        }
        System.out.println("===========================================");
        user.forEach(System.out::println);
    }

    @Test
    void test2() {
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

    @Test
    void test3(){
        long millis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(millis);
        String s = new Timestamp(millis).toString();
        System.out.println(millis);
        System.out.println(simpleDateFormat.format(date));    //设置输出格式
        System.out.println(s);      //输出Timestamp样式的时间
        /*===========================================*/
        System.out.println("/*=====================分割======================*/");
        StringBuffer stringBuilder = new StringBuffer();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        stringBuilder.append(dateFormat.format(new Date(millis))).append(String.format("%06d",
                random.nextInt(1000000)));
        System.out.println(stringBuilder);
    }

    @Test
    void test4(){
        //测试MessageDigest类的加密,该加密是没有解码的,通常是通过比对加密的结果进行比较得出结果
        StringBuilder str = new StringBuilder();
        str.append("测试MD5算法").append("成功");
        StringBuilder str1 = new StringBuilder();
        str1.append("测试MD5算法成功");
        System.out.println("/*=====================分割======================*/");
        testMD5(str);
        System.out.println(str);    //对象是传地址的
        System.out.println("/*=====================分割======================*/");
        testMD5(str1);
    }

    public static void testMD5(StringBuilder str){
        //测试MessageDigest类的加密
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.toString().getBytes(StandardCharsets.UTF_8));
            str.setLength(0);
            byte[] b = md5.digest();
            int i;
            for (byte aByte : b) {
                i = aByte;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    str.append("0");
                }
                //toHexString():以十六进制的无符号整数形式返回一个整数参数的字符串表示形式
                str.append(Integer.toHexString(i));
            }
            str.append("你好，我是测试对象是否是引用传值");
            System.out.println(str);
        } catch (Exception e) {
            System.out.println("执行MD5加密时出现异常:" + e);
        }
    }

    //十六进制转String字符串
    public static String hex2Str(StringBuilder hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String h = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(h, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    private static HttpHeaders DEFAULT_HTTP_HEADERS = null;
    @Test
    void test5(){
        final RestTemplate restTemplate = new RestTemplate();
        String prefix = "http://localhost:8080/api/insert";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("Content-Type", Collections.singletonList("application/json; charset=UTF-8"));
        httpHeaders.put("Accept", Collections.singletonList("application/json"));
        httpHeaders.put("Accept-Encoding", Collections.singletonList("utf-8")); //设置的就是客户端浏览器所能够支持的返回压缩格式
        DEFAULT_HTTP_HEADERS = httpHeaders;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "张三");
        jsonObject.put("age", 18);
        jsonObject.put("email", "chenchao20@asiainfo.com");
//        jsonObject.put("test", "测试HttpEntity");
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), DEFAULT_HTTP_HEADERS);
        System.out.println("调用服务的url是:" + prefix);  //调用服务的url是:http://localhost:8080/api/insert
        System.out.println("请求服务的json请求参数是:" + httpEntity.getBody());//请求服务的json请求参数是:{"name":"张三","age":18,"email":"abc@qq.com"}
        JSONObject result = restTemplate.postForEntity(prefix, httpEntity, JSONObject.class).getBody();
        System.out.println("请求服务的json返回参数是:" + result.toString());//请求服务的json返回参数是:{"code":"200","status":"操作成功"}
    }


    // 5 2 8 1 2 19 15 14 10 9

    /**
     * @Author: chenchao
     * @Date: 2021/8/6 11:17
     * @Description: 冒泡排序算法
     */
    @Test
    void bubbleSort() {
        int[] array = new int[10];
        Scanner arr = new Scanner(System.in);
        System.out.println("请输入10个数据:");
        for (int i = 0; i < array.length; i++) {
            array[i] = arr.nextInt();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                int temp;
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            System.out.print(array[i] + "\t");
        }
    }
    // 5 2 8 1 2 19 15 14 10 9

    /**
     * @Author: chenchao
     * @Date: 2021/8/6 11:36
     * @Description: 选择排序算法
     */
    @Test
    void selectSort() {
        int[] array = new int[10];
        Scanner arr = new Scanner(System.in);
        System.out.println("请输入10个数据:");
        for (int i = 0; i < array.length; i++) {
            array[i] = arr.nextInt();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = i; j < array.length; j++) {
                int temp;
                if (array[i] > array[j]) {
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
    // 5 2 8 1 2 19 15 14 10 9

    /**
     * @Author: chenchao
     * @Date: 2021/8/6 14:17
     * @Description: 直接插入排序算法
     */
    @Test
    void insertSort() {
        int[] array = new int[10];
        Scanner arr = new Scanner(System.in);
        System.out.println("请输入10个数据:");
        for (int i = 0; i < array.length; i++) {
            array[i] = arr.nextInt();
        }
        int i, j, temp;
        for (i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                temp = array[i];
                array[i] = array[i - 1];
                for (j = i - 2; j >= 0 && temp < array[j]; j--) {
                    array[j + 1] = array[j];
                }
                array[j + 1] = temp;
            }
        }
        for (i = 0; i < 10; i++) {
            System.out.print(array[i] + "\t");
        }
    }

}

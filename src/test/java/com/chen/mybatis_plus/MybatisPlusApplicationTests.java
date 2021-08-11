package com.chen.mybatis_plus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.model.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
/**
 *  @Author: chenchao
 *  @Date: 2021/8/11 9:29
 *  @Description: spring boot 运行测试类时：Error creating bean with name 'serverEndpointExporter' 问题
 *  原因：websocket是需要依赖tomcat等容器的启动。所以在测试过程中我们要真正的启动一个tomcat作为容器。
 *  解决方法：在@SpringBootTest注解上添加webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    /**
     *  @Author: chenchao
     *  @Date: 2021/8/3 10:54
     *  @Description: 控制输出时间的格式
     */
    @Test
    void test3(){
        long millis = System.currentTimeMillis();   //获取系统的时间
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
        //MD5以512位分组来处理输入的信息，且每一分组又被划分为16个32位子分组，经过了一系列的处理后，算法的输出由四个32位分组组成，将这四个32位分组级联后将生成一个128位散列值。
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
//            str.append("你好，我是测试对象是否是引用传值");
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

    /**
     *  @Author: chenchao
     *  @Date: 2021/8/5 9:15
     *  @Description: 测试fastJSON中的JsonObject类
     */
    @Test
    void test6(){
        //通过原生生成的JSON格式生成
        JSONObject object = new JSONObject();
        object.put("name", "张三");
        object.put("age", 18);
        object.put("email", "abc@163.com");
        System.out.println("原生生成的JSON格式生成:" + object.toString());

        //通过hashMap数据结构生成
        HashMap<String, Object> objectHashMap =  new HashMap<>();
        objectHashMap.put("name", "张三");
        objectHashMap.put("age", 18);
        objectHashMap.put("email", "abc@qq.com");
        System.out.println("hashMap数据结构生成:" + new JSONObject(objectHashMap).toString());

        //通过实体生成
        User user = new User();
        user.setName("张三");
        user.setAge(18);
        user.setEmail("abc@qq.com");
        System.out.println("实体生成Json格式:" + JSON.toJSON(user));  //生成Json格式
        String jsonString = JSONObject.toJSONString(user);  //对象转成String
        System.out.println("对象转成String:" + jsonString);

        //Json字符串转换成Json对象
        String user1 = "{\"name\":\"张三\",\"age\":18,\"email\":\"abc.@qq.com\"}";
        JSONObject jsonObject = JSONObject.parseObject(user1);
        System.out.println("Json字符串转换成Json对象:" + jsonObject.toString());

        //list对象转listJson
        ArrayList<User> users = new ArrayList<>();
        User user2 = new User();
        user2.setName("张三");
        user2.setAge(18);
        user2.setEmail("abc@qq.com");

        users.add(user2);

        User user3 = new User();
        user3.setName("李四");
        user3.setAge(20);
        user3.setEmail("abc@163.com");

        users.add(user3);

        String string = JSON.toJSON(users).toString();
        System.out.println("list转Json字符串:" + string);

        JSONArray jsonArray = JSONObject.parseArray(string);
        System.out.println("json字符串转listJson格式:" + jsonArray);

        //JsonObject转实体对象
        JSONObject object1 = new JSONObject();
        object1.put("name", "张三");
        object1.put("age", 18);
        object1.put("email", "abc@163.com");
        User user4 = JSONObject.toJavaObject(object1, User.class);
        System.out.println(user4);
        String[] split = jsonString.split(",");
        System.out.println(jsonString.split(","));
        System.out.println(split.length);
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

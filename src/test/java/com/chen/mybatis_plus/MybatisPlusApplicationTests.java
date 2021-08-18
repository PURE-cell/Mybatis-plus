package com.chen.mybatis_plus;

import ch.qos.logback.classic.spi.EventArgUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.mybatis_plus.dao.UserDao;
import com.chen.mybatis_plus.dto.UserDto;
import com.chen.mybatis_plus.model.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *  @Author: chenchao
 *  @Date: 2021/8/11 9:29
 *  @Description: spring boot 运行测试类时：Error creating bean with name 'serverEndpointExporter' 问题
 *  原因：websocket是需要依赖tomcat等容器的启动。所以在测试过程中我们要真正的启动一个tomcat作为容器。
 *  解决方法：在@SpringBootTest注解上添加webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
 */
@Slf4j
@EnableScheduling
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MybatisPlusApplicationTests {
    @Resource
    private UserDao userDao;

    //序列化RedisTemplate，否则Redis客户端查询不到key
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }


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
        /*=====================测试String.join()连接字符串======================*/
        String str = String.join("-", "one", "two", "three");
        System.out.println(str);//输出结果：one-two-three
        List <String> list = new ArrayList <String> ();
        list.add("one");
        list.add("two");
        list.add("three");
        System.out.println(String.join("-", list));//输出结果：one-two-three
        String[] array = { "one", "two", "three" };
        System.out.println(String.join("-", array));//输出结果：one-two-three
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

    /**
     *  @Author: chenchao
     *  @Date: 2021/8/13 9:07
     *  @Description: 测试MD5算法加密
     */
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
        System.out.println("resultCode = " + result.getString("code"));
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

    /**
     *  @Author: chenchao
     *  @Date: 2021/8/15 9:06
     *  @Description: Redis的使用
     */
    @Test
    void test7(){
        /*=====================Redis的String的操作======================*/
        //储存一个Redis的key:value
        redisTemplate.opsForValue().set("name", "Tom");
        String name = (String) redisTemplate.opsForValue().get("name");
        log.info("操作字符串输出:{}", name);

        //储存一个Redis的key:value，并且定时删除
        redisTemplate.opsForValue().set("name", "Mark", 10, TimeUnit.SECONDS);
        //由于设置的是10秒失效，十秒之内查询有结果，十秒之后返回为null
        String name1 = (String) redisTemplate.opsForValue().get("name");
        //设置定时器来执行输出
        /**
         *  Timer：是一个定时器工具，用来执行指定任务
         *  TimerTask：是一个抽象类，他的子类可以代表一个被Timer计划的任务
         */
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("操作字符串输出，10秒失效:{}", name1);
            }
        };
        Timer timer = new Timer();
        long delay = 0;     //等待时间5秒
        long intevalPeriod = 1000 * 5; //每次执行时间的间隔
        timer.schedule(timerTask, delay, intevalPeriod); //安排任务在一定时间执行

        //用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
        redisTemplate.opsForValue().set("key","hello world");
        redisTemplate.opsForValue().set("key", "redis", 6);
        log.info("输出结果:{}", redisTemplate.opsForValue().get("key"));

        //setIfAbsent()方法是比较key里面的value是否与现在的值相等，相等返回false;否则返回TRUE，并且更新value数据
        log.info(String.valueOf(redisTemplate.opsForValue().setIfAbsent("multi1","multi1")));
        log.info(String.valueOf(redisTemplate.opsForValue().setIfAbsent("multi111","multi111")));

        //为多个键分别设置它们的值
        Map<String,String> maps = new HashMap<String, String>();
        maps.put("multi1","multi1");
        maps.put("multi2","multi2");
        maps.put("multi3","multi3");
        redisTemplate.opsForValue().multiSet(maps);
        List<String> keys = new ArrayList<String>();
        keys.add("multi1");
        keys.add("multi2");
        keys.add("multi3");
        System.out.println(redisTemplate.opsForValue().multiGet(keys));

        //为多个键分别设置它们的值，如果存在则返回false，不存在返回true,并且更新value数据
        Map<String, String> map1 = new HashMap<>();
        map1.put("multi11","multi11");
        map1.put("multi22","multi22");
        map1.put("multi33","multi33");
        Map<String,String> maps2 = new HashMap<>();
        maps2.put("multi1","multi1");
        maps2.put("multi2","multi2");
        maps2.put("multi3","multi3");
        System.out.println(redisTemplate.opsForValue().multiSetIfAbsent(map1));
        System.out.println(redisTemplate.opsForValue().multiSetIfAbsent(maps2));

        //设置键的字符串值并返回其旧值
        redisTemplate.opsForValue().set("getSetTest","test");
        System.out.println(redisTemplate.opsForValue().getAndSet("getSetTest","test2"));
        System.out.println(redisTemplate.opsForValue().get("getSetTest"));

        //increment()支持整数、浮点型数据存储
        redisTemplate.opsForValue().increment("increment1", 1);
        redisTemplate.opsForValue().increment("increment2", 1.32);

        //如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。如果键不存在，则它被创建并设置为空字符串，之后再拼接因此APPEND在这种特殊情况下将类似于SET。
        redisTemplate.opsForValue().append("appendTest", "Hello");
        System.out.println(redisTemplate.opsForValue().get("appendTest"));
        redisTemplate.opsForValue().append("appendTest","world");
        System.out.println(redisTemplate.opsForValue().get("appendTest"));

        //遍历截取key所对应的value字符串,-1代表倒数第个元素，0代表第一个元素
        System.out.println(redisTemplate.opsForValue().get("appendTest",0,5));//输出结果:Hellow
        System.out.println(redisTemplate.opsForValue().get("appendTest",0,-1));//输出结果:Helloworld
        System.out.println(redisTemplate.opsForValue().get("appendTest",-3,-1));//输出结果:rld
        System.out.println(redisTemplate.opsForValue().get("appendTest", -1, -5));//没有输出结果

        //返回key所对应的value值得长度
        System.out.println(redisTemplate.opsForValue().size("appendTest"));

        //setBit()方法是对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)，key键对应的值value对应的ascii码,在offset的位置(从左向右数)变为value
        redisTemplate.opsForValue().set("bitTest","a");
        // 'a' 的ASCII码是 97。转换为二进制是：01100001
        // 'b' 的ASCII码是 98  转换为二进制是：01100010
        // 'c' 的ASCII码是 99  转换为二进制是：01100011
        //因为二进制只有0和1，在setbit中true为1，false为0，因此我要变为'b'的话第六位设置为1，第七位设置为0
        redisTemplate.opsForValue().setBit("bitTest",6, true);//第一位参数是键；第二位参数是二进制的第几位，从0开始；第三位参数是设置修改值，true为1，false为0
        redisTemplate.opsForValue().setBit("bitTest",7, false);
        System.out.println(redisTemplate.opsForValue().get("bitTest")); //输出: b

        //getBit()方法是获取键对应值的ascii码的在offset处位值，TRUE代表1，false代表0
        System.out.println(redisTemplate.opsForValue().getBit("bitTest",7));

        /*=====================Redis的List结构======================*/
        //leftPush()方法从左添加List数据，range()方法遍历List数据
        redisTemplate.opsForList().leftPush("lPushList","a");
        redisTemplate.opsForList().leftPush("lPushList","b");
        redisTemplate.opsForList().leftPush("lPushList","c");
        redisTemplate.opsForList().leftPush("lPushList","d");
        //输出结果： 遍历lPushList的值:[d, c, b, a]；原因：从左边添加数据，已添加的需向右移
        log.info("遍历lPushList的值:{}", redisTemplate.opsForList().range("lPushList", 0, -1));

        //rightPush()方法从右添加List数据，range()方法遍历List数据
        redisTemplate.opsForList().rightPush("rPushList","a");
        redisTemplate.opsForList().rightPush("rPushList","b");
        redisTemplate.opsForList().rightPush("rPushList","c");
        redisTemplate.opsForList().rightPush("rPushList","d");
        //输出结果：遍历rPushList的值:[a, b, c, d]；原因：从右边添加数据，已添加的需向左移
        log.info("遍历rPushList的值:{}", redisTemplate.opsForList().range("rPushList", 0, -1));

        //trim()方法截取集合元素长度，保留长度内的数据。起始和停止都是基于0的索引
        log.info(String.valueOf(redisTemplate.opsForList().range("rPushList", 0, -1)));//输出结果：[a, b, c, d]
        redisTemplate.opsForList().trim("rPushList", 0, 2);
        log.info(String.valueOf(redisTemplate.opsForList().range("rPushList", 0, -1)));//输出结果：[a, b, c]

        //size()方法返回存储在键中的列表的长度。如果键不存在，则将其解释为空列表，并返回0。当key存储的值不是列表时返回错误。
        System.out.println(redisTemplate.opsForList().size("rPushList"));//rPushList里面的值为：[a, b, c]；输出结果：3
        System.out.println(redisTemplate.opsForList().size("appendTest"));//appendTest不是列表；编译出现错误，报红：Error in execution; nested exception is io.lettuce.core.RedisCommandExecutionException

        //leftPushAll()方法是批量把一个数组插入到列表中
        String[] stringArrays = new String[]{"1","2","3"};
        redisTemplate.opsForList().leftPushAll("listArray",stringArrays);
        System.out.println(redisTemplate.opsForList().range("listArray",0,-1));//输出结果:[3, 2, 1]

        //leftPushAll()方法还可以批量把一个集合插入到列表中
        List<Object> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        redisTemplate.opsForList().leftPushAll("listCollection", strings);
        System.out.println(redisTemplate.opsForList().range("listCollection",0,-1));//输出结果：[3, 2, 1]

        //leftPushIfPresent()方法是只有存在key对应的列表才能将这个value值插入到key所对应的列表中
        System.out.println(redisTemplate.opsForList().leftPushIfPresent("leftPushIfPresent","aa"));//输出结果：0
        System.out.println(redisTemplate.opsForList().leftPushIfPresent("leftPushIfPresent","bb"));//输出结果：0
        System.out.println("不存在leftPushIfPresent的key，输出结果：" + redisTemplate.opsForList().range("leftPushIfPresent", 0, -1));//输出结果：不存在leftPushIfPresent的key，输出结果：[]
        /*=====================分割线======================*/
        System.out.println(redisTemplate.opsForList().leftPush("leftPushIfPresent","aa"));//输出结果：1
        System.out.println(redisTemplate.opsForList().leftPushIfPresent("leftPushIfPresent","bb"));//输出结果：2
        System.out.println("存在leftPushIfPresent的key，输出结果：" + redisTemplate.opsForList().range("leftPushIfPresent", 0, -1));//输出结果：存在leftPushIfPresent的key，输出结果：[bb, aa]

        //Long leftPush(K key, V pivot, V value)作用是把value值放到key对应列表中pivot值的左面，如果pivot值存在的话
        ArrayList arrayList = new ArrayList();
        arrayList.add("C++");
        arrayList.add("C#");
        arrayList.add("Java");
        arrayList.add("Python");
        redisTemplate.opsForList().leftPushAll("list",arrayList);
        System.out.println(redisTemplate.opsForList().range("list", 0, -1));//输出结果：[Python, Java, C#, C++]
        redisTemplate.opsForList().leftPush("list","Java","oc");
        System.out.print(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, oc, Java, C#, C++]

        //Long rightPush(K key, V pivot, V value)作用是把value值放到key对应列表中pivot值的右面，如果pivot值存在的话
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, oc, Java, C#, C++]
        redisTemplate.opsForList().rightPush("list","C#","oc");
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, oc, Java, C#, oc, C++]

        //void set(K key, long index, V value)的作用是在列表中index的位置设置value值，有点类似与更新
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, oc, Java, C#, oc, C++]
        redisTemplate.opsForList().set("list",1,"setValue");
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, setValue, Java, C#, oc, C++]

        //Long remove(K key, long count, Object value)的作用是从存储在键中的列表中删除等于值的元素的第一个计数事件。
        //计数参数以下列方式影响操作：
        //count> 0：删除等于从头到尾移动的值的元素。
        //count <0：删除等于从尾到头移动的值的元素。
        //count = 0：删除等于value的所有元素。
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, setValue, Java, C#, oc, C++]
        redisTemplate.opsForList().remove("list",1,"setValue");//将删除列表中存储的list列表中第一次次出现的“setValue”。
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, Java, C#, oc, C++]

        //V index(K key, long index)作用是根据下表获取列表中的值，下标是从0开始的
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, Java, C#, oc, C++]
        System.out.println(redisTemplate.opsForList().index("list",2));//输出结果：C#

        //V leftPop(K key)的作用是弹出最左边的元素，弹出之后该值在列表中将不复存在
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Python, Java, C#, oc, C++]
        System.out.println(redisTemplate.opsForList().leftPop("list"));//输出结果：Python
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Java, C#, oc, C++]

        //V rightPop(K key)的作用是弹出最右边的元素，弹出之后该值在列表中将不复存在
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Java, C#, oc, C++]
        System.out.println(redisTemplate.opsForList().rightPop("list"));//输出结果：C++
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果哟：[Java, C#, oc]

        //V rightPopAndLeftPush(K sourceKey, K destinationKey)的作用是用于移除列表的最后一个元素，并将该元素添加到另一个列表并返回。
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Java, C#, oc]
        redisTemplate.opsForList().rightPopAndLeftPush("list","rightPopAndLeftPush");
        System.out.println(redisTemplate.opsForList().range("list",0,-1));//输出结果：[Java, C#]
        System.out.println(redisTemplate.opsForList().range("rightPopAndLeftPush",0,-1));//输出结果：[oc]

        /*=====================Redis的Hash数据结构======================*/
        //put()设置插入Hash，putAll()设置批量插入Hash；Map<HK, HV> entries(H key)根据密钥获取整个哈希存储的数据，返回的是Map数据
        redisTemplate.opsForHash().put("redisHash","name","tom");
        redisTemplate.opsForHash().put("redisHash","age","26");
        redisTemplate.opsForHash().put("redisHash", "class", "6");
        redisTemplate.opsForHash().put("redisHash","email","abc@qq.com");
        Map<String,Object> testMap = new HashMap();
        testMap.put("name","jack");
        testMap.put("age","27");
        testMap.put("class", "1");
        testMap.put("email","abc@163.com");
        redisTemplate.opsForHash().putAll("redisHash1",testMap);
        System.out.println("redisHash的值" + redisTemplate.opsForHash().entries("redisHash"));//输出结果：redisHash的值{name=tom, age=26, class=6, email=abc@qq.com}
        System.out.println("redisHash1的值" + redisTemplate.opsForHash().entries("redisHash1"));//输出结果：redisHash1的值{name=jack, class=1, age=27, email=abc@163.com}

        //Long delete(H key, Object... hashKeys)的作用是删除给定的哈希hashKeys
        System.out.println(redisTemplate.opsForHash().delete("redisHash","class"));//输出结果：1
        System.out.println(redisTemplate.opsForHash().entries("redisHash"));//输出结果：{name=tom, age=26, email=abc@qq.com}

        //Boolean hasKey(H key, Object hashKey)的作用是确定哈希hashKey是否存在，存在输出true，不存在输出false
        System.out.println(redisTemplate.opsForHash().hasKey("redisHash", "age"));//输出结果：true
        System.out.println(redisTemplate.opsForHash().hasKey("redisHash", "abc"));//输出结果：false

        //HV get(H key, Object hashKey)的作用是从键中的哈希获取给定hashKey的值
        System.out.println(redisTemplate.opsForHash().get("redisHash","email"));//输出结果：abc@qq.com

        //List<HV> multiGet(H key, Collection<HK> hashKeys)的作用是从哈希中获取给定hashKey的值，返回的是List
        ArrayList keys1 = new ArrayList();
        keys.add("name");
        keys.add("age");
        keys.add("email");
        System.out.println(redisTemplate.opsForHash().multiGet("redisHash", keys1));//输出结果：[tom, 26, abc@qq.com]

        //Long(或者Double) increment(H key, HK hashKey, long delta)的作用是通过给定的delta增加散列hashKey的值（整型或浮点型），相当于加法与减法
        System.out.println(redisTemplate.opsForHash().get("redisHash","age"));//输出结果：26
        System.out.println(redisTemplate.opsForHash().increment("redisHash","age",10));//输出结果：36
        System.out.println(redisTemplate.opsForHash().increment("redisHash","age",-10));//输出结果：26
        System.out.println(redisTemplate.opsForHash().increment("redisHash","age",-2.5));//输出结果：23.5

        //Set<HK> keys(H key)的作用是获取Hash中所对应的散列表的key
        System.out.println(redisTemplate.opsForHash().entries("redisHash1"));//输出结果：{name=jack, class=1, age=27, email=abc@163.com}
        System.out.println(redisTemplate.opsForHash().keys("redisHash1"));//输出结果：[name, class, age, email]

        //Long size(H key)的作用是获取key所对应的散列表的大小个数
        System.out.println(redisTemplate.opsForHash().entries("redisHash1"));//输出结果：{name=jack, class=1, age=27, email=abc@163.com}
        System.out.println(redisTemplate.opsForHash().size("redisHash1"));//输出结果：4

        //Boolean putIfAbsent(H key, HK hashKey, HV value)的作用是如果hashKey不存在的时候，返回TRUE并且设置散列hashKey的值，否则不做更改，并且返回false
        System.out.println(redisTemplate.opsForHash().putIfAbsent("redisHash","age","20"));//输出结果：false
        System.out.println(redisTemplate.opsForHash().entries("redisHash"));//输出结果：{name=tom, age=33.5, email=abc@qq.com}
        System.out.println(redisTemplate.opsForHash().putIfAbsent("redisHash","kkk","kkk"));//输出结果：true
        System.out.println(redisTemplate.opsForHash().entries("redisHash"));//输出结果：{name=tom, age=33.5, email=abc@qq.com, kkk=kkk}

        //List<HV> values(H key)的作用是根据密钥获取整个哈希存储的值
        System.out.println(redisTemplate.opsForHash().values("redisHash"));//输出结果：[tom, 33.5, abc@qq.com, kkk]

        //Cursor<Map.Entry<HK, HV>> scan(H key, ScanOptions options)的作用是使用Cursor在key的hash中迭代，相当于迭代器。
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan("redisHash", ScanOptions.NONE);
        while(cursor.hasNext()){
            Map.Entry<Object, Object> entry = cursor.next();
            System.out.print(entry.getKey() + ":" + entry.getValue() + "\t");//输出结果：name:tom	age:33.5	email:abc@qq.com	kkk:kkk
        }

        /*=====================Redis的Set数据结构======================*/
        //Long add(K key, V... values)的作用是无序集合中添加元素，返回添加个数;也可以直接在add里面添加多个值 如：template.opsForSet().add("setTest","aaa","bbb")
        String[] strings0 = {"yes", "no", "ok", "bad"};
        String[] strings1 = {"哇", "哦", "嗯", "哈"};
        System.out.println(redisTemplate.opsForSet().add("setTest", strings0));//输出结果：4
        redisTemplate.opsForSet().add("setTest1", strings1);

        //Long remove(K key, Object... values)的作用是移除集合中一个或多个成员
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[no, yes]
        System.out.println(redisTemplate.opsForSet().remove("setTest","no"));//输出结果：1
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[yes]

        //V pop(K key)的作用是移除并返回集合中的一个随机元素
        System.out.println(redisTemplate.opsForSet().pop("setTest"));//原来的数据：[yes]；输出结果：yes
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[]

        //Boolean move(K key, V value, K destKey)的作用是将 member 元素从 source 集合移动到 destination 集合
        redisTemplate.opsForSet().move("setTest","yes","setTest1");
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[嗯, 哦, 哈, 哇, yes]

        //Boolean isMember(K key, Object o)的作用是判断 member 元素是否是集合 key 的成员
        System.out.println(redisTemplate.opsForSet().isMember("setTest", "bad"));//输出结果：true

        //Set<V> intersect(K key, K otherKey)的作用是key对应的无序集合与otherKey对应的无序集合求交集
        //Set<V> intersect(K key, Collection<K> otherKeys)的作用是key对应的无序集合与多个otherKey对应的无序集合求交集，这里就不展示了(先申请一个ArrayList，之后add)
        redisTemplate.opsForSet().add("setTest", "交集");
        redisTemplate.opsForSet().add("setTest1", "交集");
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[哈, 哇, 交集, 哦, yes, 嗯]
        System.out.println(redisTemplate.opsForSet().intersect("setTest", "setTest1"));//输出结果：[交集]

        //Long intersectAndStore(K key, K otherKey, K destKey)的作用是key无序集合与otherkey无序集合的交集存储到destKey无序集合中
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[交集, 哦, 哈, yes, 哇, 嗯]
        redisTemplate.opsForSet().intersectAndStore("setTest", "setTest1", "and");
        System.out.println(redisTemplate.opsForSet().members("and"));//输出结果：[交集]

        //Set<V> union(K key, K otherKey)的作用是key无序集合与otherKey无序集合的并集
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[交集, 哦, 哈, yes, 哇, 嗯]
        System.out.println(redisTemplate.opsForSet().union("setTest", "setTest1"));//输出结果：

        //Long unionAndStore(K key, K otherKey, K destKey)的作用是key无序集合与otherkey无序集合的并集存储到destKey无序集合中，返回值是key与otherkey的并集中个数
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[交集, 哦, 哈, yes, 哇, 嗯]
        System.out.println(redisTemplate.opsForSet().unionAndStore("setTest", "setTest1", "or"));//输出结果：9
        System.out.println(redisTemplate.opsForSet().members("or"));//输出结果：[哦, 交集, no, 哈, yes, bad, 哇, ok, 嗯]

        //Set<V> difference(K key, K otherKey)的作用是key无序集合与otherKey无序集合的差集
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[交集, 哦, 哈, yes, 哇, 嗯]
        System.out.println(redisTemplate.opsForSet().difference("setTest", "setTest1"));//输出结果：[no, bad, ok]

        //Long differenceAndStore(K key, K otherKey, K destKey)的作用是key无序集合与otherkey无序集合的差集存储到destKey无序集合中，返回值是key与otherkey的差集中个数
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().members("setTest1"));//输出结果：[交集, 哦, 哈, yes, 哇, 嗯]
        System.out.println(redisTemplate.opsForSet().differenceAndStore("setTest", "setTest1", "different"));//输出结果：3
        System.out.println(redisTemplate.opsForSet().members("different"));//输出结果：[no, bad, ok]

        //V randomMember(K key)的作用是随机获取key无序集合中的一个元素；
        //List<V> randomMembers(K key, long count)获取多个key无序集合中的元素，count表示个数
        //Set<V> distinctRandomMembers(K key, long count)的作用是获取多个key无序集合中的元素（去重），count表示个数
        System.out.println(redisTemplate.opsForSet().members("setTest"));//输出结果：[交集, no, bad, ok]
        System.out.println(redisTemplate.opsForSet().randomMember("setTest"));//输出结果：交集
        System.out.println(redisTemplate.opsForSet().randomMembers("setTest", 3));//输出结果：[bad, 交集, bad]
        System.out.println(redisTemplate.opsForSet().distinctRandomMembers("setTest", 3));//输出结果：[bad, 交集, ok]

        //Cursor<V> scan(K key, ScanOptions options)的作用是遍历set
        Cursor<Object> cursor1 = redisTemplate.opsForSet().scan("setTest", ScanOptions.NONE);
        while (cursor1.hasNext()){
            System.out.print(cursor1.next() + "\t");//输出结果：交集	no	bad	ok
        }

        /*=====================Redis的ZSet数据结构======================*/
        //Boolean add(K key, V value, double score)的作用是新增一个有序集合，存在的话为false，不存在的话为true
        System.out.println(redisTemplate.opsForZSet().add("zSet1", "zSet->1", 1.0));//输出结果：true

        //Long add(K key, Set<TypedTuple<V>> tuples)新增一个有序集合
        //Set<V> range(K key, long start, long end)通过索引区间返回有序集合成指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列
        ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<>("zSet->2", 2.0);
        ZSetOperations.TypedTuple<Object> objectTypedTuple2 = new DefaultTypedTuple<>("zSet->3", 3.0);
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        tuples.add(objectTypedTuple1);
        tuples.add(objectTypedTuple2);
        System.out.println(redisTemplate.opsForZSet().add("zSet1", tuples));//输出结果：2
        System.out.println(redisTemplate.opsForZSet().range("zSet1", 0, -1));//输出结果：[zSet->1, zSet->2, zSet->3]

        //Long remove(K key, Object... values)从有序集合中移除一个或者多个元素
        System.out.println(redisTemplate.opsForZSet().range("zSet1",0,-1));//输出结果：[zSet->1, zSet->2, zSet->3]
        System.out.println(redisTemplate.opsForZSet().remove("zSet1","zSet->3"));//输出结果：1
        System.out.println(redisTemplate.opsForZSet().range("zSet1",0,-1));//输出结果：[zSet->1, zSet->2]

        //Double incrementScore(K key, V value, double delta)的作用是增加元素的score值，并返回增加后的值
        System.out.println(redisTemplate.opsForZSet().incrementScore("zSet1", "zSet->2", 2.3));//原来的score的值为2.0；输出结果：4.3

        //Long rank(K key, Object o)的作用是返回有序集中指定成员的排名，其中有序集成员按分数值递增(从小到大)顺序排列
        //Long reverseRank(K key, Object o)的作用是返回有序集中指定成员的排名，其中有序集成员按分数值递减(从大到小)顺序排列
        System.out.println(redisTemplate.opsForZSet().range("zSet1",0,-1));//输出结果：[zSet->1, zSet->3, zSet->2]
        System.out.println(redisTemplate.opsForZSet().rank("zSet1","zSet->1"));//输出结果：0
        System.out.println(redisTemplate.opsForZSet().reverseRank("zSet1","zSet->1"));//输出结果：2

        //Set<TypedTuple<V>> rangeWithScores(K key, long start, long end)的作用是通过索引区间返回有序集合成指定区间内的成员对象，其中有序集成员按分数值递增(从小到大)顺序排列
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisTemplate.opsForZSet().rangeWithScores("zSet1", 0, -1);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = tupleSet.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> operations = iterator.next();
            System.out.println("value:" + operations.getValue() + "\t" + "score:" + operations.getScore());
            /**输出结果：
            *   value:zSet->1	score:1.0
             *  value:zSet->3	score:3.0
             *  value:zSet->2	score:4.3
            */
        }

        //Set<V> rangeByScore(K key, double min, double max)通过score分数返回有序集合指定区间内的成员，其中有序集成员按分数值递增(从小到大)顺序排列
        System.out.println(redisTemplate.opsForZSet().rangeByScore("zSet1",0,2));//输出结果：[zSet->1]

        //Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max)通过分数返回有序集合指定区间内的成员对象，其中有序集成员按分数值递增(从小到大)顺序排列
        Set<ZSetOperations.TypedTuple<Object>> tuples1 = redisTemplate.opsForZSet().rangeByScoreWithScores("zSet1",0,5);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator1 = tuples1.iterator();
        while (iterator1.hasNext()) {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator1.next();
            System.out.println("value:" + typedTuple.getValue() + "\t" + "score:" + typedTuple.getScore());
            /**
             *  输出结果：
             * value:zSet->1	score:1.0
             * value:zSet->3	score:3.0
             * value:zSet->2	score:4.3
             */
        }

        //Long count(K key, double min, double max)通过分数返回有序集合指定区间内的成员个数
        System.out.println(redisTemplate.opsForZSet().rangeByScore("zSet1",0,5));//输出结果：[zSet->1, zSet->3, zSet->2]
        System.out.println(redisTemplate.opsForZSet().count("zSet1",0,5));//输出结果：3

        //Long size(K key)获取有序集合的成员数
        //Long zCard(K key)获取有序集合的成员数
        System.out.println(redisTemplate.opsForZSet().size("zSet1"));//输出结果：3
        System.out.println(redisTemplate.opsForZSet().zCard("zSet1"));//输出结果：3

        //Double score(K key, Object o)获取指定成员的score值
        System.out.println(redisTemplate.opsForZSet().score("zSet1","zSet->1"));//输出结果：1.0

        //Long removeRange(K key, long start, long end)移除指定索引位置的成员，其中有序集成员按分数值递增(从小到大)顺序排列
        System.out.println(redisTemplate.opsForZSet().removeRange("zSet1",1,2));

        //Long removeRangeByScore(K key, double min, double max)根据指定的score值得范围来移除成员

        //Long unionAndStore(K key, K otherKey, K destKey)计算给定的一个有序集的并集，并存储在新的 destKey中，key中的value相同的话会把score值相加
        redisTemplate.opsForZSet().add("zzset1","zset->1",1.0);
        redisTemplate.opsForZSet().add("zzset1","zset->2",2.0);
        redisTemplate.opsForZSet().add("zzset1","zset->3",3.0);
        redisTemplate.opsForZSet().add("zzset1","zset->4",4.0);

        redisTemplate.opsForZSet().add("zzset2","zset->1",1.0);
        redisTemplate.opsForZSet().add("zzset2","zset->2",2.0);
        redisTemplate.opsForZSet().add("zzset2","zset->3",3.0);
        redisTemplate.opsForZSet().add("zzset2","zset->4",4.0);
        redisTemplate.opsForZSet().add("zzset2","zset->5",5.0);
        redisTemplate.opsForZSet().unionAndStore("zzset1","zzset2","destZSet11");//输出结果：5

        Set<ZSetOperations.TypedTuple<Object>> tuples2 = redisTemplate.opsForZSet().rangeWithScores("destZSet11",0,-1);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator2 = tuples2.iterator();
        while (iterator2.hasNext())
        {
            ZSetOperations.TypedTuple<Object> typedTuple = iterator2.next();
            System.out.println("value:" + typedTuple.getValue() + "\t" + "score:" + typedTuple.getScore());
            /**
             *  输出结果：
             * value:zset->1	score:2.0
             * value:zset->2	score:4.0
             * value:zset->5	score:5.0
             * value:zset->3	score:6.0
             * value:zset->4	score:8.0
             */
        }

        //Long intersectAndStore(K key, K otherKey, K destKey)的作用是计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
        System.out.println(redisTemplate.opsForZSet().intersectAndStore("zzset1", "zzset2", "destZSet2"));//输出结果：4
        Set<ZSetOperations.TypedTuple<Object>> objectTypedTuple = redisTemplate.opsForZSet().rangeWithScores("destZSet2", 0, -1);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator3 = objectTypedTuple.iterator();
        while (iterator3.hasNext()){
            ZSetOperations.TypedTuple<Object> tuples3 = iterator3.next();
            System.out.println("value:" + tuples3.getValue() + "\t" + "score:" + tuples3.getScore());
            /**
             * 输出结果：
             * value:zset->1	score:2.0
             * value:zset->2	score:4.0
             * value:zset->3	score:6.0
             * value:zset->4	score:8.0
             */
        }

        //Cursor<TypedTuple<V>> scan(K key, ScanOptions options)遍历zset
        Cursor<ZSetOperations.TypedTuple<Object>> cursor2 = redisTemplate.opsForZSet().scan("zzset1", ScanOptions.NONE);
        while (cursor2.hasNext()){
            ZSetOperations.TypedTuple<Object> tuple2 = cursor2.next();
            System.out.println(tuple2.getValue() + ":\t" + tuple2.getScore());
            /**
             * 输出结果：
             * zset->1:	1.0
             * zset->2:	2.0
             * zset->3:	3.0
             * zset->4:	4.0
             */
        }
    }

    @Test
    void test8(){
        //BeanUtils.copyProperties(user,userDto)将第一个参数对象拷贝到另一个对象中
        User user = userDao.selectById(1);
        UserDto userDto = new UserDto();
        User user1 = new User();
        BeanUtils.copyProperties(user,userDto);
        BeanUtils.copyProperties(userDto,user1);
        System.out.println(user);//结果：User{id='1', name='Jone', age=18, email='test1@baomidou.com', delete_flag='null'}
        System.out.println(userDto);//结果：UserDto(name=Jone, age=18, email=test1@baomidou.com, address=null)
        System.out.println(user1);//结果：User{id='null', name='Jone', age=18, email='test1@baomidou.com', delete_flag='null'}
    }

    /**
     *  @Author: chenchao
     *  @Date: 2021/8/17 16:49
     *  @Description: RabbitMQ生产者
     */
    @Test
    void test9() throws IOException, TimeoutException {
        final String QUEEN_NAME = "Hello World";
        String[] message = String.valueOf(userDao.selectList(null)).split("[\\[|{|,|}|\\]]");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");//设置主机名或IP
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEEN_NAME, false, false, false, null);
        for (String msg :
                message) {
            if (StringUtils.isNotBlank(msg) && !msg.equals("User")){
                channel.basicPublish("", QUEEN_NAME, null, msg.getBytes());
                log.info("发布的消息：{}", msg);
            }
        }
        channel.close();
        connection.close();
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

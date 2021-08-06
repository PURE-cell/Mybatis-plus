package com.chen.mybatis_plus;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPlusApplicationTests {

    @Test
    void contextLoads() {
        String a = " ";
        System.out.println(a);
        System.out.println(StringUtils.isNotEmpty(a));
        System.out.println(StringUtils.isNotBlank(a));
    }

}

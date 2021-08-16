package com.chen.mybatis_plus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)      //主要api配置机制初始化为swagger规范2.0
                .apiInfo(apiInfo())
                .select().apis(RequestHandlerSelectors.basePackage("com.chen.mybatis_plus.controller")) //扫描哪些controller
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Mybatis_Plus使用Swagger2构建RESTful API")  // 标题
                .description("rest api 文档构建利器")  // 描述信息
                .termsOfServiceUrl("http://www.baidu.com")  //服务网址
                .contact(new Contact("PURE", "https://blog.csdn.net/weixin_47234994","1638595707@qq.com"))  // 联系方式
                .version("2.0") //版本号
                .build();
    }
}

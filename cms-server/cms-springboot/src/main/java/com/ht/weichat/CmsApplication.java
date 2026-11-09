package com.ht.weichat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 版本启动类
 *
 * 传统 Spring MVC + JSP 工程的等价迁移：业务逻辑、后台页面与小程序接口保持一致。
 * 打包为可执行 war：mvn clean package 后 java -jar target/cms-boot.war 启动。
 *
 * @author cz
 */
@SpringBootApplication
@MapperScan("com.ht.weichat.mapper")
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}

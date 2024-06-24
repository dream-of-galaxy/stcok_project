package com.itheima.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableFeignClients(basePackages = "com.example.client")
@MapperScan("com.itheima.stock.mapper")//扫描持久层mapper接口,生成代理对象,并维护到spring的ioc容器中
@SpringBootApplication
public class JobApp {
    public static void main(String[] args) {
        SpringApplication.run(JobApp.class, args);
    }
}

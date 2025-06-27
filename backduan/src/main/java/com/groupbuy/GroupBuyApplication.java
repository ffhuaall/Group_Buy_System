package com.groupbuy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.groupbuy.mapper")
public class GroupBuyApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupBuyApplication.class, args);
    }
}
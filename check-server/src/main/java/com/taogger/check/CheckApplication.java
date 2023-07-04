package com.taogger.check;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author taogger
 * @description 启动类
 * @date 2023-07-04 15:42
 **/
@EnableDiscoveryClient
@SpringBootApplication
@EnableSpringUtil
public class CheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckApplication.class, args);
    }
}

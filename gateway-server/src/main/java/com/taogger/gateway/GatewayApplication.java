package com.taogger.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author taogger
 * @description TODO
 * @date 2023-07-04 11:31
 **/
@RefreshScope
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.taogger.gateway"}, exclude = {DataSourceAutoConfiguration.class})
//@EnableSpringUtil
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

package com.taogger.gateway.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * nacos配置操作服务注册
 * @author taogger
 * @date 2022/11/29 9:40
 */
@Configuration
@RequiredArgsConstructor
public class KJNcConfig {

    private final NacosConfigManager nacosConfigManager;

    @Bean
    public ConfigService configService() {
        return nacosConfigManager.getConfigService();
    }
}

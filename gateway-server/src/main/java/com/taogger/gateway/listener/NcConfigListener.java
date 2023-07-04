package com.taogger.gateway.listener;

import com.alibaba.nacos.api.config.ConfigService;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 配置监听
 * @author taogger
 * @date 2022/11/28 17:51
 */
@Component
@RequiredArgsConstructor
public class NcConfigListener implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private final ConfigService configService;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String group = applicationContext.getEnvironment().getProperty("spring.cloud.nacos.config.group");
        KJNcConfigManager.setConfigService(configService);
        KJNcConfigManager.setGroup(group);
        for (String dataKey : KJNcConfigManager.dataIds) {
            configService.addListener(dataKey, group, new KJSharedListener(configService, dataKey, group));
        }
    }
}

package com.taogger.gateway.listener;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yxd.kj.app.server.gateway.config.nacos.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件更改监听
 * @author taogger
 * @date 2022/11/28 17:59
 */
@Slf4j
public class KJSharedListener extends AbstractSharedListener {

    public KJSharedListener(ConfigService configService, String dataKey, String group) {
        init(configService, dataKey, group);
    }

    private final List<KJAbstractConfigHandler> configHandlers = new ArrayList<>();
    {
        configHandlers.add(new KJNcFlowConfigHandler());
        configHandlers.add(new KJNcBlackIpConfigHandler());
        configHandlers.add(new KJNcBlackRouteConfigHandler());
        configHandlers.add(new KJNcCheckConfigHandler());
        configHandlers.add(new KJDeveloperConfigHandler());
        configHandlers.add(new KJNcFilterRouteConfigHandler());
        configHandlers.add(new KJNcResubmitConfigHandler());
    }

    /**
     * 文件配置读取更改
     * @author taogger
     * @date 2022/11/29 9:16
     * @param dataId 配置文件名称
     * @param group 服务组名称
     * @param configInfo 配置内容
    **/
    @Override
    public void innerReceive(String dataId, String group, String configInfo) {
        log.info("【进入配置更改监听,dataId:{},group:{},configInfo:{}】",dataId, group, configInfo);
        for (KJAbstractConfigHandler configHandler : configHandlers) {
            if (configHandler.isMatch(dataId)) {
                configHandler.handler(configInfo);
            }
        }
    }

    /**
     * 初始化配置
     * @author taogger
     * @date 2022/11/29 11:30
     * @param configService
     * @param dataKey
     * @param group
    **/
    @SneakyThrows
    private void init(ConfigService configService, String dataKey, String group) {
        log.info("【进入配置初始化中,dataKey:{},group:{}】", dataKey, group);
        for (KJAbstractConfigHandler configHandler : configHandlers) {
            if (configHandler.isMatch(dataKey)) {
                String configInfo = configService.getConfig(dataKey, group, 3000);
                configHandler.init(configInfo);
            }
        }
    }
}

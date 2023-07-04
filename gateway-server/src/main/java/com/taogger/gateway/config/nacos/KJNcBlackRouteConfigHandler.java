package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import yxd.kj.app.server.gateway.model.BlackRouteEntity;

import java.util.List;

/**
 * 黑名单路由配置更新
 * @author taogger
 * @date 2022/11/29 16:36
 */
public class KJNcBlackRouteConfigHandler extends KJAbstractConfigHandler {

    @Override
    public void handler(String configInfo) {
        List<BlackRouteEntity> blackRouteEntities = JSONUtil.toList(configInfo, BlackRouteEntity.class);
        KJNcConfigManager.setBlackRoutes(blackRouteEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.blackRouteDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

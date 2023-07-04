package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import com.taogger.gateway.model.FilterRouteEntity;

import java.util.List;

/**
 * 过滤路由
 * @author taogger
 * @date 2022/11/29 18:43
 */
public class KJNcFilterRouteConfigHandler extends KJAbstractConfigHandler {
    @Override
    public void handler(String configInfo) {
        List<FilterRouteEntity> filterRouteEntities = JSONUtil.toList(configInfo, FilterRouteEntity.class);
        KJNcConfigManager.setFilterRoutes(filterRouteEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.filterRouteDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import yxd.kj.app.server.gateway.model.DeveloperEntity;

import java.util.List;

/**
 * 开发者管理配置
 * @author taogger
 * @date 2022/11/29 17:12
 */
public class KJDeveloperConfigHandler extends KJAbstractConfigHandler {


    @Override
    public void handler(String configInfo) {
        List<DeveloperEntity> developerEntities = JSONUtil.toList(configInfo, DeveloperEntity.class);
        KJNcConfigManager.setDevelopers(developerEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.developerDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

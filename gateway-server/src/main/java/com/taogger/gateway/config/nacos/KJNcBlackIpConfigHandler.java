package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import yxd.kj.app.server.gateway.model.BlackIpEntity;

import java.util.List;

/**
 * 黑名单ip配置更新
 * @author taogger
 * @date 2022/11/29 14:53
 */
public class KJNcBlackIpConfigHandler extends KJAbstractConfigHandler {
    @Override
    public void handler(String configInfo) {
        List<BlackIpEntity> blackIpEntities = JSONUtil.toList(configInfo, BlackIpEntity.class);
        KJNcConfigManager.setBlackIps(blackIpEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.blackIpDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

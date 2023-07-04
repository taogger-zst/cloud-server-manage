package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import yxd.kj.app.server.gateway.model.ContentCheckEntity;

import java.util.List;

/**
 * 内容检查
 * @author taogger
 * @date 2022/11/29 16:54
 */
public class KJNcCheckConfigHandler extends KJAbstractConfigHandler {

    @Override
    public void handler(String configInfo) {
        List<ContentCheckEntity> checkEntities = JSONUtil.toList(configInfo, ContentCheckEntity.class);
        KJNcConfigManager.setContentChecks(checkEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.checkContentDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

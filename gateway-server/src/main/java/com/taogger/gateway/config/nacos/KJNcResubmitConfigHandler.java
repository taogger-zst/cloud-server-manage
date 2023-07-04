package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import com.taogger.gateway.model.ResubmitEntity;

import java.util.List;

/**
 *
 * @author taogger
 * @date 2022/11/29 18:42
 */
public class KJNcResubmitConfigHandler extends KJAbstractConfigHandler {
    @Override
    public void handler(String configInfo) {
        List<ResubmitEntity> resubmitEntities = JSONUtil.toList(configInfo, ResubmitEntity.class);
        KJNcConfigManager.setResubmits(resubmitEntities);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.resubmitDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

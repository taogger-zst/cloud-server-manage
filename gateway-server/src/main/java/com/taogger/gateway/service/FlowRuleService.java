package com.taogger.gateway.service;

import cn.hutool.core.lang.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.config.nacos.KJNcConfigManager;
import yxd.kj.app.server.gateway.model.FlowRuleEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 限流配置 Service
 * //TODO 需要考虑集群问题导致两边限流数据不一致
 * @author taogger
 * @date 2022/8/17 15:24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlowRuleService {

    /**
     * 限流配置列表
     * @author taogger 
     * @date 2022/8/17 15:25
     * @param page
     * @param limit
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult list(int page, int limit) {
        var pageable = PageRequest.of(page - 1, limit);
        var entities = KJNcConfigManager.getFlows().stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        var rulePage = new PageImpl<>(entities, pageable, KJNcConfigManager.getFlows().size());
        return YXDJSONResult.ok(rulePage);
    }

    /**
     * 添加限流配置
     * @author taogger
     * @date 2022/8/17 15:30
     * @param flowRuleEntity
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult add(FlowRuleEntity flowRuleEntity) {
        Long id = new Snowflake().nextId();
        flowRuleEntity.setId(id.toString());
        KJNcConfigManager.saveFlow(flowRuleEntity);
        return YXDJSONResult.ok();
    }

    /** 
     * 删除限流配置
     * @author taogger 
     * @date 2022/8/17 16:39
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult del(String id) {
        KJNcConfigManager.delFlow(id);
        return YXDJSONResult.ok();
    }

    public YXDJSONResult detail(String id) {
        List<FlowRuleEntity> flows = KJNcConfigManager.getFlows();
        for (FlowRuleEntity flowRuleEntity : flows) {
            if (flowRuleEntity.getId().equals(id)) {
                return YXDJSONResult.ok(flowRuleEntity);
            }
        }
        return YXDJSONResult.errorMsg("数据不存在");
    }
}

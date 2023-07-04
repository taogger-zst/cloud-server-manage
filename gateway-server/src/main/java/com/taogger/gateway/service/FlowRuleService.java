package com.taogger.gateway.service;

import cn.hutool.core.lang.Snowflake;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.FlowRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        List<FlowRuleEntity> entities = KJNcConfigManager.getFlows().stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        PageImpl rulePage = new PageImpl<>(entities, pageable, KJNcConfigManager.getFlows().size());
        return ServerJSONResult.ok(rulePage);
    }

    /**
     * 添加限流配置
     * @author taogger
     * @date 2022/8/17 15:30
     * @param flowRuleEntity
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult add(FlowRuleEntity flowRuleEntity) {
        Long id = new Snowflake().nextId();
        flowRuleEntity.setId(id.toString());
        KJNcConfigManager.saveFlow(flowRuleEntity);
        return ServerJSONResult.ok();
    }

    /** 
     * 删除限流配置
     * @author taogger 
     * @date 2022/8/17 16:39
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult del(String id) {
        KJNcConfigManager.delFlow(id);
        return ServerJSONResult.ok();
    }

    public ServerJSONResult detail(String id) {
        List<FlowRuleEntity> flows = KJNcConfigManager.getFlows();
        for (FlowRuleEntity flowRuleEntity : flows) {
            if (flowRuleEntity.getId().equals(id)) {
                return ServerJSONResult.ok(flowRuleEntity);
            }
        }
        return ServerJSONResult.errorMsg("数据不存在");
    }
}

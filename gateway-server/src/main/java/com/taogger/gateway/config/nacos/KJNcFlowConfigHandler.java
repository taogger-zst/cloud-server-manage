package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import yxd.kj.app.server.gateway.model.FlowRuleEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 限流配置处理
 * @author taogger
 * @date 2022/11/28 16:22
 */
public class KJNcFlowConfigHandler extends KJAbstractConfigHandler {

    @Override
    public void handler(String configInfo) {
        List<FlowRuleEntity> rules = JSONUtil.toList(configInfo, FlowRuleEntity.class);
        //组装资源
        builderApiDefinition(rules);
        //组装限流
        builderFlowRule(rules);
        //赋值
        KJNcConfigManager.setFlows(rules);
    }

    /**
     * 构建资源信息
     * @author taogger
     * @date 2022/8/17 16:30
     * @param flows 限流
     **/
    private void builderApiDefinition(List<FlowRuleEntity> flows) {
        //获取API资源信息列表
        Set<ApiDefinition> apiDefinitions =  new HashSet<>();
        for (FlowRuleEntity flow : flows) {
            //构建当前配置的资源信息
            String[] uris = flow.getUri().split(",");
            var predicateItems = new HashSet<ApiPredicateItem>();
            for (String url : uris) {
                predicateItems.add(new ApiPathPredicateItem()
                        .setPattern(url)
                        .setMatchStrategy(flow.getUrlStrategy()));
            }
            var apiDefinition = new ApiDefinition(flow.getResource());
            apiDefinition.setPredicateItems(predicateItems);
            apiDefinitions.add(apiDefinition);
        }
        //装载进apiDefinition Map
        GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);
    }

    /**
     * 构建限流信息
     * @author taogger
     * @date 2022/8/17 16:31
     * @param flows
     **/
    public void builderFlowRule(List<FlowRuleEntity> flows) {
        Set<GatewayFlowRule> rules = new HashSet<>();
        for (FlowRuleEntity flowRuleEntity : flows) {
            var rule = new GatewayFlowRule(flowRuleEntity.getResource());
            rule.setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME);
            if (flowRuleEntity.getBurst() != null) {
                rule.setBurst(flowRuleEntity.getBurst());
            }
            rule.setCount(flowRuleEntity.getCount());
            rule.setControlBehavior(flowRuleEntity.getControlBehavior());
            rule.setGrade(flowRuleEntity.getGrade());
            if (flowRuleEntity.getMaxQueueingTimeoutMs() != null) {
                rule.setMaxQueueingTimeoutMs(flowRuleEntity.getMaxQueueingTimeoutMs());
            }
            if (flowRuleEntity.getParamParseStrategy() != null) {
                var ruleItem = new GatewayParamFlowItem();
                ruleItem.setParseStrategy(flowRuleEntity.getParamParseStrategy());
                if (flowRuleEntity.getParamParseStrategy() == SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER
                        || flowRuleEntity.getParamParseStrategy() == SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM) {
                    ruleItem.setPattern(flowRuleEntity.getPattern());
                    ruleItem.setMatchStrategy(flowRuleEntity.getMatchStrategy());
                    ruleItem.setFieldName(flowRuleEntity.getParamFieldName());
                }
                rule.setParamItem(ruleItem);
            }
            rules.add(rule);
        }
        GatewayRuleManager.loadRules(rules);
    }

    @Override
    public Boolean isMatch(String dataId) {
        return KJNcConfigManager.flowDataId.equals(dataId);
    }

    @Override
    public void init(String configInfo) {
        handler(configInfo);
    }
}

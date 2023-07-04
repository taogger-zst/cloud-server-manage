package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sentinel限流实体
 *
 * @author taogger
 * @date 2022/8/17 14:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowRuleEntity {

    private String id;

    /**
     * 限流资源名称
     */
    private String resource;

    /**
     * 限流地址，多个逗号分隔
     */
    private String uri;

    /**
     * 限流地址策略
     * SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX / 前缀 / 1
     * SentinelGatewayConstants.URL_MATCH_STRATEGY_EXACT / 精确 / 0
     * SentinelGatewayConstants.URL_MATCH_STRATEGY_REGEX / 正则 / 2
     */
    private Integer urlStrategy;

    /**
     * 限流大小
     */
    private Integer count;

    /**
     * 限流模式
     * RuleConstant.FLOW_GRADE_THREAD / 线程数 / 0
     * RuleConstant.FLOW_GRADE_QPS / 每秒并发数 / 1
     */
    private Integer grade;

    /**
     * 额外允许的请求数
     */
    private Integer burst;

    /**
     * 流量控制模式： 快速失败、均匀排队
     * RuleConstant.CONTROL_BEHAVIOR_DEFAULT / 快速失败 / 0
     * RuleConstant.CONTROL_BEHAVIOR_WARM_UP / 均匀排队 / 1
     */
    private Integer controlBehavior;

    /**
     * 最长排队时长(毫秒,均匀排队模式下生效)
     */
    private Integer maxQueueingTimeoutMs;

    /**
     * 参数提取策略
     * SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP / 客户端ip / 0
     * SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HOST  / 主机 / 1
     * SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER / 请求头 / 2
     * SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM / 地址参数 / 3
     */
    private Integer paramParseStrategy;

    /**
     * 参数名称(header或者url_param模式使用)
     */
    private String paramFieldName;

    /**
     * 参数限流-匹配值(header或者url_param模式使用)
     */
    private String pattern;

    /**
     * 参数限流-匹配策略(header或者url_param模式使用)
     * SentinelGatewayConstants.PARAM_MATCH_STRATEGY_EXACT  / 精确 / 0
     * SentinelGatewayConstants.PARAM_MATCH_STRATEGY_CONTAINS / 包含 / 3
     * SentinelGatewayConstants.PARAM_MATCH_STRATEGY_REGEX / 正则 / 2
     */
    private Integer matchStrategy;

}

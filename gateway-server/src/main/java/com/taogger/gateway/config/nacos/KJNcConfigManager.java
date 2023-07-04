package com.taogger.gateway.config.nacos;

import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.taogger.gateway.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置管理服务
 * @author taogger
 * @date 2022/11/29 9:28
 */
@Slf4j
public class KJNcConfigManager {

    public static final String flowDataId = "gateway-server-sentinel-flow.json";
    public static final String blackIpDataId = "gateway-server-black-ip.json";
    public static final String blackRouteDataId = "gateway-server-black-route.json";
    public static final String checkContentDataId = "gateway-server-check-content.json";
    public static final String developerDataId = "gateway-server-developer.json";
    public static final String filterRouteDataId = "gateway-server-filter.json";
    public static final String resubmitDataId = "gateway-server-resubmit.json";
    private static ConfigService configService;
    private static String group;

    public static List<String> dataIds = new ArrayList<>();
    static {
        dataIds.add(flowDataId);
        dataIds.add(blackIpDataId);
        dataIds.add(blackRouteDataId);
        dataIds.add(checkContentDataId);
        dataIds.add(developerDataId);
        dataIds.add(filterRouteDataId);
        dataIds.add(resubmitDataId);
    }
    //限流配置
    private static List<FlowRuleEntity> flows = new ArrayList<>();
    //ip黑名单配置
    private static List<BlackIpEntity> blackIps = new ArrayList<>();
    //route黑名单配置
    private static List<BlackRouteEntity> blackRoutes = new ArrayList<>();
    //内容检查实体配置
    private static List<ContentCheckEntity> checkEntities = new ArrayList<>();
    //开发者配置管理
    private static List<DeveloperEntity> developerEntities = new ArrayList<>();
    //过滤路由管理
    private static List<FilterRouteEntity> filterRoutes = new ArrayList<>();
    //重复提交管理
    private static List<ResubmitEntity> resubmitEntities = new ArrayList<>();
    public static List<ResubmitEntity> getResubmitEntities() { return resubmitEntities; }
    public static List<BlackRouteEntity> getBlackRoutes() { return blackRoutes; }
    public static List<BlackIpEntity> getBlackIps() { return blackIps; }
    public static List<FlowRuleEntity> getFlows() { return flows; }
    public static List<ContentCheckEntity> getCheckEntities() { return checkEntities; }
    public static List<DeveloperEntity> getDeveloperEntities() { return developerEntities; }
    public static List<FilterRouteEntity> getFilterRoutes() { return filterRoutes; }
    public static void setConfigService(ConfigService configService) { KJNcConfigManager.configService = configService; }
    public static void setGroup(String group) { KJNcConfigManager.group = group; }

    /**
     * 删除限流
     * @author taogger
     * @date 2022/11/30 11:32
     * @param id
    **/
    public synchronized static void delFlow(String id) {
        FlowRuleEntity delObject = null;
        for (FlowRuleEntity flowRuleEntity : KJNcConfigManager.getFlows()) {
            if (flowRuleEntity.getId().equals(id)) {
                delObject = flowRuleEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getFlows().remove(delObject);
        }
        //发布配置
        publishFlow();
    }

    /**
     * 赋值限流
     * @author taogger
     * @date 2022/11/30 11:32
     * @param flows
    **/
    public synchronized static void setFlows(List<FlowRuleEntity> flows) {
        KJNcConfigManager.flows = flows;
    }

    /**
     * 保存限流
     * @author taogger
     * @date 2022/11/30 11:32
     * @param flowRuleEntity
    **/
    public synchronized static void saveFlow(FlowRuleEntity flowRuleEntity) {
        Boolean isExist = Boolean.FALSE;
        for (FlowRuleEntity flow : flows) {
            if (flow.getId().equals(flowRuleEntity.getId())) {
                flow.setBurst(flowRuleEntity.getBurst());
                flow.setCount(flowRuleEntity.getCount());
                flow.setControlBehavior(flowRuleEntity.getControlBehavior());
                flow.setGrade(flowRuleEntity.getGrade());
                flow.setMatchStrategy(flowRuleEntity.getMatchStrategy());
                flow.setMaxQueueingTimeoutMs(flowRuleEntity.getMaxQueueingTimeoutMs());
                flow.setParamFieldName(flowRuleEntity.getParamFieldName());
                flow.setParamParseStrategy(flowRuleEntity.getParamParseStrategy());
                flow.setPattern(flowRuleEntity.getPattern());
                flow.setResource(flowRuleEntity.getResource());
                flow.setUri(flowRuleEntity.getUri());
                flow.setUrlStrategy(flowRuleEntity.getUrlStrategy());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            flows.add(flowRuleEntity);
        }
        //发布配置
        publishFlow();
    }

    /**
     * 发布限流配置
     * @author taogger
     * @date 2022/11/30 13:42
    **/
    private static void publishFlow() {
        try {
            configService.publishConfig(KJNcConfigManager.flowDataId, group, JSONUtil.toJsonStr(KJNcConfigManager.getFlows()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布限流配置失败,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除黑名单ip
     * @author taogger
     * @date 2022/11/30 11:32
     * @param id
    **/
    public synchronized static void delBlackIp(String id) {
        BlackIpEntity delObject = null;
        for (BlackIpEntity blackIpEntity : KJNcConfigManager.getBlackIps()) {
            if (blackIpEntity.getId().equals(id)) {
                delObject = blackIpEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getBlackIps().remove(delObject);
        }
        publishBlackIp();
    }

    /**
     * 赋值黑名单ip
     * @author taogger
     * @date 2022/11/30 11:33
     * @param blackIps
    **/
    public synchronized static void setBlackIps(List<BlackIpEntity> blackIps) {
        KJNcConfigManager.blackIps = blackIps;
    }

    /**
     * 保存黑名单ip
     * @author taogger
     * @date 2022/11/30 11:34
     * @param blackIpEntity
    **/
    public synchronized static void saveBlackIp(BlackIpEntity blackIpEntity) {
        Boolean isExist = Boolean.FALSE;
        for (BlackIpEntity blackIp : blackIps) {
            if (blackIp.getId().equals(blackIpEntity.getId())) {
                blackIp.setIp(blackIpEntity.getIp());
                blackIp.setExpireTime(blackIpEntity.getExpireTime());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            blackIps.add(blackIpEntity);
        }
        publishBlackIp();
    }

    /**
     * 发布黑名单ip配置
     * @author taogger
     * @date 2022/11/30 13:42
    **/
    private static void publishBlackIp() {
        try {
            configService.publishConfig(KJNcConfigManager.blackIpDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getBlackIps()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布黑名单ip配置失败,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除黑名单路由
     * @author taogger
     * @date 2022/11/30 11:34
     * @param id
    **/
    public synchronized static void delBlackRoute(String id) {
        BlackRouteEntity delObject = null;
        for (BlackRouteEntity BlackRouteEntity : KJNcConfigManager.getBlackRoutes()) {
            if (BlackRouteEntity.getId().equals(id)) {
                delObject = BlackRouteEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getBlackRoutes().remove(delObject);
        }
        publishBlackRoute();
    }

    /**
     * 赋值黑名单路由
     * @author taogger
     * @date 2022/11/30 11:34
     * @param blackRoutes
    **/
    public synchronized static void setBlackRoutes(List<BlackRouteEntity> blackRoutes) {
        KJNcConfigManager.blackRoutes = blackRoutes;
    }

    /**
     * 保存黑名单路由
     * @author taogger
     * @date 2022/11/30 11:35
     * @param blackRouteEntity
    **/
    public synchronized static void saveBlackRoute(BlackRouteEntity blackRouteEntity) {
        Boolean isExist = Boolean.FALSE;
        for (BlackRouteEntity blackRoute : blackRoutes) {
            if (blackRoute.getId().equals(blackRouteEntity.getId())) {
                blackRoute.setRoute(blackRouteEntity.getRoute());
                blackRoute.setExpireTime(blackRouteEntity.getExpireTime());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            blackRoutes.add(blackRouteEntity);
        }
        publishBlackRoute();
    }

    /**
     * 发布黑名单路由配置
     * @author taogger
     * @date 2022/11/30 13:42
     **/
    private static void publishBlackRoute() {
        try {
            configService.publishConfig(KJNcConfigManager.blackRouteDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getBlackRoutes()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布黑名单路由配置,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除内容审核
     * @author taogger
     * @date 2022/11/30 11:35
     * @param id
    **/
    public synchronized static void delContentCheck(String id) {
        ContentCheckEntity delObject = null;
        for (ContentCheckEntity contentCheckEntity : KJNcConfigManager.getCheckEntities()) {
            if (contentCheckEntity.getId().equals(id)) {
                delObject = contentCheckEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getCheckEntities().remove(delObject);
        }
        publishContentCheck();
    }

    /**
     * 赋值内容审核
     * @author taogger
     * @date 2022/11/30 11:35
     * @param contentChecks
    **/
    public synchronized static void setContentChecks(List<ContentCheckEntity> contentChecks) {
        KJNcConfigManager.checkEntities = contentChecks;
    }

    /**
     * 保存内容审核
     * @author taogger
     * @date 2022/11/30 11:35
     * @param contentCheckEntity
    **/
    public synchronized static void saveContentCheck(ContentCheckEntity contentCheckEntity) {
        Boolean isExist = Boolean.FALSE;
        for (ContentCheckEntity checkEntity : checkEntities) {
            if (checkEntity.getId().equals(contentCheckEntity.getId())) {
                checkEntity.setContentType(contentCheckEntity.getContentType());
                checkEntity.setUri(contentCheckEntity.getUri());
                checkEntity.setMethod(contentCheckEntity.getMethod());
                checkEntity.setParams(contentCheckEntity.getParams());
                checkEntity.setType(contentCheckEntity.getType());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            checkEntities.add(contentCheckEntity);
        }
        publishContentCheck();
    }

    /**
     * 发布内容审核配置
     * @author taogger
     * @date 2022/11/30 13:42
     **/
    private static void publishContentCheck() {
        try {
            configService.publishConfig(KJNcConfigManager.checkContentDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getCheckEntities()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布内容审核配置,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除开发者管理
     * @author taogger
     * @date 2022/11/30 11:35
     * @param id
    **/
    public synchronized static void delDeveloper(String id) {
        DeveloperEntity delObject = null;
        for (DeveloperEntity developerEntity : KJNcConfigManager.getDeveloperEntities()) {
            if (developerEntity.getId().equals(id)) {
                delObject = developerEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getDeveloperEntities().remove(delObject);
        }
        publishDevelopers();
    }

    /**
     * 赋值开发者管理
     * @author taogger
     * @date 2022/11/30 11:36
     * @param developers
    **/
    public synchronized static void setDevelopers(List<DeveloperEntity> developers) {
        KJNcConfigManager.developerEntities = developers;
    }

    /**
     * 保存开发者管理
     * @author taogger
     * @date 2022/11/30 11:36
     * @param developerEntity
    **/
    public synchronized static void saveDevelopers(DeveloperEntity developerEntity) {
        Boolean isExist = Boolean.FALSE;
        for (DeveloperEntity developer : developerEntities) {
            if (developer.getId().equals(developerEntity.getId())) {
                developer.setName(developerEntity.getName());
                developer.setNamespace(developerEntity.getNamespace());
                developer.setUpdateTime(developerEntity.getUpdateTime());
                developer.setCreateTime(developerEntity.getCreateTime());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            developerEntities.add(developerEntity);
        }
        publishDevelopers();
    }

    /**
     * 发布开发者配置
     * @author taogger
     * @date 2022/11/30 13:42
     **/
    private static void publishDevelopers() {
        try {
            configService.publishConfig(KJNcConfigManager.developerDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getDeveloperEntities()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布开发者配置,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除过滤路由
     * @author taogger
     * @date 2022/11/30 11:36
     * @param id
    **/
    public synchronized static void delFilterRouter(String id) {
        FilterRouteEntity delObject = null;
        for (FilterRouteEntity filterRouteEntity : KJNcConfigManager.getFilterRoutes()) {
            if (filterRouteEntity.getId().equals(id)) {
                delObject = filterRouteEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getFilterRoutes().remove(delObject);
        }
        publishFilterRoute();
    }

    /**
     * 赋值过滤路由
     * @author taogger
     * @date 2022/11/30 11:36
     * @param developers
    **/
    public synchronized static void setFilterRoutes(List<FilterRouteEntity> developers) {
        KJNcConfigManager.filterRoutes = developers;
    }

    /**
     * 保存过滤路由
     * @author taogger
     * @date 2022/11/30 11:37
     * @param filterRouteEntity
    **/
    public synchronized static void saveFilterRoute(FilterRouteEntity filterRouteEntity) {
        Boolean isExist = Boolean.FALSE;
        for (FilterRouteEntity filter : filterRoutes) {
            if (filter.getId().equals(filterRouteEntity.getId())) {
                filter.setName(filterRouteEntity.getName());
                filter.setUri(filterRouteEntity.getUri());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            filterRoutes.add(filterRouteEntity);
        }
        publishFilterRoute();
    }

    /**
     * 发布过滤路由配置
     * @author taogger
     * @date 2022/11/30 13:42
     **/
    private static void publishFilterRoute() {
        try {
            configService.publishConfig(KJNcConfigManager.filterRouteDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getFilterRoutes()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布内容审核配置,异常信息为:{}】",e.getErrMsg());
        }
    }

    /**
     * 删除重复提交
     * @author taogger
     * @date 2022/11/30 11:37
     * @param id
    **/
    public synchronized static void delResubmit(String id) {
        ResubmitEntity delObject = null;
        for (ResubmitEntity resubmitEntity : KJNcConfigManager.getResubmitEntities()) {
            if (resubmitEntity.getId().equals(id)) {
                delObject = resubmitEntity;
                break;
            }
        }
        if (delObject != null) {
            KJNcConfigManager.getResubmitEntities().remove(delObject);
        }
        publishResubmit();
    }

    /**
     * 赋值重复提交
     * @author taogger
     * @date 2022/11/30 11:37
     * @param resubmits
    **/
    public synchronized static void setResubmits(List<ResubmitEntity> resubmits) {
        KJNcConfigManager.resubmitEntities = resubmits;
    }

    /**
     * 保存重复提交
     * @author taogger
     * @date 2022/11/30 11:37
     * @param resubmitEntity
    **/
    public synchronized static void saveResubmit(ResubmitEntity resubmitEntity) {
        Boolean isExist = Boolean.FALSE;
        for (ResubmitEntity resubmit : resubmitEntities) {
            if (resubmit.getId().equals(resubmitEntity.getId())) {
                resubmit.setContentType(resubmitEntity.getContentType());
                resubmit.setExpireTime(resubmitEntity.getExpireTime());
                resubmit.setMethod(resubmitEntity.getMethod());
                resubmit.setParams(resubmitEntity.getParams());
                resubmit.setUrl(resubmitEntity.getUrl());
                isExist = Boolean.TRUE;
            }
        }
        if (!isExist) {
            resubmitEntities.add(resubmitEntity);
        }
        publishResubmit();
    }

    /**
     * 发布重复提交配置
     * @author taogger
     * @date 2022/11/30 13:42
     **/
    private static void publishResubmit() {
        try {
            configService.publishConfig(KJNcConfigManager.resubmitDataId, group,
                    JSONUtil.toJsonStr(KJNcConfigManager.getResubmitEntities()), ConfigType.JSON.getType());
        } catch (NacosException e) {
            log.error("【发布重复提交配置,异常信息为:{}】",e.getErrMsg());
        }
    }
}

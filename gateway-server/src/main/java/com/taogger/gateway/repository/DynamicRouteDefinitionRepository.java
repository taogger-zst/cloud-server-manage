package com.taogger.gateway.repository;

import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态路由
 * @author taogger
 * @date 2022/11/29 11:50
 */
@Component
@RequiredArgsConstructor
public class DynamicRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    private final ConfigService configService;
    private String dataId = "gateway-server-dynamic-route.json";

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    private ApplicationEventPublisher applicationEventPublisher;

    private List<RouteDefinition> routeDefinitions = new ArrayList<>();

    public List<RouteDefinition> getRoutes() {
        return routeDefinitions;
    }

    @SneakyThrows
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            if (ObjectUtils.isEmpty(r.getId())) {
                return Mono.error(new IllegalArgumentException("id may not be empty"));
            }
            Boolean isExist = Boolean.FALSE;
            for (RouteDefinition routeDs : routeDefinitions) {
                if (routeDs.getId().equals(r.getId())) {
                    routeDs.setFilters(r.getFilters());
                    routeDs.setPredicates(r.getPredicates());
                    routeDs.setUri(r.getUri());
                    routeDs.setMetadata(r.getMetadata());
                    routeDs.setOrder(r.getOrder());
                    isExist = Boolean.TRUE;
                    break;
                }
            }
            if (!isExist) {
                routeDefinitions.add(r);
                //发布配置
                try {
                    configService.publishConfig(dataId, group, JSONUtil.toJsonStr(routeDefinitions), ConfigType.JSON.getType());
                } catch (NacosException e) {
                    routeDefinitions.remove(r);
                    return Mono.error(new IllegalArgumentException("发布配置失败,nacos异常"));
                }
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            RouteDefinition toDelDefinition = null;
            for (RouteDefinition routeDs : routeDefinitions) {
                if (routeDs.getId().equals(id)) {
                    toDelDefinition = routeDs;
                    break;
                }
            }
            if (toDelDefinition != null) {
                routeDefinitions.remove(toDelDefinition);
                //发布配置
                try {
                    configService.publishConfig(dataId, group, JSONUtil.toJsonStr(routeDefinitions), ConfigType.JSON.getType());
                } catch (NacosException e) {
                    routeDefinitions.add(toDelDefinition);
                    return Mono.error(new IllegalArgumentException("发布配置失败,nacos异常"));
                }
                return Mono.empty();
            } else {
                return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
            }
        });
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        //初始化获取配置、开启监听
        var config = configService.getConfig(dataId, group, 3000);
        routeDefinitions = JSONUtil.toList(config, RouteDefinition.class);
        configService.addListener(dataId, group, new AbstractSharedListener() {
            @Override
            public void innerReceive(String dataId, String group, String configInfo) {
                var routeDs = JSONUtil.toList(configInfo, RouteDefinition.class);
                routeDefinitions.clear();
                routeDefinitions.addAll(routeDs);
                applicationEventPublisher.publishEvent(this);
            }
        });
        applicationEventPublisher.publishEvent(this);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}

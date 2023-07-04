package com.taogger.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.repository.DynamicRouteDefinitionRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态路由
 * // TODO 需要考虑网关集群问题导致两边动态路由不一致
 * @author taogger
 * @date 2022/8/10 17:03
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DynamicRouteService {

    private final DynamicRouteDefinitionRepository definitionRepository;
    /**
     * 获取全部路由
     * @author taogger
     * @date 2022/8/10 17:27
     * @return {@link Flux<RouteDefinition>}
    **/
    public YXDJSONResult routes(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<RouteDefinition> definitions = definitionRepository.getRoutes().stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        var rulePage = new PageImpl<>(definitions, pageable, definitionRepository.getRoutes().size());
        return YXDJSONResult.ok(rulePage);
    }

    /**
     * 增加路由
     * @author taogger
     * @date 2022/8/10 17:07
     * @param definition
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult add(RouteDefinition definition) {
        definitionRepository.save(Mono.just(definition)).subscribe();
        return YXDJSONResult.ok();
    }

    /**
     * 删除路由
     * @author taogger
     * @date 2022/8/10 17:14
     * @param id
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult delete(String id) {
        definitionRepository.delete(Mono.just(id)).subscribe();
        return YXDJSONResult.ok();
    }
}


package com.taogger.gateway.service;

import cn.hutool.core.lang.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.config.nacos.KJNcConfigManager;
import yxd.kj.app.server.gateway.model.FilterRouteEntity;

import java.util.stream.Collectors;

/**
 * 过滤路由服务
 * @author taogger
 * @date 2022/8/15 14:04
 */
@Service
@RequiredArgsConstructor
public class FilterRouteService {

    /**
     * 过滤路由列表
     * @author taogger
     * @date 2022/8/15 14:06
     * @param page
     * @param limit
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult list(int page, int limit) {
        var pageable = PageRequest.of(page - 1, limit);
        var entities = KJNcConfigManager.getFilterRoutes().stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        var rulePage = new PageImpl<>(entities, pageable, KJNcConfigManager.getFilterRoutes().size());
        return YXDJSONResult.ok(rulePage);
    }

    /**
     * 添加过滤路由
     * @author taogger
     * @date 2022/8/15 14:10
     * @param filterRouteEntity
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult add(FilterRouteEntity filterRouteEntity) {
        Long id = new Snowflake().nextId();
        filterRouteEntity.setId(id.toString());
        KJNcConfigManager.saveFilterRoute(filterRouteEntity);
        return YXDJSONResult.ok();
    }

    /**
     * 删除过滤路由
     * @author taogger
     * @date 2022/8/15 14:11
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult del(String id) {
        KJNcConfigManager.delFilterRouter(id);
        return YXDJSONResult.ok();
    }
}

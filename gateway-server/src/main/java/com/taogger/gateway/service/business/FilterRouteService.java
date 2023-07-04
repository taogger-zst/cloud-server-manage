package com.taogger.gateway.service.business;

import cn.hutool.core.lang.Snowflake;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.FilterRouteEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        List<FilterRouteEntity> entities = KJNcConfigManager.getFilterRoutes().stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        PageImpl rulePage = new PageImpl<>(entities, pageable, KJNcConfigManager.getFilterRoutes().size());
        return ServerJSONResult.ok(rulePage);
    }

    /**
     * 添加过滤路由
     * @author taogger
     * @date 2022/8/15 14:10
     * @param filterRouteEntity
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult add(FilterRouteEntity filterRouteEntity) {
        Long id = new Snowflake().nextId();
        filterRouteEntity.setId(id.toString());
        KJNcConfigManager.saveFilterRoute(filterRouteEntity);
        return ServerJSONResult.ok();
    }

    /**
     * 删除过滤路由
     * @author taogger
     * @date 2022/8/15 14:11
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult del(String id) {
        KJNcConfigManager.delFilterRouter(id);
        return ServerJSONResult.ok();
    }
}

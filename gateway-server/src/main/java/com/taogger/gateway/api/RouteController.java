package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.FilterRouteEntity;
import com.taogger.gateway.service.business.DynamicRouteService;
import com.taogger.gateway.service.business.FilterRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 动态路由Api
 * @author taogger
 * @date 2022/8/10 17:28
 */
@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final DynamicRouteService dynamicRouteService;
    private final FilterRouteService filterRouteService;

    /** 
     * 路由列表
     * @author taogger
     * @date 2022/8/11 9:24
     * @return {@link Flux<RouteDefinition>}
    **/
    @GetMapping("/dynamic/list")
    public ServerJSONResult dynamicList(int pageNum, int pageSize) {
        return dynamicRouteService.routes(pageNum, pageSize);
    }

    /**
     * 保存路由
     * @author taogger
     * @date 2022/8/11 9:24
     * @param routeDefinition
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/dynamic/add")
    public ServerJSONResult dynamicAdd(@RequestBody RouteDefinition routeDefinition) {
        return dynamicRouteService.add(routeDefinition);
    }

    /**
     * 删除路由
     * @author taogger
     * @date 2022/8/11 9:25
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/dynamic/del/{id}")
    public ServerJSONResult dynamicUpd(@PathVariable("id") String id) {
        return dynamicRouteService.delete(id);
    }


    /**
     * 过滤路由列表
     * @author taogger
     * @date 2022/8/15 14:13
     * @param pageNum
     * @param pageSize
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/filter/list")
    public ServerJSONResult filterList(int pageNum, int pageSize) {
        return filterRouteService.list(pageNum, pageSize);
    }

    /** 
     * 新增过滤路由
     * @author taogger 
     * @date 2022/8/15 14:15
     * @param filterRouteEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/filter/add")
    public ServerJSONResult filterAdd(@RequestBody FilterRouteEntity filterRouteEntity) {
        return filterRouteService.add(filterRouteEntity);
    }

    /**
     * 删除过滤路由
     * @author taogger
     * @date 2022/8/15 14:16
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/filter/del/{id}")
    public ServerJSONResult filterDel(@PathVariable("id") String id) {
        return filterRouteService.del(id);
    }
}

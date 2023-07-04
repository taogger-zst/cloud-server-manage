package com.taogger.gateway.api;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.model.FilterRouteEntity;
import yxd.kj.app.server.gateway.service.DynamicRouteService;
import yxd.kj.app.server.gateway.service.FilterRouteService;

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
    public YXDJSONResult dynamicList(int pageNum, int pageSize) {
        return dynamicRouteService.routes(pageNum, pageSize);
    }

    /**
     * 保存路由
     * @author taogger
     * @date 2022/8/11 9:24
     * @param routeDefinition
     * @return {@link YXDJSONResult}
    **/
    @PostMapping("/dynamic/add")
    public YXDJSONResult dynamicAdd(@RequestBody RouteDefinition routeDefinition) {
        return dynamicRouteService.add(routeDefinition);
    }

    /**
     * 删除路由
     * @author taogger
     * @date 2022/8/11 9:25
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @DeleteMapping("/dynamic/del/{id}")
    public YXDJSONResult dynamicUpd(@PathVariable("id") String id) {
        return dynamicRouteService.delete(id);
    }


    /**
     * 过滤路由列表
     * @author taogger
     * @date 2022/8/15 14:13
     * @param pageNum
     * @param pageSize
     * @return {@link YXDJSONResult}
    **/
    @GetMapping("/filter/list")
    public YXDJSONResult filterList(int pageNum, int pageSize) {
        return filterRouteService.list(pageNum, pageSize);
    }

    /** 
     * 新增过滤路由
     * @author taogger 
     * @date 2022/8/15 14:15
     * @param filterRouteEntity
     * @return {@link YXDJSONResult}
    **/
    @PostMapping("/filter/add")
    public YXDJSONResult filterAdd(@RequestBody FilterRouteEntity filterRouteEntity) {
        return filterRouteService.add(filterRouteEntity);
    }

    /**
     * 删除过滤路由
     * @author taogger
     * @date 2022/8/15 14:16
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @DeleteMapping("/filter/del/{id}")
    public YXDJSONResult filterDel(@PathVariable("id") String id) {
        return filterRouteService.del(id);
    }
}

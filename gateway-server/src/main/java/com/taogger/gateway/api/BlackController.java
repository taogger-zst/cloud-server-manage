package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.BlackIpEntity;
import com.taogger.gateway.model.BlackRouteEntity;
import com.taogger.gateway.service.BlackIpService;
import com.taogger.gateway.service.BlackRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 黑名单管理
 * @author taogger
 * @date 2022/8/15 17:22
 */
@RestController
@RequestMapping("/black")
@RequiredArgsConstructor
public class BlackController {

    private final BlackIpService blackIpService;
    private final BlackRouteService blackRouteService;

    /**
     * ip 地址
     * @author taogger
     * @date 2022/8/15 18:28
     * @param pageNum
     * @param pageSize
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/ip/list")
    public ServerJSONResult ipList(int pageNum, int pageSize) {
        return blackIpService.list(pageNum, pageSize);
    }

    /**
     * ip 增加
     * @author taogger
     * @date 2022/8/15 18:28
     * @param blackIpEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/ip/add")
    public ServerJSONResult ipAdd(@RequestBody BlackIpEntity blackIpEntity) {
        return blackIpService.add(blackIpEntity);
    }


    /**
     * ip删除
     * @author taogger
     * @date 2022/8/16 9:21
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/ip/del/{id}")
    public ServerJSONResult ipDel(@PathVariable("id") String id) {
        return blackIpService.del(id);
    }

    /**
     * 路由黑名单列表
     * @author taogger
     * @date 2022/8/16 10:40
     * @param pageNum
     * @param pageSize
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/route/list")
    public ServerJSONResult routeList(int pageNum, int pageSize) {
        return blackRouteService.list(pageNum, pageSize);
    }

    /**
     * 路由黑名单添加
     * @author taogger
     * @date 2022/8/16 10:41
     * @param blackRouteEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/route/add")
    public ServerJSONResult routeAdd(@RequestBody BlackRouteEntity blackRouteEntity) {
        return blackRouteService.add(blackRouteEntity);
    }

    /**
     * 路由黑名单删除
     * @author taogger
     * @date 2022/8/16 10:43
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/route/del/{id}")
    public ServerJSONResult routeDel(@PathVariable("id") String id) {
        return blackRouteService.del(id);
    }
}

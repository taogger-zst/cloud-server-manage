package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.DeveloperEntity;
import com.taogger.gateway.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * 开发者管理api
 * @author taogger
 * @date 2022/8/11 15:25
 */
@RestController
@RequestMapping("/developer")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.nacos.discovery.metadata.env",havingValue = "dev")
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 开发者信息列表
     * @author taogger
     * @date 2022/8/12 11:31
     * @param pageNum
     * @param pageSize
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/list")
    public ServerJSONResult list(int pageNum, int pageSize) {
        return developerService.list(pageNum, pageSize);
    }

    /**
     * 添加开发者信息
     * @author taogger
     * @date 2022/8/12 9:15
     * @param developerEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/add")
    public ServerJSONResult add(@RequestBody DeveloperEntity developerEntity) {
        return developerService.add(developerEntity);
    }

    /**
     * 删除开发者信息
     * @author taogger
     * @date 2022/8/12 11:13
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public ServerJSONResult del(@PathVariable("id") String id) {
        return developerService.del(id);
    }
}

package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.ContentCheckEntity;
import com.taogger.gateway.service.ContentCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 内容审核配置api
 * @author taogger
 * @date 2022/8/18 14:29
 */
@RestController
@RequestMapping("/content/check")
@RequiredArgsConstructor
public class ContentCheckController {

    private final ContentCheckService contentCheckService;

    /**
     * 内容审核配置列表
     * @author taogger
     * @date 2022/8/18 14:30
     * @param pageNum
     * @param pageSize
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/list")
    public ServerJSONResult list(int pageNum, int pageSize) {
        return contentCheckService.list(pageNum, pageSize);
    }

    /**
     * 添加内容审核配置
     * @author taogger
     * @date 2022/8/18 14:32
     * @param contentCheckEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/add")
    public ServerJSONResult add(@RequestBody ContentCheckEntity contentCheckEntity) {
        return contentCheckService.add(contentCheckEntity);
    }

    /**
     * 删除内容审核配置
     * @author taogger
     * @date 2022/8/18 14:38
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public ServerJSONResult del(@PathVariable("id") String id) {
        return contentCheckService.del(id);
    }
}

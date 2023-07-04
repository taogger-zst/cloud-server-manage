package com.taogger.gateway.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.model.ContentCheckEntity;
import yxd.kj.app.server.gateway.service.ContentCheckService;

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
     * @return {@link YXDJSONResult}
    **/
    @GetMapping("/list")
    public YXDJSONResult list(int pageNum, int pageSize) {
        return contentCheckService.list(pageNum, pageSize);
    }

    /**
     * 添加内容审核配置
     * @author taogger
     * @date 2022/8/18 14:32
     * @param contentCheckEntity
     * @return {@link YXDJSONResult}
    **/
    @PostMapping("/add")
    public YXDJSONResult add(@RequestBody ContentCheckEntity contentCheckEntity) {
        return contentCheckService.add(contentCheckEntity);
    }

    /**
     * 删除内容审核配置
     * @author taogger
     * @date 2022/8/18 14:38
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public YXDJSONResult del(@PathVariable("id") String id) {
        return contentCheckService.del(id);
    }
}

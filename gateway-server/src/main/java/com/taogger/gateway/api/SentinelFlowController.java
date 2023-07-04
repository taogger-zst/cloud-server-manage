package com.taogger.gateway.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.model.FlowRuleEntity;
import yxd.kj.app.server.gateway.service.FlowRuleService;

/**
 * 限流接口
 * @author taogger
 * @date 2022/8/17 11:40
 */
@RestController
@RequestMapping("/flow/role")
@RequiredArgsConstructor
public class SentinelFlowController {

    private final FlowRuleService flowRuleService;

    /**
     * 限流列表
     * @author taogger
     * @date 2022/8/17 16:57
     * @param pageNum
     * @param pageSize
     * @return {@link YXDJSONResult}
    **/
    @GetMapping("/list")
    public YXDJSONResult list(int pageNum, int pageSize) {
        return flowRuleService.list(pageNum, pageSize);
    }

    /**
     * 添加限流配置
     * @author taogger
     * @date 2022/8/17 16:58
     * @param flowRuleEntity
     * @return {@link YXDJSONResult}
    **/
    @PostMapping("/add")
    public YXDJSONResult add(@RequestBody FlowRuleEntity flowRuleEntity) {
        return flowRuleService.add(flowRuleEntity);
    }

    /**
     * 删除限流配置
     * @author taogger
     * @date 2022/8/17 17:00
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public YXDJSONResult del(@PathVariable("id") String id) {
        return flowRuleService.del(id);
    }

    /**
     * 详情
     * @author taogger
     * @date 2022/8/17 17:15
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @GetMapping("/detail/{id}")
    public YXDJSONResult detail(@PathVariable("id") String id) {
        return flowRuleService.detail(id);
    }
}

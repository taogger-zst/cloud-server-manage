package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.FlowRuleEntity;
import com.taogger.gateway.service.FlowRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/list")
    public ServerJSONResult list(int pageNum, int pageSize) {
        return flowRuleService.list(pageNum, pageSize);
    }

    /**
     * 添加限流配置
     * @author taogger
     * @date 2022/8/17 16:58
     * @param flowRuleEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/add")
    public ServerJSONResult add(@RequestBody FlowRuleEntity flowRuleEntity) {
        return flowRuleService.add(flowRuleEntity);
    }

    /**
     * 删除限流配置
     * @author taogger
     * @date 2022/8/17 17:00
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public ServerJSONResult del(@PathVariable("id") String id) {
        return flowRuleService.del(id);
    }

    /**
     * 详情
     * @author taogger
     * @date 2022/8/17 17:15
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/detail/{id}")
    public ServerJSONResult detail(@PathVariable("id") String id) {
        return flowRuleService.detail(id);
    }
}

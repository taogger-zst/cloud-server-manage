package com.taogger.gateway.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.model.ResubmitEntity;
import yxd.kj.app.server.gateway.service.ResubmitService;

/**
 * 重复提交管理
 * @author taogger
 * @date 2022/8/16 14:12
 */
@RestController
@RequestMapping("/resubmit")
@RequiredArgsConstructor
public class ResubmitController {

    private final ResubmitService resubmitService;

    /**
     * 列表接口
     * @author taogger
     * @date 2022/8/16 14:14
     * @param pageNum
     * @param pageSize
     * @return {@link YXDJSONResult}
    **/
    @GetMapping("/list")
    public YXDJSONResult list(int pageNum, int pageSize) {
        return resubmitService.list(pageNum, pageSize);
    }

    /**
     * 添加
     * @author taogger
     * @date 2022/8/16 14:15
     * @param resubmitEntity
     * @return {@link YXDJSONResult}
    **/
    @PostMapping("/add")
    public YXDJSONResult add(@RequestBody ResubmitEntity resubmitEntity) {
        return resubmitService.add(resubmitEntity);
    }

    /**
     * 删除
     * @author taogger
     * @date 2022/8/16 14:16
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public YXDJSONResult del(@PathVariable("id") String id) {
        return resubmitService.del(id);
    }
}

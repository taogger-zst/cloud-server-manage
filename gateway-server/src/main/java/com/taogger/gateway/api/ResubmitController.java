package com.taogger.gateway.api;

import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.model.ResubmitEntity;
import com.taogger.gateway.service.ResubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
     * @return {@link ServerJSONResult}
    **/
    @GetMapping("/list")
    public ServerJSONResult list(int pageNum, int pageSize) {
        return resubmitService.list(pageNum, pageSize);
    }

    /**
     * 添加
     * @author taogger
     * @date 2022/8/16 14:15
     * @param resubmitEntity
     * @return {@link ServerJSONResult}
    **/
    @PostMapping("/add")
    public ServerJSONResult add(@RequestBody ResubmitEntity resubmitEntity) {
        return resubmitService.add(resubmitEntity);
    }

    /**
     * 删除
     * @author taogger
     * @date 2022/8/16 14:16
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @DeleteMapping("/del/{id}")
    public ServerJSONResult del(@PathVariable("id") String id) {
        return resubmitService.del(id);
    }
}

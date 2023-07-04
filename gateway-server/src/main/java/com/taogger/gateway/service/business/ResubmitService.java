package com.taogger.gateway.service.business;

import cn.hutool.core.lang.Snowflake;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.ResubmitEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 重复提交service
 * @author taogger
 * @date 2022/8/16 14:10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResubmitService {

    /**
     * 列表接口
     * @author taogger
     * @date 2022/8/16 14:11
     * @param page
     * @param limit
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //分页
        List<ResubmitEntity> entities = KJNcConfigManager.getResubmitEntities().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        PageImpl resubmitEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getResubmitEntities().size());
        return ServerJSONResult.ok(resubmitEntities);
    }

    /**
     * 添加重复提交
     * @author taogger
     * @date 2022/8/15 14:10
     * @param resubmitEntity
     * @return {@link ServerJSONResult}
     **/
    @SneakyThrows
    public ServerJSONResult add(ResubmitEntity resubmitEntity) {
        Long id = new Snowflake().nextId();
        resubmitEntity.setId(id.toString());
        KJNcConfigManager.saveResubmit(resubmitEntity);
        return ServerJSONResult.ok();
    }

    /**
     * 删除重复提交
     * @author taogger
     * @date 2022/8/15 14:11
     * @param id
     * @return {@link ServerJSONResult}
     **/
    @SneakyThrows
    public ServerJSONResult del(String id) {
        KJNcConfigManager.delResubmit(id);
        return ServerJSONResult.ok();
    }
}

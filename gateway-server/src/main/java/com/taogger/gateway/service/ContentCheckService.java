package com.taogger.gateway.service;

import cn.hutool.core.lang.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.config.nacos.KJNcConfigManager;
import yxd.kj.app.server.gateway.model.ContentCheckEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容审核service
 * @author taogger
 * @date 2022/8/16 14:00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentCheckService {


    /**
     * 内容审核列表
     * @author taogger 
     * @date 2022/8/16 14:02
     * @param page
     * @param limit
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult list(int page, int limit) {
        var pageable = PageRequest.of(page - 1, limit);
        //分页
        List<ContentCheckEntity> entities = KJNcConfigManager.getCheckEntities().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        var blackIpEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getCheckEntities().size());
        return YXDJSONResult.ok(blackIpEntities);
    }

    /**
     * 添加内容审核
     * @author taogger
     * @date 2022/8/16 14:07
     * @param contentCheckEntity
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult add(ContentCheckEntity contentCheckEntity) {
        Long id = new Snowflake().nextId();
        contentCheckEntity.setId(id.toString());
        KJNcConfigManager.saveContentCheck(contentCheckEntity);
        return YXDJSONResult.ok();
    }

    /**
     * 删除内容审核
     * @author taogger
     * @date 2022/8/16 14:09
     * @param id
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult del(String id) {
        KJNcConfigManager.delContentCheck(id);
        return YXDJSONResult.ok();
    }
}

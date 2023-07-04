package com.taogger.gateway.service.business;

import cn.hutool.core.lang.Snowflake;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.ContentCheckEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //分页
        List<ContentCheckEntity> entities = KJNcConfigManager.getCheckEntities().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        PageImpl blackIpEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getCheckEntities().size());
        return ServerJSONResult.ok(blackIpEntities);
    }

    /**
     * 添加内容审核
     * @author taogger
     * @date 2022/8/16 14:07
     * @param contentCheckEntity
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult add(ContentCheckEntity contentCheckEntity) {
        Long id = new Snowflake().nextId();
        contentCheckEntity.setId(id.toString());
        KJNcConfigManager.saveContentCheck(contentCheckEntity);
        return ServerJSONResult.ok();
    }

    /**
     * 删除内容审核
     * @author taogger
     * @date 2022/8/16 14:09
     * @param id
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult del(String id) {
        KJNcConfigManager.delContentCheck(id);
        return ServerJSONResult.ok();
    }
}

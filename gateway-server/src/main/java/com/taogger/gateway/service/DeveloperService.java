package com.taogger.gateway.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.config.nacos.KJNcConfigManager;
import yxd.kj.app.server.gateway.mapper.ConfigInfoMapper;
import yxd.kj.app.server.gateway.model.ConfigInfo;
import yxd.kj.app.server.gateway.model.DeveloperEntity;
import yxd.kj.app.server.gateway.model.TenantInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 开发者管理api
 * @author taogger
 * @date 2022/8/11 15:28
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DeveloperService {

    private final TenantInfoService tenantInfoService;
    private final ConfigInfoMapper configInfoMapper;
    private final NacosConfigManager nacosConfigManager;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    /**
     * 新增开发者
     * @author taogger
     * @date 2022/8/11 15:42
     * @param developerEntity
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult add(DeveloperEntity developerEntity) {
        var now = LocalDateTime.now();
        String id = developerEntity.getNamespace() + RandomUtil.randomString(6);
        developerEntity.setId(id);
        developerEntity.setCreateTime(now);
        developerEntity.setUpdateTime(now);
        KJNcConfigManager.saveDevelopers(developerEntity);
        long count = tenantInfoService.count();
        var tenantInfo = new TenantInfo();
        tenantInfo.setTenantDesc(developerEntity.getName());
        tenantInfo.setTenantId(id);
        tenantInfo.setCreateSource("nacos");
        tenantInfo.setGmtCreate(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        tenantInfo.setGmtModified(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        tenantInfo.setId(count + 1);
        tenantInfo.setTenantName(developerEntity.getName());
        tenantInfo.setKp("1");
        tenantInfoService.save(tenantInfo);
        //新加配置信息
        var queryWrapper = new LambdaQueryWrapper<ConfigInfo>();
        queryWrapper.eq(ConfigInfo::getTenantId,namespace);
        //创建configService
        Properties properties = nacosConfigManager.getNacosConfigProperties().assembleConfigServiceProperties();
        properties.put(PropertyKeyConst.NAMESPACE, id);
        ConfigService configService = NacosFactory.createConfigService(properties);
        var configInfos = configInfoMapper.selectList(queryWrapper);
        if (!configInfos.isEmpty()) {
            for (ConfigInfo c : configInfos) {
                configService.publishConfig(c.getDataId(), c.getGroupId(), c.getContent());
            }
        }
        configService.shutDown();
        return YXDJSONResult.ok();
    }

    /**
     * 删除开发者
     * @author taogger
     * @date 2022/8/12 11:21
     * @param id
     * @return {@link YXDJSONResult}
    **/
    @SneakyThrows
    public YXDJSONResult del(String id) {
        KJNcConfigManager.delDeveloper(id);
        var queryWrapper = new LambdaQueryWrapper<TenantInfo>();
        queryWrapper.eq(TenantInfo::getTenantId,id);
        tenantInfoService.remove(queryWrapper);

        var configQueryWrapper = new LambdaQueryWrapper<ConfigInfo>();
        configQueryWrapper.eq(ConfigInfo::getTenantId,id);
        configInfoMapper.delete(configQueryWrapper);
        return YXDJSONResult.ok();
    }

    /**
     * 开发者列表接口
     * @author taogger
     * @date 2022/8/12 11:29
     * @param page
     * @param limit
     * @return {@link YXDJSONResult}
    **/
    public YXDJSONResult list(int page, int limit) {
        var pageable = PageRequest.of(page - 1, limit);
        //分页
        var entities = KJNcConfigManager.getDeveloperEntities().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        var developerEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getDeveloperEntities().size());
        return YXDJSONResult.ok(developerEntities);
    }
}

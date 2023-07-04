package com.taogger.gateway.service.business;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.mapper.ConfigInfoMapper;
import com.taogger.gateway.model.ConfigInfo;
import com.taogger.gateway.model.DeveloperEntity;
import com.taogger.gateway.model.TenantInfo;
import com.taogger.gateway.service.ITenantInfoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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

    private final ITenantInfoService iTenantInfoService;
    private final ConfigInfoMapper configInfoMapper;
    private final NacosConfigManager nacosConfigManager;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    /**
     * 新增开发者
     * @author taogger
     * @date 2022/8/11 15:42
     * @param developerEntity
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult add(DeveloperEntity developerEntity) {
        LocalDateTime now = LocalDateTime.now();
        String id = developerEntity.getNamespace() + RandomUtil.randomString(6);
        developerEntity.setId(id);
        developerEntity.setCreateTime(now);
        developerEntity.setUpdateTime(now);
        KJNcConfigManager.saveDevelopers(developerEntity);
        long count = iTenantInfoService.count();
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setTenantDesc(developerEntity.getName());
        tenantInfo.setTenantId(id);
        tenantInfo.setCreateSource("nacos");
        tenantInfo.setGmtCreate(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        tenantInfo.setGmtModified(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        tenantInfo.setId(count + 1);
        tenantInfo.setTenantName(developerEntity.getName());
        tenantInfo.setKp("1");
        iTenantInfoService.save(tenantInfo);
        //新加配置信息
        LambdaQueryWrapper<ConfigInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ConfigInfo::getTenantId,namespace);
        //创建configService
        Properties properties = nacosConfigManager.getNacosConfigProperties().assembleConfigServiceProperties();
        properties.put(PropertyKeyConst.NAMESPACE, id);
        ConfigService configService = NacosFactory.createConfigService(properties);
        List<ConfigInfo> configInfos = configInfoMapper.selectList(queryWrapper);
        if (!configInfos.isEmpty()) {
            for (ConfigInfo c : configInfos) {
                configService.publishConfig(c.getDataId(), c.getGroupId(), c.getContent());
            }
        }
        configService.shutDown();
        return ServerJSONResult.ok();
    }

    /**
     * 删除开发者
     * @author taogger
     * @date 2022/8/12 11:21
     * @param id
     * @return {@link ServerJSONResult}
    **/
    @SneakyThrows
    public ServerJSONResult del(String id) {
        KJNcConfigManager.delDeveloper(id);
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(TenantInfo::getTenantId,id);
        iTenantInfoService.remove(queryWrapper);

        LambdaQueryWrapper<ConfigInfo> configQueryWrapper = Wrappers.lambdaQuery();
        configQueryWrapper.eq(ConfigInfo::getTenantId,id);
        configInfoMapper.delete(configQueryWrapper);
        return ServerJSONResult.ok();
    }

    /**
     * 开发者列表接口
     * @author taogger
     * @date 2022/8/12 11:29
     * @param page
     * @param limit
     * @return {@link ServerJSONResult}
    **/
    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //分页
        List<DeveloperEntity> entities = KJNcConfigManager.getDeveloperEntities().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        PageImpl developerEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getDeveloperEntities().size());
        return ServerJSONResult.ok(developerEntities);
    }
}

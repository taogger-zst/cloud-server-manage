package com.taogger.gateway.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import yxd.kj.app.server.gateway.mapper.TenantInfoMapper;
import yxd.kj.app.server.gateway.model.TenantInfo;

/**
 * nacos 命名空间操作service
 * @author taogger
 * @date 2022/8/11 16:47
 */
@Service
public class TenantInfoService extends ServiceImpl<TenantInfoMapper,TenantInfo> {

}
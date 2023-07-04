package com.taogger.gateway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taogger.gateway.mapper.TenantInfoMapper;
import com.taogger.gateway.model.TenantInfo;
import com.taogger.gateway.service.ITenantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author taogger
 * @description nacos配置信息service
 * @date 2023-07-04 15:35
 **/
@Service
@RequiredArgsConstructor
public class TenantInfoServiceImpl extends ServiceImpl<TenantInfoMapper, TenantInfo> implements ITenantInfoService {
}

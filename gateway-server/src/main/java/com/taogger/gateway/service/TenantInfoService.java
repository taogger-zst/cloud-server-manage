package com.taogger.gateway.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taogger.gateway.mapper.TenantInfoMapper;
import com.taogger.gateway.model.TenantInfo;
import org.springframework.stereotype.Service;

/**
 * nacos 命名空间操作service
 * @author taogger
 * @date 2022/8/11 16:47
 */
@Service
public class TenantInfoService extends ServiceImpl<TenantInfoMapper, TenantInfo>{

}

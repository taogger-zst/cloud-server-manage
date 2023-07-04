package com.taogger.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import yxd.kj.app.api.constants.TokenConstant;
import yxd.kj.app.server.gateway.constant.FilterConstant;
import yxd.kj.app.server.gateway.service.DeviceService;
import yxd.kj.app.server.gateway.utils.Device;
import yxd.kj.app.server.gateway.utils.DevicePlatform;

/**
 * 设备过滤器,识别请求过滤的设备
 * @author taogger
 * @date 2022/8/25 11:12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceFilter implements GlobalFilter, Ordered {

    private final DeviceService deviceService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //识别设备
        Device device = deviceService.getDevice(exchange.getRequest());
        if (device.getDevicePlatform() == DevicePlatform.IOS) {
            exchange.getAttributes().put(TokenConstant.DEVICE_OS,"ios");
        } else if (device.getDevicePlatform() == DevicePlatform.ANDROID) {
            exchange.getAttributes().put(TokenConstant.DEVICE_OS,"android");
        } else if (device.getDevicePlatform() == DevicePlatform.UNKNOWN) {
            exchange.getAttributes().put(TokenConstant.DEVICE_OS,"other");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterConstant.DEVICE_ORDER;
    }
}

package com.taogger.gateway.filter;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.taogger.gateway.utils.IpUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * sentinel 限流过滤器增强
 * @author taogger
 * @date 2022/9/7 9:23
 */
public class KJSentinelGatewayFilter extends SentinelGatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取ip
        ServerHttpRequestDecorator mutatedRequest = new ServerHttpRequestDecorator(
                exchange.getRequest()) {
            @Override
            public InetSocketAddress getRemoteAddress() {
                //更改remoteAddress ip, 用于sentinel 限流获取真实的ip
                InetSocketAddress remoteAddress = super.getRemoteAddress();
                String ip = IpUtils.get(exchange.getRequest());
                InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,remoteAddress.getPort());
                return inetSocketAddress;
            }
        };
        return super.filter(exchange.mutate().request(mutatedRequest)
                .build(), chain);
    }
}

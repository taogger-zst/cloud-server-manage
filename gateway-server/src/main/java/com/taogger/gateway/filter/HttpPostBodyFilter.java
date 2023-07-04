package com.taogger.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yxd.kj.app.server.gateway.constant.FilterConstant;

import java.io.UnsupportedEncodingException;

/**
 * POST请求中Body的处理,用于处理请求传参
 * @author taogger
 * @date 2022/8/11 13:38
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HttpPostBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var method = request.getMethod();
        //var contentType = request.getHeaders().getFirst("Content-Type");
        //&& contentType.startsWith("multipart/form-data")
        if (method == HttpMethod.POST){
            return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    try {
                        var bodyString = new String(bytes, "utf-8");
                        exchange.getAttributes().put(FilterConstant.POST_BODY,bodyString);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    DataBufferUtils.release(dataBuffer);
                    var cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory()
                                .wrap(bytes);
                        return Mono.just(buffer);
                    });

                    var mutatedRequest = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    return chain.filter(exchange.mutate().request(mutatedRequest)
                            .build());
                });
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterConstant.HTTP_POST_FILTER_ORDER;
    }
}

package com.taogger.gateway.filter;

import cn.hutool.http.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.constant.ErrorEnum;
import yxd.kj.app.server.gateway.constant.FilterConstant;
import yxd.kj.app.server.gateway.model.BlackRouteEntity;
import yxd.kj.app.server.gateway.service.BlackIpService;
import yxd.kj.app.server.gateway.service.BlackRouteService;
import yxd.kj.app.server.gateway.utils.IpUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 黑名单过滤器
 * @author taogger
 * @date 2022/8/16 10:59
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BlackFilter implements GlobalFilter, Ordered {

    private final BlackRouteService blackRouteService;
    private final BlackIpService blackIpService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //查看当前ip是否是在黑名单中
        var ipNotExpire = blackIpService.getNotExpire();
        var ip = IpUtils.get(exchange.getRequest());
        long count = ipNotExpire.stream().filter(b -> b.getIp().equals(ip)).count();
        if (count > 0) {
            return resultHandler(exchange,chain, ErrorEnum.BLACK_UNABLE_ACCESS.getMsg());
        }
        //查看当前路由是否在黑名单中
        var routeNotExpire = blackRouteService.getNotExpire();
        var requestUrl = exchange.getRequest().getPath().value();
        var urls = routeNotExpire.stream().map(BlackRouteEntity::getRoute).collect(Collectors.toList());
        //匹配
        if (checkUrls(urls,requestUrl)) {
            return resultHandler(exchange,chain, ErrorEnum.BLACK_UNABLE_ACCESS.getMsg());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterConstant.BLACK_ORDER;
    }


    private Mono<Void> resultHandler(ServerWebExchange exchange,
                                     GatewayFilterChain chain,
                                     String msg) {
        try {
            //处理请求
            var result = YXDJSONResult.accessErrorMsg(msg);
            return Mono.defer(() -> {
                byte[] bytes = new byte[0];
                try {
                    bytes = new ObjectMapper().writeValueAsBytes(result);
                } catch (Exception e) {
                    log.error("【黑名单过滤器】,响应异常:{}", e);
                }
                var response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.getHeaders().add(Header.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON_VALUE);
                var buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Flux.just(buffer));
            });
        } catch (Exception e) {
            log.error("【黑名单过滤器】,处理formUrl参数异常:{}", e);
        }
        return chain.filter(exchange);
    }

    /**
     * 对url进行校验匹配
     * @author taogger
     * @date 2022/7/26 13:52
     * @param urls
     * @param path
     **/
    private boolean checkUrls(List<String> urls,String path){
        var pathMatcher = new AntPathMatcher();
        for (String url : urls) {
            if (pathMatcher.match(url,path))
                return true;
        }
        return false;
    }
}

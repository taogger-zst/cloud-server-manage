package com.taogger.gateway.exception;

import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.constant.ErrorEnum;

import java.nio.charset.Charset;

/**
 * @author taogger
 * @date 2022/7/20 9:20
 * 用于处理没有登录或token过期时的自定义返回结果
 */
@Component
public class RequestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        var body = JSONUtil.toJsonStr(YXDJSONResult.errorDisposeMsg(ErrorEnum.INVALID_TOKEN.getMsg()));
        var buffer =  response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
        return response.writeWith(Mono.just(buffer));
    }
}
package com.taogger.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.constant.ErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author taogger
 * @date 2022/7/20 9:16
 * 用于网关的全局异常处理
 * @Order(-1)： 优先级一定要比ResponseStatusExceptionHandler低
 */
@Order(-1)
@Component
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalErrorExceptionHandler.class);

    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("网关异常捕获,ex:{}",ex);
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        ServerJSONResult result = ServerJSONResult.errorMsg(ErrorEnum.UNAUTHORIZED.getMsg());
        // JOSN格式返回
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //找不到服务
        if (ex instanceof NotFoundException) {
            result = ServerJSONResult.errorDisposeMsg(ErrorEnum.NOTFOUND_SERVER.getMsg());
        }
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        //处理TOKEN失效的异常
        if (ex instanceof InvalidTokenException){
            result = ServerJSONResult.errorDisposeMsg(ErrorEnum.INVALID_TOKEN.getMsg());
        }
        ServerJSONResult finalResult = result;
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                //todo 返回响应结果，根据业务需求，自己定制
                return bufferFactory.wrap(new ObjectMapper().writeValueAsBytes(finalResult));
            }
            catch (Exception e) {
                log.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}

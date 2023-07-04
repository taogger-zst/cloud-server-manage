package com.taogger.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import yxd.kj.app.api.utils.YXDJSONResult;
import yxd.kj.app.server.gateway.constant.ErrorEnum;

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
        var response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        var result = YXDJSONResult.errorMsg(ErrorEnum.UNAUTHORIZED.getMsg());
        // JOSN格式返回
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //找不到服务
        if (ex instanceof NotFoundException) {
            result = YXDJSONResult.errorDisposeMsg(ErrorEnum.NOTFOUND_SERVER.getMsg());
        }
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        //处理TOKEN失效的异常
        if (ex instanceof InvalidTokenException){
            result = YXDJSONResult.errorDisposeMsg(ErrorEnum.INVALID_TOKEN.getMsg());
        }
        var finalResult = result;
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

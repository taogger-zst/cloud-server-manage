package com.taogger.gateway.exception;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.DefaultBlockRequestHandler;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.constant.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * sentinel 限流异常处理
 * @author taogger
 * @date 2022/8/17 11:31
 */
@Component
public class SentinelBlockRequestHandler extends DefaultBlockRequestHandler {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
        if (acceptsHtml(exchange)) {
            return htmlErrorResponse(ex);
        }
        // JSON result by default.
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(buildErrorResult(ex)));
    }

    private Mono<ServerResponse> htmlErrorResponse(Throwable ex) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.TEXT_PLAIN)
                .syncBody(ErrorEnum.SENTINEL_FLOW_BLOCK.getMsg());
    }

    private ServerJSONResult buildErrorResult(Throwable ex) {
        return ServerJSONResult.errorMsg(HttpStatus.TOO_MANY_REQUESTS.value(),
                ErrorEnum.SENTINEL_FLOW_BLOCK.getMsg());
    }

    /**
     * Reference from {@code DefaultErrorWebExceptionHandler} of Spring Boot.
     */
    private boolean acceptsHtml(ServerWebExchange exchange) {
        try {
            List<MediaType> acceptedMediaTypes = exchange.getRequest().getHeaders().getAccept();
            acceptedMediaTypes.remove(MediaType.ALL);
            MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            return acceptedMediaTypes.stream()
                    .anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
        } catch (InvalidMediaTypeException ex) {
            return false;
        }
    }
}

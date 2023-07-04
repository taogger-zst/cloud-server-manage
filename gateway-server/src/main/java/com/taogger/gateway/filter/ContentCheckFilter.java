package com.taogger.gateway.filter;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.constant.FilterConstant;
import com.taogger.gateway.model.ContentCheckEntity;
import com.taogger.gateway.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容审核过滤器
 * @author taogger
 * @date 2022/8/11 14:29
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ContentCheckFilter implements GlobalFilter, Ordered {

    @DubboReference
    private CheckRpcService checkRpcService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String path = uri.getPath();
        List<ContentCheckEntity> contentCheckEntities = KJNcConfigManager.getCheckEntities();
        contentCheckEntities = contentCheckEntities.stream().filter(c -> c.getUri().equals(path)).collect(Collectors.toList());
        for (ContentCheckEntity content : contentCheckEntities) {
            //判断方法类型是否一样
            if (content.getMethod().equals(request.getMethodValue())) {
                //获取参数
                //取出参数,如果没有对应的参数,则直接跳过
                if (content.getContentType().equals(ContentType.FORM_URLENCODED.getValue())) {
                    return formUrlEncodedHandler(content,exchange,chain);
                }
                Map<String,String> paramsValue = FilterUtils.getContentCheckParamsValue(request, exchange,content);
                if (paramsValue != null && !paramsValue.isEmpty()) {
                    return resultHandler(exchange, chain, paramsValue);
                }
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 处理urlEncoded传参方式
     * @author taogger
     * @date 2022/8/11 10:57
     * @param content 内容审核实体
     * @param exchange 交换器
     * @param chain 调用链
     * @return {@link Mono<Void>}
     **/
    public Mono<Void> formUrlEncodedHandler(ContentCheckEntity content, ServerWebExchange exchange,
                                            GatewayFilterChain chain) {
        return exchange.getFormData().flatMap(formData -> {
            String name = content.getParams();
            String type = content.getType();
            String[] params = name.split(",");
            String[] paramsType = type.split(",");
            List<String> text = new ArrayList<>();
            List<String> image = new ArrayList<>();
            for (int i = 0; i < params.length; i++) {
                if (formData.containsKey(params[i])) {
                    String param = formData.getFirst(params[i]);
                    if (StringUtils.isNotBlank(param)) {
                        if (paramsType[i].equals("text")) {
                            text.add(param);
                        } else if (paramsType[i].equals("image")) {
                            image.add(param);
                        }
                    } else {
                        return chain.filter(exchange);
                    }
                } else {
                    return chain.filter(exchange);
                }
            }
            Map<String,String> checkParams = new HashMap<>();
            if (!text.isEmpty()) {
                checkParams.put("text",text.stream().collect(Collectors.joining(",")));
            }
            if (!image.isEmpty()) {
                checkParams.put("image",image.stream().collect(Collectors.joining(",")));
            }
            if (checkParams.isEmpty()) {
                return chain.filter(exchange);
            } else {
                return resultHandler(exchange,chain,checkParams);
            }
        });
    }

    private Mono<Void> resultHandler(ServerWebExchange exchange,
                                     GatewayFilterChain chain,
                                     Map<String,String> params) {
        try {
            //取出参数
            String msg = null;
            String text = params.get("text");
            String image = params.get("image");
            if (StringUtils.isNotBlank(text)) {
                String[] textArray = text.split(",");
                for (String str : textArray) {
                    CheckRpcResponse checkRpcResponse = checkRpcService.textCheck(str);
                    if (!checkRpcResponse.getPass()) {
                        msg = checkRpcResponse.getAbnormal();
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(image)) {
                String[] imageArray = image.split(",");
                for (String str : imageArray) {
                    CheckRpcResponse checkRpcResponse = checkRpcService.imageCheck(str);
                    if (!checkRpcResponse.getPass()) {
                        msg = checkRpcResponse.getAbnormal();
                        break;
                    }
                }
            }
            if (msg != null) {
                ServerJSONResult result = ServerJSONResult.errorDisposeMsg(msg);
                return msgHandler(exchange, result);
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("【内容审核过滤器】,处理formUrl参数异常:{}", e);
            ServerJSONResult result = ServerJSONResult.errorDisposeMsg("外部服务异常,暂时无法操作");
            return msgHandler(exchange,result);
        }
    }


    public Mono<Void> msgHandler(ServerWebExchange exchange,ServerJSONResult result) {
        return Mono.defer(() -> {
            byte[] bytes = new byte[0];
            try {
                bytes = new ObjectMapper().writeValueAsBytes(result);
            } catch (Exception e) {
                log.error("【内容审核过滤器】,响应异常:{}", e);
            }
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add(Header.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON_VALUE);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(buffer));
        });
    }

    @Override
    public int getOrder() {
        return FilterConstant.CONTENT_CHECK_ORDER;
    }
}

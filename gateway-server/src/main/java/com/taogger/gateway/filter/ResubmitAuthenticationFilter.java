package com.taogger.gateway.filter;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taogger.common.constants.TokenConstant;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.constant.FilterConstant;
import com.taogger.gateway.model.ResubmitEntity;
import com.taogger.gateway.service.lock.DistributedLocker;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 重复提交过滤器 , 在token过滤器之后
 * @author taogger
 * @date 2022/7/29 15:40
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResubmitAuthenticationFilter implements GlobalFilter, Ordered {

    /**
     * 重复提交锁前缀
     */
    private static final String NO_REPEAT_LOCK_PREFIX = "kj:re_submit_lock:";

    /**
     * 锁对象
     */
    private final DistributedLocker distributedLocker;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String path = uri.getPath();
        //根据地址查询是否有重复提交记录
        List<ResubmitEntity> resubmitEntities = KJNcConfigManager.getResubmitEntities();
        resubmitEntities = resubmitEntities.stream().filter(r->r.getUrl().equals(path)).collect(Collectors.toList());
        if (!resubmitEntities.isEmpty()) {
            for (ResubmitEntity r : resubmitEntities) {
                //判断是否是一样的方法类型
                if (request.getMethodValue().equals(r.getMethod())) {
                    //取出参数,如果没有对应的参数,则直接跳过
                    if (r.getContentType().equals(ContentType.FORM_URLENCODED.getValue())) {
                        return formUrlEncodedHandler(r, exchange, chain);
                    }
                    String paramsValue = FilterUtils.getResubmitParamsValue(request, exchange,r);
                    if (StringUtils.isNotBlank(paramsValue)) {
                        return resultHandler(r, exchange, chain, paramsValue);
                    }
                }
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 处理urlEncoded传参方式
     * @author taogger
     * @date 2022/8/11 10:57
     * @param resubmit 重复提交实体
     * @param exchange 交换器
     * @param chain 调用链
     * @return {@link Mono<Void>}
    **/
    public Mono<Void> formUrlEncodedHandler(ResubmitEntity resubmit,ServerWebExchange exchange,
                                            GatewayFilterChain chain) {
        return exchange.getFormData().flatMap(formData -> {
            LinkedHashMap<String,Object> redisParams = new LinkedHashMap<>();
            String[] split = resubmit.getParams().split(",");
            if (split[0].equals(TokenConstant.USER_ID)) {
                //如果有userId,但是没有token解析中的userId数据，则重复提交判断失效
                String userId = FilterUtils.getUserId(exchange);
                if (StringUtils.isBlank(userId)) {
                    return chain.filter(exchange);
                } else {
                    redisParams.put(split[0],userId);
                }
            }
            for (int i = 0; i < split.length; i ++) {
                if (split[i].equals(TokenConstant.USER_ID)) {
                    continue;
                }
                //判断是否有包含该参数，如果有参数，并且有值才会进行重复提交判断
                if (formData.containsKey(split[i])) {
                    String paramsValue = formData.getFirst(split[i]);
                    if (StringUtils.isNotBlank(paramsValue)) {
                        redisParams.put(split[i],paramsValue);
                    } else {
                        return chain.filter(exchange);
                    }
                } else {
                    return chain.filter(exchange);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String param : redisParams.keySet()) {
                stringBuilder.append(redisParams.get(param));
            }
            return resultHandler(resubmit, exchange, chain, stringBuilder.toString());
        });
    }

    private Mono<Void> resultHandler(ResubmitEntity resubmit,ServerWebExchange exchange,
                                     GatewayFilterChain chain,
                                     String value) {
        try {
            //因为是重复提交,因此不需要释放锁,让其过期自动释放
            //因为释放锁后就没有锁这条记录的时间,导致这次请求完成后下次请求因为锁不存在,可以轻松获取到锁
            //所有我们的expireTime设置相当于没用了
            Boolean lock = distributedLocker.tryFairLock(NO_REPEAT_LOCK_PREFIX + resubmit.getId() + value,
                    TimeUnit.SECONDS, 0, resubmit.getExpireTime());
            if (!lock) {
                //处理请求
                ServerJSONResult result = ServerJSONResult.errorDisposeMsg("操作频繁,请稍后再试");
                return Mono.defer(() -> {
                    byte[] bytes = new byte[0];
                    try {
                        bytes = new ObjectMapper().writeValueAsBytes(result);
                    } catch (Exception e) {
                        log.error("【重复提交过滤器】,响应异常:{}", e);
                    }
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add(Header.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON_VALUE);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return response.writeWith(Flux.just(buffer));
                });
            }
        } catch (Exception e) {
            log.error("【重复提交过滤器】,处理formUrl参数异常:{}", e);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterConstant.RESUBMIT_ORDER;
    }
}

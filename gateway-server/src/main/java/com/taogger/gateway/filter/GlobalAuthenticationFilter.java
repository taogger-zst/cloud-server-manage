package com.taogger.gateway.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.taogger.common.constants.TokenConstant;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.constant.ErrorEnum;
import com.taogger.gateway.constant.FilterConstant;
import com.taogger.gateway.constant.SysConstant;
import com.taogger.gateway.model.FilterRouteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author taogger
 * @date 2022/7/20 9:22
 * 分为如下几步：
 * 1、白名单直接放行
 * 2、校验token
 * 3、读取token中存放的用户信息
 * 4、重新封装用户信息，加密成功json数据放入请求头中传递给下游微服务
 */
@Component
@RequiredArgsConstructor
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {
    /**
     * JWT令牌的服务
     */
    private final TokenStore tokenStore;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 全局过滤器、校验token，传递参数
     * @author taogger
     * @date 2022/7/26 14:22
    **/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().value();
        List<FilterRouteEntity> all = KJNcConfigManager.getFilterRoutes();
        List<String> filterUri = all.stream().map(FilterRouteEntity::getUri).collect(Collectors.toList());
        // 检查token是否存在 与 是否是白名单
        String token = getToken(exchange);
        if (StringUtils.isBlank(token)) {
            // 过滤路由放行，比如授权服务、静态资源.....
            if (checkUrls(filterUri,requestUrl)){
                return chain.filter(exchange);
            }
            return invalidTokenMono(exchange);
        }
        //3 判断是否是有效的token
        OAuth2AccessToken oAuth2AccessToken;
        try {
            //解析token，使用tokenStore
            oAuth2AccessToken = tokenStore.readAccessToken(token);
            Map<String, Object> additionalInformation = oAuth2AccessToken.getAdditionalInformation();
            //令牌的唯一ID
            String jti = additionalInformation.get(TokenConstant.JTI).toString();
            /**查看黑名单中是否存在这个jti，如果存在则这个令牌不能用****/
            Boolean hasKey = stringRedisTemplate.hasKey(SysConstant.JTI_KEY_PREFIX + jti);
            if (hasKey) {
                return invalidTokenMono(exchange);
            }
            /**校验该用户是否有权限访问该服务**/
            //取出用户身份信息
            String user_name = additionalInformation.get("user_name").toString();
            //获取用户权限
            List<String> authorities = (List<String>) additionalInformation.get("authorities");
            //从additionalInformation取出userId
            String userId = additionalInformation.get(TokenConstant.USER_ID).toString();
            String clientId = additionalInformation.get(TokenConstant.CLIENT_ID).toString();
            //组装参数
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TokenConstant.PRINCIPAL_NAME, user_name);
            jsonObject.put(TokenConstant.AUTHORITIES_NAME,authorities);
            jsonObject.put(TokenConstant.USER_ID,userId);
            //过期时间，单位秒
            jsonObject.put(TokenConstant.EXPR,oAuth2AccessToken.getExpiresIn());
            jsonObject.put(TokenConstant.JTI,jti);
            jsonObject.put(TokenConstant.CLIENT_ID, clientId);
            //将解析后的token加密放入请求头中，方便下游微服务解析获取用户信息
            String base64 = Base64.encode(jsonObject.toJSONString());
            //放入请求头中
            ServerHttpRequest tokenRequest = exchange.getRequest().mutate().header(TokenConstant.TOKEN_NAME, base64).build();
            ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
            return chain.filter(build);
        } catch (InvalidTokenException e) {
            //解析token异常，直接返回token无效
            return invalidTokenMono(exchange);
        }


    }

    @Override
    public int getOrder() {
        return FilterConstant.GLOBAL_AUTH_ORDER;
    }

    /**
     * 对url进行校验匹配
     * @author taogger
     * @date 2022/7/26 13:52
     * @param urls
     * @param path
    **/
    private boolean checkUrls(List<String> urls,String path){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String url : urls) {
            if (pathMatcher.match(url,path))
                return true;
        }
        return false;
    }


    /**
     * 从请求头中获取Token
     * @author taogger
     * @date 2022/7/26 13:52
     * @param exchange
    **/
    private String getToken(ServerWebExchange exchange) {
        String tokenStr = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(tokenStr)) {
            return null;
        }
        String token = tokenStr.split(" ")[1];
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }

    /**
     * 无效的token
     * @author taogger
     * @date 2022/7/26 13:52
     * @param exchange
    **/
    private Mono<Void> invalidTokenMono(ServerWebExchange exchange) {
        return buildReturnMono(ServerJSONResult.errorDisposeMsg(ErrorEnum.INVALID_TOKEN.getMsg()), exchange);
    }


    private Mono<Void> buildReturnMono(ServerJSONResult result, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset:utf-8");
        return response.writeWith(Mono.just(buffer));
    }
}

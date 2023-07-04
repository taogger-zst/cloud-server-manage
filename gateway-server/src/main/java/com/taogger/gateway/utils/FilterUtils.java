package com.taogger.gateway.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import yxd.kj.app.api.constants.TokenConstant;
import yxd.kj.app.server.gateway.constant.FilterConstant;
import yxd.kj.app.server.gateway.model.ContentCheckEntity;
import yxd.kj.app.server.gateway.model.ResubmitEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 过滤器工具类
 * @author taogger
 * @date 2022/8/11 15:05
 */
public class FilterUtils {

    /**
     * 取出重复提交中标识的参数--重复提交,如果没有对应的参数，直接返回null
     * @author taogger
     * @date 2022/8/11 11:35
     * @param request
     * @param resubmit
     * @return {@link String}
     **/
    public static String getResubmitParamsValue(ServerHttpRequest request, ServerWebExchange exchange
            , ResubmitEntity resubmit) {
        var redisParams = new LinkedHashMap<String,Object>();
        var name = resubmit.getParams();
        String[] split = name.split(",");
        if (split[0].equals(TokenConstant.USER_ID)) {
            //如果有userId,但是没有token解析中的userId数据，则重复提交判断失效
            String userId = getUserId(exchange);
            if (userId == null || userId.isBlank()) {
                return null;
            } else {
                redisParams.put(split[0],userId);
            }
        }
        for (var i = 0; i < split.length; i++) {
            if (split[i].equals(TokenConstant.USER_ID)) {
                continue;
            }
            if (request.getMethod() == HttpMethod.GET
                    || request.getMethod() == HttpMethod.DELETE) {
                var param = request.getQueryParams().getFirst(split[i]);
                if (param == null || param.isBlank()) {
                    return null;
                }
                redisParams.put(split[i], param);
            } else if (request.getMethod() == HttpMethod.POST
                    || request.getMethod() == HttpMethod.PUT) {
                var param = getBody(exchange, name);
                if (param == null || param.isBlank()) {
                    return null;
                }
                redisParams.put(split[i], param);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : redisParams.keySet()) {
            stringBuilder.append(redisParams.get(param));
        }
        return stringBuilder.toString();
    }

    /**
     * 取出重复提交中标识的参数--内容审核
     * map.get("text"),map.get("image")
     * @author taogger
     * @date 2022/8/11 15:09
     * @param request
     * @param exchange
     * @param content
     * @return {@link String}
    **/
    public static Map<String,String> getContentCheckParamsValue(ServerHttpRequest request, ServerWebExchange exchange
            , ContentCheckEntity content) {
        var name = content.getParams();
        var type = content.getType();
        String[] params = name.split(",");
        String[] paramsType = type.split(",");
        var text = new ArrayList<String>();
        var image = new ArrayList<String>();
        for (var i = 0; i < params.length; i++) {
            String value = null;
            if (request.getMethod() == HttpMethod.GET
                    || request.getMethod() == HttpMethod.DELETE) {
                value = request.getQueryParams().getFirst(params[i]);
            } else if (request.getMethod() == HttpMethod.POST
                    || request.getMethod() == HttpMethod.PUT) {
                value = getBody(exchange, params[i]);
            }
            if (value == null) {
                return null;
            } else {
                if (paramsType[i].equals("text")) {
                    text.add(value);
                } else if (paramsType[i].equals("image")) {
                    image.add(value);
                }
            }
        }
        var checkParams = new HashMap<String,String>();
        if (!text.isEmpty()) {
            checkParams.put("text",text.stream().collect(Collectors.joining(",")));
        }
        if (!image.isEmpty()) {
            checkParams.put("image",image.stream().collect(Collectors.joining(",")));
        }
        return checkParams;
    }


    public static String getBody(ServerWebExchange exchange,String name) {
        var putBody = getBody(exchange);
        if (putBody != null && !putBody.isBlank()) {
            return JSONObject.parseObject(putBody).getString(name);
        }
        return null;
    }

    public static String getBody(ServerWebExchange exchange) {
        return exchange.getAttribute(FilterConstant.POST_BODY);
    }

    /**
     * 获取用户id
     * @author taogger
     * @date 2022/8/16 15:27
     * @param exchange
     * @return {@link String}
     **/
    public static String getUserId(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(TokenConstant.TOKEN_NAME);
        if (token != null) {
            String json = Base64.decodeStr(token);
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(json);
            String userId=jsonObject.getStr(TokenConstant.USER_ID);
            return userId;
        }
        return null;
    }
}

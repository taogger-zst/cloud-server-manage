package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重复提交实体
 * @author taogger
 * @date 2022/8/11 9:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResubmitEntity {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 需要设置重复提交的地址
     */
    private String url;

    /**
     * 作为唯一标识的参数,如userId
     */
    private String params;

    /**
     * 请求方法
     * org.springframework.http.HttpMethod
     */
    private String method;

    /**
     * 参数类型
     * cn.hutool.http.ContentType
     */
    private String contentType;

    /**
     * 过期时间,时间限制,间隔几秒不能再次提交
     */
    private Integer expireTime;
}

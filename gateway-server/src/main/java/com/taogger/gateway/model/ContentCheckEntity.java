package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内容审核实体
 * @author taogger
 * @date 2022/8/11 14:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentCheckEntity {
    /**
     * 唯一标识
     */
    private String id;

    /**
     * 需要设置内容审核的地址
     */
    private String uri;

    /**
     * 需要审核的参数对应的值,多个参数用","号隔开
     */
    private String params;

    /**
     * 参数类型,与params参数对应,params参数有几个这个就有几个，按","号隔开
     */
    private String type;

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
}

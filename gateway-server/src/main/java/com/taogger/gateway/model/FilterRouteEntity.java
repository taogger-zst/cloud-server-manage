package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 过滤路由/不需要走token校验的路由
 * @author taogger
 * @date 2022/8/15 13:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRouteEntity {

    /**
     * 路由id,雪花算法生成
     */
    private String id;

    /**
     * 过滤路由名称
     */
    private String name;

    /**
     * 过滤的接口地址
     */
    private String uri;

}

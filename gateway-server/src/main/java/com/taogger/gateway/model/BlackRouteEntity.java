package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 黑名单-路由实体
 * @author taogger
 * @date 2022/8/16 10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackRouteEntity {

    private String id;

    /**
     * 路由地址
     */
    private String route;

    /**
     * 过期时间, -1为永久加入黑名单
     */
    private Long expireTime;
}

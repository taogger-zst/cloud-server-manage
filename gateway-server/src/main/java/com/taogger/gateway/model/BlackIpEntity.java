package com.taogger.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ip黑名单实体
 * @author taogger
 * @date 2022/8/15 17:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackIpEntity {

    private String id;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 过期时间, -1为永久加入黑名单
     */
    private Long expireTime;

}

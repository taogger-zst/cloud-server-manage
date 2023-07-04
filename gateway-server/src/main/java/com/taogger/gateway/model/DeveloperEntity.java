package com.taogger.gateway.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yxd.kj.app.utils.serialize.DateYMDHMSSerialize;

import java.time.LocalDateTime;

/**
 * 开发者实体
 * @author taogger
 * @date 2022/8/11 15:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperEntity {
    /**
     * 唯一标识
     */
    private String id;

    /**
     * 开发者名称
     */
    private String name;

    /**
     * 命名空间名称
     */
    private String namespace;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateYMDHMSSerialize.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = DateYMDHMSSerialize.class)
    private LocalDateTime updateTime;
}

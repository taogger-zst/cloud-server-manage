package com.taogger.gateway.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * nacos命名空间实体--这个只在开发环境中使用
 * @author taogger
 * @date 2022/8/11 16:33
 */
@TableName(value = "tenant_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String kp;

    /**
     * 命名空间表
     */
    private String tenantId;

    /**
     * 命名空间名称
     */
    private String tenantName;

    /**
     * 命名空间描述
     */
    private String tenantDesc;

    /**
     * 创建源...
     */
    private String createSource = "nacos";

    /**
     * 创建时间时间戳
     */
    private Long gmtCreate;

    /**
     * 更新时间时间戳
     */
    private Long gmtModified;

}

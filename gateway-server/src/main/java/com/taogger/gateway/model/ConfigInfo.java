package com.taogger.gateway.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置信息
 * @author taogger
 * @date 2022/8/12 11:32
 */
@TableName(value = "config_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String dataId;
    private String groupId;
    private String content;
    private String md5;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String srcUser;
    private String srcIp;
    private String appName;
    private String tenantId;
    private String cDesc;
    private String cUse;
    private String effect;
    private String type;
    private String cSchema;
    private String encryptedDataKey;
}

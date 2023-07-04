package com.taogger.common.dubbo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author taogger
 * @description 内容审查结果
 * @date 2023-07-04 15:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckRpcResponse implements Serializable {
    /**
     * 原因
     */
    private String abnormal;

    /**
     * 是否通过审查
     */
    private Boolean pass;
}

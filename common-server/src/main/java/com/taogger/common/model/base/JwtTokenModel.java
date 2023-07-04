package com.taogger.common.model.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author taogger
 * @description jwt-token
 * @date 2023-05-24 20:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenModel {

    /**
     * token真实值
     */
    private String value;
    /**
     * 角色数组id、按逗号分开
     */
    private List<Integer> roles;
}

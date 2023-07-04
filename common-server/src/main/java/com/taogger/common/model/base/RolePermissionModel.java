package com.taogger.common.model.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author taogger
 * @description 角色权限model
 * @date 2023-05-25 11:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionModel {

    private String uri;
    private String method;

}

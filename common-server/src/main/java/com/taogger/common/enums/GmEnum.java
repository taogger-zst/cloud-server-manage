package com.taogger.common.enums;

/**
 * @author taogger
 * @description 通用枚举
 * @date 2023-07-04 11:24
 **/
public enum GmEnum {
    YES(1),
    NO(0);

    GmEnum(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}

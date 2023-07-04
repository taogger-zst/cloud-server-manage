package com.taogger.gateway.constant;

/**
 * 异常枚举
 * @author taogger
 * @date 2022/7/29 11:24
 */
public enum ErrorEnum {

    BLACK_UNABLE_ACCESS("暂时无法访问"),
    NO_PERMISSION("您无权限访问"),
    SENTINEL_FLOW_BLOCK("访问频繁,请稍后重试"),
    UNAUTHORIZED("系统异常,请稍后重试/(ㄒoㄒ)/~~"),
    NOTFOUND_SERVER("服务失联了,请稍后重试(lll￢ω￢)"),
    INVALID_TOKEN("无效的授权令牌");

    private final String msg;

    public String getMsg() {
        return msg;
    }

    ErrorEnum(String msg) {
        this.msg = msg;
    }
}

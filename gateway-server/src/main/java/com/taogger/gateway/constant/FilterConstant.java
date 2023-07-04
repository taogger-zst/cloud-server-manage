package com.taogger.gateway.constant;

/**
 * 过滤器常量类
 * @author taogger
 * @date 2022/8/11 13:40
 */
public class FilterConstant {

    /**
     * 黑名单过滤器顺序
     */
    public static final Integer BLACK_ORDER = -7;

    /**
     * 设备识别过滤器顺序
     */
    public static final Integer DEVICE_ORDER = -6;

    /**
     * post过滤器顺序
     */
    public static final Integer HTTP_POST_FILTER_ORDER = -5;

    /**
     * auth过滤器顺序
     */
    public static final Integer GLOBAL_AUTH_ORDER = -4;

    /**
     * 重复请求过滤器顺序
     */
    public static final Integer RESUBMIT_ORDER = -3;

    /**
     * 内容审核过滤器顺序
     */
    public static final Integer CONTENT_CHECK_ORDER = -2;

    /**
     * body传参头
     */
    public static final String POST_BODY = "POST_BODY";
}

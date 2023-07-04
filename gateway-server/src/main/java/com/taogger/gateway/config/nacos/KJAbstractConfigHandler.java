package com.taogger.gateway.config.nacos;

/**
 * 配置处理
 * @author taogger
 * @date 2022/11/28 16:24
 */
public abstract class KJAbstractConfigHandler {

    /**
     * 处理配置
     * @author taogger 
     * @date 2022/11/28 16:27
     * @param configInfo
    **/
    public abstract void handler(String configInfo);

    /** 
     * 是否匹配
     * @author taogger 
     * @date 2022/11/28 16:26
     * @param dataId
     * @return {@link Boolean}
    **/
    public abstract Boolean isMatch(String dataId);
    
    /** 
     * 初始化配置加载
     * @author taogger 
     * @date 2022/11/29 11:29
     * @param configInfo
    **/
    public abstract void init(String configInfo);
}

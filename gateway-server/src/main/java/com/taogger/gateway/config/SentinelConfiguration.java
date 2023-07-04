package com.taogger.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import yxd.kj.app.api.model.HostInfo;
import yxd.kj.app.server.gateway.filter.KJSentinelGatewayFilter;

import java.util.Collections;
import java.util.List;

/**
 * sentinel Configuration
 * @author taogger
 * @date 2022/8/17 10:08
 */
@Configuration
public class SentinelConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public SentinelGatewayFilter sentinelGatewayFilter() {
        return new KJSentinelGatewayFilter();
    }

    /**
     * 获取当前服务信息
     * @author taogger
     * @date 2022/8/17 16:19
     * @param environment
     * @return {@link HostInfo}
    **/
    @Bean
    public HostInfo hostInfo(ConfigurableEnvironment environment) {
        var target = new InetUtilsProperties();
        ConfigurationPropertySources.attach(environment);
        Binder.get(environment).bind(InetUtilsProperties.PREFIX, Bindable.ofInstance(target));
        try (InetUtils utils = new InetUtils(target)) {
            InetUtils.HostInfo firstNonLoopbackHostInfo = utils.findFirstNonLoopbackHostInfo();
            HostInfo hostInfo = new HostInfo();
            hostInfo.setHostname(firstNonLoopbackHostInfo.getHostname());
            hostInfo.setIpAddress(firstNonLoopbackHostInfo.getIpAddress());
            return hostInfo;
        }
    }
}

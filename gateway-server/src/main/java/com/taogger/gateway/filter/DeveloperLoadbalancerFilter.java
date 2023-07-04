package com.taogger.gateway.filter;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.taogger.gateway.loadbalancer.DeveloperLoadbalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * 开发者环境切换负载均衡器
 * @author taogger
 * @date 2022/8/10 11:53
 */
@Component
@Slf4j
public class DeveloperLoadbalancerFilter extends ReactiveLoadBalancerClientFilter {

    private final LoadBalancerClientFactory clientFactory;

    private final GatewayLoadBalancerProperties properties;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;


    public DeveloperLoadbalancerFilter(LoadBalancerClientFactory clientFactory, GatewayLoadBalancerProperties properties,
                                       NacosDiscoveryProperties nacosDiscoveryProperties) {
        super(clientFactory, properties);
        this.clientFactory = clientFactory;
        this.properties = properties;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        // preserve the original url
        addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
        }

        URI requestUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String serviceId = requestUri.getHost();
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(clientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RequestDataContext.class, ResponseData.class, ServiceInstance.class);
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(
                new RequestDataContext(new RequestData(exchange.getRequest()), getHint(serviceId)));
        return choose(lbRequest, serviceId, supportedLifecycleProcessors).doOnNext(response -> {

            if (!response.hasServer()) {
                supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<>(CompletionContext.Status.DISCARD, lbRequest, response)));
                throw NotFoundException.create(properties.isUse404(), "Unable to find instance for " + url.getHost());
            }

            ServiceInstance retrievedInstance = response.getServer();

            URI uri = exchange.getRequest().getURI();

            // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
            // if the loadbalancer doesn't provide one.
            String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }

            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(retrievedInstance,
                    overrideScheme);

            URI requestUrl = reconstructURI(serviceInstance, uri);

            if (log.isTraceEnabled()) {
                log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
            }
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
            exchange.getAttributes().put(GATEWAY_LOADBALANCER_RESPONSE_ATTR, response);
            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, response));
        }).then(chain.filter(exchange))
                .doOnError(throwable -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
                                CompletionContext.Status.FAILED, throwable, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR)))))
                .doOnSuccess(aVoid -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
                                CompletionContext.Status.SUCCESS, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR),
                                new ResponseData(exchange.getResponse(), new RequestData(exchange.getRequest()))))));
    }

    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    private Mono<Response<ServiceInstance>> choose(Request<RequestDataContext> lbRequest, String serviceId,
                                                   Set<LoadBalancerLifecycle> supportedLifecycleProcessors) {
        String env = nacosDiscoveryProperties.getMetadata().get("env");
        String namespace = lbRequest.getContext().getClientRequest().getHeaders().getFirst("NAMESPACE");
        if (env.equals("dev") && StringUtils.isNotBlank(namespace)) {
            DeveloperLoadbalancer developerLoadbalancer = new DeveloperLoadbalancer(serviceId,
                    namespace,nacosDiscoveryProperties);
            return developerLoadbalancer.choose(lbRequest);
        }
        return result(lbRequest,serviceId,supportedLifecycleProcessors);
    }

    private Mono<Response<ServiceInstance>> result(Request<RequestDataContext> lbRequest,String serviceId,
                                                   Set<LoadBalancerLifecycle> supportedLifecycleProcessors) {
        ReactorServiceInstanceLoadBalancer loadBalancer = this.clientFactory.getInstance(serviceId,
                ReactorServiceInstanceLoadBalancer.class);
        if (loadBalancer == null) {
            throw new NotFoundException("No loadbalancer available for " + serviceId);
        }
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        return loadBalancer.choose(lbRequest);
    }

    private String getHint(String serviceId) {
        LoadBalancerProperties loadBalancerProperties = clientFactory.getProperties(serviceId);
        Map<String, String> hints = loadBalancerProperties.getHint();
        String defaultHint = hints.getOrDefault("default", "default");
        String hintPropertyValue = hints.get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }

}

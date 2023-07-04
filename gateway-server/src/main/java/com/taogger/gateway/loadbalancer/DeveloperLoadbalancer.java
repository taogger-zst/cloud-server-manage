package com.taogger.gateway.loadbalancer;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开发者Loadbalancer--只用于开发环境
 * 性能占用大,速度满,用于开发环境节省开发者资源
 * @author taogger
 * @date 2022/8/10 10:11
 */
@Slf4j
public class DeveloperLoadbalancer implements ReactorServiceInstanceLoadBalancer {

    private final AtomicInteger position = new AtomicInteger(new Random().nextInt(1000));

    private final String serviceId;

    private final String namespace;

    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private NacosNamingService namingService = null;


    /**
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     */
    public DeveloperLoadbalancer(String serviceId,
                                 String namespace,
                                 NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.namespace = namespace;
        this.serviceId = serviceId;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        RequestDataContext requestDataContext = (RequestDataContext) request.getContext();
        Properties properties = nacosDiscoveryProperties.getNacosProperties();
        Properties copyProperties = new Properties();
        copyProperties.putAll(properties);
        copyProperties.setProperty("namespace",namespace);
        try {
            namingService = new NacosNamingService(copyProperties);
            List<Instance> allInstances = namingService.getAllInstances(serviceId);
            List<ServiceInstance> builderServiceInstances = new ArrayList<ServiceInstance>();
            allInstances.forEach(i -> {
                DefaultServiceInstance serviceInstance = new DefaultServiceInstance(i.getInstanceId(), i.getServiceName(),
                        i.getIp(), i.getPort(), Boolean.FALSE);
                builderServiceInstances.add(serviceInstance);
            });
            return Mono.just(getInstanceResponse(builderServiceInstances));
        } catch (NacosException e) {
            log.error("路由转换失败,请求数据为:{},异常信息为:{}", JSONUtil.toJsonStr(requestDataContext),e);
            return Mono.just(new EmptyResponse());
        } finally {
            executorService.execute(() -> {
                try {
                    if (namingService != null) {
                        namingService.shutDown();
                        namingService = null;
                    }
                } catch (NacosException e) {
                    log.error("namingService.shutDown(),异常信息为:{}", e);
                }
            });
        }
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        // TODO: enforce order?
        int pos = Math.abs(this.position.incrementAndGet());

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}

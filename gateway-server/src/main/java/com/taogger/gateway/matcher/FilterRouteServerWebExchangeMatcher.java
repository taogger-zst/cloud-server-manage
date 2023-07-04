package com.taogger.gateway.matcher;

import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.FilterRouteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 过滤路由匹配
 * @author taogger
 * @date 2022/8/15 15:35
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FilterRouteServerWebExchangeMatcher implements ServerWebExchangeMatcher {

    /**
     * 路由匹配
     * @author taogger
     * @date 2022/8/15 15:57
     * @param exchange
     * @return {@link Mono< MatchResult>}
    **/
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        List<FilterRouteEntity> all = KJNcConfigManager.getFilterRoutes();
        List<String> uris = all.stream().map(FilterRouteEntity::getUri).collect(Collectors.toList());
        List<ServerWebExchangeMatcher> serverWebExchangeMatchers = getServerWebExchangeMatchers(uris);
        return new OrServerWebExchangeMatcher(serverWebExchangeMatchers).matches(exchange);
    }

    private List<ServerWebExchangeMatcher> getServerWebExchangeMatchers(List<String> uris) {
        List<ServerWebExchangeMatcher> matchers = new ArrayList<>(uris.size());
        for (String uri : uris) {
            matchers.add(new PathPatternParserServerWebExchangeMatcher(uri, null));
        }
        return matchers;
    }
}

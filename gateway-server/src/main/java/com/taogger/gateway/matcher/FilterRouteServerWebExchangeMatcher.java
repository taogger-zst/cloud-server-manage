package com.taogger.gateway.matcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import yxd.kj.app.server.gateway.config.nacos.KJNcConfigManager;
import yxd.kj.app.server.gateway.model.FilterRouteEntity;

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
        var all = KJNcConfigManager.getFilterRoutes();
        var uris = all.stream().map(FilterRouteEntity::getUri).collect(Collectors.toList());
        var serverWebExchangeMatchers = getServerWebExchangeMatchers(uris);
        return new OrServerWebExchangeMatcher(serverWebExchangeMatchers).matches(exchange);
    }

    private List<ServerWebExchangeMatcher> getServerWebExchangeMatchers(List<String> uris) {
        var matchers = new ArrayList<ServerWebExchangeMatcher>(uris.size());
        for (String uri : uris) {
            matchers.add(new PathPatternParserServerWebExchangeMatcher(uri, null));
        }
        return matchers;
    }
}

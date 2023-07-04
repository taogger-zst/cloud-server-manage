package com.taogger.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import yxd.kj.app.server.gateway.exception.RequestAccessDeniedHandler;
import yxd.kj.app.server.gateway.exception.RequestAuthenticationEntryPoint;
import yxd.kj.app.server.gateway.matcher.FilterRouteServerWebExchangeMatcher;

/**
 * @author taogger
 * @date 2022/7/20 9:15
 * 网关的OAuth2.0资源的配置类
 * 由于spring cldou gateway使用的Flux，因此需要使用@EnableWebFluxSecurity注解开启，而不是平常的web应用了
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT的鉴权管理器
     * @author taogger
     * @date 2022/7/26 13:49
    **/
    private final ReactiveAuthorizationManager<AuthorizationContext> accessManager;

    /**
     * token过期的异常处理
     * @author taogger
     * @date 2022/7/26 13:49
    **/
    private final RequestAuthenticationEntryPoint requestAuthenticationEntryPoint;

    /**
     * 权限不足的异常处理
     * @author taogger
     * @date 2022/7/26 13:49
    **/
    private final RequestAccessDeniedHandler requestAccessDeniedHandler;

    /**
     * token校验管理器
     * @author taogger
     * @date 2022/7/26 13:50
    **/
    private final ReactiveAuthenticationManager tokenAuthenticationManager;

    /**
     * 过滤路由
     */
    private final FilterRouteServerWebExchangeMatcher filterRouteServerWebExchangeMatcher;

    /**
     * 配置spring security权限管理
     * @author taogger
     * @date 2022/7/26 13:50
    **/
    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception{
        //认证过滤器，放入认证管理器tokenAuthenticationManager
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(tokenAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeExchange()
                //过滤路由直接放行
                .matchers(filterRouteServerWebExchangeMatcher).permitAll()
                //白名单直接放行
               // .pathMatchers(ArrayUtil.toArray(sysConfig.getIgnoreUrls(),String.class)).permitAll()
                //其他的请求必须鉴权，使用鉴权管理器
                .anyExchange().access(accessManager)
                //鉴权的异常处理，权限不足，token失效
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(requestAuthenticationEntryPoint)
                .accessDeniedHandler(requestAccessDeniedHandler)
                .and()
                //token的认证过滤器，用于校验token和认证
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    /**
     * 跨域配置
     * @author taogger
     * @date 2022/8/12 14:26
     * @return {@link CorsWebFilter}
    **/
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 配置跨域
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许哪个请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许哪个方法进行跨域
        corsConfiguration.addAllowedMethod("*");
        // 允许哪个请求来源进行跨域
        corsConfiguration.addAllowedOriginPattern("*");
        // 是否允许携带cookie进行跨域
        corsConfiguration.setAllowCredentials(true);
        // 存活时间
        corsConfiguration.setMaxAge(18000L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}

package com.taogger.gateway.config;

import com.taogger.gateway.constant.SysConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author taogger
 * @date 2022/7/20 9:11
 * 鉴权管理器 用于认证成功之后对用户的权限进行鉴权
 * 第二个版本，集成RBAC，实现动态权限校验
 */
@Component
@RequiredArgsConstructor
public class JwtAccessManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        return authentication
                //判断是否认证成功
                .filter(Authentication::isAuthenticated)
                //获取认证后的全部权限
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                //如果权限包含则判断为true
                .any(authority->{
                    return authority.equals(SysConstant.ROLE_USER);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /*@Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        //匹配url
        var antPathMatcher = new AntPathMatcher();
        //从Redis中获取当前路径可访问角色列表
        var uri = authorizationContext.getExchange().getRequest().getURI();
        //请求方法 POST,GET
        var method = authorizationContext.getExchange().getRequest().getMethodValue();

        var restFulPath = method + SysConstant.METHOD_SUFFIX + uri.getPath();

        //获取所有的uri->角色对应关系
        Map<Object, Object> entries =  RedisUtils.hashGetAll(SysConstant.OAUTH_URLS);
        //角色集合
        var authorities = new ArrayList<String>();
        entries.forEach((path, roles) -> {
            //路径匹配则添加到角色集合中
            if (antPathMatcher.match(path.toString(), restFulPath)) {
                JSONArray objects = JSON.parseArray(roles.toString());
                authorities.addAll(Arrays.asList(objects.toArray(new String[]{})));
            }
        });
        //认证通过且角色匹配的用户可访问当前路径
        return mono
                //判断是否认证成功
                .filter(Authentication::isAuthenticated)
                //获取认证后的全部权限
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                //如果权限包含则判断为true
                .any(authority->{
                    //超级管理员直接放行
                    if (StrUtil.equals(SysConstant.ROLE_ROOT_CODE,authority))
                        return true;
                    //其他必须要判断角色是否存在交集
                    return CollectionUtil.isNotEmpty(authorities) && authorities.contains(authority);
                    return true;
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }*/
}

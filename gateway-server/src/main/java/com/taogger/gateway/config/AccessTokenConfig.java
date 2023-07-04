package com.taogger.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 令牌的配置
 * @author taogger
 * @date 2022/7/20 9:10
 */
@Configuration
public class AccessTokenConfig {

    @Value("${jwt.sign-key}")
    private String signKey;

    /**
     * 令牌的存储策略
     * @author taogger
     * @date 2022/7/26 12:03
    **/
    @Bean
    public TokenStore tokenStore() {
        //使用JwtTokenStore生成JWT令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JwtAccessTokenConverter
     * TokenEnhancer的子类，在JWT编码的令牌值和OAuth身份验证信息之间进行转换。
     * @author taogger
     * @date 2022/7/26 12:04
    **/
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 设置秘钥
        converter.setSigningKey(signKey);
        return converter;
    }
}

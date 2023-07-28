package com.github.jaxing.config;

import com.github.jaxing.auth.CustomJWTAuthProviderImpl;
import com.github.jaxing.common.domain.VertxHolder;
import com.github.jaxing.utils.ConfigUtils;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.authorization.JWTAuthorization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjxin
 * @date 2023/06/13
 */
@Configuration
public class AuthConfig {

    @Bean
    public JWTAuth jwtAuth() {
        return new CustomJWTAuthProviderImpl(VertxHolder.getVertx(), new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer(ConfigUtils.get("jwt.key"))));
    }

    @Bean
    public JWTAuthorization jwtAuthorization() {
        return JWTAuthorization.create("roles");
    }
}

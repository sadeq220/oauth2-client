package bmi.ir.ssoclient.config.gatewayfilters;

import bmi.ir.ssoclient.userInfo.UserInfoJWT;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
/**
 * add JWT header(Authorization) to http routing requests
 * This header contains authenticated user roles and authorities
 */
public class JWTGlobalFilter implements GlobalFilter, Ordered {
    private final UserInfoJWT userInfoJWT;

    public JWTGlobalFilter(UserInfoJWT userInfoJWT) {
        this.userInfoJWT = userInfoJWT;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (UserInfoModel) securityContext.getAuthentication().getPrincipal())
                .map(userInfoModel -> userInfoJWT.createJWT(userInfoModel))
                .map(jwtToken -> exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken).build())
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

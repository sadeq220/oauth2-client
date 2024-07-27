package bmi.ir.ssoclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class OAuth2AuthorizationResponseStoreAuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    public OAuth2AuthorizationResponseStoreAuthenticationSuccessHandler(@Value("${ui.uri}") String uri) {
        super(uri);
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken){
            ServerWebExchange exchange = webFilterExchange.getExchange();
            oAuth2AuthenticationToken.setDetails(exchange.getRequest().getQueryParams());
        }
        return super.onAuthenticationSuccess(webFilterExchange, authentication);
    }
}

package bmi.ir.ssoclient.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

public class RedirectServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final ServerRedirectStrategy serverRedirectStrategy = new DefaultServerRedirectStrategy();
    private final URI redirectURI;
    public RedirectServerAuthenticationSuccessHandler(String uri){
        this.redirectURI=URI.create(uri);
    }
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        return serverRedirectStrategy.sendRedirect(exchange,redirectURI);
    }
}

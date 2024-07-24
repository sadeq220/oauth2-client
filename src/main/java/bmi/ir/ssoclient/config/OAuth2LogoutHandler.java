package bmi.ir.ssoclient.config;

import bmi.ir.ssoclient.controller.model.RudimentaryUserAuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OAuth2LogoutHandler implements ServerLogoutHandler {
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Autowired
    public OAuth2LogoutHandler(ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = authorizedClientRepository.loadAuthorizedClient(oauth2Authentication.getAuthorizedClientRegistrationId(), authentication, exchange.getExchange());
        Object details = oauth2Authentication.getDetails();//TODO customize details object
        return Mono.empty();
    }
}

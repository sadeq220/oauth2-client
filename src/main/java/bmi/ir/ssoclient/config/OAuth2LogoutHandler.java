package bmi.ir.ssoclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LogoutHandler implements ServerLogoutHandler {
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
    private final String authorizationServerLogoutUri;
    private final String revokeAccessTokenUri;
    private final String terminateSessionQueryParam = "sessionTicket";

    @Autowired
    public OAuth2LogoutHandler(ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
                               @Value("${oauth2.baam.server.logout.uri}") String authorizationServerLogoutUri,
                               @Value("${oauth2.baam.server.revoke.access.token}") String revokeAccessTokenUri) {
        this.authorizedClientRepository = authorizedClientRepository;
        this.authorizationServerLogoutUri = authorizationServerLogoutUri;
        this.revokeAccessTokenUri = revokeAccessTokenUri;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        Mono<OAuth2AuthorizedClient> oAuth2AuthorizedClientMono = authorizedClientRepository.loadAuthorizedClient(oauth2Authentication.getAuthorizedClientRegistrationId(), authentication, exchange.getExchange());
        MultiValueMap<String, String> authorizationResponseParams = (MultiValueMap<String, String>) oauth2Authentication.getDetails();

        //terminate authorization server session
        String ticket = authorizationResponseParams.getFirst("ticket");
        URI authorizationServerLogoutWithSessionTicket = UriComponentsBuilder.fromHttpUrl(authorizationServerLogoutUri).queryParam(terminateSessionQueryParam, ticket).build().toUri();
        return oAuth2AuthorizedClientMono.flatMap(oAuth2AuthorizedClient -> {
                    OAuth2AccessToken accessToken = oAuth2AuthorizedClient.getAccessToken();
                    return WebClient.create()
                            .get()
                            .uri(authorizationServerLogoutWithSessionTicket)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(voidResponseEntity -> {

                                ClientRegistration clientRegistration = oAuth2AuthorizedClient.getClientRegistration();
                                String clientId = encodeClientCredential(clientRegistration.getClientId());
                                String clientSecret = encodeClientCredential(clientRegistration.getClientSecret());
                                return WebClient.create()
                                        .post()
                                        .uri(revokeAccessTokenUri)
                                        .headers(httpHeaders -> httpHeaders.setBasicAuth(clientId, clientSecret))
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .body(revokeAccessTokenBody(accessToken.getTokenValue()), MultiValueMap.class)
                                        .retrieve()
                                        .toBodilessEntity();
                            });
                })
                .then(Mono.empty());
    }

    private String encodeClientCredential(String clientCredential) {
        try {
            return URLEncoder.encode(clientCredential, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException ex) {
            // Will not happen since UTF-8 is a standard charset
            throw new IllegalArgumentException(ex);
        }
    }
    private Mono<MultiValueMap<String,String>> revokeAccessTokenBody(String accessToken){
        MultiValueMap<String,String> properties = new LinkedMultiValueMap<>();
        properties.add("token_type_hint", "access_token");
        properties.add("token",accessToken);
        return Mono.just(properties);
    }
}

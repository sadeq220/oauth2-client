package bmi.ir.ssoclient.config;

import bmi.ir.ssoclient.cryptography.SecretKeyReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
//@EnableWebFluxSecurity // for reactive filters
public class OAuth2Specialization {
    /**
     * for reactive securityFilterChain
     */
//    @Bean
//    public SecurityWebFilterChain securityConfigurer(ServerHttpSecurity http) {
//    return null;
//    }
    @Bean
    /**
     * oauth2 filter chain
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler,
                                                   AuthenticationFailureHandler authenticationFailureHandler) throws Exception {
        http
                .csrf(httpSecurityCsrfConfigurer -> {httpSecurityCsrfConfigurer.disable();})
                .authorizeHttpRequests((authorizeRequests)->authorizeRequests.requestMatchers("/air/**").permitAll().requestMatchers("/oauth2/**").permitAll().anyRequest().authenticated())
               // .authorizeHttpRequests((authorizeRequests)->authorizeRequests.requestMatchers("/air/**").permitAll().anyRequest().authenticated()) // request matcher part of SecurityFilterChain
                //.oauth2Client((oauth2client)->{})
                .exceptionHandling(exceptionHandlingConfigurer -> {exceptionHandlingConfigurer.authenticationEntryPoint(this.authenticationEntryPoint());})
                .oauth2Login((oauth2login)->{oauth2login.authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig.authorizationRequestResolver(oAuth2AuthorizationRequestResolver));
                                             //oauth2login.tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig.accessTokenResponseClient(this.tokenEndpointCustomizer()));
                                             oauth2login.successHandler(authenticationSuccessHandler);
                                             oauth2login.failureHandler(authenticationFailureHandler);
                                            });
        return http.build();
    }
    /**
     * A repository for OAuth 2.0 / OpenID Connect 1.0 {@link ClientRegistration}(s).
     */

    @Bean
    public ClientRegistrationRepository oauthRegistrationRepository(SecretKeyReader secretKeyReader,BaamClientRegistrationProperties baamClientRegistrationProperties){
        List<ClientRegistration> clientRegistrations = List.of(
                this.googleClientRegistration(),
                this.bamClientRegistration(secretKeyReader.getOAuth2ClientSecretKey(),baamClientRegistrationProperties));
        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }
    private ClientRegistration googleClientRegistration(){
        return ClientRegistration
                .withRegistrationId("google")
                .clientId("449798276031-nht828ucdaaeevt5kop6vivdhnlgr2sp.apps.googleusercontent.com")
                .clientSecret("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")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("openid", "profile", "email", "address", "phone")// openid scope is required for oidc authenticationProvider
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .redirectUri("{baseUrl}/login/oauth2/code/")
                .tokenUri("https://oauth2.googleapis.com/token") // to acquire an access-token by authorization grant request
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs") // id-token Signature Verifier
                .jwkSetUri("http://localhost/google/jwkset") //mocked jwk set
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .clientName("Google")
                .build();
    }
    private ClientRegistration bamClientRegistration(byte[] clientSecretKey,BaamClientRegistrationProperties clientProperties){
        return ClientRegistration
                .withRegistrationId("baam")
                .clientId(clientProperties.getClientId())
                .clientSecret(new String(clientSecretKey))
                .authorizationUri(clientProperties.getAuthorizationUri())
                .authorizationGrantType(clientProperties.getAuthorizationGrantType())
                .scope(clientProperties.getScopes())
                .redirectUri(clientProperties.getRedirectUri())
                .clientAuthenticationMethod(clientProperties.getClientAuthenticationMethod())
                .tokenUri(clientProperties.getTokenUri())
                .build();
    }
    private DelegatingAuthenticationEntryPoint authenticationEntryPoint()  {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> matcherToEntryPoint = new LinkedHashMap<>();
        AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/**");
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/baam");
        matcherToEntryPoint.put(antPathRequestMatcher,loginUrlAuthenticationEntryPoint);
        return new DelegatingAuthenticationEntryPoint(matcherToEntryPoint);
    }
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository){
        DefaultOAuth2AuthorizationRequestResolver defaultOAuth2AuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        StringKeyGenerator keyGenerator = KeyGenerators.string();
        defaultOAuth2AuthorizationRequestResolver.setAuthorizationRequestCustomizer(builder -> {
            builder.additionalParameters(this.authorizationUriAdditionalParams());
            builder.state(keyGenerator.generateKey()); // use HexEncodingStringKeyGenerator because baam does not accept base64 encoding
        });
        return defaultOAuth2AuthorizationRequestResolver;
    }
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenEndpointCustomizer(){
        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        tokenResponseClient.setRequestEntityConverter(new OAuth2AuthorizationCodeGrantRequestEntityConverter(){
            @Override
            /**
             * to customize access token request request-body parameters
             */
            protected MultiValueMap<String, String> createParameters(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
                MultiValueMap<String, String> parameters = super.createParameters(authorizationCodeGrantRequest); // access token request-body parameters
                parameters.set(OAuth2ParameterNames.REDIRECT_URI,"http://localhost:8080/login/oauth2/code"); // customize redirect_uri parameter for access token request
                return parameters;
            }
        });
        return tokenResponseClient;
    }
    private Map<String,Object> authorizationUriAdditionalParams(){
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("sso",1);
        return additionalParams;
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(@Value("${ui.uri}") String uiURI){
        return new SimpleUrlAuthenticationSuccessHandler(uiURI);
    }
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(@Value("${ui.uri}") String uiURI){
        return new SimpleUrlAuthenticationFailureHandler(uiURI+"?error");
    }
}

package bmi.ir.ssoclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(httpSecurityCsrfConfigurer -> {httpSecurityCsrfConfigurer.disable();})
                .authorizeHttpRequests((authorizeRequests)->authorizeRequests.requestMatchers("/air/**").permitAll().requestMatchers("/oauth2/**").permitAll().anyRequest().authenticated())
               // .authorizeHttpRequests((authorizeRequests)->authorizeRequests.requestMatchers("/air/**").permitAll().anyRequest().authenticated()) // request matcher part of SecurityFilterChain
                //.oauth2Client((oauth2client)->{})
                //.exceptionHandling(exceptionHandlingConfigurer -> {exceptionHandlingConfigurer.authenticationEntryPoint(this.authenticationEntryPoint());});
                .oauth2Login((oauth2login)->{oauth2login.authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig.authorizationRequestResolver(this.authorizationRequestResolver()));});
        return http.build();
    }
    /**
     * A repository for OAuth 2.0 / OpenID Connect 1.0 {@link ClientRegistration}(s).
     */

    @Bean
    public ClientRegistrationRepository oauthRegistrationRepository(){
        List<ClientRegistration> clientRegistrations = List.of(this.googleClientRegistration(),this.bamClientRegistration());
        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }
    private ClientRegistration googleClientRegistration(){
        return ClientRegistration
                .withRegistrationId("google")
                .clientId("449798276031-nht828ucdaaeevt5kop6vivdhnlgr2sp.apps.googleusercontent.com")
                .clientSecret("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")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .redirectUri("http://localhost:8080/login/oauth2/code/")
                .tokenUri("https://oauth2.googleapis.com/token") // to acquire an access-token by authorization grant request
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs") // id-token Signature Verifier
                .jwkSetUri("http://localhost/google/jwkset") //mocked jwk set
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .clientName("Google")
                .build();
    }
    private ClientRegistration bamClientRegistration(){
        return ClientRegistration
                .withRegistrationId("baam")
                .clientId("mika-local-client")
                .clientSecret("nG6mR3sB6kR7eG2bO6hF4yS3dB3cG2bB1jC5pC1qC1")
                .authorizationUri("http://185.135.30.10:9443/identity/oauth2/auth/authorize")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("batch-user-info")
                .redirectUri("http://localhost:8080/login/oauth2/code/")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .tokenUri("http://185.135.30.10:9443/identity/oauth2/auth/token")
                .build();
    }
    private DelegatingAuthenticationEntryPoint authenticationEntryPoint()  {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> matcherToEntryPoint = new LinkedHashMap<>();
        AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/**");
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google");
        matcherToEntryPoint.put(antPathRequestMatcher,loginUrlAuthenticationEntryPoint);
        return new DelegatingAuthenticationEntryPoint(matcherToEntryPoint);
    }
    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(){
        DefaultOAuth2AuthorizationRequestResolver defaultOAuth2AuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(this.oauthRegistrationRepository(), "/oauth2/authorization");
        StringKeyGenerator keyGenerator = KeyGenerators.string();
        defaultOAuth2AuthorizationRequestResolver.setAuthorizationRequestCustomizer(builder -> {
            builder.additionalParameters(this.authorizationUriAdditionalParams());
            builder.state(keyGenerator.generateKey()); // use HexEncodingStringKeyGenerator because baam does not accept base64 encoding
        });
        return defaultOAuth2AuthorizationRequestResolver;
    }
    private Map<String,Object> authorizationUriAdditionalParams(){
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("sso",1);
        return additionalParams;
    }
}

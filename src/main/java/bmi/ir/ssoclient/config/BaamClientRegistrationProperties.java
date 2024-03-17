package bmi.ir.ssoclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.List;


@ConfigurationProperties(prefix = "oauth2.baam.client")
public class BaamClientRegistrationProperties {
    private final String clientId;
    private final String authorizationUri;
    private final String tokenUri;
    private final List<String> scopes;
    private final AuthorizationGrantType authorizationGrantType = AuthorizationGrantType.AUTHORIZATION_CODE;
    private final String redirectUri = "{baseUrl}/login/oauth2/code/";
    private final ClientAuthenticationMethod clientAuthenticationMethod= ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

    @ConstructorBinding
    public BaamClientRegistrationProperties(String clientId, String authorizationUri, String tokenUri, List<String> scopes) {
        this.clientId = clientId;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.scopes = scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public AuthorizationGrantType getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public ClientAuthenticationMethod getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }
}

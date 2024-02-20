package bmi.ir.ssoclient.config;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
/**
 * automatically registered with OAuth2LoginAuthenticationProvider
 * the input is result of tokenUri endpoint
 * the result will be set in SecurityContextHolder
 */
public class UserDetailsService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        return null;
    }
}

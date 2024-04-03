package bmi.ir.ssoclient.config;

import bmi.ir.ssoclient.cryptography.SecretKeyReader;
import bmi.ir.ssoclient.userInfo.UserInfoAccessor;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
/**
 * automatically registered with OAuth2LoginAuthenticationProvider
 * the input is result of tokenUri endpoint
 * the result will be set in SecurityContextHolder as a principal object
 */
public class UserDetailsService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SecretKeyReader secretKeyReader;
    private final UserInfoAccessor userInfoAccessor;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    public UserDetailsService(SecretKeyReader secretKeyReader,
                              UserInfoAccessor userInfoAccessor) {
        this.secretKeyReader = secretKeyReader;
        this.userInfoAccessor = userInfoAccessor;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2AccessToken accessToken = userRequest.getAccessToken();// baam access token is JWT
        Algorithm algorithm = Algorithm.HMAC256(Base64.getEncoder().encode(secretKeyReader.getOAuth2AccessTokenSecretKey()));
        DecodedJWT decodedJWT = JWT.decode(accessToken.getTokenValue());// auth0 SDK
        try {
            algorithm.verify(decodedJWT);// only verify signature and ignore "exp" and "nbf" claims
        }catch (SignatureVerificationException ex) {
            logger.error("access token signature invalid!",ex);
            throw new OAuth2AuthenticationException("access token signature invalid!");
        }
        try{
        String nationalId = decodedJWT.getClaims().get("ssn").asString();
        return userInfoAccessor.getIdentity(nationalId);
        } catch (Exception ex){
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }
}

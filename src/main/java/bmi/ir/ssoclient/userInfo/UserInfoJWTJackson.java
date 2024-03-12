package bmi.ir.ssoclient.userInfo;

import bmi.ir.ssoclient.controller.model.UserInfoDto;
import bmi.ir.ssoclient.cryptography.SecretKeyReader;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserInfoJWTJackson implements UserInfoJWT{
    private final ObjectMapper objectMapper;
    private final SecretKeyReader secretKeyReader;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserInfoJWTJackson(ObjectMapper objectMapper,SecretKeyReader secretKeyReader) {
        this.objectMapper = objectMapper;
        this.secretKeyReader = secretKeyReader;
    }

    @Override
    public String createJWT(UserInfoModel userInfoModel) {
        try {
            UserInfoDto userInfoDto = UserInfoDto.create(userInfoModel);
            Algorithm algorithm = Algorithm.HMAC256(secretKeyReader.getOAuth2AccessTokenSecretKey()); // TODO use asymmetric signature algorithms
            String userInfoJson = objectMapper.writeValueAsString(userInfoDto);
            return JWT.create().withPayload(userInfoJson).sign(algorithm);
        } catch (JsonProcessingException e) {
            logger.error("error serializing to json!",e);
            throw new RuntimeException("error serializing to json!",e);
        }
    }
}

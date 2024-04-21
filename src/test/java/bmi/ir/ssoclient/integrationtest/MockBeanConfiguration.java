package bmi.ir.ssoclient.integrationtest;

import bmi.ir.ssoclient.cryptography.SecretKeyReader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockBeanConfiguration {
    @Bean
    /**
     * create a stub secretKeyReader
     */
    public SecretKeyReader secretKeyReader(){
        return new SecretKeyReader() {
            @Override
            public byte[] getOAuth2AccessTokenSecretKey() {
                return new byte[8];
            }

            @Override
            public byte[] getOAuth2ClientSecretKey() {
                return new byte[8];
            }
        };
    }
}

package bmi.ir.ssoclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MVCConfig {
    @Bean
    public ObjectMapper jacksonMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
}

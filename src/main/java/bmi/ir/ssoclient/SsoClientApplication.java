package bmi.ir.ssoclient;

import bmi.ir.ssoclient.config.BaamClientRegistrationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BaamClientRegistrationProperties.class)
public class SsoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoClientApplication.class, args);
	}

}

package bmi.ir.ssoclient.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = MockBeanConfiguration.class)
class SsoClientApplicationTests {

	@Test
	void contextLoads() {
	}

}

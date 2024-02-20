package bmi.ir.ssoclient.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.servlet.function.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.function.Function;


@Configuration
public class APIGatewayRouterConfig {
    private final SecureRandom nonBlockingPRNG;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public APIGatewayRouterConfig(){
        try {
            this.nonBlockingPRNG = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            logger.error(this.getClass()+" constructing error!",e);
            throw new RuntimeException(this.getClass()+" constructing error!",e);
        }
    }
    @Bean
    /**
     * add routing to spring cloud gateway
     * use RequestPredicate and HandlerFunction
     * use HandlerFunctions.http() to define a 'Host' header of request
     */
    public RouterFunction<ServerResponse> getRoute(){
        return RouterFunctions.route().before(addCorrelationId()).GET("gateway/**",HandlerFunctions.http("http://localhost:9810")).build();
    }

    public Function<ServerRequest,ServerRequest> addCorrelationId(){
        return serverRequest -> {
            ServerRequest.Builder builder = ServerRequest.from(serverRequest);
            byte[] randomBytes=new byte[8];
            nonBlockingPRNG.nextBytes(randomBytes);
            builder.header("Correlation-Id", new String(Hex.encode(randomBytes)));
            return builder.build();
        };
    }
}

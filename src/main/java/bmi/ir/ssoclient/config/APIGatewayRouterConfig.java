package bmi.ir.ssoclient.config;

import bmi.ir.ssoclient.userInfo.UserInfoJWT;
import bmi.ir.ssoclient.userInfo.model.UserInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.servlet.function.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


@Configuration
public class APIGatewayRouterConfig {
    private final SecureRandom nonBlockingPRNG;
    private final UserInfoJWT userInfoJWT;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String REQUEST_TIMESTAMP_ATTRIBUTE = "request.forward.time";

    public APIGatewayRouterConfig(UserInfoJWT userInfoJWT){
        try {
            this.nonBlockingPRNG = SecureRandom.getInstance("SHA1PRNG");
            this.userInfoJWT = userInfoJWT;
        } catch (NoSuchAlgorithmException e) {
            logger.error(this.getClass()+" constructing error!",e);
            throw new RuntimeException(this.getClass()+" constructing error!",e);
        }
    }
    @Bean
    /**
     * add routing to spring cloud gateway with normal WebMvc.fn
     * In WebMvc.fn, an HTTP request is handled with a HandlerFunction: a function that takes ServerRequest and returns a ServerResponse.
     * Both the request and the response object have immutable contracts.
     * spring cloud gateway provides special HandlerFunction that routs request to another host.
     * use RequestPredicate and HandlerFunction
     * use HandlerFunctions.http() to define a 'Host' header of request
     */
    public RouterFunction<ServerResponse> getRoute(){
        return RouterFunctions.route()
                .before(addJWT())
                .before(addCorrelationId()) // Pre-filter
                .before(addTimestamp())
                .after(collectMetrics()) // Post-filter
                .GET("gateway/**",HandlerFunctions.http("http://localhost:9810"))
                .GET("/google/**",HandlerFunctions.http("http://localhost"))
                .build();
    }

    /**
     * One way to tie the microservice activities together is to use a special ID for each transaction called a “correlation ID”
     * see https://www.bandwidth.com/blog/a-recipe-for-adding-correlation-ids-in-java-microservices/
     */
    public Function<ServerRequest,ServerRequest> addCorrelationId(){
        return serverRequest -> {
            ServerRequest.Builder builder = ServerRequest.from(serverRequest);
            byte[] randomBytes=new byte[8];
            nonBlockingPRNG.nextBytes(randomBytes);
            builder.header(CORRELATION_ID_HEADER, new String(Hex.encode(randomBytes)));
            return builder.build();
        };
    }
    public Function<ServerRequest,ServerRequest> addJWT(){
        return serverRequest -> {
            ServerRequest.Builder builder = ServerRequest.from(serverRequest);
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            if (!authentication.isAuthenticated()) {
                throw new RuntimeException("user in not authenticated!");
            }
            UserInfoModel principal = (UserInfoModel) authentication.getPrincipal();
            String jwt = userInfoJWT.createJWT(principal);
            builder.header(HttpHeaders.AUTHORIZATION,"Bearer "+jwt);
            return builder.build();
        };
    }
    public Function<ServerRequest,ServerRequest> addTimestamp(){
        return serverRequest -> {
            ServerRequest.Builder builder = ServerRequest.from(serverRequest);
            builder.attribute(REQUEST_TIMESTAMP_ATTRIBUTE,System.currentTimeMillis());
            return builder.build();
        };
    }
    public BiFunction<ServerRequest,ServerResponse,ServerResponse> collectMetrics(){
    return (serverRequest, serverResponse) -> {
        Map<String, Object> attributes = serverRequest.attributes();
        String correlationIdHeader = serverRequest.headers().header(CORRELATION_ID_HEADER).get(0);
        long requestForwardTime = (long) attributes.get(REQUEST_TIMESTAMP_ATTRIBUTE);
        long rtt= System.currentTimeMillis() - requestForwardTime; // round trip time

        logger.info("endpoint:{} {} correlationId:{} rtt:{}",serverRequest.method(),serverRequest.uri(),correlationIdHeader,rtt);
        return serverResponse;
    };
    }
}

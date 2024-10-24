package bmi.ir.ssoclient.config;

import bmi.ir.ssoclient.userInfo.UserInfoJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class APIGatewayRouterConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String REQUEST_TIMESTAMP_ATTRIBUTE = "request.forward.time";

//    @Bean
//    /**
//     * add routing to spring cloud gateway with normal WebMvc.fn
//     * In WebMvc.fn, an HTTP request is handled with a HandlerFunction: a function that takes ServerRequest and returns a ServerResponse.
//     * Both the request and the response object have immutable contracts.
//     * spring cloud gateway provides special HandlerFunction that routs request to another host.
//     * use RequestPredicate and HandlerFunction
//     * use HandlerFunctions.http() to define a 'Host' header of request
//     */
//    public RouterFunction<ServerResponse> getRoute(){
//        return RouterFunctions.route()
//                .before(addJWT())
//                .before(addCorrelationId()) // Pre-filter
//                .before(addTimestamp())
//                .after(collectMetrics()) // Post-filter
//                .GET("gateway/**",HandlerFunctions.http("http://localhost:9810"))
//                .GET("/google/**",HandlerFunctions.http("http://localhost"))
//                .build();
//    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, List<GatewayFilter> gatewayFilters){
        return builder.routes()
                .route("backend",predicateSpec -> predicateSpec.path("/bff/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .rewritePath("/bff/?(?<segment>.*)","/${segment}")
                                .filters(gatewayFilters))
                        .uri("http://localhost:9090"))
                .route(predicateSpec -> predicateSpec.path("/**")
                        .uri("http://localhost:3000"))
                .build();
    }
/*
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
    }*/
}

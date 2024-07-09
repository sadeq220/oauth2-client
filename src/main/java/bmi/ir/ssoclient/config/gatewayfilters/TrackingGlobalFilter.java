package bmi.ir.ssoclient.config.gatewayfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
/**
 * One way to tie the microservice activities together is to use a special ID for each transaction called a “correlation ID”
 * see https://www.bandwidth.com/blog/a-recipe-for-adding-correlation-ids-in-java-microservices/
 */
public class TrackingGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private final SecureRandom nonBlockingPRNG;

    public TrackingGlobalFilter(){
        try {
            this.nonBlockingPRNG = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            logger.error(this.getClass()+" constructing error!",e);
            throw new RuntimeException(this.getClass()+" constructing error!",e);
        }
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders requestHeaders = request.getHeaders();
        if (!requestHeaders.containsKey(CORRELATION_ID_HEADER)) {
            byte[] randomBytes = new byte[8];
            nonBlockingPRNG.nextBytes(randomBytes);
            String correlationId = new String(Hex.encode(randomBytes));
            request.mutate().header(CORRELATION_ID_HEADER, correlationId).build();
        }
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, requestHeaders.getFirst(CORRELATION_ID_HEADER))));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

package bmi.ir.ssoclient.config.gatewayfilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MetricsGlobalFilter implements GlobalFilter, Ordered {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long epochRoutingTime= System.currentTimeMillis();
        String correlationId = exchange.getRequest().getHeaders().getFirst(RoutingConstants.CORRELATION_ID_HEADER);
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            long rtt= System.currentTimeMillis() - epochRoutingTime; // round trip time
            logger.info("endpoint:{} {} correlationId:{} rtt:{}", exchange.getRequest().getMethod(),exchange.getRequest().getURI(), correlationId,rtt);
        }));
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

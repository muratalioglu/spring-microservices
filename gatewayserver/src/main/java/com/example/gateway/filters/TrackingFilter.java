package com.example.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class TrackingFilter implements GlobalFilter {

    private final static Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        String correlationId = filterUtils.getCorrelationId(requestHeaders);
        if (correlationId != null)
            logger.debug("tmx-correlation-id found in tracking filter: {}", correlationId);
        else {
            correlationId = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationId);
            logger.debug("tmx-correlation-id generated in tracking filter: {}", correlationId);
        }

        String authToken = filterUtils.getAuthToken(requestHeaders);
        if (authToken != null)
            logger.debug("tmx-auth-token found in tracking filter: {}", authToken);
        else {
            authToken = generateAuthToken();
            exchange = filterUtils.setAuthToken(exchange, authToken);
            logger.debug("tmx-auth-token generated in tracking filter: {}", authToken);
        }

        return chain.filter(exchange);
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
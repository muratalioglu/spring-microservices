package com.example.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

    final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Bean
    public GlobalFilter globalFilter() {
        return ((exchange, chain) -> chain
                .filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                    String correlationId = filterUtils.getCorrelationId(requestHeaders);
                    String authToken = filterUtils.getAuthToken(requestHeaders);

                    if (correlationId != null) {
                        logger.debug(
                                "Adding the correlation id to the outbound headers: {}",
                                correlationId
                        );

                        exchange.getResponse()
                                .getHeaders()
                                .add(FilterUtils.CORRELATION_ID, correlationId);
                    }

                    if (authToken != null) {
                        logger.debug(
                                "Adding the auth token to the outbound headers: {}",
                                authToken
                        );

                        exchange.getResponse()
                                .getHeaders()
                                .add(FilterUtils.AUTH_TOKEN, authToken);
                    }

                    logger.debug(
                            "Completing outgoing request for: {}",
                            exchange.getRequest().getURI()
                    );
                })));
    }
}
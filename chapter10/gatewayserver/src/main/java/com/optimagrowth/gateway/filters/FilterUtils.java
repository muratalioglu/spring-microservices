package com.optimagrowth.gateway.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Optional;

@Component
public class FilterUtils {

    public static final String CORRELATION_ID = "tmx-correlation-id";

    public String getCorrelationId(HttpHeaders requestHeaders) {

        List<String> correlationIdList = requestHeaders.get(CORRELATION_ID);
        if (correlationIdList == null || correlationIdList.isEmpty())
            return null;

        return requestHeaders.get(CORRELATION_ID).stream()
                .findFirst()
                .orElse(null);
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange,
                                              String name, String value) {

        return exchange
                .mutate()
                .request(
                        exchange
                                .getRequest()
                                .mutate()
                                .header(name, value)
                                .build()
                )
                .build();
    }
}
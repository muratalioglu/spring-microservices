package com.example.gateway.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Objects;

@Component
public class FilterUtils {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN = "tmx-auth-token";
    public static final String USER_ID = "tmx-user-id";
    public static final String ORG_ID = "tmx-org-id";
    public static final String PRE_FILTER_TYPE = "pre";
    public static final String POST_FILTER_TYPE = "post";
    public static final String ROUTE_FILTER_TYPE = "route";

    public String getCorrelationId(HttpHeaders requestHeaders) {

        List<String> headers = requestHeaders.get(CORRELATION_ID);

        return headers != null ? headers.get(0) : null;
    }

    public String getAuthToken(HttpHeaders requestHeaders) {

        List<String> headers = requestHeaders.get(AUTH_TOKEN);

        return headers != null ? headers.get(0) : null;
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate()
                .request(
                        exchange.getRequest().mutate()
                                .header(name, value)
                                .build()
                )
                .build();
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public ServerWebExchange setAuthToken(ServerWebExchange exchange, String authToken) {
        return this.setRequestHeader(exchange, AUTH_TOKEN, authToken);
    }
}
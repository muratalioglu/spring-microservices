package com.example.book;

import com.example.gateway.filters.UserContextInterceptor;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableDiscoveryClient
public class BookServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookServiceApplication.class, args);
	}

	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptorList =
				restTemplate.getInterceptors();

		if (interceptorList.isEmpty())
			restTemplate.setInterceptors(
					List.of(new UserContextInterceptor())
			);
		else {
			interceptorList.add(new UserContextInterceptor());
			restTemplate.setInterceptors(interceptorList);
		}

		return restTemplate;
	}

	@Bean
	public CircuitBreakerRegistry getCircuitBreakerRegistry() {
		CircuitBreakerConfig circuitBreakerConfig =
				CircuitBreakerConfig.custom()
						.failureRateThreshold(50)
						.waitDurationInOpenState(Duration.ofMillis(30000))
						.permittedNumberOfCallsInHalfOpenState(2)
						.slidingWindowSize(4)
						.recordExceptions(
								HttpServerErrorException.class
						)
						.build();

		return CircuitBreakerRegistry.of(circuitBreakerConfig);
	}

	@Bean
	public BulkheadRegistry getBulkheadRegistry() {
		BulkheadConfig bulkheadConfig =
				BulkheadConfig.custom()
						.maxConcurrentCalls(3)
						.maxWaitDuration(Duration.ofMillis(2000))
						.build();

		return BulkheadRegistry.of(bulkheadConfig);
	}
}

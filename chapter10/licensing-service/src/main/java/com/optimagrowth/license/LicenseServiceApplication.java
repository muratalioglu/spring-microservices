package com.optimagrowth.license;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.events.model.OrganizationChangeModel;
import com.optimagrowth.license.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@SpringBootApplication
@EnableBinding(Sink.class)
public class LicenseServiceApplication {

    @Autowired
    private ServiceConfig serviceConfig;

    private static Logger logger =
            LoggerFactory.getLogger(LicenseServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LicenseServiceApplication.class, args);
	}

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        String hostname = serviceConfig.getRedisServer();
        int port = Integer.parseInt(serviceConfig.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

//    @StreamListener(Sink.INPUT)
//    public void sinkLogger(OrganizationChangeModel orgChange) {
//        logger.debug(
//                "Received an {} event for organization id: {}",
//                orgChange.getAction(),
//                orgChange.getOrganizationId()
//        );
//    }

	@Bean
	public LocaleResolver localeResolver() {

		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {

		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setBasenames("messages");
		return messageSource;
	}

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new UserContextInterceptor());

        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}

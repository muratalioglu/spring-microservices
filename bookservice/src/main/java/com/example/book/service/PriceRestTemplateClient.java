package com.example.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceRestTemplateClient {

    @Autowired
    RestTemplate restTemplate;

    public Double getPrice(Integer productId) {
        ResponseEntity<Double> responseEntity =
                restTemplate.getForEntity(
                        String.format("http://priceservice/v1/prices/%s", productId),
                        Double.class
                );
        return responseEntity.getBody();
    }
}
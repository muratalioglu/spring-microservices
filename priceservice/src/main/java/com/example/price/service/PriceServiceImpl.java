package com.example.price.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;
import java.util.Random;

@Service
public class PriceServiceImpl implements PriceService {

    Map<Integer, Double> priceMap =
            Map.of(
                    1, 30.50,
                    2, 72.30,
                    3, 20.00
            );

    @Override
    public Double getPrice(Integer productId) {

        Random random = new Random();

        boolean success = random.nextBoolean();

        if (!success)
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get price");

        return priceMap.get(productId);
    }

    @Override
    public Double getPriceBulkhead(Integer productId) {

        try {
            Thread.sleep(10000);
        } catch (Exception ignored) {

        }

        return priceMap.get(productId);
    }
}

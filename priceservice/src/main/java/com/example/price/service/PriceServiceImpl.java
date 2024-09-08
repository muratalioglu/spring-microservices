package com.example.price.service;

import org.springframework.stereotype.Service;

import java.util.Map;

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
        return priceMap.get(productId);
    }
}

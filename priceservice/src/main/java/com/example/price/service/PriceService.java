package com.example.price.service;

public interface PriceService {

    Double getPrice(Integer productId);

    Double getPriceBulkhead(Integer productId);
}
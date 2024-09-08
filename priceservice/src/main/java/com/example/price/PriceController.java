package com.example.price;

import com.example.price.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceController {

    @Autowired
    private PriceService priceService;

    @GetMapping("/v1/prices/{productId}")
    public ResponseEntity<Double> getPrice(@PathVariable Integer productId) {

        Double price = priceService.getPrice(productId);
        if (price == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(price);
    }
}

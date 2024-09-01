package com.example.book.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "discount-coupon")
@RefreshScope
@Getter
@Setter
public class DiscountCoupon {
    private String code;
    private String password;
}
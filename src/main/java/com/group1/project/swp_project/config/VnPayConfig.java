package com.group1.project.swp_project.config;


import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class VnPayConfig {
    @Value("${vnpay.tmnCode}")
    public String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    public String vnp_HashSecret;

    @Value("${vnpay.url}")
    public String vnp_Url;

    @Value("${vnpay.returnUrl}")
    public String vnp_ReturnUrl;
}

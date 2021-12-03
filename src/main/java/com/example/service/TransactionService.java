package com.example.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class TransactionService {

    @Value("${webpay.url}")
    private String webpayUrl;

    @Value("${webpay.data.ref}")
    private String webpayReference;

    @Value("${passport.authentication.url}")
    private String passportAuthUrl;

    @Value("${passport.authorization}")
    private String authorization;

    @Value("${transferservice.base.url}")
    private String transferService;


}

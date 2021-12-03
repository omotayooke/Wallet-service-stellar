package com.interswitch.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

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

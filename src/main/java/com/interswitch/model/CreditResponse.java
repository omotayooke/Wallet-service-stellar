package com.interswitch.model;

import lombok.Data;

@Data
public class CreditResponse {

    private String responseCode;
    private String transactionReference;
    private String responseMessage;
    private String status;
    private String clientRef;
    private String systemResponseCode;
    private String transDate;
}

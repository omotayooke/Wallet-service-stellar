package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class RateResponse {
    private String senderAssetCode;
    private String recipientAssetCode;
    private String price;
    private String amountToDeduct;

    public RateResponse(String senderAssetCode, String recipientAssetCode, String price, String amountToDeduct) {
        this.senderAssetCode = senderAssetCode;
        this.recipientAssetCode = recipientAssetCode;
        this.price = price;
        this.amountToDeduct = amountToDeduct;
    }
}



package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OrderbookResponse {
    private String sellingAssetCode;
    private String buyingAssetCode;
    private List<Bid> asks;
    private List<Bid> bids;

    public OrderbookResponse(String buyingAssetCode, String sellingAssetCode, List<Bid> asks, List<Bid> bids) {
        this.buyingAssetCode = buyingAssetCode;
        this.sellingAssetCode = sellingAssetCode;
        this.asks = asks;
        this.bids= bids;
    }
}



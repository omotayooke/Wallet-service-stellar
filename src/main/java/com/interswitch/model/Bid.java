package com.interswitch.model;

import lombok.Data;

@Data
public class Bid {
    private String price;
    private String amount;

    public Bid(String price, String amount) {
        this.price = price;
        this.amount = amount;
    }
}

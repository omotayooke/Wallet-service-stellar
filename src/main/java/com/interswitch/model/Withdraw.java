package com.interswitch.model;

import lombok.Data;

@Data
public class Withdraw {

    private String currencyCode;
    private String creditBankCode;
    private String creditNuban;
    private String amount;
    private String narration;
}

package com.interswitch.model;

import com.interswitch.service.CurrencyService;

public class AccountBalance {
    private String assetType;
    private String balance;
    private String currencyCode;

    public AccountBalance(String balance, String currencyCode) {
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public AccountBalance(String currencyCode) {
        super();
        this.currencyCode = currencyCode;
    }
    public AccountBalance() {
        super();
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getBalance() {
        return balance;
    }



    public String setBalance(String balance) {
        this.balance = balance;
        return balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        if (currencyCode == null)
            this.currencyCode = "XLM";
        else
            this.currencyCode = currencyCode;
    }



}



package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditRequest {

    private int transactionAmount;
   // private String rrn;
    private int currencyCode;
    private String destinationInstitutionAlias;
    private int channelCode;
    private String sourceAccountNumber;
    private String terminalId;
    private String clientRef;
    private String destinationAccountNumber;
    private String narration;
    private String sourceInstitutionAlias;
    private String sourceAccountName;
    private String paymentLocation;

}

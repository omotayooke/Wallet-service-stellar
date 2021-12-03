package com.interswitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameEnquiry {
    private String destinationAccountNumber;
    private String sourceAccountNumber;
    private String sourceAccountName;
    private String destinationInstitutionCode;
    private int transactionAmount;
    private String currencyCode;
    private String clientRef;
    private String mobileNumber;
    private String emailAddress;
    private int channelCode;
}

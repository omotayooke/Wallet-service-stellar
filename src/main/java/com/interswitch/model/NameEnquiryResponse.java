package com.interswitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NameEnquiryResponse {

    public String responseCode;
    public String accountName;
    public boolean canCredit;
    public String responseMessage;
    public String accountNumber;
    public String transactionReference;
}

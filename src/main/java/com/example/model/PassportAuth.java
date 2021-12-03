package com.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassportAuth {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("merchant_code")
    private String merchantCode;
    @JsonProperty("core_id")
    private String coreId;
    @JsonProperty("payableCode")
    private String payableCode;
    @JsonProperty("requestor_id")
    private String requestorId;
    @JsonProperty("client_name")
    private String clientName;
    @JsonProperty("payable_id")
    private String payableId;
    @JsonProperty("payment_code")
    private String paymentCode;
    @JsonProperty("institution_id")
    private String institutionId;
    @JsonProperty("jti")
    private String jti;
}


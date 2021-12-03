package com.interswitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import shadow.com.google.gson.annotations.SerializedName;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Rate {
    private String buyingAssetCode;
    private String sellingAssetCode;
    private String recipientAmount;
    }

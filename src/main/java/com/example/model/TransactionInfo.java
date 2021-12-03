package com.example.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class TransactionInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String email;
    private String transactionRef;
    private String transactionType;
     private String description;
    private String currencyToBuy;
    private String paymentReference;
    private String amount;
    private String responseCode;
    private LocalDateTime transactionDate;
    private String stellarStatus;
    private String memo;
    private LocalDateTime stellarProcessingDate;
    @Column(columnDefinition="nvarchar(max)")
    private String parameters;
}

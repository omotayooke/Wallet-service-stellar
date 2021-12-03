package com.interswitch.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "accountcurrency")
public class AccountCurrency {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int accountId;
    private String email;
    private String currencyCode;
}

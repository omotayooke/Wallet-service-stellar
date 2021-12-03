package com.example.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Currency extends Error{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String currencyCode;
    private String currencyName;
    private String charge;

}

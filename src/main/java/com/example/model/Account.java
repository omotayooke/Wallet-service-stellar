package com.example.model;

import javax.persistence.*;
import java.util.*;
import lombok.Data;

@Entity
@Data
@Table(name = "account")
public class Account extends Error{
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private int id;
    private String publicKey;
    private String privateKey;
    @Transient
    private List<AccountBalance> balance;
    private String email;

    public Account(String publicKey, String privateKey, String email) {
        super();
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.email = email;
    }

    public Account() {
        super();
    }
}

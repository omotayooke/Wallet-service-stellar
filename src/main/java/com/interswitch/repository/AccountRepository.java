package com.interswitch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.interswitch.model.Account;


public interface AccountRepository extends JpaRepository<Account, Long> {
	 Account findByEmail(String email);

	 Account findByPublicKey(String publicKey);
}
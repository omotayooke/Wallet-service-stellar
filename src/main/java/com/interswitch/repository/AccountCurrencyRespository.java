package com.interswitch.repository;

import com.interswitch.model.AccountCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountCurrencyRespository extends JpaRepository<AccountCurrency, Long>{
        List<AccountCurrency> findByEmail(String email);

        List<AccountCurrency> findByEmailAndAccountId(String email, int accountId);
}

package com.interswitch.repository;

import com.interswitch.model.Account;
import com.interswitch.model.AccountCurrency;
import com.interswitch.model.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionInfo, Long> {

  //  List<TransactionInfo> findByFailedStellarStatus();

}

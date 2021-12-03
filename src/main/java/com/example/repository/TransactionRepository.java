package com.example.repository;

import com.example.model.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionInfo, Long> {

  //  List<TransactionInfo> findByFailedStellarStatus();

}

package com.interswitch.repository;

import com.interswitch.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    List<Currency>  findAll();

}

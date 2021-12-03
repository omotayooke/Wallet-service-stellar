package com.example.service;

import com.example.model.AccountCurrency;
import com.example.model.Currency;
import com.example.repository.AccountCurrencyRespository;
import com.example.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private AccountCurrencyRespository accountCurrencyRespository;

    public List<Currency> findAllCurrency(){
        return currencyRepository.findAll();
    }

    public void saveCurrrency(Currency currency){
        currencyRepository.save(currency);
    }

    public void saveCustomerCurrency(AccountCurrency accountCurrency){
        accountCurrencyRespository.save(accountCurrency);
    }

    public List<AccountCurrency> findAcctCurrencyByEmail(String email){
        return accountCurrencyRespository.findByEmail(email);
    }

    public List<AccountCurrency> findAcctCurrencyByEmailAccountid(String email, int accountId){
        return accountCurrencyRespository.findByEmailAndAccountId(email, accountId);
    }

    public String getDecimalPlaces(String currencyCode){
        String decimalPlaces;
        switch (currencyCode){
            case "NGN":
                decimalPlaces = "%.2f";
                break;
            case "USD":
                decimalPlaces = "%.2f";
                break;
            case "GHC":
                decimalPlaces = "%.2f";
                break;
            case "KHC":
                decimalPlaces = "%.2f";
                break;
            default:
                decimalPlaces = "%.7f";
        }
        return decimalPlaces;
    }

}

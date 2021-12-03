package com.example.controller;

import com.example.model.*;
import com.example.service.AccountService;
import com.example.service.TransactionService;
import com.example.service.UserService;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private static HttpServletRequest request;



    @PostMapping("/admin/withdraw")
    public ModelAndView buyProcess(Withdraw withdraw, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        Map<String, String> transactions = new HashMap<>();
        String message = "";
        String type = "";
        if ("XLM".equals(withdraw.getCurrencyCode()))
        {
            message = "You can withdraw your XLM Currency";
        type = "danger";
        }
        else {
            var value = accountService.withdraw(withdraw, user.getEmail());
            if (value == true){
                var response = transferService(withdraw, user.getEmail());

                if ("00".equals(response.getResponseCode())){
                    message = "Your transaction was successful";
                    type = "success";
                    transactions = transactions(withdraw, response, "Withdrawal");
                }
                else if ("09".equals(response.getResponseCode())){
                    message = response.getResponseMessage();
                    type = "warning";
                    transactions = transactions(withdraw, response, "Withdrawal");
                }
                else{
                    message = response.getResponseMessage();
                    type = "danger";
                }
            }
            else{
                message = "We are unable to process your transaction at this time, please try again";
                type = "danger";
            }
        }
            modelAndView.addObject("userName",
                    "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());

//        modelAndView.addObject("clientip", getClientIp(request));
        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("message", message);
        modelAndView.addObject("type", type);
        modelAndView.setViewName("receipt");
        return modelAndView;
    }


    @PostMapping(value = {"/nameenquiry" })
    public String nameEnquiry(@RequestBody NameEnquiry nameEnquiry) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        nameEnquiry.setSourceAccountNumber("1234567890");
        nameEnquiry.setSourceAccountName("StellarPOC");
        nameEnquiry.setCurrencyCode("566");
        nameEnquiry.setTransactionAmount(100);
        nameEnquiry.setEmailAddress(user.getEmail());
        RestTemplate restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "BEARER "+ getPassportAccessToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String url = transactionService.getTransferService() + "transfer/inquiries/credit";

        HttpEntity httpEntity = new HttpEntity(nameEnquiry, httpHeaders);
        var resp = restTemplate.postForObject(url, httpEntity
                , NameEnquiryResponse.class);

        if ("00".equals(resp.getResponseCode())){
            //modelAndView.addObject("name", resp.accountName);
            return resp.accountName;
        }
        else{
            modelAndView.addObject("name", resp.getResponseMessage());
            return resp.getResponseMessage();
        }
            //    return "";
    }

    private String getPassportAccessToken(){
        RestTemplate restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, transactionService.getAuthorization());
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> param= new LinkedMultiValueMap<String, String>();
        param.add("grant_type", "client_credentials");
        param.add("scope", "profile");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(param,
                httpHeaders);
        var resp = restTemplate.postForObject(transactionService.getPassportAuthUrl(), httpEntity, PassportAuth
        .class);
        return resp.getAccessToken();
    }

    public CreditResponse transferService(Withdraw withdraw, String email){
        CreditRequest creditRequest = new CreditRequest();
        Date date = new Date();
        RestTemplate restTemplate = new RestTemplate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        String ref = calendar.getWeekYear() + "" + String.format("%15s", calendar.getTimeInMillis()+"").replace(' ',
                '0');
        int minorAmount = Integer.valueOf(withdraw.getAmount()) * 100;
        creditRequest.setChannelCode(1);
        //creditRequest.setClientRef(ref);
        creditRequest.setClientRef("ASAAASWWEWW");
        creditRequest.setCurrencyCode(566);
        creditRequest.setDestinationAccountNumber(withdraw.getCreditNuban());
        creditRequest.setDestinationInstitutionAlias("058");
       // creditRequest.setNarration(withdraw.getNarration());
        creditRequest.setNarration("Tran/99'9\\\\9004'24303/30313//08578/EZ@EKIE/LANG");
        creditRequest.setPaymentLocation("Tran/999900424303/3031308578/EZEKIE/LANG");
        creditRequest.setSourceAccountName("506099*********7499");
        creditRequest.setSourceAccountNumber("506099*********7499");
        creditRequest.setSourceInstitutionAlias("058");
        creditRequest.setTerminalId("3IWP0076");
        creditRequest.setTransactionAmount(minorAmount);

        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer "+ getPassportAccessToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String url = transactionService.getTransferService() + "accounts/credits/transaction";

        HttpEntity httpEntity = new HttpEntity(creditRequest, httpHeaders);
        var resp = restTemplate.postForObject(url, httpEntity
                , CreditResponse.class);
        resp.setClientRef(creditRequest.getClientRef());
        resp.setTransDate(format1.format(calendar.getTime()));

        return resp;
    }

    private Map<String, String> transactions(Withdraw withdraw, CreditResponse creditResponse, String type){
        Map<String, String> trans = new HashMap<>();
        trans.put("Transaction Type", type);
        trans.put("Transaction Reference", creditResponse.getClientRef() );
        trans.put("Transaction Date", creditResponse.getTransDate());
        trans.put("Beneficiary Name", withdraw.getCreditNuban());
        trans.put("Beneficiary Account Number", withdraw.getCreditNuban());
        trans.put("Narration", withdraw.getNarration());
        trans.put("Amount", withdraw.getAmount());
        trans.put("Status", WordUtils.capitalizeFully(creditResponse.getStatus().replace('_', ' ') ));
        trans.put("Credit Reference", creditResponse.getTransactionReference() );
        return trans;
    }


}

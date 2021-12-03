package com.interswitch.controller;

import com.interswitch.model.*;
import com.interswitch.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderbookController {

    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/admin/fund/calculateRate", produces = MediaType.APPLICATION_JSON_VALUE)
    public RateResponse getAmountToDeduct(@RequestBody Rate rate){
//        TODO: Handle nullpointer exceptions
        var response = accountService.getRate(rate.getBuyingAssetCode(), rate.getSellingAssetCode(), rate.getRecipientAmount());

        var deductedAmount = Float.parseFloat(response) * Float.parseFloat(rate.getRecipientAmount());
        String amount = String.valueOf(deductedAmount);

        return new RateResponse(rate.getBuyingAssetCode(), rate.getSellingAssetCode(), response, amount);

//
//        if (response.equals("null")){
//            return Optional.empty();
//        }
//        else {
//            var deductedAmount = Float.parseFloat(response) * Float.parseFloat(rate.getRecipientAmount());
//            String amount = String.valueOf(deductedAmount);
//
//            this.rateResponse = new RateResponse(rate.getBuyingAssetCode(), rate.getSellingAssetCode(), response, amount);
//            return Optional.of(this.rateResponse);
//        }

    }

    @GetMapping(value = "/admin/orderBook", produces = MediaType.APPLICATION_JSON_VALUE)
        public OrderbookResponse getOrderBook (@RequestBody OrderBook orderBook){
        var response = accountService.getOrderBook(orderBook.getSellingCurrencyCode(), orderBook.getBuyingCurrencyCode());

        var base = response.getBase();
        var baseCode = base.toXdr().getAlphaNum4().getAssetCode().toString();
        var counter = response.getCounter();
        var counterCode = counter.toXdr().getAlphaNum4().getAssetCode().toString();

        var asks = response.getAsks();
        List<Bid> ask = new ArrayList<>();
        for (int i = 0; i < asks.length; i++) {
            var askPrice = asks[i].getPrice();
            var askAmount = asks[i].getAmount();
            ask.add(new Bid(askPrice, askAmount));
        }

        var bids = response.getBids();
        List<Bid> bid = new ArrayList<>();
        for (int i = 0; i < bids.length; i++) {
            var bidPrice = bids[i].getPrice();
            var bidAmount = bids[i].getAmount();
            bid.add(new Bid(bidPrice, bidAmount));
        }
//
//        var bidPrice = bids[0].getPrice();
//        var bidAmount = bids[0].getAmount();
//        var bid = new Bid(bidPrice, bidAmount);
//        var bid = bids[0].getPrice();
        return new OrderbookResponse(baseCode, counterCode, ask, bid);
    }


}

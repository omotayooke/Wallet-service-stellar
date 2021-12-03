package com.example.flows;

import org.stellar.sdk.KeyPair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CreateAccount {

    private String friendbot="https://horizon-testnet.stellar.org/friendbot?addr=%s";


    public CreateAccount() {
    }

    public KeyPair generateKeyPair(){
        return KeyPair.random();
    }


    public String createAccountObject(KeyPair pair) throws IOException {
        String friendbotUrl = String.format(friendbot, pair.getAccountId());

        InputStream response = new URL(friendbotUrl).openStream();

        return new Scanner(response, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    }
}

package com.example.service;

import java.io.IOException;
import java.util.*;

import com.example.model.AccountBalance;
import com.example.model.Withdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.Asset;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Operation;
import org.stellar.sdk.requests.ErrorResponse;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.OrderBookResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.example.model.Account;

import com.example.repository.AccountRepository;
import org.stellar.sdk.Transaction;

@Service("accountService")
public class AccountService {

    @Autowired
    public AccountService(AccountRepository accountRepository, UserService userService, CurrencyService currencyService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.currencyService = currencyService;
    }

	private final AccountRepository accountRepository;
	private final UserService userService;
	private CurrencyService currencyService;

	@Value("${stellar.network.url}")
	private String network;

	@Value("${stellar.network.friendbot}")
	public String friendbot;

//	ISSUER KEYPAIR
	@Value("${isw.account.publicKey}")
    private String INTERSWITCH_PUBKEY;
	@Value("${isw.account.privateKey}")
    private String INTERSWITCH_SEED;

//	ANCHOR KEYPAIR
    @Value("${demi.account.publicKey}")
	private String DEMI_PUBKEY;
    @Value("${demi.account.privateKey}")
    private String DEMI_SEED;

	private static int DEFAULT_OPERATION_FEE = 100;

	private static String DEFAULT_LIMIT = "1000";

	private static int TWO_MINUTES = 120;


    public Account findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	public Account findByPublicKey(String publicKey){
        return accountRepository.findByPublicKey(publicKey);
    }

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getFriendbot() {
		return friendbot;
	}

	public void setFriendbot(String friendbot) {
		this.friendbot = friendbot;
	}

	public boolean userHasNoAccount(String email){
        return accountRepository.findByEmail(email) == null;
    }

    private boolean checkBalanceExists(String accountID, String currency){
        var balances = getBalances(accountID);
        for (AccountBalance bal : balances) {
            if (bal.getCurrencyCode().equals(currency)){
                return true;
            }
        }
        return false;
    }

    public OrderBookResponse getOrderBook(String buyingAssetCode, String sellingAssetCode){
//        TODO: Implement this
        Server server = new Server(network);

        var buyingAsset = Asset.createNonNativeAsset(buyingAssetCode, INTERSWITCH_PUBKEY);
        var sellingAsset = Asset.createNonNativeAsset(sellingAssetCode, INTERSWITCH_PUBKEY);

        try {
            return server.orderBook().buyingAsset(buyingAsset).sellingAsset(sellingAsset).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Asset getAsset(String assetCode){
        var issuerID = INTERSWITCH_PUBKEY;
        if (assetCode.equals("XLM")){
            String assetType = "native";
            return Asset.create(assetType, assetCode, issuerID);
        }
        else {
            return Asset.createNonNativeAsset(assetCode, issuerID);
        }
    }

    public String calculateDeductedAmount(String buyingAssetCode, String sellingAssetCode, String recipientAmount){
        Server server = new Server(network);

        var buyingAsset = Asset.createNonNativeAsset(buyingAssetCode, INTERSWITCH_PUBKEY);
        var sellingAsset = Asset.createNonNativeAsset(sellingAssetCode, INTERSWITCH_PUBKEY);

        try {
            var orderbookResponse = server.orderBook().buyingAsset(buyingAsset).sellingAsset(sellingAsset).execute();
            var rate = Float.parseFloat(orderbookResponse.getAsks()[0].getPrice());
            var deductedAmount = rate * Float.parseFloat(recipientAmount);
            return String.valueOf(deductedAmount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRate(String buyingAssetCode, String sellingAssetCode, String recipientAmount){
        Server server = new Server(network);

        var buyingAsset = Asset.createNonNativeAsset(buyingAssetCode, INTERSWITCH_PUBKEY);
        var sellingAsset = Asset.createNonNativeAsset(sellingAssetCode, INTERSWITCH_PUBKEY);

        try {
            var orderbookResponse = server.orderBook().buyingAsset(buyingAsset).sellingAsset(sellingAsset).execute();
            var rate = Float.parseFloat(orderbookResponse.getAsks()[0].getPrice());
            return String.valueOf(rate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            return "null";
        }
        return null;
    }

	private Transaction.Builder createTransaction(KeyPair sourceAccount){

        System.out.println("Creating new Transaction:");
	    Server server = new Server(network);
	    AccountResponse response = null;
        try {
            response = server.accounts().account(sourceAccount.getAccountId());
        } catch (Exception e) {
            if (e instanceof ErrorResponse) {
                System.out.println("Error Response: " + ((ErrorResponse) e).getBody());
            }
            else{
                e.printStackTrace();
            }
        }

        assert null != response;
        return new Transaction.Builder(response, Network.TESTNET);
    }

    private Transaction addSignature(Transaction tx, KeyPair keyPair){
        tx.sign(keyPair);
        System.out.println(String.format("Added signature %s", keyPair.getSignatureHint()));
        return tx;
    }

    private boolean submitTransaction(Transaction tx){
        boolean isSuccess = false;
        Server server = new Server(network);
        System.out.println("Submitting transaction: ");
        try {
            SubmitTransactionResponse submitTransactionResponse = server.submitTransaction(tx);
            System.out.print("Was submitTransaction successful : ");
            System.out.println(submitTransactionResponse.isSuccess() + "\n" + submitTransactionResponse.getResultXdr());
            System.out.println("DecodedTransactionResult = " + submitTransactionResponse.getDecodedTransactionResult());
//            System.out.println("TransactionResultCode = " + submitTransactionResponse.getExtras().getResultCodes().getTransactionResultCode());

            isSuccess = true;
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return isSuccess;
    }

    private Operation createAccountOperation(String sourceID, String destinationID, String startingBalance){
        System.out.println("New CreateAccount Operation: ");
        return new CreateAccountOperation.Builder(destinationID, startingBalance)
                .setSourceAccount(sourceID)
                .build();
	}

    private Operation changeTrustOperation(String sourceID, String assetCode, String issuerID){
        System.out.println("New ChangeTrust Operation: ");
        Asset asset = Asset.createNonNativeAsset(assetCode, issuerID);
        return new ChangeTrustOperation.Builder(asset, DEFAULT_LIMIT)
                .setSourceAccount(sourceID)
                .build();
    }

    private Operation paymentOperation(String sourceSeed, String destinationID, String amount, String assetCode, String issuerID){
        System.out.println("New Payment Operation: ");
        Asset asset;
	    if (assetCode.equals("XLM")){
            String assetType = "native";
            asset = Asset.create(assetType, assetCode, issuerID);
        }
	    else {
	        asset = Asset.createNonNativeAsset(assetCode, issuerID);
        }
        return new PaymentOperation.Builder(destinationID, asset, amount)
                .setSourceAccount(KeyPair.fromSecretSeed(sourceSeed).getAccountId())
                .build();
    }

    private Operation pathPaymentStrictSendOperation(String senderSeed, String recipientID, String amount, String sendAssetCode, String destinationAssetCode){
        Asset sendAsset = Asset.createNonNativeAsset(sendAssetCode, INTERSWITCH_PUBKEY);
        Asset destinationAsset = Asset.createNonNativeAsset(destinationAssetCode, INTERSWITCH_PUBKEY);
        return new PathPaymentStrictSendOperation.Builder(sendAsset, amount, recipientID, destinationAsset, "1")
                .setSourceAccount(KeyPair.fromSecretSeed(senderSeed).getAccountId())
                .build();
    }

    private Operation pathPaymentStrictReceiveOperation(String senderSeed, String recipientID, String amount, String sendAssetCode, String destinationAssetCode){
        Asset sendAsset = Asset.createNonNativeAsset(sendAssetCode, INTERSWITCH_PUBKEY);
        Asset destinationAsset = Asset.createNonNativeAsset(destinationAssetCode, INTERSWITCH_PUBKEY);
        return new PathPaymentStrictReceiveOperation.Builder(sendAsset, String.valueOf(Integer.MAX_VALUE), recipientID, destinationAsset, amount)
                .setSourceAccount(KeyPair.fromSecretSeed(senderSeed).getAccountId())
                .build();
    }

    private Operation createPassiveSellOfferOperation(String sellingAssetCode, String buyingAssetCode, String amount, String price){


        Asset sellingAsset = getAsset(sellingAssetCode);
        Asset buyingAsset = getAsset(buyingAssetCode);

        return new CreatePassiveSellOfferOperation.Builder(sellingAsset, buyingAsset, amount, price)
                .build();
    }

    private Operation manageSellOffer(String sellingAssetCode, String buyingAssetCode, String amount, String price){
        Asset sellingAsset = getAsset(sellingAssetCode);
        Asset buyingAsset = getAsset(buyingAssetCode);
        return new ManageSellOfferOperation.Builder(sellingAsset, buyingAsset, amount, price)
                .build();
    }
    private Operation manageBuyOffer(String sellingAssetCode, String buyingAssetCode, String amount, String price){
        Asset sellingAsset = getAsset(sellingAssetCode);
        Asset buyingAsset = getAsset(buyingAssetCode);
        return new ManageBuyOfferOperation.Builder(sellingAsset, buyingAsset, amount, price)
                .build();
    }

    public Account openAccount(String email, String currency){
        var account = accountRepository.findByEmail(email);
        if (account == null) {
            System.out.println("User has no account");
            KeyPair pair = KeyPair.random();
            var key = pair.getAccountId();
            var seed = String.valueOf(pair.getSecretSeed());
            System.out.println("Public Key: " + key + "\nPrivate Key: " + seed);
            var ISWKeyPair = KeyPair.fromSecretSeed(INTERSWITCH_SEED);

            var builder = createTransaction(ISWKeyPair);
            builder.addOperation(createAccountOperation(INTERSWITCH_PUBKEY, key, "1000"));
            Account acc = new Account(key, seed, email);
            accountRepository.save(acc);
            if (!currency.equals("XLM")) {
                builder.addOperation(changeTrustOperation(pair.getAccountId(), currency, INTERSWITCH_PUBKEY));
            }

            builder.addMemo(Memo.text(String.format("Open %s account", currency)))
            .setTimeout(TWO_MINUTES)
            .setOperationFee(DEFAULT_OPERATION_FEE);
            var tx = builder.build();
            addSignature(tx, ISWKeyPair);
            if (!currency.equals("XLM"))
                addSignature(tx, pair);
            submitTransaction(tx);
            return acc;
        }
        else {
            if (checkBalanceExists(account.getPublicKey(), currency)){
                account.setCode("01");
                account.setMessage(String.format("User already has a %s account", currency));
                System.out.println(String.format("User already has a %s account", currency));
            }
            else {
                var sourceSeed = account.getPrivateKey();
                changeTrust(sourceSeed, currency, INTERSWITCH_PUBKEY);
            }
        }
            return account;
    }

    public boolean issueFunds(String destinationEmail, String assetCode, String amount){
        var issuerSeed = INTERSWITCH_SEED;
        KeyPair issuer = KeyPair.fromSecretSeed(issuerSeed);

        var destinationSeed = accountRepository.findByEmail(destinationEmail).getPrivateKey();
        KeyPair destination = KeyPair.fromSecretSeed(destinationSeed);

        var issuerID = issuer.getAccountId();
        var destinationID = destination.getAccountId();

        var memo = String.format("Issue %s", assetCode);

        if (assetCode.equals("NGN")){
            return transferFunds(issuerSeed, destinationID, amount, "NGN", "", memo);
        }
        else {
            return transferFunds(issuerSeed, destinationID, amount, "NGN", assetCode, memo);
        }
    }


    public boolean fundAccount(String destinationSeed, String assetCode, String amount, String memo){
        var anchorSeed = DEMI_SEED;

        KeyPair anchor = KeyPair.fromSecretSeed(anchorSeed);
        KeyPair destination = KeyPair.fromSecretSeed(destinationSeed);

        var anchorID = anchor.getAccountId();
        var destinationID = destination.getAccountId();

        if (assetCode.equals("NGN")){
            return transferFunds(anchorSeed, destinationID, amount, "NGN", "", memo);
        }
        else {
            return transferFunds(anchorSeed, destinationID, amount, "NGN", assetCode, memo);
        }
    }

    public boolean transferFunds(String sourceSeed, String destinationID, String amount, String sendAsset, String destinationAsset, String memo){
        Network.TESTNET.getNetworkPassphrase();

        KeyPair issuer = KeyPair.fromSecretSeed(INTERSWITCH_SEED);
        var issuerID = issuer.getAccountId();

        KeyPair source = KeyPair.fromSecretSeed(sourceSeed);
        KeyPair destination = null;
        var destinationSeed = "";

        var email = "";
        try {
//            if (to.contains("@")){
            if (destinationID.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                email = destinationID;
                if (accountRepository.findByEmail(email) == null) {
                    openAccount(email, destinationAsset);
                }
                destinationSeed = findByEmail(email).getPrivateKey();
                destinationID = findByEmail(email).getPublicKey();
            }
            destinationSeed = findByPublicKey(destinationID).getPrivateKey();
                destination = KeyPair.fromSecretSeed(destinationSeed);
        }
        catch (IllegalArgumentException e){
            System.out.println("Unrecognized recipient");
            System.out.println(e.getMessage());
        }

        System.out.println(String.format(" Transfering %s %s to : %s" , amount, destinationAsset, destination.getAccountId()));
        System.out.println(String.format(" Transfering from %s account of: %s", sendAsset, source.getAccountId()));
        System.out.println(String.format(" Transfer memo : %s", memo));



        // Start building the transaction.
        var builder = createTransaction(source);
        // A memo allows you to add your own metadata to a transaction.
        // It's
        // optional and does not affect how Stellar treats the
        // transaction.

//        TODO: Check for trustline

        boolean changeTrust = false;

        if (!checkBalanceExists(destinationID, destinationAsset)) {
//            NO TRUSTLINE EXISTS, CREATE ONE
            changeTrust = true;
            builder.addOperation(changeTrustOperation(destinationID, destinationAsset, issuerID));
        }

        if (destinationAsset.equals(""))
            destinationAsset = sendAsset;

        if (sendAsset.equals(destinationAsset)){
            builder.addOperation(paymentOperation(sourceSeed, destinationID, amount, sendAsset, INTERSWITCH_PUBKEY));
        }
        else {
            builder.addOperation(pathPaymentStrictReceiveOperation(sourceSeed, destinationID, amount, sendAsset, destinationAsset));
        }
        builder.addMemo(Memo.text(memo))
                .setTimeout(120);

        var tx = builder.build();
        // Sign the transaction to prove you are actually the person sending it.
        addSignature(tx, source);

        if (changeTrust) {
            addSignature(tx, destination);
        }

        // And finally, send it off to Stellar!
        return submitTransaction(tx);
//        TODO: Display submitTransactionResult to user
    }

    private boolean changeTrust(String sourceSeed, String assetCode, String issuerID){

        Network.TESTNET.getNetworkPassphrase();
        KeyPair sourceAccount = KeyPair.fromSecretSeed(sourceSeed);

        var builder = createTransaction(sourceAccount);

//                TODO: Add option for user to select limit
        builder.addOperation(changeTrustOperation(sourceAccount.getAccountId(), assetCode, issuerID));
        builder.addMemo(Memo.text("Allowing asset: "+assetCode))
                .setTimeout(TWO_MINUTES)
                .setOperationFee(DEFAULT_OPERATION_FEE);
        var tx = builder.build();
        addSignature(tx, sourceAccount);
        System.out.println(String.format("%s balance added to account: %s", assetCode, sourceAccount.getAccountId()));
        return submitTransaction(tx);
    }

    public ArrayList<AccountBalance> getBalances(String accountKey) {

        ArrayList<AccountBalance> balances = new ArrayList<>();
        Network.TESTNET.getNetworkPassphrase();
        Server server = new Server(network);
        KeyPair destination = KeyPair.fromAccountId(accountKey);

        System.out.println("Using network : " + network);
        try {
            System.out.println("Account ID: " + destination.getAccountId());
            AccountResponse account = server.accounts().account(destination.getAccountId());
            System.out.println("Balances for account " + accountKey);

            for (AccountResponse.Balance balance : account.getBalances()){
                AccountBalance accountBalance = new AccountBalance();

                accountBalance.setCurrencyCode(balance.getAssetCode());
                accountBalance.setAssetType(balance.getAssetType());

                var floatBalance = Float.valueOf(balance.getBalance());
                var format = currencyService.getDecimalPlaces(accountBalance.getCurrencyCode());
                accountBalance.setBalance(String.format(format, floatBalance));

                balances.add(accountBalance);

                System.out.println(String.format("Type: %s, Code: %s, Balance: %s", accountBalance.getAssetType(),
                        accountBalance.getCurrencyCode(), accountBalance.getBalance()));
            }

        } catch (Exception e) {
            if (e instanceof NullPointerException)
                return new ArrayList<>();
            if (e instanceof ErrorResponse) {
                System.out.println("Error Response: " + ((ErrorResponse) e).getBody());
                return new ArrayList<>();

            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//            catch (NullPointerException e) {
//                e.printStackTrace();
//                return new ArrayList<>();
//            }
//            catch (ErrorResponse e) {
//                System.out.println("Error Response: " + e.getBody());
//                return new ArrayList<>();
//            }
//            catch (IOException e) {
//                    return new ArrayList<>();
//            }


        return balances;
    }

    public Boolean withdraw(Withdraw withdraw, String email){

        String customerPrivate = accountRepository.findByEmail(email).getPrivateKey();
//        if ("NGN".equals(withdraw.getCurrencyCode())){
        return transferFunds(customerPrivate, DEMI_PUBKEY, withdraw.getAmount(), withdraw.getCurrencyCode(),
                "NGN",   withdraw.getNarration());

    }

    public boolean redeemFunds(){

        return false;
    }


}

package com.interswitch.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.interswitch.model.*;
import com.interswitch.model.Currency;
import com.interswitch.service.CurrencyService;
import com.interswitch.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.interswitch.service.AccountService;
import com.interswitch.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private static HttpServletRequest request;

	@GetMapping(value = { "/", "/login" })
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@GetMapping(value = "/registration")
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@PostMapping(value = "/registration")
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			System.out.println("There was an error...");
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User registered successfully. Please login.");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("login");

		}
		return modelAndView;
	}

	@GetMapping("/admin/home")
	public ModelAndView home(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String message = "";
        var balances = getBalance(user);

		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        if (balances.size() != 0)
            modelAndView.addObject("userID",
            "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp(request));
		modelAndView.addObject("network", accountService.getNetwork());
		modelAndView.addObject("transfer", new Transfer());
        modelAndView.addObject("accounts", balances);
		modelAndView.addObject("message",  message);
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	@PostMapping("/admin/home")
	public ModelAndView home2(Transfer transfer, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String message = "";

		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp(request));
		modelAndView.addObject("transfer", transfer);
		modelAndView.addObject("message", message);

		modelAndView.setViewName("admin/send");
		return modelAndView;
	}


	@GetMapping("/admin/fund")
	public String fund(Model model, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String message = "";
		var balances = getBalance(user);
		List<Currency>currencyList = currencyService.findAllCurrency();

		model.addAttribute("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        model.addAttribute("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		model.addAttribute("clientip", getClientIp(request));
		model.addAttribute("network", accountService.getNetwork());
		model.addAttribute("transfer", new Transfer());
		model.addAttribute("webpayurl", transactionService.getWebpayUrl());
		model.addAttribute("webpayref", transactionService.getWebpayReference());
		model.addAttribute("accounts", balances);
		model.addAttribute("currencylist", currencyList);
		model.addAttribute("message", message);
		return "admin/fund";
	}

	@PostMapping("/admin/fund")
	public ModelAndView fundProcess(TransactionInfo transactionInfo, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		if (accountService.userHasNoAccount(user.getEmail()))
		    accountService.openAccount(user.getEmail(), transactionInfo.getCurrencyToBuy());
        accountService.fundAccount(accountService.findByEmail(user.getEmail()).getPrivateKey(), transactionInfo.getCurrencyToBuy(), transactionInfo.getAmount(), transactionInfo.getMemo());
		String message = "";

		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		modelAndView.addObject("clientip", getClientIp(request));
modelAndView.addObject("message", message);
		modelAndView.setViewName("admin/send");
		return modelAndView;
	}



	@GetMapping("/admin/withdraw")
	public String withdraw(Model model, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String message = "";
		Map<String, String> currency  = new HashMap<>();

		var balances = getBalance(user);
		List<Currency>currencyList = currencyService.findAllCurrency();
		if (balances.size() > 0) {
			for (var bal : balances) {
				for (var curr : currencyList) {
					if (bal.getCurrencyCode().equals(curr.getCurrencyCode())) {
						currency.put(curr.getCurrencyCode(), curr.getCurrencyName());
					}
				}
			}
		}
		else {
			currencyList = new ArrayList<>();
			message = "You don't have an existing account. Kindly open an account to funds";
		}

		model.addAttribute("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        model.addAttribute("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		model.addAttribute("clientip", getClientIp(request));
		model.addAttribute("network", accountService.getNetwork());
		model.addAttribute("withdraw", new Withdraw());
		model.addAttribute("accounts", balances);
		model.addAttribute("currencylist", currency);
		model.addAttribute("banks", getBanks());
		model.addAttribute("message", message);
		return "admin/withdraw";
	}



	@GetMapping(value = "/admin/send")
	public String Transfer(Model model, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String message = "";
		User user = userService.findUserByEmail(auth.getName());
		List<Currency>currencyList = currencyService.findAllCurrency();

		model.addAttribute("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        model.addAttribute("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		model.addAttribute("clientip", getClientIp(request));
		model.addAttribute("transfer", new Transfer());
		model.addAttribute("currencylist", currencyList);
		model.addAttribute("message", message);
		return "admin/send";

	}


	@PostMapping(value = "/admin/send")
	public ModelAndView send(Transfer transfer, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String message = "";
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp(request));
        accountService.transferFunds(accountService.findByEmail(user.getEmail()).getPrivateKey(),
                transfer.getTo(), transfer.getAmount(), transfer.getCurrencyToSend(), transfer.getCurrencyToReceive(), transfer.getMemo());
//        accountService.calculateDeductedAmount(transfer.getCurrencyToSend(), transfer.getCurrencyToReceive(),transfer.getAmount());
		modelAndView.addObject("transfer", transfer);
		modelAndView.addObject("message", message);

		modelAndView.setViewName("admin/send");
		return modelAndView;
	}

	@GetMapping(value = "/admin/create")
	public String create(Model model, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String message = "";
		model.addAttribute("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        model.addAttribute("userID",
                "Account ID: " + " ");
		//model.addAttribute("adminMessage", "Content Available Only for Users with Admin Role");
		model.addAttribute("clientip", getClientIp(request));
		List<Currency>currencyList = currencyService.findAllCurrency();

		model.addAttribute("currencylist", currencyList);
		model.addAttribute("Currency", new Currency());
		model.addAttribute("message", message);
        return "admin/create";
	}

    @PostMapping(value = "/admin/create")
    public ModelAndView openAccount(@ModelAttribute Currency currency,	HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
		String message = "";
        modelAndView.addObject("userName",
                "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + " ");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.addObject("clientip", getClientIp(request));

        Account acc = accountService.openAccount( user.getEmail(), currency.getCurrencyCode());
        modelAndView.addObject("transfer", new Transfer());
        if (!"00".equals(acc.getCode())){
			message= acc.getMessage();
		}
        modelAndView.addObject("message", message);

		var balances = getBalance(user);
		modelAndView.addObject("accounts", balances);

        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @GetMapping(value = "/admin/details")
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName",
                "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("userID",
                "Account ID: " + accountService.findByEmail(user.getEmail()).getPublicKey());

        var balances = getBalance(user);
        modelAndView.addObject("network", accountService.getNetwork());
        modelAndView.addObject("accounts", balances);

        modelAndView.setViewName("admin/details");

        return modelAndView;
    }

	private static String getClientIp(HttpServletRequest request) {

		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		System.out.println("Remote Address: " + remoteAddr);
		return remoteAddr;
	}

	private List<AccountBalance> getBalance(User user){
        var balances = new ArrayList<AccountBalance>();
        var acc = accountService.findByEmail(user.getEmail());
        if (acc != null) {
            balances = accountService.getBalances(acc.getPublicKey());
        }
        return balances;
    }

    private Map<String, String> getBanks(){
	    Map<String, String> bankCodes = new HashMap<>();
	    bankCodes.put("FBN", "First Bank Plc");
        bankCodes.put("GTB", "Guaranty Trust Bank Plc");
        bankCodes.put("ACCESS", "Access Bank Plc");
        bankCodes.put("ZBN", "Zenith Bank Plc");
        bankCodes.put("SCB", "Standard Chartered Bank Plc");
        bankCodes.put("SIBTC", "Stanbic IBTC Bank Plc");
        bankCodes.put("WEMA", "Wema Bank Plc");
        bankCodes.put("STERLING", "Sterling Bank Plc");
	    return bankCodes;
    }

}

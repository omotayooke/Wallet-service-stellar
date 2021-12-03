package com.example.model;

public class Transfer {
	
	private String from;
	private String to;
	private String amount;
	private String currencyToSend;
	private String currencyToReceive;
    private String memo;
    private String type;

    public String getCurrencyToSend() {
        return currencyToSend;
    }

    public void setCurrencyToSend(String currencyToSend) {
        this.currencyToSend = currencyToSend;
    }

    public String getCurrencyToReceive() {
        return currencyToReceive;
    }

    public void setCurrencyToReceive(String currencyToReceive) {
        this.currencyToReceive = currencyToReceive;
    }

    public String getType() {
		return type;
	}
    public void setType(String type) {
		this.type = type;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Transfer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	

}

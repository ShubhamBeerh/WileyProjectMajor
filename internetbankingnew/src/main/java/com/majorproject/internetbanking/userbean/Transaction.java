package com.majorproject.internetbanking.userbean;

import java.util.Date;

//Transaction
public class Transaction {
	int accountNo;
	String details;
	Date dateOfTransaction;
	
	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Transaction(int accountNo, String details, Date dateOfTransaction, double amount) {
		super();
		this.accountNo = accountNo;
		this.details = details;
		this.dateOfTransaction = dateOfTransaction;
		this.amount = amount;
	}
	
	public int getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Date getDateOfTransaction() {
		return dateOfTransaction;
	}
	public void setDateOfTransaction(Date dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	double amount;
}

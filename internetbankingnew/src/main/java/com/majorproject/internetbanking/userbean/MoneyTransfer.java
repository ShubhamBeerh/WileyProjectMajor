package com.majorproject.internetbanking.userbean;

public class MoneyTransfer {
	int accountNo;
	int beneficiaryAcNo;
	double amount;
	public MoneyTransfer() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MoneyTransfer(int accountNo, int beneficiaryAcNo, double amount) {
		super();
		this.accountNo = accountNo;
		this.beneficiaryAcNo = beneficiaryAcNo;
		this.amount = amount;
	}
	public int getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}
	public int getBeneficiaryAcNo() {
		return beneficiaryAcNo;
	}
	public void setBeneficiaryAcNo(int beneficiaryAcNo) {
		this.beneficiaryAcNo = beneficiaryAcNo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}

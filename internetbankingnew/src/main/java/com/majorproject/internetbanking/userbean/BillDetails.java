package com.majorproject.internetbanking.userbean;


//BillDetails
public class BillDetails {
	String username;
	String billNumber;
	
	public BillDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BillDetails(String username,String billNumber) {
		super();
		this.username = username;
		this.billNumber = billNumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	@Override
	public String toString() {
		return "BillDetails [username=" + username + ", billNumber=" + billNumber + "]";
	}




}

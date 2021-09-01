package com.majorproject.internetbanking.ibservice;

import java.sql.SQLException;
import java.util.List;

import com.majorproject.internetbanking.userbean.BillDetails;
import com.majorproject.internetbanking.userbean.Credentials;
import com.majorproject.internetbanking.userbean.MoneyTransfer;
import com.majorproject.internetbanking.userbean.Transaction;
import com.majorproject.internetbanking.userbean.UserBean;

public interface InterBankService {
	public UserBean addUser(UserBean user) throws SQLException;
	
	public Credentials checkUser(Credentials credentials) throws SQLException;
	
	public UserBean viewUser(String username) throws SQLException;
	
	public String generateBill(String Type, BillDetails billDetails) throws SQLException;
	
	public String payBill(String billNo) throws SQLException;
	
	public List<Transaction> getTransaction(String username) throws SQLException;
	
	public String moneyTransfer(String username,MoneyTransfer transfer) throws SQLException;
}

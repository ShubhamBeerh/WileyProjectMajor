package com.majorproject.internetbanking.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.majorproject.internetbanking.ibservice.InterBankService;
import com.majorproject.internetbanking.userbean.BillDetails;
import com.majorproject.internetbanking.userbean.Credentials;
import com.majorproject.internetbanking.userbean.MoneyTransfer;
import com.majorproject.internetbanking.userbean.Transaction;
import com.majorproject.internetbanking.userbean.UserBean;

@CrossOrigin
@RestController
public class IBController {
	
	@Autowired
	InterBankService inter;
	
	@GetMapping("/home")
	public String homePage() {
		return "Welcome to International Internet Bank";
	}
	
	@PostMapping("/register")
	public UserBean newUser(@RequestBody UserBean user) throws SQLException {
		return this.inter.addUser(user);
	}
	
	@PostMapping("/login")
	public String checkUser(@RequestBody Credentials credentials) throws SQLException {
		if(this.inter.checkUser(credentials)==null)
			return "Invalid Username or Password! Please try again!";
		else
			return "Login Successfull!";
	}
	
	@GetMapping("/Mainpage/view/{username}")
	public UserBean viewUser(@PathVariable String username) throws SQLException {
		return this.inter.viewUser(username);
	}
	
	@PostMapping("/Mainpage/billDetails/{billType}")
	// Remove bill type from BillDetails and pass it directly from post mapping
	public String generateAmount(@PathVariable String billType, @RequestBody BillDetails details) throws SQLException {
		System.out.println(details.getUsername());
		return this.inter.generateBill(billType,details);
		
	}
	
	@GetMapping("/Mainpage/billDetails/{billNo}")
	public String payBill(@PathVariable String billNo) throws SQLException {
		return this.inter.payBill(billNo);
	}
	
	@GetMapping("/Mainpage/transactionStatus/{username}")
	public List<Transaction> passBook(@PathVariable String username) throws SQLException {
		return this.inter.getTransaction(username);
	}
	
	@PostMapping("/Mainpage/moneyTransfer/{username}")
	public ResponseEntity<Object> moneyTransfer(@PathVariable String username, @RequestBody MoneyTransfer transfer) throws SQLException{
		if(this.inter.moneyTransfer(username, transfer)==null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Balance!");
		return ResponseEntity.status(HttpStatus.OK).body("Payment Successfully Done!");
	}
		
}

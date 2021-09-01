package com.majorproject.internetbanking.ibservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.majorproject.internetbanking.userbean.BillDetails;
import com.majorproject.internetbanking.userbean.Credentials;
import com.majorproject.internetbanking.userbean.MoneyTransfer;
import com.majorproject.internetbanking.userbean.Transaction;
import com.majorproject.internetbanking.userbean.UserBean;

@Service
public class InterBankServiceImpl implements InterBankService {
	String url="jdbc:mysql://localhost:3306/mydb";
	String dbuser="root";
	String dbpassword="cov@lent12";
	
	@Override
	public UserBean addUser(UserBean user) throws SQLException {
		// TODO Auto-generated method stub
		String username = user.getUsername();
		String password = user.getPassword();
		String name = user.getName();
		String address = user.getAddress();
		String email = user.getEmail();
		long mobileNo = user.getMobileNo();
		long aadharNo = user.getAadharCard();
		String panCard = user.getPanCard();
		double accountBal = user.getAccBalance();
		String accountTypes = user.getAccountTypes();
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		System.out.println("Connection successfully made!!!");
		String query = "insert into user values (null,?,?,?,?,?,?,?)";
		PreparedStatement st = con.prepareStatement(query);
		//User Table
		st.setString(1,username);
		st.setString(2,name);
		st.setString(3,address);
		st.setString(4,email);
		st.setLong(5,mobileNo);
		st.setLong(6,aadharNo);
		st.setString(7,panCard);
		int row = st.executeUpdate();
		
		//Accounts Table
		Random rand = new Random();
		//10001
		//0+100000 = 100000
		//99999+100000 = 199999
		int number = rand.nextInt(99999)+100000;
		String sixDig = String.format("%06d",number);
		int sixDigNum = Integer.parseInt(sixDig);
		String bankName = "IIB";
		String IFSC = bankName+sixDigNum;
		query = "select UserID from user where AadharNumber = ?";
		st = con.prepareStatement(query);
		st.setLong(1,aadharNo);
		System.out.println("!!!! Statement ====="+st);
		ResultSet rs = st.executeQuery();
		int userID=0;
		while(rs.next()) {
		System.out.println("!!!! Result Set value : "+rs.getInt(1));
		userID = rs.getInt(1);
		}
		query = "insert into accounts values(?,?,?,?,?)";
		st = con.prepareStatement(query);
		st.setInt(1,userID);
		st.setInt(2,sixDigNum);
		st.setString(3,IFSC);
		st.setString(4,randString());
		st.setString(5,"Rs."+accountBal);
		row = st.executeUpdate();
		
		//User Data Table
		query = "insert into UserData values (?,?,?)";
		st = con.prepareStatement(query);
		st.setInt(1,userID);
		st.setString(2,username);
		st.setString(3,password);
		st.executeUpdate();
		
		//Type Table 
		query = "select TypeCode from accounts where User_UserID= ?";
		st = con.prepareStatement(query);
		st.setInt(1,userID);
		String typeCode = "";
		rs = st.executeQuery();
		while(rs.next()) {
			typeCode = rs.getString(1);
		}
		
		String[] typeCodeArr = accountTypes.split(",");
		query = "insert into TypeTable values(?,?)";
		st = con.prepareStatement(query);
		for(int i=0;i<typeCodeArr.length;i++) {
			st.setString(1,typeCode);
			st.setString(2,typeCodeArr[i]);
			st.addBatch();
		}
		st.executeBatch();
		st.close();
		con.close();
		return user;
	}
	
	@Override
	public Credentials checkUser(Credentials credentials) throws SQLException {
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		String query = "select * from UserData";
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);
		while(rs.next()) {
			if(rs.getString(2).equals(credentials.getUsername())) {
				if(rs.getString(3).equals(credentials.getPassword()))
					return credentials;
				else
					return null;
			}
		}
		return null;
		
	}
	
	@Override
	public UserBean viewUser(String username) throws SQLException {
		// TODO Auto-generated method stub
		UserBean user=null;
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		String query = "select * from user";
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);
		
		//UserBean Object creation
		while(rs.next()) {
			if(username.equals(rs.getString(2))) {
				user= new UserBean(rs.getString(2),"•••••",rs.getString(3),rs.getString(4),rs.getString(5),rs.getLong(6),rs.getLong(7),rs.getString(8),0.0,null);
				break;
			}
		}
		
		//Setting Account Types to the already created UserBean object
		query = "select AccountType from TypeTable where TypeCode = (select TypeCode from Accounts where User_UserID = (select UserID from User where Username = ?))";
		PreparedStatement prep = con.prepareStatement(query);
		prep.setString(1,username);
		rs = prep.executeQuery();
		StringBuilder accTypes = new StringBuilder();
		while(rs.next()) {
			accTypes.append(rs.getString(1)+",");
		}
		String accountTypes = accTypes.substring(0, (accTypes.length()-1));
		user.setAccountTypes(accountTypes);
		
		//Setting Balance to the already created UserBean object
		query= "select Balance from Accounts where User_UserID=(select UserID from User where Username = ?)";
		prep = con.prepareStatement(query);
		prep.setString(1,username);
		rs=prep.executeQuery();
		String balance="";
		while(rs.next()) {
			balance = rs.getString(1);
		}
		double bal = getBalance(balance);
		user.setAccBalance(bal);
		
		return user;
	}
	
	
	//Bill Generation
	@Override
	public String generateBill(String Type, BillDetails billDetails) throws SQLException {
		// TODO Auto-generated method stub
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		System.out.println("CONNECTION MADE SUCCESSFULLY");
		String query = "select UserID from user where username = ?";
		PreparedStatement prep = con.prepareStatement(query);
		System.out.println(billDetails.getUsername());
		prep.setString(1,billDetails.getUsername());
		ResultSet rs = prep.executeQuery();
		int userID=0;
		while(rs.next()) {
			userID = rs.getInt(1);
		}
		System.out.println("!!!!!!! USER ID ======"+userID);
		query = "select BillType from BillDetails where User_UserID=?";
		prep = con.prepareStatement(query);
		prep.setInt(1,userID);
		String billType="";
		rs = prep.executeQuery();
		
		while(rs.next()) {
			billType=rs.getString(1);
		if(billType.equals(Type)) {
			query = "select BillNumber from BillData where BillDetails_User_UserID= ? and BillDetails_BillType= ?";
			prep=con.prepareStatement(query);
			prep.setInt(1, userID);
			prep.setString(2,billType);
			String billNum="";
			rs=prep.executeQuery();
			while(rs.next()) {
				billNum=rs.getString(1);
			if(billNum.equals(billDetails.getBillNumber())) {
				//Update the amount in db corresponding to the bill number and return amount
				query = "update BillData set amount = ? where BillNumber = ?";
				double amount = randAmount();
				prep=con.prepareStatement(query);
				prep.setDouble(1, amount);
				prep.setString(2, billNum);
				prep.executeUpdate();
				prep.close();
				con.close();
				return "Rs."+amount;
			}
		}
				//Insert the bill number, generate a random amount , insert the amount in db and return the amount
				double amount = insertBillNumberDetails(con,prep,userID,billType,billDetails.getBillNumber());
				prep.close();
				con.close();
				return "Rs."+amount;
		}
	}
			// Insert the billType, insert the billNumber, generate a random amount, insert the amount in the db and return the amount
			query = "insert into BillDetails values(?,?)";
			prep=con.prepareStatement(query);
			prep.setInt(1, userID);
			prep.setString(2,Type);
			prep.executeUpdate();
			double amount = insertBillNumberDetails(con,prep,userID,Type,billDetails.getBillNumber());
			prep.close();
			con.close();
			return "Rs."+amount;
	}
	
	@Override
	public String payBill(String billNo) throws SQLException {
		// TODO Auto-generated method stub
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		String query = "select balance from accounts where User_UserID = (select BillDetails_User_UserID from BillData where BillNumber = ?)";
		PreparedStatement prep = con.prepareStatement(query);
		prep.setString(1,billNo);
		ResultSet rs = prep.executeQuery();
		String balance = "";
		while(rs.next()) {
			balance = rs.getString(1);
		}
		double bal = getBalance(balance);
		query = "select amount from BillData where BillNumber = ?";
		prep= con.prepareStatement(query);
		prep.setString(1,billNo);
		rs = prep.executeQuery();
		double amount= 0.0;
		while(rs.next()) {
			amount = rs.getDouble(1);
		}
		if(bal<amount)
			return "Insufficient Balance in Account!";
		/* 1) Subtract amount from balance
		 * 2) Update amount to be 0 in Bill Data
		 * 3) Update balance = balance-amount in Accounts Table*/
		double remBalance = bal-amount;
		query = "update BillData set amount = 0.0 where BillNumber = ?";
		prep = con.prepareStatement(query);
		prep.setString(1, billNo);
		prep.executeUpdate();
		
		query = "update Accounts set balance = ? where User_UserID = (select BillDetails_User_UserID from BillData where BillNumber = ?)";
		prep = con.prepareStatement(query);
		String rem = "Rs."+remBalance;
		prep.setString(1, rem);
		prep.setString(2, billNo);
		prep.executeUpdate();
		prep.close();
		con.close();
		return "Bill Payed Successfully!";
	}
	
	//Transaction
	@Override
	public List<Transaction> getTransaction(String username) throws SQLException {
		// TODO Auto-generated method stub
		List<Transaction> transactionList = new ArrayList<Transaction>();
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		String query = "select * from Transaction where Accounts_AccountNo = (select AccountNo from Accounts where User_UserID = (select UserID from user where Username =?));";
		PreparedStatement prepSt = con.prepareStatement(query);
		prepSt.setString(1, username);
		ResultSet rs = prepSt.executeQuery();
		while(rs.next()) {
			transactionList.add(new Transaction(rs.getInt(1),rs.getString(2),rs.getDate(3),rs.getDouble(4)));
		}
		return transactionList;
	}

	@Override
	public String moneyTransfer(String username,MoneyTransfer transfer) throws SQLException {
		// TODO Auto-generated method stub
		Connection con = DriverManager.getConnection(url,dbuser,dbpassword);
		String query = "select Balance from Accounts where User_UserID = (select UserID from user where Username = ?)";
		PreparedStatement prepSt = con.prepareStatement(query);
		prepSt.setString(1,username);
		ResultSet rs= prepSt.executeQuery();
		double balance=0.0;
		while(rs.next()) {
			balance = getBalance(rs.getString(1));
		}
		System.out.println("TOTAL BALANCE : "+balance);
		if(balance<transfer.getAmount())
			return null;
		double remBalance = balance-transfer.getAmount();
		
		// Account number will be generated based on username already in the textbox in UI.
		//Make a utility that sets account number directly in the account number text box (disabled by default) as soon as the money transfer page opens.
		query = "update Accounts set balance = ? where AccountNo = ?";
		prepSt= con.prepareStatement(query);
		prepSt.setString(1,"Rs."+remBalance);
		prepSt.setInt(2, transfer.getAccountNo());
		prepSt.executeUpdate();
		
		query = "insert into MoneyTransfer values(?,?,?)";
		prepSt = con.prepareStatement(query);
		prepSt.setInt(1, transfer.getAccountNo());
		prepSt.setInt(2, transfer.getBeneficiaryAcNo());
		prepSt.setDouble(3, transfer.getAmount());
		prepSt.executeUpdate();
		return "OK";
	}
	
	public String randString() {
		   String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	        StringBuilder salt = new StringBuilder();
	        Random rnd = new Random();
	        while (salt.length() < 4) { // length of the random string.
	            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
	            salt.append(SALTCHARS.charAt(index));
	        }
	        String saltStr = salt.toString();
	        return saltStr;
	    }
	
	public double randAmount() {
		double rangeMin = 10;
		double rangeMax = 10000;
		Random r = new Random();
		double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		return randomValue;
	}
	
	public double insertBillNumberDetails(Connection con, PreparedStatement prep,int userID, String billType, String billNumber) throws SQLException {
		String query = "insert into BillData values (?,?,?,?)";
		prep = con.prepareStatement(query);
		prep.setInt(1, userID);
		prep.setString(2, billType);
		prep.setString(3,billNumber);
		double randAm = randAmount();
		prep.setDouble(4,randAm);
		prep.executeUpdate();
		return randAm;
	}
	
	public double getBalance(String balance) {
		double bal = Double.parseDouble(balance.substring((balance.indexOf('.')+1)));
		return bal;
	}

}

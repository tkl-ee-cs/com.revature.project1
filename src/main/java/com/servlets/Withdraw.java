package com.servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Withdraw
 */
@WebServlet("/withdraw")
public class Withdraw extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Withdraw() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//checks for session and returns back to main page if none found
		HttpSession session = request.getSession(false);
		if(session==null) {
			request.setAttribute("info", "...Redirecting, invalid session...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		
		//Get session version of user
		Customer customer = (Customer) session.getAttribute("user");
//		System.out.println(customer);
		if(customer.getType().equals("employee")) {
			request.setAttribute("warning", "...Permissions issue, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//Check if the account exists and matches records
		int ba = Integer.parseInt(request.getParameter("withdraw-account-select"));
		if(!hasBankAccount(customer,ba)) {
			request.setAttribute("warning", "...Invalid Bank Account, input a valid Bank Account or choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		//Checks withdraw amount against amount within the database as well as session
		float amount = Float.parseFloat(request.getParameter("withdraw-amount"));
		
		if((!hasFunds(customer,ba,amount)) & (get_account_balance(ba) < amount)) {
			request.setAttribute("warning", "...Insufficient funds, input into valid amount or choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		
		int doneWithdraw = set_account_w_withdraw(ba,amount);
		if(doneWithdraw > 0) {
			request.setAttribute("success", "...Funds have successfully withdrawn, please make your next transaction...");
		}else {
			request.setAttribute("warning", "...Unable to withdraw funds, please try again...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Check if the session to see if active account exists
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean hasBankAccount(Customer customer, int ba) {
//		System.out.println("Called hasBankAccount");
		ArrayList<BankAccount> accts = customer.getActiveAcntlist();

		for(BankAccount act:accts) {
			if(act.getAccount_id() == ba) {return true;}
		}
		return false;
	}	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Check session version of bank account for sufficient funds
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean hasFunds(Customer customer, int bankAccount, float request) {
//		System.out.println("Called hasFunds");
		boolean sessFlag = false;

		//check session version of bank account for sufficient funds
		ArrayList<BankAccount> accts = customer.getAcntlist();
		for(BankAccount act:accts) {
			if(act.getAccount_id() == bankAccount) {
				if(act.getBalance() >= request) {sessFlag = true;}
			}
		}
		return sessFlag;
	}	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET BALANCE FROM ACCOUNT ID 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected float get_account_balance(int account_id) {
		CallableStatement cstmt = null;
		float bal = 0;
		
//		System.out.println("Called get_account_balance()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_balance_w_actid(?,?)");
			cstmt.setInt(1,account_id);
			cstmt.setFloat(2,0);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.execute();
//			System.out.println("Balance for # "+ account_id +": " + cstmt.getInt(2)); 
			bal = cstmt.getFloat(2);
			cstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return bal;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//SET WITHDRAW AMOUNT TO BANK ACCOUNT USING ACCOUNT ID
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int set_account_w_withdraw(int id, float amount) {
		CallableStatement cstmt = null;
		int transaction_id = 0;
		
//		System.out.println("Called connectDB set_account_w_withdraw()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL withdraw(?,?,?)");
			cstmt.setInt(1,id);
			cstmt.setFloat(2,amount);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(3, 0);
			cstmt.execute();
			transaction_id = cstmt.getInt(3);
//			System.out.println("Transaction id: " + transaction_id); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return transaction_id;
	}
	
}

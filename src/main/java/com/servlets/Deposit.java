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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class Deposit
 */
@WebServlet("/deposit")
public class Deposit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Deposit.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Deposit() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter Deposit() doGet");

		//checks for session and returns back to main page if none found
		HttpSession session = request.getSession(false);
		if(session==null) {
			request.setAttribute("info", "...Redirecting, invalid session...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}

		//Get session version of user, sets attributes for request scope in welcome
		Customer customer = (Customer) session.getAttribute("user");
//		System.out.println(customer);
		if(customer.getType().equals("employee")) {
			request.setAttribute("warning", "...Permissions issue, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//Check if the account exists and matches records
		int ba = Integer.parseInt(request.getParameter("deposit-account-select"));
		if(!hasBankAccount(customer,ba)) {
			request.setAttribute("warning", "...Invalid Bank Account, input a valid Bank Account or choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		// gets the funds that will be deposited into account
		float depAmount = Float.parseFloat(request.getParameter("deposit-amount"));
		int doneDeposit = set_account_w_deposit(ba,depAmount);
		if(doneDeposit > 0) {
			request.setAttribute("success", "...Funds have successfully been deposited, please make your next transaction...");
		}else {
			request.setAttribute("warning", "...Unable to deposit funds, please try again...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter Deposit() doPost");
		doGet(request, response);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Check if the session to see if active account exists
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean hasBankAccount(Customer customer, int ba) {
        logger.info("Called hasBankAccount()");
		ArrayList<BankAccount> accts = customer.getActiveAcntlist();

		for(BankAccount act:accts) {
			if(act.getAccount_id() == ba) {return true;}
		}
		return false;
	}	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//SET DEPOSIT AMOUNT TO BANK ACCOUNT USING ACCOUNT ID
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int set_account_w_deposit(int id, float amount) {
		CallableStatement cstmt = null;
		int transaction_id = 0;
		
        logger.info("Called set_account_w_deposit()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL deposit(?,?,?)");
			cstmt.setInt(1,id);
			cstmt.setFloat(2,amount);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(3, 0);
			cstmt.execute();
			transaction_id = cstmt.getInt(3);
	        logger.info("Transaction id: " + transaction_id); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return transaction_id;
	}
}

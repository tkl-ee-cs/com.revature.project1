package com.servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * Servlet implementation class Accept
 */
@WebServlet("/accept")
public class Accept extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Accept.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Accept() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter Accept() doGet");
        
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
			request.setAttribute("warning", "...Permission settings issue, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//Check transaction source account has sufficient funds
		if(request.getParameter("accept-transfer-select")==null) {
			
		}
		String transId = request.getParameter("accept-transfer-select");
		int tid;
		ArrayList<Transaction> pend;
		Transaction tx = null;
		if(transId == null) { //return if there are no pending transfers
			request.setAttribute("warning", "...No pending transfers, please choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}else {//else get all the transaction details stored in session
			pend = customer.getPendlist();
			tid = Integer.parseInt(transId);
			for(Transaction t:pend) {
				if(t.getTransaction_id() == tid) {tx = t;}
			}
		}
		//Decline the Request, before checking for sufficient funds since it does not matter
		String choice = request.getParameter("accept-decline-select");
		if(choice.equals("decline")) {
			reject_transfer(tid);
			request.setAttribute("info", "...Declined posted transfer request, please make your next selection...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}else if(choice.equals("accept")) {//accept if that is the choice, or if something else goes wrong, send warning message
			//check for sufficient funds, and decline if not enough funds
			if((!hasFunds(customer,tx.getSrc(),tx.amount)) & (get_account_balance(tx.getSrc()) < tx.getAmount())) {
				request.setAttribute("warning", "...Insufficient funds, unable to process the request...");
				request.getRequestDispatcher("welcome").forward(request, response);
				return;
			}else { //accept transfer as there are enough funds 
				accept_transfer(tid);
				request.setAttribute("success", "...Accepted posted transfer request, please make your next selection...");
			}
		}else{//catch for any other issue
			request.setAttribute("warning", "...Unable to post transfer, please try again...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter Accept() doPost");
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Check session version of bank account for sufficient funds
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean hasFunds(Customer customer, int bankAccount, float request) {
        logger.info("Called hasFunds");
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
		
        logger.info("Called get_account_balance()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_balance_w_actid(?,?)");
			cstmt.setInt(1,account_id);
			cstmt.setFloat(2,0);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.execute();
	        logger.info("Balance for # "+ account_id +": " + cstmt.getInt(2)); 
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
	//ACCEPT TRANSFER REQUEST 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void accept_transfer(int tid) {
		CallableStatement cstmt = null;
		
        logger.info("Called accept_transfer()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL finalize_transfer(?)");
			cstmt.setInt(1,tid);
			cstmt.execute();
	        logger.info("Transaction successfully accepted..."); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//REJECT TRANSFER REQUEST 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void reject_transfer(int tid) {
		CallableStatement cstmt = null;
		
        logger.info("Called reject_transfer()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL reject_transfer(?)");
			cstmt.setInt(1,tid);
			cstmt.execute();
	        logger.info("Transaction rejected..."); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
	}
}

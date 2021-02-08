package com.servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Servlet implementation class approveAccount
 */
@WebServlet("/approve-account")
public class approveAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Login.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public approveAccount() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter approveAccount() doGet");
		HttpSession session = request.getSession(false);
		if(session==null) {
			request.setAttribute("info", "...Redirecting, invalid session...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		
		try{//Get session version of user, check if employee
		Employee employee = (Employee) session.getAttribute("user");
//		System.out.println(employee);
		}catch(ClassCastException e) {
			session.invalidate();
			request.setAttribute("danger", "...Invalid permission, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		
		//check if there is a selection
		String pendAccount = request.getParameter("approve-bank-select");
		if(pendAccount==null) {
			request.setAttribute("warning", "...Invalid selection, please make anoter selection...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		//check if valid account to review
		int forReview = Integer.parseInt(pendAccount);
		BankAccount ba = get_pending_account(forReview);
		if((forReview<1) | (ba==null)) {
			request.setAttribute("warning", "...Pending account is not available, please make another selection...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		
		String action = request.getParameter("approve-reject-bank");
		int doneAction = 0;
		if(action.equals("rjct")) {//reject the account
			doneAction = accept_reject_account(forReview,"rejected");
			request.setAttribute("info", "...Bank Account application has been rejected...");
		}else if(action.equals("aprv")) {//approve the account
			doneAction = accept_reject_account(forReview,"active");
			request.setAttribute("success", "...Bank Account application has been approved, customer now has access to account...");
		}else {//if selection was neither aprv/rjct
			request.setAttribute("warning", "...An Issue occurred while trying to process application, please try again later...");
		}
		
		if(doneAction < 1) { //if unable to properly change status of account to active/rejected
			request.setAttribute("warning", "...Unable to process application at this time, please make another selection...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter approveAccount() doPost");
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET PENDING ACCOUNT EXISTANCE
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static BankAccount get_pending_account(int id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String stmt;
		BankAccount ba = null;

        logger.info("Called get_pending_account()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT a.account_id, a.balance, a.status "
					+ "FROM accounts a WHERE a.status = 'pending' AND a.account_id = ?";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setInt(1,id);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
	            logger.info("ResultSet row: "+ rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3));
	        	ba = new BankAccount(rs.getInt(1),rs.getFloat(2),rs.getString(3));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		return ba;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//ACCEPT/REJECT BANK ACCOUNT APPLICATION 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int accept_reject_account(int account_id,String action) {
		CallableStatement cstmt = null;
		int uid = 0;

        logger.info("Called accept_reject_account()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL approve_reject_application(?,?,?)");
			cstmt.setInt(1,account_id);
			cstmt.setString(2,action);
			cstmt.setInt(3,0);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.execute();
			uid = cstmt.getInt(3);
	        logger.info("User id: " + uid); 
			cstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return uid;
	}
}

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
 * Servlet implementation class approveUser
 */
@WebServlet("/approve-user")
public class approveUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Login.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public approveUser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter approveUser() doGet");
        
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
		String pendCustomer = request.getParameter("approve-user-account");
		if(pendCustomer==null) {
			request.setAttribute("warning", "...Invalid selection, please make anoter selection...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		//check if valid account to review
		int forReview = Integer.parseInt(pendCustomer);
		Customer c = get_customer_w_id(forReview);
		if((forReview<1) | (c==null)) {
			request.setAttribute("warning", "...User request is not available, please make another selection...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		
		String action = request.getParameter("approve-reject-select");
		int doneAction = 0;
		if(action.equals("rjct")) {//reject the account
			doneAction = accept_reject_customer(c.getUsername(),"rejected");
			request.setAttribute("info", "...User request has been rejected...");
		}else if(action.equals("aprv")) {//approve the account
			doneAction = accept_reject_customer(c.getUsername(),"active");
			request.setAttribute("success", "...User request has been approved, the customer may now login at this time...");
		}else {//if selection was neither aprv/rjct
			request.setAttribute("warning", "...An Issue occurred while trying to process request, please try again later...");
		}
		
		if(doneAction < 1) { //if unable to properly change status of account to active/rejected
			request.setAttribute("warning", "...Unable to process user request at this time, please make another selection...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter approveUser() doPost");
		doGet(request, response);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER DETAILS FROM ID
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected Customer get_customer_w_id(int id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Customer usr = null;
		String stmt;
		
        logger.info("Called get_customer_w_id()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT user_id, user_name, account_type FROM users WHERE "
					+ "status = 'pending' AND account_type = 'customer';";
			pstmt = connection.prepareStatement(stmt);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
	            logger.info("ResultSet row: " +rs.getInt(1)+" "+rs.getString(2));
	        	usr = new Customer(rs.getInt(1),rs.getString(2),"pending");
	        }
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		return usr;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//ACCEPT/REJECT USER ACCOUNT REQUEST 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static int accept_reject_customer(String user_name,String action) {
		CallableStatement cstmt = null;
		int uid = 0;
	
        logger.info("Called accept_reject_customer()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			cstmt = connection.prepareCall("CALL approve_reject_customer(?,?,?)");
			cstmt.setString(1,user_name);
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

package com.servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * Servlet implementation class Apply
 */
@WebServlet("/apply")
public class Apply extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Apply.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Apply() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter Apply() doGet");
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
		
		float balance = Float.parseFloat(request.getParameter("starting-balance"));
		int pendingBA = set_account_w_user(customer.getUsername(), balance);
		if(pendingBA > 0) {
			request.setAttribute("success", "...Request for bank account has been successfully submitted for review...");
		}else {
			request.setAttribute("warning", "...Unable to submit a request at this time, please try again or make another selection...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter Apply() doPost");
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//SET ACCOUNT USING USERNAME
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int set_account_w_user(String username, float balance) {
		int aid = -1;
		CallableStatement cstmt = null;
		
        logger.info("Called set_account_w_user()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL set_account_w_name(?,?,?)");
			cstmt.setFloat(1,balance);
			cstmt.setString(2,username);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(3, 0);
			cstmt.execute();
			aid = cstmt.getInt(3);
	        logger.info("ID of account is : " + aid);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return aid;
	}
	
}

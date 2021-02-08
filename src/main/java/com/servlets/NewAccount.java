package com.servlets;

import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;

//import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class NewAccount
 */
@WebServlet("/newaccount")
public class NewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(NewAccount.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewAccount() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter NewAccount() doGet");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter NewAccount() doPost");
		response.setContentType("text/html");
		
		String name = request.getParameter("new-name");
		String password = request.getParameter("new-pw");
		float balance = Float.parseFloat(request.getParameter("new-bal"));
		int valSubmit = -1;

		String user_type = get_type_w_name(name);
		if(user_type!=null) {
	        logger.info("This is " + name + " with password.");
			request.setAttribute("warning", "...Username already exists, please try again...");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}else {
			valSubmit = set_new_user(name,password,balance);
			if(valSubmit > 0) {
			request.setAttribute("success", "Customer account request has been submitted for review. ");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			}else {
			request.setAttribute("danger", "...Error occurred, please try again...");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER TYPE BY USERNAME
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected String get_type_w_name(String username) {
		String user_type = null;
		CallableStatement cstmt = null;
		
        logger.info("Called_type_w_name()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_type_w_name(?,?)");
			cstmt.setString(1,username);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			cstmt.setString(2, null);
			cstmt.execute();
			user_type = cstmt.getString(2);
	        logger.info("Type of account is : "+ user_type);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return user_type;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//SET/CREATE A NEW USER (PENDING) AND CORRESPONDING BANK ACCOUNT (PENDING)
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int set_new_user(String username,String password, float balance) {
		int uid = -1;
		CallableStatement cstmt = null;
		
        logger.info("Called set_new_user()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){

			cstmt = connection.prepareCall("CALL set_account_wo_user(?,?,?,?,?,?)");
			cstmt.setFloat(1,balance);
			cstmt.setString(2,username);
			cstmt.setString(3,password);
			cstmt.setString(4,"customer");
			cstmt.setString(5,"pending");
			cstmt.registerOutParameter(6, java.sql.Types.INTEGER);
			cstmt.setInt(6, 0);
			cstmt.execute();
			uid = cstmt.getInt(6);
	        logger.info("ID of account is : " + uid);
			return uid;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return uid;
	}
	
}
package com.servlets;

import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Login.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter Login() doGet");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter Login() doPost");
		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		
		//get field parameters and gets id/status/type from database
		String name = request.getParameter("login-name");
		String password = request.getParameter("login-pw");
		

		int valId = get_id_w_name_pw(name,password);
		String userStat =  get_status_w_id(valId);
		String userType = get_type_w_name(name);
		
        logger.info("This is " + name + " with a password");
		if((valId <= 0)) { 
			request.setAttribute("warning", "...Username/Password does not match with our records...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}else if((valId > 0)&(userStat.equals("active"))) {
			//sets the attributes id/name/status/type into request scope, leaves user/account creation to welcome servlet
			request.setAttribute("id", valId);
			request.setAttribute("name", name);
			request.setAttribute("type", userType);
			request.setAttribute("status", userStat);
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}else if((userStat.equals("rejected"))|(userStat.equals("pending"))) {
			request.setAttribute("danger", "...Login attempt from unapproved account...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}else {
			request.setAttribute("warning", "...Issue occurred, please try again...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER ID WITH USERNAME AND PASSWORD
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int get_id_w_name_pw(String name, String password) {
		int uid = 0;
		CallableStatement cstmt = null;
		
		try { //Loading the Driver, precedes first call to the database
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
        logger.info("Called get_id_w_name_pw()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_id_w_name_pw(?,?,?)");
			cstmt.setString(1,name);
			cstmt.setString(2,password);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(3, 0);
			cstmt.execute();
			uid = cstmt.getInt(3);
	        logger.info("ID is : "+ uid + ", taking in " + name + ", and password");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}

		return uid;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER STATUS WITH USER ID
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected String get_status_w_id(int id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String stmt;
		String status = null; 
			
        logger.info("Called get_status_w_id");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT u.status FROM users u WHERE u.user_id = ?";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setInt(1,id);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
	            logger.info(rs.getString(1));
	        	status = rs.getString(1);
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		
		return status;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER TYPE BY USERNAME
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static String get_type_w_name(String username) {
		String act_type = null;
		CallableStatement cstmt = null;
		
        logger.info("Called get_type_w_name()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_type_w_name(?,?)");
			cstmt.setString(1,username);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			cstmt.setString(2, null);
			cstmt.execute();
			act_type = cstmt.getString(2);
	        logger.info("Type of account is : "+ act_type);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return act_type;
	}
}

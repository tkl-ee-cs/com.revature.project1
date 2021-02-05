package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		
		String name = request.getParameter("login-name");
		String password = request.getParameter("login-pw");
//		System.out.println("This is " + name + " " + password);
		int valID = get_id_w_name_pw(name,password);
		String valUser = get_status_w_id(valID);
		
		if((valID > 0)&(valUser.equals("active"))) {
//			System.out.println("This is " + name + " " + password);
//clear all session data
			session.setAttribute("id", valID);
			session.setAttribute("name", name);
				
			RequestDispatcher rd = request.getRequestDispatcher("welcome");
			rd.forward(request, response);
		}else if((valUser.equals("rejected"))|(valUser.equals("pending"))) {
//invalidate session
			request.setAttribute("danger", "...Login attempt from unapproved customer account...");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		else {
//invalidate session
			request.setAttribute("warning", "...Username or Password does not match...");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
	}

	protected int get_id_w_name_pw(String name, String password) {
		int uid = 0;
		CallableStatement cstmt = null;
		
//		System.out.println("Called get_id_w_name_pw()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_id_w_name_pw(?,?,?)");
			cstmt.setString(1,name);
			cstmt.setString(2,password);
			cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			cstmt.setInt(3, 0);
			cstmt.execute();
			uid = cstmt.getInt(3);
//			System.out.println("ID is : "+ uid + ", taking in" + name + ", " + password);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}

		return uid;
	}
	

	protected String get_status_w_id(int id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String stmt;
		String status = null; 
		
//		System.out.println("Called get_status_w_id");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT u.status FROM users u WHERE u.user_id = ?";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setInt(1,id);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//	    	System.out.println(rs.getString(1));
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
	
}

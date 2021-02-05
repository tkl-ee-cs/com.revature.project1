package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
		
		HttpSession session=request.getSession();
		String name = (String) session.getAttribute("name");
		String user = get_type_w_name(name);
		ArrayList<BankAccount> accts = get_accounts_w_name(name);
		List<BankAccount> list = new ArrayList<BankAccount>();
		
		if(accts.size() > 0) {
			for(BankAccount ba:accts) {
				if(ba.getStatus().equals("active")) {
					list.add(new BankAccount (ba.getAccount_id(),ba.getBalance(),ba.getStatus()));
				}
			}
		}
		
		if(user.equals("customer")) {
			session.setAttribute("type", "customer");
			request.setAttribute("uname", name);
			if(list.size() > 0) {
			request.setAttribute("tname", "List of Active Bank Accounts");
			request.setAttribute("headone", "Account ID");
			request.setAttribute("headtwo", "Balance");
			request.setAttribute("tlist", list.toArray());
			}
			request.getRequestDispatcher("/customer-menu.jsp").forward(request, response);
		}else if(user.equals("employee")) {
			session.setAttribute("type", "employee");
			request.setAttribute("uname", name);
			request.getRequestDispatcher("/employee-menu.jsp").forward(request, response);		
		}else {
//invalidate session
			request.setAttribute("error", "...Issue occurred, please log in again...");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	protected String get_type_w_name(String username) {
		String act_type = null;
		CallableStatement cstmt = null;
		
//		System.out.println("Called connectDB get_type_w_name()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_type_w_name(?,?)");
			cstmt.setString(1,username);
			cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
			cstmt.setString(2, null);
			cstmt.execute();
			act_type = cstmt.getString(2);
//			System.out.println("Type of account is : "+ act_type);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return act_type;
	}
		
	protected ArrayList<BankAccount> get_accounts_w_name(String username) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<BankAccount> accts = new ArrayList<BankAccount>();

//		System.out.println("Called get_accounts_w_name()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			String stmt = "	SELECT a.account_id,a.balance,a.status"
					+ "	FROM user_accounts ua RIGHT JOIN accounts a ON ua.account_id = a.account_id"
					+ "	INNER JOIN users u ON ua.user_id = u.user_id"
					+ "	WHERE u.user_name = ?;";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setString(1,username);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//	    		System.out.println("ResultSet row: "+ rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3));
				accts.add(new BankAccount(rs.getInt(1),rs.getInt(2),rs.getString(3)));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		return accts;
	}
}

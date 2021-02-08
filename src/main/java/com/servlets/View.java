package com.servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class View
 */
@WebServlet("/view")
public class View extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public View() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());

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
		
		
		
		//check if user is customer or employee
		String user = request.getParameter("view-user-account");
		User account = get_user_w_name(user);
		Customer cust = null;
		Employee empl = null;
		if(account==null) {
			request.setAttribute("warning", "...Account with username does not exist, please try again...");
			request.getRequestDispatcher("welcome").forward(request, response);
		}else if (account.getType().equals("customer")) {
			cust = (Customer) account;
			request.setAttribute("username", cust.getUsername());
			request.setAttribute("userid", cust.getUser_id());
			request.setAttribute("type", cust.getType());
			request.setAttribute("status", cust.getStatus());
			
			ArrayList<BankAccount> accts = get_accounts_w_name(cust.getUsername());
			if(accts != null) {request.setAttribute("accts", accts);}
			
			ArrayList<Transaction> list = get_transaction_pending(cust.getUser_id());
			if(list != null) {request.setAttribute("pends", list);}
			request.getRequestDispatcher("/output-view.jsp").forward(request, response);
		}else if (account.getType().equals("employee")) {
			empl = (Employee) account;
			request.setAttribute("username", empl.getUsername());
			request.setAttribute("userid", empl.getUser_id());
			request.setAttribute("type", empl.getType());
			request.setAttribute("status", empl.getStatus());
			request.getRequestDispatcher("/output-view.jsp").forward(request, response);
		}else {
			request.setAttribute("warning", "...An issue occurred, please try again...");
			request.getRequestDispatcher("welcome").forward(request, response);
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER ACCOUNT FROM USERNAME 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected User get_user_w_name(String user_name) {
		CallableStatement cstmt = null;
		User ua = null;
		
//		System.out.println("Called get_user_w_name()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_user_w_name(?,?,?,?)");
			cstmt.setString(1,user_name);
			cstmt.setInt(2,0);
			cstmt.setString(3,null);
			cstmt.setString(4,null);
			cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
			cstmt.execute();
//			System.out.println("User id: " + cstmt.getInt(2) + ": " + cstmt.getString(3) + " " + cstmt.getString(4)); 
			if(cstmt.getString(4)==null) {
				return null;
			}else if(cstmt.getString(4).equals("customer")) {
				ua = new Customer(cstmt.getInt(2),user_name,cstmt.getString(3));
			}else if(cstmt.getString(4).equals("employee")) {
				ua = new Employee(cstmt.getInt(2),user_name);
			}
			cstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return ua;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET BANK ACCOUNTS USING USERNAME
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
				accts.add(new BankAccount(rs.getInt(1),rs.getFloat(2),rs.getString(3)));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		return accts;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET LIST OF PENDING TRANSACTIONS FOR USER
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Transaction> get_transaction_pending(int userid) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Transaction> tids = new ArrayList<Transaction>();
		String stmt;
		
//		System.out.println("Called get_transaction_pending()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT t.transaction_id, t.src_account,t.dest_account, t.status, t.amount, t.act, t.created_at "
					+ "FROM transaction_logs t INNER JOIN user_accounts ua ON t.dest_account = ua.account_id "
					+ "WHERE t.status = 'pending' AND ua.user_id=?";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setInt(1,userid);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//	    		System.out.println("ResultSet row:" + rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)
//				+" "+rs.getString(4)+" "+rs.getFloat(5)+" "+rs.getString(6)+" "+rs.getTimestamp(7));
	        	tids.add(new Transaction(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),
	        			rs.getFloat(5),rs.getString(6),rs.getTimestamp(7)));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		
		return tids;
	}
	
}

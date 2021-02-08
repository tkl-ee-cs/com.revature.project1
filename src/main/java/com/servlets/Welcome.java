package com.servlets;

import java.io.IOException;
//import java.io.PrintWriter;
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
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
		
		HttpSession session = request.getSession(false);
		int valId;
		String name;
		String usrtype; 
		String usrStat;
		
		//get session and the request attributes
		if(session == null) { //check if no session exists
//			System.out.println("Session does not exist.");
			session = request.getSession(); //create session, ensure there's a session to work with
			valId = (int) request.getAttribute("id"); //set variables based on passed login attributes
			name = (String) request.getAttribute("name");
			usrtype = (String) request.getAttribute("type");
			usrStat = (String) request.getAttribute("status");
		}else {//session exists so just set variables based on session
//			System.out.println("Session exists.");
			User user = (User) session.getAttribute("user");
			if(user==null) { // check for case that session exists but no user exists
				request.setAttribute("danger", "...An issue occurred while loading page, please log in again...");
				request.getRequestDispatcher("").forward(request, response);
				return;
				}
			valId = user.getUser_id();
			name = user.getUsername();
			usrtype = user.getType();
			usrStat = user.getStatus();
		}
		
		if(usrtype.equals("customer")) {
			//user is customer, create user account and related bank accounts
			//includes in session and sets attributes for jsp of customer menu
			Customer userAccount = new Customer(valId,name,usrStat);
			userAccount.setAcntlist(get_accounts_w_name(name));
			userAccount.setPendlist(get_transaction_pending(valId));
			session.setAttribute("user", userAccount);
			List<BankAccount> acts = userAccount.getAcntlist();
			List<BankAccount> list = userAccount.getActiveAcntlist();
			List<Transaction> pend = userAccount.getPendlist();
			request.setAttribute("uname", name);
			request.setAttribute("headone", "Account ID");
			request.setAttribute("headtwo", "Status");
			request.setAttribute("headthree", "Balance");
			if(list.size() > 0) {//checks size of list, will not display table if empty
				request.setAttribute("titlename", "List of Active Bank Accounts");
				request.setAttribute("tlist", list.toArray());
			}else if(acts.size() > 0) {
				request.setAttribute("titlename", "List of Bank Accounts");
				request.setAttribute("tlist", acts.toArray());
			}
			if(acts.size() > 0) {//checks size of acts, will not display table if empty
				request.setAttribute("alist", acts.toArray());
			}
			if(pend.size()>0) {//checks size of pending transfers, will not set attributes if empty
				request.setAttribute("plist", pend.toArray());
				request.setAttribute("from", "Transfer request from Account #");
				request.setAttribute("to", "to your Account #");
				request.setAttribute("pay", "for the amount of ");
			}
			request.getRequestDispatcher("/customer-menu.jsp").forward(request, response);
			return;
		}else if(usrtype.equals("employee")) {
			//user is employee, create user account
			//includes in session and sets attributes for jsp of customer menu
			Employee userAccount = new Employee(valId,name);
			session.setAttribute("user", userAccount);
			request.setAttribute("uname", userAccount.getUsername());
			
			List<Customer> usrs = get_pending_users();
			if(usrs.size() > 0) {request.setAttribute("clist", usrs.toArray()); }
			
			List<BankAccount> acts = get_pending_accounts();
			if(acts.size() > 0) {request.setAttribute("ulist", acts); }

			request.getRequestDispatcher("/employee-menu.jsp").forward(request, response);		
			return;
		}else {
			//login was had issues, invalidate session and start over from login
			session.invalidate();
			request.setAttribute("error", "...Issue occurred, please log in again...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET USER TYPE WITH USERNAME
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET LIST OF PENDING CUSTOMERS
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected ArrayList<Customer> get_pending_users() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Customer> usrs = new ArrayList<Customer>();
		String stmt;
		
//		System.out.println("Called get_pending_users()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT user_id, user_name FROM users WHERE "
					+ "status = 'pending' AND account_type = 'customer';";
			pstmt = connection.prepareStatement(stmt);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//	    		System.out.println("ResultSet row: " +rs.getInt(1)+" "+rs.getString(2));
	        	usrs.add(new Customer(rs.getInt(1),rs.getString(2),"pending"));
	        }
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		return usrs;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET PENDING ACCOUNTS 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<BankAccount> get_pending_accounts() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<BankAccount> accts = new ArrayList<BankAccount>();
		String stmt;
		
//		System.out.println("Called get_pending_accounts()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT DISTINCT a.account_id, a.balance, a.status, u.user_name "
					+ "FROM accounts a INNER JOIN user_accounts ua ON a.account_id=ua.account_id "
					+ "INNER JOIN users u ON ua.user_id=u.user_id "
					+ "WHERE a.status='pending' ORDER BY a.account_id";
			pstmt = connection.prepareStatement(stmt);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//	    		System.out.println("ResultSet row: "+ rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3) + " " + rs.getString(4));
				accts.add(new BankAccount(rs.getInt(1),rs.getFloat(2),rs.getString(3),rs.getString(4)));
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

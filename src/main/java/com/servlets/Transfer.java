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
 * Servlet implementation class Transfer
 */
@WebServlet("/transfer")
public class Transfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Transfer() {
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
		//Get session version of user
		Customer customer = (Customer) session.getAttribute("user");
//		System.out.println(customer);
		if(customer.getType().equals("employee")) {
			request.setAttribute("warning", "...Permissions issue, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//Check if the account exists and matches records
		int baDes = Integer.parseInt(request.getParameter("transfer-dest-account"));
		int baSrc = Integer.parseInt(request.getParameter("post-transfer-select"));
		BankAccount postDes = get_account_existance(baDes);
		BankAccount postSrc = get_account_existance(baSrc);
		if((postDes == null)|(postSrc == null)) {
			request.setAttribute("warning", "...Invalid Bank Account, input into valid Bank Account or choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		//Checks withdraw amount against amount within the database as well as session
		float amount = Float.parseFloat(request.getParameter("transfer-amount"));
		
		if((!hasFunds(customer,baSrc,amount)) & (get_account_balance(baSrc) < amount)) {
			request.setAttribute("warning", "...Insufficient funds, input a valid amount or choose another action...");
			request.getRequestDispatcher("welcome").forward(request, response);
			return;
		}
		
		int doneTransfer = post_transfer(baSrc,baDes,amount);
		if(doneTransfer > 0) {
			request.setAttribute("success", "...Transfer request was successfully posted, please make your next selection...");
		}else {
			request.setAttribute("warning", "...Unable to post transfer, please try again...");
		}
		request.getRequestDispatcher("welcome").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//POST TRANSFER REQUEST 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected int post_transfer(int src,int dst,float amount) {
		CallableStatement cstmt = null;
		int transaction_id = 0;
		
//		System.out.println("Called connectDB post_transfer()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL post_transfer(?,?,?,?)");
			cstmt.setInt(1,src);
			cstmt.setInt(2,dst);
			cstmt.setFloat(3,amount);
			cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
			cstmt.setInt(4, 0);
			cstmt.execute();
			transaction_id = cstmt.getInt(4);
//			System.out.println("Transaction id: " + transaction_id); 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return transaction_id;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET ACTIVE ACCOUNT EXISTANCE
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected BankAccount get_account_existance(int id) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String stmt;
		BankAccount ba = null;
		
//		System.out.println("Called connectDB get_account_existance()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT a.account_id, a.balance, a.status "
					+ "FROM accounts a WHERE a.status = 'active' AND account_id = ?";
			pstmt = connection.prepareStatement(stmt);
			pstmt.setInt(1,id);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
//				System.out.println("ResultSet row: "+ rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3));
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
	//Check session version of bank account for sufficient funds
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected boolean hasFunds(Customer customer, int bankAccount, float request) {
//		System.out.println("Called hasFunds");
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
		
//		System.out.println("Called get_account_balance()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			cstmt = connection.prepareCall("CALL get_balance_w_actid(?,?)");
			cstmt.setInt(1,account_id);
			cstmt.setFloat(2,0);
			cstmt.registerOutParameter(2, java.sql.Types.REAL);
			cstmt.execute();
//			System.out.println("Balance for # "+ account_id +": " + cstmt.getInt(2)); 
			bal = cstmt.getFloat(2);
			cstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    try { if (cstmt != null) cstmt.close(); } catch (Exception e) {};
		}
		return bal;
	}
}

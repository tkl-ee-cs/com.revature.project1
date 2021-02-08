package com.servlets;

import java.io.IOException;
//import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Servlet implementation class Logs
 */
@WebServlet("/logs")
public class Logs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LogManager.getLogger(Logs.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logs() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
        logger.info("Enter Logs() doGet");
        
		HttpSession session = request.getSession(false);
		if(session==null) {
			request.setAttribute("info", "...Redirecting, invalid session...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		//Get session version of user
		try{
		Employee employee = (Employee) session.getAttribute("user");
		//	System.out.println(employee);
		}catch(ClassCastException e) {
			session.invalidate();
			request.setAttribute("danger", "...Invalid permission, redirecting to login...");
			request.getRequestDispatcher("").forward(request, response);
			return;
		}
		
		ArrayList<Transaction> txLogs = get_transaction_logs();
		if(txLogs!=null) {
			request.setAttribute("logs", txLogs.toArray());
			request.getRequestDispatcher("/output-transactions.jsp").forward(request, response);
		}else {
			request.setAttribute("warning", "...Issue occured with pulling the transaction, please try again later...");
			request.getRequestDispatcher("welcome").forward(request, response);
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Enter Logs() doPost");
		doGet(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//GET ALL TRANSACTION LOGS
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected ArrayList<Transaction> get_transaction_logs() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Transaction> logs = new ArrayList<Transaction>();
		String stmt;
		
        logger.info("Called get_transaction_logs()");
		try(Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/bank","postgres","postgrespassword");){
			
			stmt = "SELECT transaction_id, src_account,dest_account, status, amount, act, created_at, updated_at "
					+ "FROM transaction_logs WHERE dest_account IS NOT NULL";
			pstmt = connection.prepareStatement(stmt);
			rs = pstmt.executeQuery();
	        while (rs.next()) {
	            logger.info("ResultSet row: "+rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)
				+" "+rs.getString(4)+" "+rs.getInt(5)+" "+rs.getString(6)+" "+rs.getTimestamp(7)+" "+rs.getTimestamp(8));
	        	logs.add(new Transaction(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),
	        			rs.getFloat(5),rs.getString(6),rs.getTimestamp(7),rs.getTimestamp(8)));
	        }
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (pstmt != null) pstmt.close(); } catch (Exception e) {};
		}
		
		return logs;
	}
	
}
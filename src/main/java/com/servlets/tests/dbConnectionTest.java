package com.servlets.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class dbConnectionTest {

	   @Test
	    void accessDatabase()
	    {
	    	try {
				Connection conn = DriverManager.getConnection(
						"jdbc:postgresql://localhost/bank","postgres","postgrespassword");
				assertTrue(conn.isValid(1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	
	   @Test
	    void dataInDatabase()
	    {
	    	try {
				Connection conn = DriverManager.getConnection(
						"jdbc:postgresql://localhost/bank","postgres","postgrespassword");
				assertTrue(conn.isValid(1));
				String stmt = "SELECT transaction_id, src_account,dest_account, status, amount, act, created_at "
						+ "FROM transaction_logs WHERE dest_account IS NOT NULL";
				PreparedStatement pstmt = conn.prepareStatement(stmt);
				ResultSet rs = pstmt.executeQuery();
				assertTrue(rs.next());
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	   
	   @Test
	    void sendtoDatabase()
	    {
	    	try {
				Connection conn = DriverManager.getConnection(
						"jdbc:postgresql://localhost/bank","postgres","postgrespassword");
				assertTrue(conn.isValid(1));
				String stmt = "SELECT transaction_id, src_account,dest_account, status, amount, act, created_at "
						+ "FROM transaction_logs WHERE dest_account IS NOT NULL";
				PreparedStatement pstmt = conn.prepareStatement(stmt);
				ResultSet rs = pstmt.executeQuery();
				assertTrue(rs.next());
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
}

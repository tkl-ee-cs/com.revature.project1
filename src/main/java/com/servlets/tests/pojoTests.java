package com.servlets.tests;

import org.junit.jupiter.api.Test;

import com.servlets.BankAccount;
import com.servlets.Customer;
import com.servlets.Employee;
import com.servlets.Transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Date;

public class pojoTests {

	   @Test
	    void customerTest() {
		   Customer customer = new Customer(1, "test", "active");
		   assertEquals(1,customer.getUser_id());
		   assertEquals("test",customer.getUsername());
		   assertEquals("active",customer.getStatus());
		   assertEquals("customer",customer.getType());
	    }
	   
	   @Test
	    void employeeTest() {
		   Employee employee = new Employee(1, "test");
		   assertEquals(1,employee.getUser_id());
		   assertEquals("test",employee.getUsername());
		   assertEquals("active",employee.getStatus());
		   assertEquals("employee",employee.getType());
	    }
	   
	   @Test
	    void bankAccountTest() {
		   BankAccount ba = new BankAccount(1, 1.0f, "active");
		   assertEquals(1,ba.getAccount_id());
		   assertEquals(1.0f,ba.getBalance());
		   assertEquals("active",ba.getStatus());
		   assertEquals("",ba.getUser());
		   BankAccount ba2 = new BankAccount(1, 1.0f, "pending","tester");
		   assertEquals(1,ba2.getAccount_id());
		   assertEquals(1.0f,ba2.getBalance());
		   assertEquals("pending",ba2.getStatus());
		   assertEquals("tester",ba2.getUser());
	    }
	   
	   @Test
	    void transactionTest() {
		   Timestamp ts1 = new Timestamp(new Date().getTime());
		   Timestamp ts2 = new Timestamp(new Date().getTime());
		   Transaction tx = new Transaction(1, 2, 3, "pending", 1.0f, "transfer",
				   ts1);
		   assertEquals(1,tx.getTransaction_id());
		   assertEquals(1.0f,tx.getAmount());
		   assertEquals(2,tx.getSrc());
		   assertEquals(3,tx.getDst());
		   assertEquals("pending",tx.getStatus());
		   assertEquals("transfer",tx.getAction());
		   assertEquals("pending",tx.getStatus());
		   assertEquals("transfer",tx.getAction());
		   assertEquals(ts1,tx.getCreated());
		   tx.setUpdated(ts2);
		   assertEquals(1,tx.getTransaction_id());
		   assertEquals(1.0f,tx.getAmount());
		   assertEquals(2,tx.getSrc());
		   assertEquals(3,tx.getDst());
		   assertEquals("pending",tx.getStatus());
		   assertEquals("transfer",tx.getAction());
		   assertEquals("pending",tx.getStatus());
		   assertEquals("transfer",tx.getAction());
		   assertEquals(ts1,tx.getCreated());
		   assertEquals(ts2,tx.getUpdated());
	    }
	
}

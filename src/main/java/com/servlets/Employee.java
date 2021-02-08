package com.servlets;

public class Employee implements User{

	int user_id;
	String user_name;
	String status;
	String type;
	
	public Employee(int user_id, String user_name) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.status = "active";
		this.type = "employee";
	}
	
	public int getUser_id() {return user_id;}
	public String getUsername() {return user_name;}
	public String getStatus() {return status;}
	public void setStatus(String status) {this.status = status;}
	public String getType() {return type;}

	@Override
	public String toString() {
		return "Employee [user_id=" + user_id + ", user_name=" + user_name + ", status=" + status + ", type=" + type
				+ "]";
	}
	
}

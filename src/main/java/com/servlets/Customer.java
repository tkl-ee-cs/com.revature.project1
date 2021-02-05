package com.servlets;

import java.util.ArrayList;

public class Customer implements User{

	int user_id;
	String user_name;
	String status;
	String type;
	ArrayList<BankAccount> acntlist;
	
	public Customer(int user_id, String user_name, String status) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.status = status;
		this.type = "customer";
	}

	public int getUser_id() {return user_id;}
	public String getUser_name() {return user_name;}
	public String getStatus() {return status;}
//	public void setStatus(String status) {this.status = status;}
	public String getType() {return type;}
	public ArrayList<BankAccount> getAcntlist() {return acntlist;}
	public void setAcntlist(ArrayList<BankAccount> acntlist) {this.acntlist = acntlist;}


	public ArrayList<BankAccount> getActiveAcntlist() {
		ArrayList<BankAccount> activelist = new ArrayList<BankAccount>();
		for(BankAccount ba:acntlist) {
			if(ba.getStatus().equals("active")) {
				activelist.add(ba);
			}
		}
		return activelist;
	}
	
	
	@Override
	public String toString() {
		return "Customer [user_id=" + user_id + ", user_name=" + user_name + ", status=" + status + ", type=" + type
				+ ", acntlist=" + acntlist + "]";
	}
}

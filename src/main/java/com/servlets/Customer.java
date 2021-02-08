package com.servlets;

import java.util.ArrayList;

public class Customer implements User{

	int user_id;
	String username;
	String status;
	String type;
	ArrayList<BankAccount> acntlist;
	ArrayList<Transaction> pendlist;
	
	public Customer(int user_id, String user_name, String status) {
		super();
		this.user_id = user_id;
		this.username = user_name;
		this.status = status;
		this.type = "customer";
	}

	public int getUser_id() {return user_id;}
	public String getUsername() {return username;}
	public String getStatus() {return status;}
//	public void setStatus(String status) {this.status = status;}
	public String getType() {return type;}
	public ArrayList<BankAccount> getAcntlist() {return acntlist;}
	public void setAcntlist(ArrayList<BankAccount> acntlist) {this.acntlist = acntlist;}
	public ArrayList<Transaction> getPendlist() {return pendlist;}

	public void setPendlist(ArrayList<Transaction> pendlist) {this.pendlist = pendlist;}

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
		return "Customer [user_id=" + user_id + ", username=" + username + ", status=" + status + ", type=" + type
				+ ", acntlist=" + acntlist + ", pendlist=" + pendlist + "]";
	}
}

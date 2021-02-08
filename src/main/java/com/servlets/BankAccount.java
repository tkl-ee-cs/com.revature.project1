package com.servlets;

	public class BankAccount {
		int account_id;
		float balance;
		String status;
		String username;
		
		public BankAccount(int account_id, float balance, String status) {
			super();
			this.account_id = account_id;
			this.balance = balance;
			this.status = status;
			this.username = "";
		}
		
		public BankAccount(int account_id, float balance, String status, String name) {
			super();
			this.account_id = account_id;
			this.balance = balance;
			this.status = status;
			this.username = name;
		}
		public int getAccount_id() {return account_id;}
		public float getBalance() {return balance;}
		public String getStatus() {return status;}
		public String getUser() {return username;}
		public void setStatus(String status) {this.status = status;}
		public void setUser(String username) {this.username = username;}

		@Override
		public String toString() {
			return "BankAccount [account_id=" + account_id + ", balance=" + balance + ", status=" + status
					+ ", username=" + username + "]";
		}
		
		
	}
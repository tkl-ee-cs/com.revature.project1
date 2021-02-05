package com.servlets;

	public class BankAccount {
		int account_id;
		int balance;
		String status;
		
		public BankAccount(int account_id, int balance, String status) {
			super();
			this.account_id = account_id;
			this.balance = balance;
			this.status = status;
		}
		
		public int getAccount_id() {return account_id;}
		public int getBalance() {return balance;}
		public String getStatus() {return status;}
		public void intoBalance(int value) {this.balance = balance + value;}
		public String setStatus() {return status;}
		
		@Override
		public String toString() {
			return "Account [account_id=" + account_id + ", balance=" + balance + "]";
		}
	}
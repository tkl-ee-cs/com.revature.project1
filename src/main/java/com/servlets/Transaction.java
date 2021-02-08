package com.servlets;

import java.sql.Timestamp;

public class Transaction {
	
	int transaction_id;
	int src;
	int dst;
	String status;
	float amount;
	String action;
	Timestamp created;
	Timestamp updated;
	
	public Transaction(int transaction_id, int src, int dst, String status, float amount, String action,
			Timestamp created) {
		super();
		this.transaction_id = transaction_id;
		this.src = src;
		this.dst = dst;
		this.status = status;
		this.amount = amount;
		this.action = action;
		this.created = created;
	}
	
	public Transaction(int transaction_id, int src, int dst, String status, float amount, String action,
			Timestamp created,Timestamp updated) {
		super();
		this.transaction_id = transaction_id;
		this.src = src;
		this.dst = dst;
		this.status = status;
		this.amount = amount;
		this.action = action;
		this.created = created;
		this.updated = updated;
	}
	
	public int getTransaction_id() {return transaction_id;}
	public int getSrc() {return src;}
	public int getDst() {return dst;}
	public String getStatus() {return status;}
	public void setStatus(String status) {this.status = status;}
	public float getAmount() {return amount;}
	public String getAction() {return action;}
	public Timestamp getCreated() {return created;}
	public Timestamp getUpdated() {return updated;}
	public void setUpdated(Timestamp updated) {this.updated = updated;}

	@Override
	public String toString() {
		return "Transaction [transaction_id=" + transaction_id + ", src=" + src + ", dst=" + dst + ", status=" + status
				+ ", amount=" + amount + ", action=" + action + ", created=" + created + ", updated=" + updated + "]";
	}
	
}

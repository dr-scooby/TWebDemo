package com.jah;

public class Memo {
	
	private String memoid;
	
	private String date;
	
	private String state;
	
	private String fedex; // fedex tracking#
	
	private String notes;
	
	
	public Memo() {
		memoid = "";
		date = "";
		state = "";
		fedex = "";
		notes = "";
	}
	
	public Memo(String memoid) {
		this.memoid = memoid;
		date = "";
		state = "";
		fedex = "";
		notes = "";
	}


	

	// -- Get & Set methods --//
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getMemoid() {
		return memoid;
	}


	public void setMemoid(String memoid) {
		this.memoid = memoid;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getFedex() {
		return fedex;
	}


	public void setFedex(String fedex) {
		this.fedex = fedex;
	}
	
	public String toString() {
		return memoid + " " + state + " FedEx " + fedex;
	}

}

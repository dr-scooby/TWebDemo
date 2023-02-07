package com.jah;

// class for Model types, used for the SQL search models
public class LModel {
	
	private String modelname;
	
	private int qty; // quantity
	
	public LModel() {
		modelname = "";
		qty = 0;
	}
	
	
	public LModel(String model, int x) {
		this.modelname = model;
		this.qty = x;
	}


	public String getModelname() {
		return modelname;
	}


	public void setModelname(String modelname) {
		this.modelname = modelname;
	}


	public int getQty() {
		return qty;
	}


	public void setQty(int qty) {
		this.qty = qty;
	}

	public String toString() {
		return "Model: " + modelname + " : QTY: " + qty;
	}
}

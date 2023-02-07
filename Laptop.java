package com.jah;

public class Laptop {
	
	private String serial; // serial#
	
	private String model ; // model of laptop, eg: G7, 850 G2
	
	private String manufacture; // HP, Lenovo, Dell, etc..
	
	private String notes;
	
	private String assigned;

	private String user; // name of user assigned to laptop
	
	public Laptop() {
		serial = "";
		model = "";
		manufacture = "";
		notes = "";
		assigned = "";
		user = "";
	}
	
	
	public Laptop(String serial) {
		this.serial = serial;
		model = "";
		manufacture = "";
		notes = "";
		assigned = "";
		user = "";
	}
	
	public Laptop(String serial, String model , String manuf, String notes) {
		this();
		this.serial = serial;
		this.model = model;
		this.manufacture = manuf;
		this.notes = notes;
		
	}
	
	
	public Laptop(String serial, String model, String manufacture) {
		this.serial = serial;
		this.model = model;
		this.manufacture = manufacture;
		notes = "";
		assigned = "";
	}
	
	public Laptop(String serial, String model , String manuf, String notes, String assigned) {
		this.serial = serial;
		this.model = model;
		this.manufacture = manuf;
		this.notes = notes;
		this.assigned = assigned;
	}
	
	public Laptop(String serial, String model , String manuf, String notes, String assigned, String userassigned) {
		this.serial = serial;
		this.model = model;
		this.manufacture = manuf;
		this.notes = notes;
		this.assigned = assigned;
		this.user = userassigned;
	}
	


	/* ----- GET & SET methods ---- */
	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacture() {
		return manufacture;
	}
	
	public String getuser() {
		return user;
	}
	

	public void setuser(String userassigned) {
		this.user = userassigned;
	}
	
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAssigned() {
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}
	
	
	public String toString() {
		return serial + ": " + model + ": " + notes;
	}

}

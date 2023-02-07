package com.jah;

/**
 * History of the Laptop
 * @author jismailx
 *
 */
public class LaptopHistory {
	
	private int ID;
	private String action ;
	private String serial;
	private String time;
	private String date;
	private String year;
	private String month;
	private String day;
	
	
	
	public LaptopHistory() {
		ID = 0;
		action = "";
		serial = "";
		time = "";
		date = "";
		year = "";
		month="";
		day = "";
	}
	
	// take a serial and action
	public LaptopHistory(String serial, String action) {
		this();
		this.serial = serial;
		this.action = action;
	}
	
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}


	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}


	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}


	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}


	/**
	 * @return the day
	 */
	public String getDay() {
		return day;
	}


	/**
	 * @param day the day to set
	 */
	public void setDay(String day) {
		this.day = day;
	}


	
	
	
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}


	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}


	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}
	/**
	 * @param serial the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	
	@Override
	public String toString() {
		return "LaptopHistory [ID=" + ID + ", action=" + action + ", serial=" + serial + ", time=" + time + ", date="
				+ date + "]";
	}

	
	
	
}

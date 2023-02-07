package com.jah;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.sql.Date;
import java.util.*;


public class DBModel {
	
	// Database info
	//private String dburl = "jdbc:mysql://localhost:3306/skyhawk";
	private String dburl = "jdbc:mysql://jfgapp1261:3306/inteldb";
	private String dbuser = "nurali";
	private String dbpass = "Java1973";

	private Connection conn; // connection to DB
	private Connection tempconn ; // a temp conn to be used as secondary
		
	public DBModel() {
		System.out.println("init DBModel");
	}

	
	public void connect() throws SQLException{
		System.out.println("\nconnect called...");
		
		try {
			//Class.forName("com.mysql.cj.jdbc.Driver"); // new mysql driver version
			Class.forName("com.mysql.jdbc.Driver"); // old mysql driver version
			conn = DriverManager.getConnection(dburl, dbuser, dbpass);
			tempconn = DriverManager.getConnection(dburl, dbuser, dbpass);
		}catch(ClassNotFoundException e) {
			System.err.println(e);
		}
	}
	
	
	// add Laptop to DB
	public boolean addLaptop(Laptop lap) {
		boolean ok = false;
		
		String sql = "insert into laptopinventory(Serial,model,manufacture) values(?,?,?,?,?);";
		
		
		try {
			connect();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, lap.getSerial());
		ps.setString(2, lap.getModel());
		ps.setString(3, lap.getManufacture());
		ps.setString(4, lap.getNotes());
		ps.setString(5, lap.getAssigned());
				
		ok = ps.executeUpdate() > 0;
		
		String serialnumber = lap.getSerial() ;
		
		// add entry to the history table
		String historysql = "insert into history(action, serial, time) values(?,?,?)";
		ps = conn.prepareStatement(historysql); // prepare a statement
		ps.setString(1, "add laptop " + serialnumber);
		ps.setString(2, serialnumber);
		ps.setString(3, new java.util.Date().toString() );
		ps.executeUpdate(); // execute
		ps.close();
		conn.close();
		}catch(SQLException s) {
			System.err.println("error in addLaptop() \n" + s);	
		}
		
		
		return ok;
	}
	
	/**
	 * 
	 * @param serial
	 * @param model
	 * @param manuf
	 * @param notes
	 * @return boolean ok true if adding laptop success, false if failed to add
	 * @throws SQLException
	 */
	public boolean addLaptop(String serial, String model, String manuf, String notes)throws SQLException{
		boolean ok = false;
		
		String sql = "insert into laptopinventory(Serial,model,manufacture, notes, assigned) values(?,?,?,?,?);";
		// select statement to check if serial already exists in DB before adding
		String sql_select = "select count(*) from laptopinventory where Serial = ? ";
				
		connect();
		
		
		PreparedStatement ps1 = conn.prepareStatement(sql_select);
		ps1.setString(1, serial);
		ResultSet rs = ps1.executeQuery();
		rs.next();
		String counts = rs.getString(1);
		if(counts.equals("0")) { // check if 0, means serial id doesn't exist in the DB
			System.out.println("nothing found for " + serial);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, serial);
			ps.setString(2, model);
			ps.setString(3, manuf);
			ps.setString(4,  notes);
			ps.setString(5,  "no");
					
			ok = ps.executeUpdate() > 0;
			
			// add entry to the history table
			String historysql = "insert into history(action, serial, time) values(?,?,?)";
			ps = conn.prepareStatement(historysql); // prepare a statement
			ps.setString(1, "add laptop " + serial + ":" + notes);
			ps.setString(2, serial);
			ps.setString(3, new java.util.Date().toString() );
			ps.executeUpdate(); // execute
			
			ps.close();
			ok = true;
			
		}else {
			System.out.println("Found : " + serial);
			ok = false; // exists, so can't add
			//ps.close();
		}
		
				
		conn.close();
		
		return ok;
	}
	
	// add laptop with following parameters:
	public boolean addLaptop(String serial, String model, String manufacture)throws SQLException {
		boolean ok = false;
		
		String sql = "insert into laptopinventory(Serial,model,manufacture, notes, assigned) values(?,?,?,?,?);";
		// select statement to check if serial already exists in DB before adding
		String sql_select = "select count(*) from laptopinventory where Serial = ? ";
		
		connect();
		PreparedStatement ps1 = conn.prepareStatement(sql_select);
		ps1.setString(1, serial);
		ResultSet rs = ps1.executeQuery();
		rs.next();
		String counts = rs.getString(1);
		if(counts.equals("0")) {
			System.out.println("nothing found for " + serial);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, serial);
			ps.setString(2, model);
			ps.setString(3, manufacture);
			ps.setString(4,  "");
			ps.setString(5,  "no");
					
			ok = ps.executeUpdate() > 0;
			
			// add entry to the history table
			String historysql = "insert into history(action, serial, time) values(?,?,?)";
			ps = conn.prepareStatement(historysql); // prepare a statement
			ps.setString(1, "add laptop " + serial);
			ps.setString(2, serial);
			ps.setString(3, new java.util.Date().toString() );
			ps.executeUpdate(); // execute
			
			ps.close();
			ok = true;
			
		}else {
			System.out.println("Found : " + serial);
			ok = false; // exists, so can't add
			//ps.close();
		}
		
				
		conn.close();
		
		return ok;
	}
	
	// delete laptop from DB
	public boolean delete(String serialsearch)throws SQLException, Exception{
		boolean ok = false;
		
		String sql = "delete from laptopinventory where Serial=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, serialsearch);
		ok = ps.executeUpdate() > 0;
		
		// add entry to the history table
		String historysql = "insert into history(action, serial, time) values(?,?,?)";
		ps = conn.prepareStatement(historysql); // prepare a statement
		ps.setString(1, "removed laptop " + serialsearch);
		ps.setString(2, serialsearch);
		ps.setString(3, new java.util.Date().toString() );
		ps.executeUpdate(); // execute
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// delete laptop, and update history with a note
	public boolean delete(String serial, String action) throws SQLException, Exception{
		boolean ok = false;
		
		String sql_delete = "delete from laptopinventory where Serial=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql_delete);
		ps.setString(1, serial);
		ok = ps.executeUpdate() > 0;
		
		// add entry to the history table
		String historysql = "insert into history(action, serial, time) values(?,?,?)";
		ps = conn.prepareStatement(historysql); // prepare a statement
		ps.setString(1, "removed laptop from inventory " + serial + " , " + action);
		ps.setString(2, serial);
		ps.setString(3, new java.util.Date().toString() );
		ps.executeUpdate(); // execute
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// list all laptops, get all listing
	public List<Laptop> listAll() throws SQLException, Exception{
		
		connect(); // connect to DB
		
		String sql = "select * from laptopinventory where assigned = 'no';" ; // sql select
		
		List<Laptop> laps = new ArrayList<>(); // create the ArrayList
		
		// create a Statement, and execute SQL, and get a ResultSet
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			String serial = rs.getString("Serial");
			String model = rs.getString("Model");
			String manuf = rs.getString("Manufacture");
			String notes = rs.getString("notes");
			String assigned = rs.getString("assigned");
			Laptop la = new Laptop(serial, model, manuf, notes, assigned); // make a Laptop Object
			laps.add(la); // add Laptop Object to the ArrayList
		}
		
		rs.close();
		conn.close();
		
		// return ArrayList
		return laps;		
	}
	
	
	// get only the serials#
	public List<Laptop> listonlySerials() throws SQLException, Exception{
		
		// connect to DB
		connect();
		
		// select only the serials, assigned is no		
		String sql = "select Serial from inteldb.laptopinventory where assigned = 'no'";
		
		List<Laptop> laps = new ArrayList<>(); // create ArrayList
		
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			String serial = rs.getString("Serial");
			Laptop la = new Laptop(serial); // make a Laptop Object
			laps.add(la); // add Laptop Object to the ArrayList
		}
		
		rs.close();
		conn.close();
		
		return laps;
	}
	
	
	// get a listing of model types
	public List<LModel> listModels() throws SQLException, Exception {
		
		String sql = "select  Model, count(Model) as QTY from inteldb.laptopinventory where assigned = 'no' group by Model order by Model;";
		
		connect(); // connect to DB
		
		// create the ArrayList of LModel
		List<LModel> models = new ArrayList<>();
		
		// create a Statement, and execute SQL, and get a ResultSet
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			String model = rs.getString(1); // first column
			int qty = rs.getInt(2); // second col
			LModel lmodel = new LModel(model, qty); // create the LModel object
			System.out.println(lmodel);
			models.add(lmodel); // add to our arraylist
		}
		
		return models;
		
	}
	
	//update the laptop
	public boolean update(Laptop l) throws SQLException {
		boolean ok = false;
		
		String sql = "update laptopinventory set Model=?, Manufacture=?, notes=? where Serial=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, l.getModel());
		ps.setString(2, l.getManufacture());
		ps.setString(3, l.getNotes());
		ps.setString(4, l.getSerial());
		ok = ps.executeUpdate() > 0;
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// assign a laptop to a user, need laptop and user name/wwid
	public boolean assignTo(Laptop l, String user) throws SQLException {
		boolean ok = false;
		System.out.println("User length: " + user.length());
		
		if(user.length()>75) {
			user = user.substring(0, 75);
			System.out.println("User length after modification: " + user);
		}
		
		// sql update the laptop assigned field to yes
		String sql_update = "update laptopinventory set assigned = ?, Model=?, Manufacture=?, notes=? where Serial = ?";
		
		// sql to insert the assigned laptop into the table assigned
		String sql_insert = "insert into assignedlaptop(laptopserial, username) values(?,?)";
		
		connect(); // connect to DB
		
		PreparedStatement ps = conn.prepareStatement(sql_update);
		ps.setString(1, "yes"); // set assigned to yes
		ps.setString(2, l.getModel());
		ps.setString(3, l.getManufacture());
		ps.setString(4, l.getNotes());		
		ps.setString(5, l.getSerial());
		ok = ps.executeUpdate() > 0;
		
		ps = conn.prepareStatement(sql_insert);
		ps.setString(1, l.getSerial());
		ps.setString(2, user);
		ok = ps.executeUpdate() > 0;
		
		// add entry to the history table
		String historysql = "insert into history(action, serial, time) values(?,?,?)";
		ps = conn.prepareStatement(historysql); // prepare a statement
		ps.setString(1, "assigned laptop " + l.getSerial() + " to user " + user);
		ps.setString(2, l.getSerial());
		ps.setString(3, new java.util.Date().toString() );
		ps.executeUpdate(); // execute
		
		ps.close();
		conn.close();
				
		return ok;
	}
	
	// un-assign the laptop from the user
	public boolean unAssign(Laptop l) throws SQLException {
		boolean ok = false;
		
		
		System.out.println("un-assign from user: " + l.getuser() + " :: notes: " + l.getNotes());
		Laptop findlap = searchSerialID(l.getSerial()); // search the DB for the laptop by Serial
		System.out.println("findlap:: " + findlap.toString() + ":: " + findlap.getuser());
		
		connect();
		// add entry to the history table
		String historysql = "insert into history(action, serial, time) values(?,?,?)";
		PreparedStatement pst = conn.prepareStatement(historysql);
		//ps = conn.prepareStatement(historysql); // prepare a statement
		pst.setString(1, "un-assigned laptop " + findlap.getSerial() + " to user " + findlap.getuser());
		pst.setString(2, findlap.getSerial());
		pst.setString(3, new java.util.Date().toString() );
		//ps.setString(4,  getDate());
		pst.executeUpdate(); // execute
		
		// update the laptop assigned field to no
		String unassign_sql = "update laptopinventory set assigned = ?, Model=?, Manufacture=?, notes=? where Serial= ?";
		
		// delete the laptop from the assignedlaptop table
		String delete_sql = "delete from assignedlaptop where assignedlaptop.laptopserial = ?";
		
		
		// un-assign
		PreparedStatement ps = conn.prepareStatement(unassign_sql);
		ps.setString(1, "no");
		ps.setString(2, l.getModel());
		ps.setString(3, l.getManufacture());
		ps.setString(4, l.getNotes());		
		ps.setString(5, l.getSerial());
		ok = ps.executeUpdate() > 0;
		
		// delete from table
		ps = conn.prepareStatement(delete_sql);
		ps.setString(1, l.getSerial());
		ok = ps.executeUpdate() > 0;
		
				
		ps.close();
		pst.close();
		conn.close();
		
		return ok;
	}
	
	// search for serial, but return only one result
	public Laptop searchSerialID(String searchserial)throws SQLException{
		Laptop lap = null;
		
		String sql = "select * from laptopinventory where Serial = ? ";
		System.out.println("inside searchSerialID: " + searchserial);
		connect();
		
		// use a prepared statement
		PreparedStatement ps = conn.prepareStatement(sql); // prepare with the SQL
		ps.setString(1, searchserial); // set it with searchserial
		// execute and get ResultSet		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			rs.beforeFirst();
			while(rs.next()) {
				String serial = rs.getString("Serial");
				String model = rs.getString("Model");
				String manuf = rs.getString("Manufacture").toLowerCase();
				String notes = rs.getString("notes");
				String assigned = rs.getString("assigned");
				if(assigned.equals("yes")) {
					String sql2 = "select username from assignedlaptop where laptopserial = ?  ";
					
					PreparedStatement ps2 = tempconn.prepareStatement(sql2);
					ps2.setString(1, serial);
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					String username = rs2.getString("username");
					lap = new Laptop(serial, model, manuf, notes, assigned, username);
				}else {
					lap = new Laptop(serial, model, manuf, notes, assigned); // make a Laptop Object
				}
			}
		}
				
		ps.close();
		rs.close();
		conn.close();
		
		return lap;
	}
	
	// search for laptop using serial, return a listing, more than one search
	public List<Laptop> searchSerial(String searchserial)throws SQLException{
		List<Laptop> laps = new ArrayList<Laptop>();
		
		String sql = "select * from laptopinventory where Serial like ?";
		
		// connect to DB
		connect();
		// using PreparedStatement to init the sql statement
		PreparedStatement ps = conn.prepareStatement(sql);
		// want to use the like with the %:
		ps.setString(1, "%" + searchserial + "%");
		//ps.setString(1, searchserial );
		// execute query
		ResultSet rs = ps.executeQuery(); 
		if(rs.next()) {
			rs.beforeFirst();
			// now loop through
			while(rs.next()) {
				String serial = rs.getString("Serial");
				String model = rs.getString("Model");
				String manuf = rs.getString("Manufacture");
				String notes = rs.getString("notes");
				String assigned = rs.getString("assigned");
				if(assigned.equals("yes")) {
					System.out.println("\nlaptop assigned :> YES\n");
					String sql2 = "select username from assignedlaptop where laptopserial = ?  ";
					
					PreparedStatement ps2 = tempconn.prepareStatement(sql2);
					ps2.setString(1, serial);
					ResultSet rs2 = ps2.executeQuery();
					if(rs2.next()) { // ** somehow this rs2.next() is called, the Resultnext then goes to the end of the row
						rs2.beforeFirst(); // ** to compensate, call this
					rs2.next(); // then calling this is fine, and works
					String username = rs2.getString("username");
					System.out.println("User Name: " + username);
					Laptop lap = new Laptop(serial, model, manuf, notes, assigned, username);
					laps.add(lap);
					}else {
						String username = "unknown user?";
						Laptop lap = new Laptop(serial, model, manuf, notes, assigned, username);
						laps.add(lap);
					}
				}else {
					Laptop lap = new Laptop(serial, model, manuf, notes, assigned);
					laps.add(lap);
				}
			}
		}
		
		ps.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// search by model only
	public List<Laptop> searchModel(String modelsearch) throws SQLException{
		List<Laptop> laps = new ArrayList<>();
		
		String sql = "select * from laptopinventory where model like ? and assigned='no'";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "%" + modelsearch + "%");
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String serial = rs.getString("Serial");
			String model = rs.getString("model");
			String manufacture = rs.getString("manufacture");
			String notes = rs.getString("notes");
			String assigned = rs.getString("assigned");
			Laptop lap = new Laptop(serial, model, manufacture, notes, assigned);
			laps.add(lap);
		}
		ps.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// search History for laptop
	public List<LaptopHistory> searchHistory(String searchid) throws SQLException{
		String sql = "select * from history where serial =?";
		
		List<LaptopHistory> laps = new ArrayList<LaptopHistory>();
		
		connect();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, searchid);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			// get data from DB
			String id = rs.getString("ID");
			String action = rs.getString("action");
			String serial = rs.getString("serial");
			String time = rs.getString("time");
			String date = rs.getString("datestamp");
			// create hitory object
			LaptopHistory lap = new LaptopHistory(serial, action);
			int i = Integer.parseInt(id); // convert String to int
			// set the info
			lap.setID(i);
			lap.setTime(time);
			lap.setDate(date);
			laps.add(lap); // add to History Array List
			
			
		}
		
		ps.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// search the history action
	public List<LaptopHistory> searchHistoryAction(String search)throws SQLException{
		List<LaptopHistory> laps = new ArrayList<LaptopHistory>();
		
		String sql = "select * from history where action like ?";
		
		connect();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "%" + search + "%");
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			// get data from DB
			String id = rs.getString("ID");
			String action = rs.getString("action");
			String serial = rs.getString("serial");
			String time = rs.getString("time");
			String date = rs.getString("datestamp");
			// create hitory object
			LaptopHistory lap = new LaptopHistory(serial, action);
			int i = Integer.parseInt(id); // convert String to int
			// set the info
			lap.setID(i);
			lap.setTime(time);
			lap.setDate(date);
			laps.add(lap); // add to History Array List
			
			
		}
		
		ps.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// condition String value = Equal, Greater
	public List<LaptopHistory> searchHistoryDate(String year, String month, String day, String condition)throws SQLException{
		List<LaptopHistory> laps = new ArrayList<LaptopHistory>();
		String id = "";
		String action = "";
		String serial = "";
		String time  = "";
		String date = "";
		String sql = "";
		String dates = "";
		dates += year+"-"+month+"-"+day;
		System.out.println("Condition: " + condition);
		
		PreparedStatement pst;
		ResultSet rs;
		
		if(condition.contains("Greater")) {
			//sql = "select * from history where year(datestamp)=? and month(datestamp)>=? and day(datestamp)>=?";
			sql = "select * from history where year(datestamp)=? and datestamp > ?";
			connect();
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, year);
			pst.setString(2, dates);
			//pst.setString(3, day);
			rs = pst.executeQuery();
			while(rs.next()) {
				// get data from DB
				id = rs.getString("ID");
				action = rs.getString("action");
				serial = rs.getString("serial");
				time = rs.getString("time");
				date = rs.getString("datestamp");
				// create hitory object
				LaptopHistory lap = new LaptopHistory(serial, action);
				int i = Integer.parseInt(id); // convert String to int
				// set the info
				lap.setID(i);
				lap.setTime(time);
				lap.setDate(date);
				laps.add(lap); // add to History Array List
			}
		}else if(condition.contains("Equal")) {
			sql = "select * from history where year(datestamp)=? and month(datestamp) =? and day(datestamp) =?";
			connect();
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, year);
			pst.setString(2, month);
			pst.setString(3, day);
			rs = pst.executeQuery();
			while(rs.next()) {
				// get data from DB
				id = rs.getString("ID");
				action = rs.getString("action");
				serial = rs.getString("serial");
				time = rs.getString("time");
				date = rs.getString("datestamp");
				// create hitory object
				LaptopHistory lap = new LaptopHistory(serial, action);
				int i = Integer.parseInt(id); // convert String to int
				// set the info
				lap.setID(i);
				lap.setTime(time);
				lap.setDate(date);
				laps.add(lap); // add to History Array List
			}
		}else {
			sql = "select * from history where year(datestamp)=? and month(datestamp) =? and day(datestamp)=?";
			connect();
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, year);
			pst.setString(2, month);
			pst.setString(3, day);
			rs = pst.executeQuery();
			while(rs.next()) {
				// get data from DB
				id = rs.getString("ID");
				action = rs.getString("action");
				serial = rs.getString("serial");
				time = rs.getString("time");
				date = rs.getString("datestamp");
				// create hitory object
				LaptopHistory lap = new LaptopHistory(serial, action);
				int i = Integer.parseInt(id); // convert String to int
				// set the info
				lap.setID(i);
				lap.setTime(time);
				lap.setDate(date);
				laps.add(lap); // add to History Array List
			}
		}
		
		
		
		pst.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// search history by date
	public List<LaptopHistory> searchHistoryDate(String year, String month, String day)throws SQLException{
		List<LaptopHistory> laps = new ArrayList<LaptopHistory>();
		String id = "";
		String action = "";
		String serial = "";
		String time  = "";
		String date = "";
		
		String sql = "select * from history where year(datestamp)=? and month(datestamp)=? and day(datestamp)=?";
		
		connect();
		
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, year);
		pst.setString(2, month);
		pst.setString(3, day);
		ResultSet rs = pst.executeQuery();
		while(rs.next()) {
			// get data from DB
			id = rs.getString("ID");
			action = rs.getString("action");
			serial = rs.getString("serial");
			time = rs.getString("time");
			date = rs.getString("datestamp");
			// create hitory object
			LaptopHistory lap = new LaptopHistory(serial, action);
			int i = Integer.parseInt(id); // convert String to int
			// set the info
			lap.setID(i);
			lap.setTime(time);
			lap.setDate(date);
			laps.add(lap); // add to History Array List
		}
		
		return laps;
	}
	
	// search by notes
	public List<Laptop> searchNotes(String searchnotes) throws SQLException{
		List<Laptop> laps = new ArrayList<>();
		
		String sql = "select * from laptopinventory where notes like ? ";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "%" + searchnotes + "%");
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String serial = rs.getString("Serial");
			String model = rs.getString("model");
			String manufacture = rs.getString("manufacture");
			String notes = rs.getString("notes");
			String assigned = rs.getString("assigned");
			Laptop lap = new Laptop(serial, model, manufacture, notes, assigned);
			laps.add(lap);
		}
		ps.close();
		rs.close();
		conn.close();
		
		return laps;
	}
	
	// search for the user assigned to laptop
	public List<Laptop> searchUser(String user) throws SQLException {
		List<Laptop> laps = new ArrayList<>();
		
		String sql = "select * from assignedlaptop where username like ?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "%" + user + "%");
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String lapserial = rs.getString("laptopserial");
			String username = rs.getString("username");
			
			String sql2 = "select * from laptopinventory where serial = ?";
			PreparedStatement ps2 = tempconn.prepareStatement(sql2);
			ps2.setString(1, lapserial);
			ResultSet rs2 = ps2.executeQuery();
			rs2.next();
			String model = rs2.getString("Model");
			String manuf = rs2.getString("Manufacture");
			String notes = rs2.getString("notes");
			// Laptop(String serial, String model , String manuf, String notes, String assigned, String userassigned)
			Laptop la = new Laptop(lapserial, model, manuf, notes, "yes", username);
			laps.add(la);
		}
		rs.close();
		ps.close();
		conn.close();
		
		return laps;
	}
	
	// need to know how many laptops are not assigned, what we have on hand
	public int getUnAssigned() throws SQLException {
		int size = 0;
		
		String sql = "select count(*) from laptopinventory where assigned = 'no';";
		connect();
		// create a Statement, and execute SQL, and get a ResultSet
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			size = rs.getInt(1);
		}
		
		
		rs.close();
		conn.close();
		
		return size;
	}
	
	
	// check credentials on the database
	public boolean checkLogin(String searchuname, String searchpassword) throws SQLException {
		boolean ok = false;
		
		String sql = "select uname, pass from login where uname=? and pass=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, searchuname);
		ps.setString(2, searchpassword);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			ok = true;
			
			String sqlinsert = "insert into loginfo(uname, time, notes) values(?,?, ?)";
			try {
				java.util.Date date = new java.util.Date();
				
				PreparedStatement ps2 = conn.prepareStatement(sqlinsert);
				ps2.setString(1, searchuname);
				ps2.setString(2, date.toString());
				ps2.setString(3, "logged in");
				int x  = ps2.executeUpdate();
				if(x > 0) {
					System.out.println("\nSuccess adding login \n");
				}else {
					System.out.println("\n Error adding login");
				}
			}catch(SQLException s) {
				System.err.println(s.getMessage());
			}
		}
		
		ps.close();
		rs.close();
		conn.close();
		
		return ok;
		
	}
	
	// log out user
	public void logout(String uname) throws SQLException {
		connect();
		
		String sqlinsert = "insert into loginfo(uname, time, notes) values(?,?, ?)";
		try {
			java.util.Date date = new java.util.Date();
			
			PreparedStatement ps2 = conn.prepareStatement(sqlinsert);
			ps2.setString(1, uname);
			ps2.setString(2, date.toString());
			ps2.setString(3, "logged out");
			int x  = ps2.executeUpdate();
			if(x > 0) {
				System.out.println("\nSuccess adding login \n");
			}else {
				System.out.println("\n Error adding login");
			}
			
			ps2.close();
			conn.close();
						
		}catch(SQLException s) {
			System.err.println(s.getMessage());
		}	
		
	}// end logout
	
	
	//create a memo, need memoID & laptop serials as array
	public boolean createMemo(String memoid, String[] serials) throws SQLException {
		boolean ok = false;
		String sql_memo = "insert into memo(MemoID, Date, State) values(?,?,?)";
		String sql_memos = "insert into memos(memoID, serial) values(?,?)";
		String state = "packed, on hand";
		
		connect();
		// check if memoID already exists in DB
		if(isMemoIDExist(memoid)) {
			ok = false;
		}else {
			java.util.Date date = new java.util.Date();
			
			PreparedStatement ps = conn.prepareStatement(sql_memo);
			ps.setString(1, memoid);
			ps.setString(2, date.toString());
			ps.setString(3, state);
			int x = ps.executeUpdate();
			if(x > 0) {
				System.out.println("success adding memoID");
				ok = true;
				String serial = "";
				ps = conn.prepareStatement(sql_memos);
				
				// need to update the notes field, get the notes
				String sql_getNotes = "select notes from laptopinventory where Serial= ?";
				String sql_updateNotes = "update laptopinventory set notes=? where Serial=?";
				// loop through serials , and update the memo and notes fields
				for(int i =0; i < serials.length; i++) {
					
					String notes = "";
					PreparedStatement ps2 = tempconn.prepareStatement(sql_getNotes);
					
					serial = serials[i].trim(); // get the serial#
					if(isLaptopSerialExists(serial)) { // check if serial exists in DB
						// insert the serial, memoid into the memos table
						ps.setString(1, memoid);
						ps.setString(2, serial);
						ps.executeUpdate();
						
						ps2.setString(1, serial);
						ResultSet rs = ps2.executeQuery();
						rs.next();
						notes = rs.getString("notes");
						notes = notes + " :: added to Memo ID " + memoid;
						PreparedStatement ps3 = tempconn.prepareStatement(sql_updateNotes);
						ps3.setString(1, notes);
						ps3.setString(2, serial);
						int success = ps3.executeUpdate();
						if(success > 0) {
							System.out.println("success updating notes");
						}else
							System.out.println("failed to update notes");
						
						// update the history table, putting note serial added to memoID
						addtoHistory(serial, "add "+ serial + " to MemoID " + memoid);
						
					}else {
						System.out.println("Serial doesn't exist, deal with it....");
					}
						
				}
				ps.close();
				conn.close();
		}else
			System.out.println("error creating memoID");	
		}
		
		
		return ok;
	}
	
	// add info to the history table
	private void addtoHistory(String serial, String action) {
		String history_sql = "insert into history(action, serial, time) values(?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(history_sql);
			pst.setString(1, action);
			pst.setString(2, serial);
			pst.setString(3, new java.util.Date().toString());
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// get all memo listing
	public ArrayList getMemos() throws SQLException {
		ArrayList<Memo> listing = new ArrayList<Memo>();
		
		String sql = "select * from memo";
		
		connect();
		
		// create a statement, and execute SQL
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			String memoid = rs.getString("MemoID");
			String date = rs.getString("Date");
			String state = rs.getString("State");
			String fedex = rs.getString("FedEx");
			Memo mem = new Memo(memoid);
			mem.setDate(date);
			mem.setState(state);
			mem.setFedex(fedex);
			System.out.println("Memo: " + mem);
			
			listing.add(mem);
		}
		
		st.close();
		rs.close();
		conn.close();
		
		return listing;
	}
	
	// search the memo table for the serial#, get the memoID
	public String getMemoID(String serial) throws SQLException {
		String memoid = "";
		
		String sql = "select memoID, serial from memos where serial=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, serial);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			memoid = rs.getString("memoID");
		}else {
			memoid = "nothing";
		}
		
		return memoid;
		
	}
	
	public void getMemoID(String[] serials) {
		
	}
	
	// check if Serial exists in DB
	public boolean isLaptopSerialExists(String serial)throws SQLException{
		boolean exists = false;
		
		System.out.println("Check if Laptop serial exists: " + serial);
		
		String sql = "select Serial from laptopinventory where Serial=?";
		
		connect();
		
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, serial);
		ResultSet rs = pst.executeQuery();
		if(rs.next()) {
			System.out.println("Found serial in DB:: " + serial);
			exists = true;
		}else {
			exists = false;
			System.out.println("NOT found in DB:: " + serial);
		}
		
		return exists;
	}
	
	// check if memoID exists in DB
	public boolean isMemoIDExist(String memoid)throws SQLException{
		boolean ok = false;
		System.out.println("checking if memoID exists: " + memoid);
		
		String sql = "select MemoID from memo where MemoID = ?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, memoid);
		ResultSet rs = ps.executeQuery();
		// check if ResultSet is empty or not
		if( rs.next() == false) {
			System.out.println("resultset empty");
			// means the memoid doesn't exist
			ok = false;
		}else {
			System.out.println("resultset not empty");
			ok = true;
		}
			
		return ok;
	}
	
	
	// get a Memo Object
	public Memo getMemo(String memoid) throws SQLException {
		Memo mem = new Memo();
		
		String sql = "select * from memo where MemoID=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, memoid);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String date = rs.getString("Date");
			String state = rs.getString("State");
			String fedex = rs.getString("FedEx");
			
			mem.setDate(date);
			mem.setState(state);
			mem.setFedex(fedex);
		}
		
		System.out.println("getMemo: " + mem);
		
		return mem;
	}
	
	// search for a memoID
	// and return list of laptops serials
	public ArrayList<Laptop> searchMemoID(String memoid) throws SQLException {
		ArrayList<Laptop> laps = new ArrayList<>();
		// this SQL query is when the MemoID is still on hand, box is packed but not shipped out yet,
		// we can get the info on the laptop, notes, details etc..
		// once it is shipped out, stated changes to in transit(or delivered), and another SQL query needs to be used
		String sql = "select laptopinventory.Serial, laptopinventory.Model, laptopinventory.Manufacture, laptopinventory.notes, laptopinventory.assigned, memos.memoID "
				+ "from inteldb.laptopinventory, inteldb.memos "
				+ "where memos.serial = laptopinventory.Serial and memos.MemoID = ?";
		
		// this would be the query to use once the box is shipped out(state changes), we can just get the serials of laptops on this memo
		// SELECT * FROM inteldb.memos where memoID = '1304896311';
		
		String SQL_memo_state = "select State from inteldb.memo where MemoID=?"; // get the state of the memo
		connect();
		PreparedStatement pst = conn.prepareStatement(SQL_memo_state);
		pst.setString(1, memoid);
		ResultSet rst = pst.executeQuery();
		String thestate = "";
		while(rst.next()) {
			thestate = rst.getString(1);
		}
		
		System.out.println("the memo State: " + thestate);
		pst.close();
		rst.close();
		if(thestate.equals("packed, on hand")) {
			//connect();
			String serial = "" ;
			String model = "";
			String manuf = "";
			String notes = "";
			String assig = "";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, memoid);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				serial = rs.getString("Serial");
				 model = rs.getString("Model");
				 manuf = rs.getString("Manufacture");
				 notes = rs.getString("notes");
				 assig = rs.getString("assigned");
				 Laptop la = new Laptop(serial, model, manuf, notes, assig);
				 laps.add(la);			 
			}
			
			if(laps.isEmpty()) {
				laps.clear();
				System.out.println("\nlaps is empty\n");
				String sql2 = "select serial from memos where memoID = ?";
				ps = conn.prepareStatement(sql2);
				ps.setString(1, memoid);
				rs = ps.executeQuery();
				while(rs.next()) {
					String aserial = rs.getString("serial");
					Laptop la = new Laptop(aserial);
					laps.add(la);
				}
			}
		
		// close connection
		rs.close();
		ps.close();
		
		}else {
			// this would be the query to use once the box is shipped out(state changes), we can just get the serials of laptops on this memo
			String sql_memo = " SELECT * FROM inteldb.memos where memoID = ? ";
			String lapsserial = "";
			
			PreparedStatement pst_memo = conn.prepareStatement(sql_memo);
			pst_memo.setString(1, memoid);
			ResultSet rs3 = pst_memo.executeQuery();
			while(rs3.next()) {
				lapsserial = rs3.getString("serial");
				Laptop lap = new Laptop(lapsserial);
				laps.add(lap);
			}
			
			pst_memo.close();
			rs3.close();
		}
		
		
		conn.close();
		
		return laps;
		
	}
	
	// memo is Tranist. need a fedex# , remove the laptops in the memo from the laptop inventory
	// since those laptops are shipped out, and no longer on hand
	public boolean memoinTransit(String memoid, String fedex) throws Exception{
		boolean ok = false;
		
		String sql_update_memo = "update memo set State=?, FedEx=? where MemoID=?";
		String state = "in transit";
		
		connect();
		
		// first update the memo table with state and fedex tracking
		PreparedStatement ps = conn.prepareStatement(sql_update_memo);
		ps.setString(1, state);
		ps.setString(2, fedex);
		ps.setString(3, memoid);
		ok = ps.executeUpdate() > 0;
		
		// now get the serials listed in memos,
		String sql_getSerials = "select serial from memos where memoID=?";
		ps = conn.prepareStatement(sql_getSerials);
		ps.setString(1, memoid);
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			String serial = rs.getString("serial");
			delete(serial, " see MemoID " + memoid); // call the delete to remove from inventory
		}
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// update Memo with a FedEx#, maybe still on hand, haven't shipped out yet
	public boolean updateMemo(String memoid, String fedex)throws Exception{
		boolean ok = false;
		
		String sql_update_memo = "update memo set FedEx=? where MemoID=?";
		
		System.out.println("calling connect from updateMemo...");
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql_update_memo);
		ps.setString(1, fedex);
		ps.setString(2, memoid);
		ok = ps.executeUpdate() > 0;
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// memo delivered
	public boolean memoDelivered(String memoid) throws SQLException {
		boolean ok = false;
		
		String sql = "update memo set State=? where MemoID=?";
		
		connect();
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "Delivered");
		ps.setString(2, memoid);
		ok = ps.executeUpdate() > 0;
		
		ps.close();
		conn.close();
		
		return ok;
	}
	
	// change a memo state, info provided by client
	// State: in transit, delivered
	public boolean memoState(String memoid, String state) {
		boolean ok = false;
		
		String sql_update_memo = "update memo set State=? where MemoID=?";
		
		
		
		return ok;
	}
	
	// process a csv file
	public boolean processFile(File afile) {
		boolean ok = false;
		
		try {
			if( afile.canRead() )
				System.out.println("can read");
			String filename = afile.getName();
			System.out.println("file name: " + filename );
			System.out.println("dir: " + afile.getAbsolutePath());
			String ext = getExt(afile);
			if(ext.equals("csv")) {
				System.out.println("csv file");
				try {
					BufferedReader buff = new BufferedReader(new FileReader(afile)) ;
					String line = null;
					int linecount =0;
					int headerline = 0; // header line is the first line
					int headercols; // number of columns
					while( (line = buff.readLine()) != null ) {
						if(headerline == 0) {
							String[] headsplit = line.split("[,]");
							headercols = headsplit.length;
							System.out.println("number of columns: " + headercols);
							headerline ++;
						}else {
							String[] splitup = line.split("[,]"); // split line by , 
							String serial = splitup[0];
							String model = splitup[1];
							String manuf = splitup[2];
							System.out.println("serial: " + serial);
							System.out.println("model: " + model);
							System.out.println("Manufacture: " + manuf);
							
							if ( addLaptop(serial, model, manuf) == true)
								ok = true;
							
						}
					}
				}catch(Exception e) {
					System.err.println("error " + e.getMessage());
				}
			}else {
				System.out.println("not csv file");
			}
		}catch(Exception e) {
			System.err.println("error in Thread ProcessFile \n" + e.getMessage());
		}
		
		return ok;
	}
	
	private String getExt(File f) {
		String ext = "";
		
		char dot = '.';
		String filename = f.getName();
		int doti = filename.lastIndexOf(dot);
		ext = filename.substring(doti+1);
		
		return ext;
	}
	
}

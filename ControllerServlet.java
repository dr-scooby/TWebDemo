package com.jah;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.tomcat.jni.File;
import java.io.File;
import java.util.*;


/**
 * Servlet implementation class ControllerServlet
 */
//@WebServlet("/addlaptop")
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBModel dbmodel;
   
	
	public void init() {
		dbmodel = new DBModel();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getServletPath();
		
		try {
			//HistoryAction
			switch(action) {
			case "/addlaptop":
				addLaptop(request, response) ;
				break;
			case "/delete":
				delete(request, response);
				break;
			case "/deleteMe":
				deleteMe(request, response);
				break;
			case "/listall":
				listall(request, response);
				break;
			case "/listserialsonly":
				listserialsonly(request, response);
				break;
			case "/listmodeltype":
				listModel(request, response);
				break;
			case "/searchlaptop":
				searchLaptop(request, response);
				break;
			case "/edit":
				showEditform(request, response);
				break;
			case "/update":
				updateLaptop(request, response);
				break;
			case "/login":
				login(request, response);
				break;
			case "/logout":
				logout(request, response);
				break;
			case "/addbulk":
				addbulk(request, response);
				break;
			case "/fileupload":
				fileupload(request, response);
				break;
			case "/memo":
				memo(request, response);
				break;
			case "/createMemo":
				createMemo(request, response);
				break;
			case "/searchMemo":
				searchMemo(request, response);
				break;
			case "/updateMemo":
				updateMemo(request, response);
				break;
			case "/History":
				History(request, response);
				break;
			case "/HistoryAction":
				HistoryAction(request, response);
				break;
			case "/historydate":
				HistoryDate(request, response);
				break;
			default:
				listall(request, response);
				break;
			}
			
		}catch(Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	// add laptop to database
	private void addLaptop(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String serial = request.getParameter("laptopserial");
		String model = request.getParameter("model");
		String manufac = request.getParameter("manufacture");
		String notes = request.getParameter("notes");
		
		System.out.println("adding : " + serial + " " + model + " manufacture " + manufac + " note: " + notes);
		
		if(notes != null || !notes.isEmpty() ) { // check if notes is NOT empty, null
			if( dbmodel.addLaptop(serial, model, manufac, notes) == true) {
				System.out.println("success, added new laptop to DB");
				String message = "success added to db " + serial;
				request.setAttribute("message", message);
			}else {
				System.out.println("failed to add");
				String message = "failed to add, serial exists: " + serial;
				request.setAttribute("message", message);
			}
		}else {
			if( dbmodel.addLaptop(serial, model, manufac) == true) {
				System.out.println("success, added new laptop to DB");
				String message = "success added to db " + serial;
				request.setAttribute("message", message);
			}else {
				System.out.println("failed to add");
				String message = "failed to add, serial exists: " + serial;
				request.setAttribute("message", message);
			}
		}
		
		// dispatch to the jsp
		RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
		dispatch.forward(request, response);
	}
	
	
	// search by date on history
	private void HistoryDate(HttpServletRequest request, HttpServletResponse response) {
		String date = request.getParameter("historydate");
		String exact_date_radio = request.getParameter("dexact"); // get the radio parameter
		System.out.println("Date: " + date);
		System.out.println("Radio: " + exact_date_radio);
		
		String year = "";
		String month = "";
		String day = "";
		// date will come in as 2022-09-21
		String[] data = date.split("-"); 
		for(int i=0; i<data.length; i++) {
			switch(i){
			case 0: 
				System.out.println("year: " + data[i]);
				year = data[i];
				break;
			case 1:
				System.out.println("month: " + data[i]);
				month = data[i];
				break;
			case 2:
				System.out.println("day: " + data[i]);
				day =  data[i];
				break;
			default:
				System.out.println("unknown: " + data[i]);
				break;
				
			}
			
		}// end for loop
		
		String message = "";
		try {
			List<LaptopHistory> lapshistory = dbmodel.searchHistoryDate(year, month, day, exact_date_radio);
			if(lapshistory.isEmpty()) {
				message = "History empty for date " + date;
			}else {
				message = "History not empty for date " + date;
				request.setAttribute("history", lapshistory);
				int size = lapshistory.size();
				request.setAttribute("size", size);
			}
			
			try {
				request.setAttribute("message", message);
				RequestDispatcher dis = request.getRequestDispatcher("History.jsp");
				dis.forward(request, response);
				return;
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// get the info from client
	// search History Action
	private void HistoryAction(HttpServletRequest request, HttpServletResponse response) {
		String historyaction = request.getParameter("historyaction");
		System.out.println("Action: " + historyaction);
		
		String message = "";
		try {
			List<LaptopHistory> lapshistory = dbmodel.searchHistoryAction(historyaction);
			if(lapshistory.isEmpty()) {
				message = "History empty set";
				//request.setAttribute("laphistory", lapshistory);
			}else {
				message = "History not empty";
				request.setAttribute("history", lapshistory);
				int size = lapshistory.size();
				request.setAttribute("size", size);
			}
			
			try {
				request.setAttribute("message", message);
				RequestDispatcher dis = request.getRequestDispatcher("History.jsp");
				dis.forward(request, response);
				return;
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("exception", e.getMessage());
			RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
			try {
				dis.forward(request, response);
				return;
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	// do a history on the laptop using serial
	private void History(HttpServletRequest request, HttpServletResponse response) {
		
		String searchlaptopserial = request.getParameter("searchlaptopserial").trim().strip();
		
		System.out.println("History search on: " + searchlaptopserial );
		String message = "";
		try {
			List<LaptopHistory> lapshistory = dbmodel.searchHistory(searchlaptopserial);
			if(lapshistory.isEmpty()) {
				message = "History empty set";
				//request.setAttribute("laphistory", lapshistory);
			}else {
				message = "History not empty";
				request.setAttribute("history", lapshistory);
				int size = lapshistory.size();
				request.setAttribute("size", size);
			}
			
			try {
				request.setAttribute("message", message);
				RequestDispatcher dis = request.getRequestDispatcher("History.jsp");
				dis.forward(request, response);
				return;
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("exception", e.getMessage());
			RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
			try {
				dis.forward(request, response);
				return;
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	// search for a laptop
	private void searchLaptop(HttpServletRequest request, HttpServletResponse response)throws SQLException, ServletException, IOException {
		String lapserial = request.getParameter("laptopserial").trim().strip();
		String model = request.getParameter("model").strip().trim();
		String manufacture = request.getParameter("manufacture");
		String notes = request.getParameter("notes");
		String serialcheck = request.getParameter("serialcheck");
		String notescheck = request.getParameter("notescheck");
		String usercheck = request.getParameter("usercheck"); // the user checkbox 
		String username = request.getParameter("username");
		
		//System.out.println("search for: " + lapserial +" "+ model + " " + manufacture);
		//System.out.println("Serial check box: " + serialcheck);
		System.out.println("Notes check box: " + notescheck);
		System.out.println("User check box: " + usercheck);
		
		if(usercheck != null) { // user checkbox is checked
			List<Laptop> searchuser = dbmodel.searchUser(username);
			int size = searchuser.size();
			request.setAttribute("searchresults", searchuser);
			request.setAttribute("searchsize", size);
			RequestDispatcher dispatch = request.getRequestDispatcher("searchresults.jsp");
			dispatch.forward(request, response);
			return;
		}
		
		if(notescheck != null) {
			List<Laptop> searchnotes = dbmodel.searchNotes(notes);
			int sizeint = searchnotes.size();
			request.setAttribute("searchresults", searchnotes);
			request.setAttribute("searchsize", sizeint);
			RequestDispatcher dispatch = request.getRequestDispatcher("searchresults.jsp");
			dispatch.forward(request, response);
			return;
			
		}else if(model.isEmpty()) {
			System.out.println("model is empty");
			System.out.println("Search Serial: " + lapserial);
			List<Laptop> searchlaps = dbmodel.searchSerial(lapserial);
			String thesize = "";
			int size = searchlaps.size();
			if(size > 0) {
				thesize = "got some results";
			}else
				thesize = "no resutls";
			
			request.setAttribute("searchresults", searchlaps);
			request.setAttribute("searchsize", size);
			RequestDispatcher dispatch = request.getRequestDispatcher("searchresults.jsp");
			dispatch.forward(request, response);
			return;
			
		}else if(!model.isEmpty()){
			System.out.println("model NOT empty " + model);
			List<Laptop> searchlaps = dbmodel.searchModel(model);
			String thesize = "";
			int size = searchlaps.size();
			if(size > 0) {
				thesize = "got some results";
			}else
				thesize = "no resutls";
			
			request.setAttribute("searchresults", searchlaps);
			request.setAttribute("searchsize", size);
			RequestDispatcher dispatch = request.getRequestDispatcher("searchresults.jsp");
			dispatch.forward(request, response);
			return;
		}		
		
	}
	
	
	// list all laptops
	private void listall(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("\nlistall called");
		
		List<Laptop> laps = dbmodel.listAll();
		int unassignedint = dbmodel.getUnAssigned();
		request.setAttribute("listlaptops", laps);
		request.setAttribute("onhand", unassignedint);
		RequestDispatcher dispatch = request.getRequestDispatcher("listall.jsp");
		dispatch.forward(request, response);
	}

	
	// list by model type
	private void listModel(HttpServletRequest request, HttpServletResponse response) throws SQLException, Exception {
		
		System.out.println("listModel called");
		List<LModel> models = dbmodel.listModels();
		System.out.println("listmodel size: " + models.size());
		request.setAttribute("listmodels", models);
		RequestDispatcher dispatch = request.getRequestDispatcher("listmodel.jsp");
		dispatch.forward(request, response);
	}
	
	// get only the serial#, 
	private void listserialsonly(HttpServletRequest request, HttpServletResponse response){
		
		System.out.println("list_serials_only");
		// listonlySerials
		try {
			List<Laptop> laps = dbmodel.listonlySerials();
			request.setAttribute("serials", laps);
			request.setAttribute("size", laps.size());
			RequestDispatcher dis = request.getRequestDispatcher("listserials.jsp");
			dis.forward(request, response);
			return;
		}catch(Exception e) {
			System.err.println("error in listserialsonly \n" + e.getMessage());
		}
		
	}
	
	// update the laptop 
	private void updateLaptop(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		
		System.out.println("update called...");
		String message = "";
		
		String serialid = request.getParameter("laptopserial");
		String model = request.getParameter("model");
		String manufacture = request.getParameter("manufacture");
		String notes = request.getParameter("notes");
		String checkassigntonewuser_checkbox = request.getParameter("checkassigntonewuser");
		String radio_un_assign = request.getParameter("radioassign");
		System.out.println("the radio: " + radio_un_assign);
		
		
		if(radio_un_assign != null) {
			if(radio_un_assign.equals("unassignto")) {
				Laptop lap = new Laptop(serialid, model, manufacture, notes);
				if( dbmodel.unAssign(lap) == true) {
					message = "success un-assign " + serialid;
				}else {
					message = "Failed to un-assign " + serialid;
				}
			}
		}else if(checkassigntonewuser_checkbox != null) {
			System.out.println("check box is checked ");
			String assign_to_user = request.getParameter("assigntouser");
			System.out.println("Assign to user: " + assign_to_user);
			Laptop lap = new Laptop(serialid, model, manufacture, notes);
			if( dbmodel.assignTo(lap, assign_to_user) == true) {
				message = "success updated " + serialid;
			}
		}else {
			System.out.println("updating: " + serialid + " " + model + " " + manufacture + " " + notes);
						
			Laptop lap = new Laptop(serialid, model, manufacture, notes);
			if( dbmodel.update(lap) == true) {
				message = "success updated " + serialid;
				System.out.println("updated success");
				
			}else {
				message = "failed to update " + serialid;
			}
		}
		
		request.setAttribute("message", message);
		RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp"); // send to success.jsp for now, need to change
		dispatch.forward(request, response);
		
	}
	
	// show the edit form to edit a laptop info/details
	private void showEditform(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		
		System.out.println("showeditform called");
		String serialsearch = request.getParameter("serialid");
		System.out.println("editing: " + serialsearch);
		Laptop la = dbmodel.searchSerialID(serialsearch); // search serial in DB, get a Laptop object
		System.out.println("found in DB: " + la.getSerial() + "  " + la.getManufacture() );
		
		//set the request attribute, send the edit form
		request.setAttribute("laptop", la);
		RequestDispatcher dispatch = request.getRequestDispatcher("editform.jsp");
		dispatch.forward(request, response);
		
	}
	
	/* -- we have a 2 step approach to delete a unit from the DB -- */
	// 1. delete a laptop, need to show the delete form first, then the user calls deleteMe
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("delete form called");
		String serialsearch = request.getParameter("serialid");
		System.out.println("deleting: " + serialsearch);
			try {
			Laptop la = dbmodel.searchSerialID(serialsearch); // search serial in DB, get a Laptop object
			System.out.println("found in DB: " + la.getSerial() + "  " + la.getManufacture() );
			//set the request attribute, send the edit form
			request.setAttribute("laptop", la);
			RequestDispatcher dispatch = request.getRequestDispatcher("deleteform.jsp");
			dispatch.forward(request, response);
			return;
		}catch(SQLException s) {
			
			request.setAttribute("exception", s.getMessage());
			RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
			dis.forward(request, response);
		}
		
	}
	
	// 2. deleteMe is called after delete, user confirmed
	private void deleteMe(HttpServletRequest request, HttpServletResponse response) throws Exception{
		System.out.println("deleteMe called\n");
		String message = "";
		String serialsearch = request.getParameter("laptopserial");
		System.out.println("removing from DB: " + serialsearch);
		boolean ok = dbmodel.delete(serialsearch);
		if(ok) {
			System.out.println("ok, deleted.");
			message = "deleted from DB: " + serialsearch;
		}else {
			System.out.println("not deleted");
			message = "error deleting: " + serialsearch;
		}
		
		request.setAttribute("message", message);
		RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
		dispatch.forward(request, response);
	}
	
	// login
	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
		
		String uname = request.getParameter("uname");
		String password = request.getParameter("passw");
		String hiddenlink = request.getParameter("hiddenlink");
		System.out.println("The hiddenlink: " + hiddenlink);
		
		String message = "";
		
		boolean usercheck = dbmodel.checkLogin(uname, password); // check in DB if credentials are correct
		
		if(usercheck) {
			// get the session
			HttpSession session = request.getSession();
			// set the user to the session
			session.setAttribute("user", uname);
			message = "credentials passed";
			request.setAttribute("message", message);
			if(hiddenlink !=null && !hiddenlink.equals("null")) {
				if(hiddenlink.equals("editform.jsp")) {
					String serial = request.getParameter("editserial");
					System.out.println("edit serial, login:> " + serial);
					Laptop l = dbmodel.searchSerialID(serial);
					System.out.println("Laptop to edit from login: " + l.toString() );
					request.setAttribute("laptop", l);
					RequestDispatcher dispatch = request.getRequestDispatcher(hiddenlink);
					dispatch.forward(request, response);
					return;
				}else {
					System.out.println("hidding link NOT null");
					RequestDispatcher dispatch = request.getRequestDispatcher(hiddenlink);
					dispatch.forward(request, response);
					return;
				}
				/*if(hiddenlink.equals("addlaptop.jsp")) {
					RequestDispatcher dispatch = request.getRequestDispatcher("addlaptop.jsp");
					dispatch.forward(request, response);
					return;
				}else if(hiddenlink.equals("memos.jsp")) {
					RequestDispatcher dispatch = request.getRequestDispatcher("memos.jsp");
					dispatch.forward(request, response);
					return;
				}else {
					RequestDispatcher dispatch = request.getRequestDispatcher("welcome.jsp");
					dispatch.forward(request, response);
					return;
				}*/
			}else {
				System.out.println("hidding link is NULL");
				//response.sendRedirect("welcome.jsp");
				RequestDispatcher dispatch = request.getRequestDispatcher("welcome.jsp");
				dispatch.forward(request, response);
				return;
				
			}
		}else {
			message = " somehting wrong with credentials, either username or password is incorrect, please try again\n Un-Authorised use of this application will result in termination! ";
			request.setAttribute("message", message);
			RequestDispatcher dispatch = request.getRequestDispatcher("login.jsp");
			dispatch.forward(request, response);
		}
	}
	
	// log out user
	private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		
		// get the HttpSession
		HttpSession session = request.getSession();
		String user = (String)session.getAttribute("user");
		dbmodel.logout(user);
		session.removeAttribute("user");
		session.invalidate();
		RequestDispatcher dispatch = request.getRequestDispatcher("login.jsp");
		dispatch.forward(request, response);
	}
	
	
	// add bulk laptops
	private void addbulk(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		String data1 = (String)request.getParameter("bulklaptops");
		String[] data2lines = data1.split("\\r?\\n"); // split by new line
		String serial = "";
		String model = "";
		String manuf = "";
		Laptop lap ;
		ArrayList<Laptop> laps = new ArrayList<>();
		
		System.out.println("data: " + data1);
		System.out.println("split by new line: ");
		for(String s: data2lines) {
			System.out.println(s.trim().strip());
			String[] commas = s.split(","); // split by commas
			System.out.println("split by comma - lenght:" + commas.length);
			lap = new Laptop();
			for(int i=0; i < commas.length; i++) {
				System.out.println( commas[i].strip() );
				switch(i) {
				case 0:
					serial = commas[i].strip();
					System.out.println("Serial: " + serial);
					lap.setSerial(serial);
					break;
				case 1: 
					model = commas[i].strip();
					System.out.println("Model: " + model);
					lap.setModel(model);
					break;
				case 2:
					manuf = commas[i].strip();
					System.out.println("Manufacture: " + manuf);
					lap.setManufacture(manuf);
					break;
				}// end switch
				
				
			}// end for 
			System.out.println("Laptop object: " + lap );
			if( dbmodel.addLaptop(serial, model, manuf) )
				laps.add(lap);
			else
					System.out.println("error adding: " + lap);
		}// end for
		
		request.setAttribute("bulkdata", data1);
		request.setAttribute("data2", data2lines);
		request.setAttribute("bulkadded", laps);
		RequestDispatcher dispatch = request.getRequestDispatcher("showbulk.jsp");
		dispatch.forward(request, response);
		
	}
	
	// process the file upload
	private void fileupload(HttpServletRequest request, HttpServletResponse response) {
		String message = "";
		
		try {
			ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory() );
			List<FileItem> multifiles ;
			multifiles = sf.parseRequest(request);
			for(FileItem item: multifiles) {
				File f = new File("C:\\mywork2\\upload2\\" + item.getName() );
				// hand off file to the ProcessFile thread
				//ProcessFile pf = new ProcessFile(f);
				//pf.start(); // start the thread
				
				//item.write( new File( "C:\\mywork2\\upload\\" + item.getName()) );
				item.write( f );
				if( dbmodel.processFile(f) == true) {
					message = "success adding new laptops to inventory DB";
				}else {
					message = "error in db, could not add laptops";
				}
				//ProcessFile pf = new ProcessFile(f);
				//pf.start();
			}
			
			request.setAttribute("message", message);
				
			// dispatch to the jsp
			RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
			dispatch.forward(request, response);
		}catch(Exception e) {
			System.err.println(e);
			String errormessage = e.getMessage();
			request.setAttribute("message", errormessage);
			// dispatch to the jsp
						
			try {
				RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
				dispatch.forward(request, response);
				return;
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
		}
	} // end file upload
	
	// get a listing of memos
	private void memo(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		
		ArrayList<Memo> listing = dbmodel.getMemos();
		request.setAttribute("memolisting", listing);
		RequestDispatcher dispatch = request.getRequestDispatcher("memos.jsp");
		dispatch.forward(request, response);
	}
	
	// create new memo
	private void createMemo(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		// get info from the client HTTP request
		String memoid = request.getParameter("MemoID");
		String datas = request.getParameter("serials");
		String[] serials = datas.split("\\r?\\n"); // split the string into new lines
		// serials
		
		if(memoid.equals("") || memoid == null) {
			String message = "MemoID can't be null, need to enter a MemoID to create";
			request.setAttribute("message", message);
			RequestDispatcher dis = request.getRequestDispatcher("fail.jsp");
			dis.forward(request, response);
			return;
		}else {
			try {
				if(	dbmodel.createMemo(memoid, serials)) {
				
					String message = "memo added " + memoid;
					request.setAttribute("message", message);
					
					// dispatch to the jsp
					RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
					dispatch.forward(request, response);
					return;
				}else {
					String message = "failed to create Memo " + memoid;
					request.setAttribute("message", message);
					// dispatch to the jsp
					RequestDispatcher dispatch = request.getRequestDispatcher("fail.jsp");
					dispatch.forward(request, response);
					//return;;
				}
			}catch(SQLException s) {
				request.setAttribute("exception", s.getMessage());
				RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
				dis.forward(request, response);
			}catch(IOException io) {
				request.setAttribute("exception", io.getMessage());
				RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
				dis.forward(request, response);
			}catch(Exception e) {
				request.setAttribute("exception", e.getMessage());
				RequestDispatcher dis = request.getRequestDispatcher("Error.jsp");
				dis.forward(request, response);
			}
		}
	}
	
	
	// search memo for memoID or serial
	private void searchMemo(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		String memoid = request.getParameter("MemoID");
		String serial = request.getParameter("serials");
		String[] serials = serial.split("\\r?\\n");
		
		if( !memoid.isEmpty()) {
			System.out.println("MemoID: " + memoid);
			boolean ok = dbmodel.isMemoIDExist(memoid); // check if memoid exists in DB
			if(ok == true) { // it does
				boolean found = true;
				Memo memo = dbmodel.getMemo(memoid); // get a Memo Object/Bean
				request.setAttribute("memobean", memo);
				request.setAttribute("isfound", found);
				ArrayList<Laptop> laps = dbmodel.searchMemoID(memoid); // get the laptops listed in the memoid
				request.setAttribute("memoresults", laps); // set the arraylist to the request
				int size = laps.size();
				request.setAttribute("searchsize", size);
				request.setAttribute("memoid", memoid);
				RequestDispatcher dispatch = request.getRequestDispatcher("memoresults.jsp");
				dispatch.forward(request, response);
				return;
			}else {
				// memoID doesn't exist in DB
				boolean found = false;
				request.setAttribute("isfound", found);
				request.setAttribute("memoid", memoid);
				RequestDispatcher dispatch = request.getRequestDispatcher("memoresults.jsp");
				dispatch.forward(request, response);
				return;
			}
			
		}else if( !serial.isEmpty()) {
			System.out.println("serial not null, searching for: " + serial);
			
				//System.out.println(serials[i]);
				// search the db for the serial# in the memos table
			String result = dbmodel.getMemoID(serial);
			if(result.equals("nothing")) {
				boolean found = false;
				request.setAttribute("found", found);
				request.setAttribute("nothing", result);
				request.setAttribute("serial", serial);
				RequestDispatcher dispatch = request.getRequestDispatcher("memoserial.jsp");
				dispatch.forward(request, response);
				return;
			}else {
				boolean found = true;
				request.setAttribute("found", found);
				request.setAttribute("memoID", result);
				request.setAttribute("serial", serial);
				RequestDispatcher dispatch = request.getRequestDispatcher("memoserial.jsp");
				dispatch.forward(request, response);
				return;
			}
		}
	}
	
	// update Memo info
	private void updateMemo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String memoid = request.getParameter("memoID");
		String state = request.getParameter("state");
		
		String fedex = request.getParameter("fedex");
		
		System.out.println("\nUpdateMemo info:\nMemoID:  " + memoid);
		System.out.println("State: " + state);
		System.out.println("fedex: " + fedex);
		
		if(state.equals("intransit")) {
			// dbmodel memoinTransit(String memoid, String fedex)
			if(dbmodel.memoinTransit(memoid, fedex)) {
				String message = "updated memo, inventory";
				request.setAttribute("message", message);
				RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
				dispatch.forward(request, response);
				return;
			}
		}else if(state.equals("delivered")) {
			if(dbmodel.memoDelivered(memoid)) {
				String message = "updated Memo " + memoid + " state to Delivered";
				request.setAttribute("message", message);
				RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
				dispatch.forward(request, response);
				return;
			}
		}else if(state.equals("onhand")) { // package is on hand, but we have a FedEx#, ship out later
			if(dbmodel.updateMemo(memoid, fedex)) {
				String message = "success updated Memo " + memoid + " with FedEx " +    fedex       ; 
				request.setAttribute("message", message);
				RequestDispatcher dispatch = request.getRequestDispatcher("success.jsp");
				dispatch.forward(request, response);
				return;
			}
		}
	}
}

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Location;
import classes.ProfilePoster;
import classes.ProfileSearcher;
import classes.Session;
import classes.Utility;

import managers.DBManager;
import managers.EmailManager;

public class ServletProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;

	public ServletProfile() {
        super();
		dbManager = DBManager.getInstance();
    }
	
	private enum EnumAction
	{ 
		createProfile,
		editProfile,
		getProfileById,
		searchJobSearcher,
		getProfileBySessionKey,
		UNKNOWN;
	private static EnumAction getAct(String Str)//why static?
	{
	try {return valueOf(Str);}
	catch (Exception ex){return UNKNOWN;}
	}
	};
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("action");
		switch(EnumAction.getAct(action)){
		
			case getProfileById:
				getProfileById(request,response);
				break;
				
			case createProfile:
				createProfile(request, response); //implement error checks
				break;
				
			case editProfile:
				editProfile(request,response);
				break;
				
			case searchJobSearcher:
				searchJobSearcher(request,response);
				break;
				
			case getProfileBySessionKey:
				getProfileBySessionKey(request, response);
				break;
				
			default:
				System.out.print("Dont you try to hack =D");
				break;
		
		}//ENDOF SWITCH
	
	}//ENDOF processReq Func
	
	
	private void getProfileById(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		//Poster = 1, Searcher = 2
		int accountType = Integer.parseInt(request.getParameter("accountType")); 	
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		
		//Initialize Return statments
		boolean isSuccessful = false;
		String message = "Failure to create new profile";
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
		//Get Job Poster Profile			
			if(accountType == 1){
				System.out.println("Getting Job Poster Profile");
				
				ProfilePoster poster = new ProfilePoster();
				
			/**Get field values */
				String query = 
					"SELECT * FROM tableProfilePoster WHERE idAccount=" + accountID;
				
				System.out.println("getJobAdById query:" + query);
				isSuccessful = stmt.execute(query);
				
				ResultSet result = stmt.getResultSet();
				
				if (result.first()){
					System.out.println("getJobAd successful");
					message = "getJobAd successful";
					
					poster.accountID		= result.getInt("idAccount");
					poster.name				= result.getString("name");
					poster.phone		 	= result.getString("phone");
					poster.selfDescription	= result.getString("selfDescription");
				}
				else{ //Error case
					isSuccessful = false;
					message = "Error: Profile not found with ID=" + accountID;
					System.out.println("Error: Profile not found with ID=" + accountID);
				}
				
				
			/**Get Location values */
				ArrayList<Location> addressList = new ArrayList<Location>();
				Location address = new Location();
				
				query = "SELECT * FROM tableLocationProfile WHERE " +
						"idAccount= '" + accountID +"'";
				result = stmt.getResultSet();
				
				if(!result.first()){
					System.out.println("Error: failed to find the inserted location");
				}
				else{
					while(result.next()){
						//Get Address, Longitude, Latitude
						address.address = result.getString("location");
						address.longitude = result.getDouble("longitude");
						address.latitude = result.getDouble("latitude");	
					}
					addressList.add(address);
				}
			
				poster.addressList= addressList;
				
			}
			
		//Get Job Searcher Profile
			else{
				System.out.println("Getting Job Searcher Profile");
				
				ProfileSearcher searcher = new ProfileSearcher();
				
				/**Get field values */
					String query = 
						"SELECT * FROM tableProfileSearcher WHERE idAccount=" + accountID;
					
					System.out.println("getJobAdById query:" + query);
					isSuccessful = stmt.execute(query);
					
					ResultSet result = stmt.getResultSet();
					
					if (result.first()){
						System.out.println("getJobAd successful");
						message = "getJobAd successful";
						
						searcher.accountID			= result.getInt("idAccount");
						searcher.name				= result.getString("name");
						searcher.phone		 		= result.getString("phone");
						searcher.selfDescription	= result.getString("selfDescription");
						searcher.educationLevel		= result.getInt("educationLevel");
						searcher.preferredStartDate = result.getLong("startingDate");
						
					}
					else{ //Error case
						isSuccessful = false;
						message = "Error: Profile not found with ID=" + accountID;
						System.out.println("Error: Profile not found with ID=" + accountID);
					}
					
					
				/**Get Location values */
					ArrayList<Location> addressList = new ArrayList<Location>();
					Location address = new Location();
					
					query = "SELECT * FROM tableLocationProfile WHERE " +
							"idAccount= '" + accountID +"'";
					result = stmt.getResultSet();
					
					if(!result.first()){
						System.out.println("Error: failed to find the inserted location");
					}
					else{
						while(result.next()){
							//Get Address, Longitude, Latitude
							address.address = result.getString("location");
							address.longitude = result.getDouble("longitude");
							address.latitude = result.getDouble("latitude");	
						}
						addressList.add(address);
					}
				
					searcher.addressList= addressList;
					
				/** TODO: GET EMPLOYMENT PREFERENCE **/
				
					
			}
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
	
		}
		
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
	    System.out.println("Checkpoint: End of create profile - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		//TODO pass back XML formatted profile data
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	private void getProfileBySessionKey(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		//Poster = 1, Searcher = 2
		String sessionKey = request.getParameter("sessionKey");
		
		Session currSession = dbManager.getSessionByKey( sessionKey );
		
		//Initialize Return statements
		boolean isSuccessful = false;
		String message = "Failure to fetch profile";
		
		Connection conn = null;	
		Statement stmt = null;
		Object profile = null;
		
		earlyExit:
		{
		try{

			
			String acctType = "";
			
			ArrayList<Location> profileAddressList = new ArrayList<Location>();
			
			if( currSession.checkPrivilege( "searcher") ) {
				acctType = "Searcher";
				ProfileSearcher searcher = new ProfileSearcher();
				searcher.addressList = profileAddressList;
				profile = searcher;
			}
			else if ( currSession.checkPrivilege( "poster") ) {
				acctType = "Poster";
				
				ProfilePoster poster = new ProfilePoster();
				poster.addressList = profileAddressList;
				profile = poster;
			}
			else {
				break earlyExit;
			}
			
			System.out.println("Getting Job " + acctType + " Profile");
			
			conn = dbManager.getConnection();
			stmt = conn.createStatement();
			
			//TODO add join statement to retrieve email info
			String query = 
					"SELECT * FROM tableProfile"+ acctType +" WHERE idAccount=" + currSession.getIdAccount();
			
			System.out.println("getJobAdBySessionKey query:" + query);
			isSuccessful = stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			if (result.first()){

				System.out.println("getJobAd successful");
				message = "getJobAd successful";
				
				if ( currSession.getAccountType().equals("searcher") ) {
					ProfileSearcher searcher = (ProfileSearcher)profile;
					searcher.accountID			= result.getInt("idAccount");
					searcher.name				= result.getString("name");
					searcher.phone		 		= result.getString("phone");
					searcher.selfDescription	= result.getString("selfDescription");
					searcher.educationLevel		= result.getInt("educationLevel");
					searcher.preferredStartDate = result.getLong("startingDate");
				}
				if ( currSession.getAccountType().equals("poster") ) {
					ProfilePoster poster = (ProfilePoster)profile;
					
					poster.accountID		= result.getInt("idAccount");
					poster.name				= result.getString("name");
					poster.phone		 	= result.getString("phone");
					poster.selfDescription	= result.getString("selfDescription");
					
				}
					
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: Profile not found with ID=" + currSession.getIdAccount();
				System.out.println("Error: Profile not found with ID=" + currSession.getIdAccount());
			}
				
				
			/**Get Location values */
			
			query = "SELECT * FROM tableLocationProfile WHERE " +
						"idAccount= '" + currSession.getIdAccount() +"'";
			
			result = stmt.getResultSet();
			
			ArrayList<Location> fetchedAddressList = new ArrayList<Location>();
			
			if(!result.first()){
					System.out.println("Error: failed to find the inserted location");
			}
			else{
				Location address = new Location();
				while(result.next()){
					//Get Address, Longitude, Latitude
					address.address = result.getString("location");
					address.longitude = result.getDouble("longitude");
					address.latitude = result.getDouble("latitude");	
				}
				fetchedAddressList.add(address);
			}
			
			profileAddressList = fetchedAddressList;
			
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
	
		}
		
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
		}//earlyExit:
	    System.out.println("Checkpoint: End of create profile - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		
		if ( currSession.getAccountType().equals("searcher") ) {
			XMLResponse.append( ((ProfileSearcher)profile).toXMLContent() );
		}
		else if ( currSession.getAccountType().equals("poster") ) {
			XMLResponse.append( ((ProfilePoster)profile).toXMLContent() );
		}
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	
	private void searchJobSearcher(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//TODO: port search from job ad search
		
		
	}
	
	
	
	
	private void createProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("Checkpoint: Inside createProfile");
		
		//Account Type: Poster = 1, Searcher = 2
		int accountType = Integer.parseInt(request.getParameter("accountType")); 	
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		
		String name;
		String phone;
		String selfDescription;
		String empPref;
		long preferredStartDate;
		int educationLevel;
		
		//Initialize Return statments
		boolean isSuccessful = false;
		String message = "Failure to create new profile";
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
		//Create Job Poster Profile
			if(accountType == 1){
				System.out.println("Creating Job Poster Profile");
				stmt = conn.createStatement();
				
				name = request.getParameter("posterName");
				phone = request.getParameter("posterPhone");
				selfDescription = request.getParameter("posterDescription");

				//Check format
				name = Utility.checkInputFormat( name );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );
				
			//Add new entry with specified paramters into database
				String query = 
					"INSERT INTO tableProfilePoster(idAccount, name, phone, selfDescription) " + 
					"VALUES ('"
						+ accountID + "','"
						+ name + "','"
						+ phone + "','"
						+ selfDescription + 
					"')";
				
				System.out.println("New PosterProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("New Profile Creation success (DB)");
				}
				else{
					System.out.println("Error: row not inserted");
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT idAccount FROM tableProfilePoster WHERE " + " idAccount='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() ){
					System.out.println("Profile Created in DB");
					isSuccessful = true;
					message = "Create new profile success";
				}
				else{
					System.out.println("Error: result.first() is false ");
				}
			}
			
		//Create Job Searcher Profile
			else{
				System.out.println("Creating Job Searcher Profile");
				
				name = request.getParameter("searcherName");
				phone = request.getParameter("searcherPhone");
				selfDescription = request.getParameter("searcherDescripton");
				empPref = request.getParameter("empPref");
				educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
				preferredStartDate = Long.parseLong(request.getParameter("startingDate"));

				stmt = conn.createStatement();
				
				//Check format
				name = Utility.checkInputFormat( name );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );

				//TODO: include address
				String query = 
					"INSERT INTO tableProfileSearcher(idAccount, name, phone, selfDescription, educationLevel, startingDate) " + 
					"VALUES ('"
						+ accountID + "','"
						+ name + "','"
						+ phone + "','"
						+ selfDescription + 
						+ educationLevel + "','"
						+ preferredStartDate + 
					"')";

				// if successful, 1 row should be inserted
				System.out.println("New SearcherProfile Query:" + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted == 1){
					System.out.println("New Profile Creation success (DB)");
				}
				else{
					stmt.close();
				}
				
				//Check if profile is created successfully
				query = "SELECT name FROM tableProfileSearcher WHERE " + " idAccount='" + accountID + "'"; 
				ResultSet result = stmt.executeQuery(query);
				if( result.first() ){
					System.out.println("Profile Created in DB");
					isSuccessful =  true;
					message = "Create new profile success";
				}
				else{
					System.out.println("Error: result.first() is false ");
				}
			}
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
	
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
	    System.out.println("Checkpoint: End of create profile - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	    

	    
	}
	
	
	private void editProfile(HttpServletRequest request, HttpServletResponse response) 
								throws ServletException, IOException{
		
		//Account Type: Poster = 1, Searcher = 2
		int accountType = Integer.parseInt(request.getParameter("strAccountType")); 	
		int accountID = Integer.parseInt(request.getParameter("accountID"));
		
		String name;
		String secEmail;
		String phone;
		String selfDescription;
		String empPref;
		long preferredStartDate;
		int educationLevel;
		
		//Initialize return statements
		boolean isSuccessful = false;
		String message = "failed to edit profile";
		
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
		//Edit Job Poster Profile
			if(accountType == 1){

				stmt = conn.createStatement();
				
				name = request.getParameter("posterName");
				secEmail = request.getParameter("posterSecEmail");
				phone = request.getParameter("posterPhone");
				selfDescription = request.getParameter("posterDescription");
				
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );
				
				String query = 
					"UPDATE tableProfilePoster SET " 
					+ "name='" 				+ name + "','" 
					+ "secondaryEmail='" 	+ secEmail + "','" 
					+ "phone='" 			+ phone + "','" 
					+ "selfDescription='" 	+ selfDescription + "' " +
					"WHERE idAccount='" 	+ accountID + "' ";
					
				
				System.out.println("New PosterProfile Query:" + query);
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
				}
				else{
					isSuccessful = true;
					message = "Edit Poster Profile success!";
					System.out.println("Edit Poster Profile success!");
				}
				
			}
			
		//Edit Job Searcher Profile
			else{
				name = request.getParameter("searcherName");
				secEmail = request.getParameter("searcherSecEmail");
				phone = request.getParameter("searcherPhone");
				selfDescription = request.getParameter("searcherDescripton");
				empPref = request.getParameter("empPref");
				educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
				preferredStartDate = Long.parseLong(request.getParameter("startingDate"));
				
				stmt = conn.createStatement();
				
				name = Utility.checkInputFormat( name );
				secEmail = Utility.checkInputFormat( secEmail );
				phone = Utility.checkInputFormat( phone );
				selfDescription = Utility.checkInputFormat( selfDescription );

				//TODO: include address
				String query = 
					"UPDATE tableProfileSearcher SET " 
					+ "name= '" 			+ name + "','" 
					+ "secondaryEmail='" 	+ secEmail + "','" 
					+ "phone='" 		+ phone + "','" 
					+ "selfDescription='" 	+ selfDescription + "','"
					+ "empPref='" 			+ empPref + "','"
					+ "educationLevel='" 	+ educationLevel + "','" 
					+ "startingDate='"	+ preferredStartDate + "' " +
					"WHERE idAccount='" 	+ accountID + "' ";
					
				
				// if successful, 1 row should be inserted
				System.out.println("New SearcherProfile Query:" + query);
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
				}
				else{
					isSuccessful = true;
					message = "Edit Searcher Profile Success!";
					System.out.println("Edit Searcher Profile success!");
				}
			}
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
	
		}
		
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}

}







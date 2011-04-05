package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.DBColName;
import classes.DbQuery;
import classes.JobAdvertisement;
import classes.Location;
import classes.ProfilePoster;
import classes.ProfileSearcher;
import classes.Session;
import classes.Utility;

import servlets.ServletDocument;

import managers.AccountManager;
import managers.DBManager;
import managers.EmailManager;

public class ServletProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;
	private DBColName DbDict =	ServletInitializer.retDbColName();
	private DbQuery DBQ =new DbQuery();
	private AccountManager accManager;	

	public ServletProfile() {
        super();
		dbManager = DBManager.getInstance();
		accManager = new AccountManager();
    }
	
	private enum EnumAction
	{ 
		//createProfile,
		editProfile,
		getProfileById,
		getProfileSearcherById,
		getProfileBySessionKey,
		searchProfile,
		searchSearcherProfile,
		viewAllSearchers,
		smrSearcherProfile,
		
		saveCandidate,
		listCandidate,
		deleteCandidate,
		
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
		

		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);
		
		
		switch(EnumAction.getAct(action)){
		
			case getProfileById:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					getProfileById(request,response);
				break;
				
			case getProfileSearcherById:
				if(session.checkPrivilege( response, "poster", "admin", "superAdmin") )
					getProfileSearcherById(request,response);
				break;
				
			case editProfile:
				if(session.checkPrivilege( response, "searcher", "poster") )
					editProfile(request,response, session);
				break;
				
			case getProfileBySessionKey:
				if(session.checkPrivilege( response, "searcher", "poster") )
					getProfileBySessionKey(request, response, session);
				break;
							
			case searchSearcherProfile:
				if(session.checkPrivilege( response, "poster", "admin", "superAdmin") )
					searchSearcherProfile(request, response);
				break;
				
			case viewAllSearchers:
				if(session.checkPrivilege( response, "poster", "admin", "superAdmin") )
					viewAllSearchers(request, response);
				break;
			case smrSearcherProfile:
				if(session.checkPrivilege( response, "searcher" ) )
					searcherProfileSummary(request, response, session);
				break;
				
			case saveCandidate:
				if(session.checkPrivilege( response, "poster") )
					saveCandidate( request, response, session );
				
			case deleteCandidate:
				if(session.checkPrivilege( response, "poster") )
					//TODO add session checking
					deleteCandidate( request, response );

			case listCandidate:
				if(session.checkPrivilege( response, "poster") )
					//TODO add session checking
					listCandidate( request, response );
		}//ENDOF SWITCH
	
	}//ENDOF processReq Func
	
	
	private void getProfileById(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		int accountID = Integer.parseInt(request.getParameter("idAccount"));
		
		//Initialize Return statments
		boolean isSuccessful = false;
		String message = "Failure to create new profile";
		
		ProfileSearcher searcher = new ProfileSearcher();
		ProfilePoster poster = new ProfilePoster();
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		String accountType = "";
		
		earlyExit: {
		try{
			stmt = conn.createStatement();

		
		String query = 		
			"SELECT * FROM tableAccount WHERE idAccount='" + accountID +"';";
		
		System.out.println( query );
		stmt.executeQuery(query);
		
		ResultSet result = stmt.getResultSet();
		
		// get info out from account table
		if ( result.first() ) {
			accountType = result.getString("type");
			if( accountType.equalsIgnoreCase("searcher") ) {
				searcher.accountID	= result.getInt("idAccount");
				searcher.email		= result.getString("email");
				searcher.secondaryEmail = result.getString("secondaryEmail");
				accountType = "Searcher";
			}
			else if ( accountType.equalsIgnoreCase("poster") ) {
				poster.accountID	= result.getInt("idAccount");
				poster.email		= result.getString("email");
				poster.secondaryEmail = result.getString("secondaryEmail");
				accountType = "Poster";
			}
			
		}
		
		query = "SELECT * FROM tableProfile"+ accountType +" INNER JOIN tableAccount " + 
		"USING (idAccount) WHERE idAccount='" + accountID + "';";
		
		isSuccessful = stmt.execute(query);
		
		result = stmt.getResultSet();
		
		//Get Job Poster Profile
			if( accountType.equalsIgnoreCase("poster") ){
				System.out.println("Getting Job poster Profile");

				if (result.first()){
					System.out.println("got Job poster profile");
					message = "got Job poster profile";
					
					poster.accountID		= result.getInt("idAccount");
					poster.name				= result.getString("name");
					poster.phone		 	= result.getString("phone");
					poster.selfDescription	= result.getString("selfDescription");
				}
				else{ //Error case
					isSuccessful = false;
					message = "Error: Profile not found with ID=" + accountID;
					System.out.println("Error: Profile not found with ID=" + accountID);
					break earlyExit;
				}
				
				
			/**Get Location values */
				ArrayList<Location> addressList = new ArrayList<Location>();
				Location address = new Location();
				
				query = "SELECT * FROM tableLocationProfile WHERE " +
						"idAccount='" + accountID +"';";
				stmt.executeQuery(query);
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
			else if (accountType.equalsIgnoreCase("searcher") ){
				System.out.println("Getting Job Searcher Profile");
				
				/**Get field values */
					
					if (result.first()){
						System.out.println("got Job searcher profile");
						message = "got Job poster profile";
						
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
						break earlyExit;
					}
					
					
				/**Get Location values */
					ArrayList<Location> addressList = new ArrayList<Location>();
					Location address = new Location();
					
					query = "SELECT * FROM tableLocationProfile WHERE " +
							"idAccount='" + accountID +"';";
					stmt.executeQuery(query);
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
		}//earlyExit:
		
	    System.out.println("Checkpoint: End of create profile - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		//TODO pass back XML formatted profile data
		if( accountType.equalsIgnoreCase("searcher") ) {
			XMLResponse.append(searcher.toXMLContent() );
			XMLResponse.append( ServletDocument.getFilesXMLByOwnerID( accountID ));
		} else if ( accountType.equalsIgnoreCase("poster") ) {
			XMLResponse.append(poster.toXMLContent() );
		}
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		
		System.out.println(XMLResponse);
		response.getWriter().println(XMLResponse);
	}
	
	
/************************************************************************************************************************************* 
 * getProfileSearcherById(HttpServletRequest request, HttpServletResponse response)
 * 
 * Method to retrieve Job Searcher's profile and account information. A mySQL query is created from these parameters, 
 * and is passed to DBManager to process a result set. The method creates an XML table from this result set and passes 
 * it as a HttpServletResponse to the XMLHttp Object that invoked this method.
 * 
 * Caller function: getProfileSearcherById from Profile.js
 * 
 * @param request - The HttpServletRequest that invokes this method
 * @param response - The HttpServletResponse that is passed back to the XMLHttp Object that calls this method
 * @throws ServletException
 * @throws IOException
 ************************************************************************************************************************************/
	private void getProfileSearcherById(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		boolean isSuccessful = false;
		String message = "Profile Search Failed";
		
		//Convert Job Searcher's id from String to integer
		String profileSearcherId = request.getParameter("accountId");
		int intProfileId = -1;
		try{
			intProfileId = Integer.parseInt(profileSearcherId);
		}
		catch(NumberFormatException e){
			response.sendRedirect("error.html");
		}
		
		//Create connection with database and statement to store/execute query
		Connection conn=dbManager.getConnection();
		Statement stmt =null;
		ProfileSearcher profileSearcher = new ProfileSearcher();
		
		try{
			stmt = conn.createStatement();
			//Query to gather Job Searcher's data (profile summary, account info -for emails, location)
			String query = "SELECT * " +
					"FROM tableProfileSearcher " +
					"INNER JOIN tableAccount USING (idAccount)" +
					"LEFT OUTER JOIN tableLocationProfile USING (idAccount) " +
					"WHERE idAccount="+profileSearcherId;
			
			System.out.println("getProfileSearcherById query:"+query);
			
			//Execute query and get result set
			stmt.executeQuery(query);
			ResultSet result = stmt.getResultSet();
			
			//create ArrayList locationList to store Job Searcher's location(s)
			Location location = new Location("Not Specified");
			ArrayList<Location> locationList = new ArrayList<Location>();
			
			if(result.first()){ //if there is an entry from result set - check first one
				message = "Profile Search successful";
				
				String empPref = "";
				int fullTime = 0;
				int partTime = 0;
				int internship = 0;
				
				//Load Job Searcher's profile and account information into profileSearcher object
				profileSearcher.accountID = result.getInt("idAccount");
				profileSearcher.name = result.getString("name");
				profileSearcher.phone = result.getString("phone");
				profileSearcher.selfDescription = result.getString("selfDescription");
				profileSearcher.preferredStartDate = result.getLong("startingDate");
				profileSearcher.educationLevel = result.getInt("educationLevel");		
				profileSearcher.email = result.getString("email");
				profileSearcher.secondaryEmail = result.getString("secondaryEmail");
				
				//Load location values into locationList and pass it on to profileSearcher object's addressList
				location.address = result.getString("location");
				location.longitude = result.getDouble("longitude");
				location.latitude = result.getDouble("latitude");
				locationList.add(location);
				profileSearcher.addressList = locationList;
				
				//Load Employment Preference vales
				fullTime = result.getInt("fullTime");
				partTime = result.getInt("partTime");
				internship = result.getInt("internship");	
				//Convert integer values that define employment availability to Full-time, Part-time, or Internship
				if( (fullTime+partTime+internship) == 3   || (fullTime+partTime+internship) == 0)
					empPref = "N/A"; //no preference
				else{					
					if( fullTime == 1)
						empPref += "Full-time ";
					if(partTime == 1)
						empPref += "Part-time ";
					if(internship == 1)
						empPref += "Internship";
				}
				profileSearcher.employmentPreference = empPref;
				
				isSuccessful = true;
			}
			else{
				System.out.println("Error: Profile not found with ID:" +profileSearcherId);
			}
		}/**end of try block**/
		
		catch (SQLException e) {
			//TODO log SQL exception
			Utility.logError("SQL exception : " + e.getMessage());
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
	    }/**end of finally block**/

		//Create XML table to store all results from result set
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n"); //tag to see if Searcher info is loaded successfully
		XMLResponse.append("\t<message>" + message + "</message>\n");
		//append Job Searcher's data (stored in profielSearcher object) to XML table
		XMLResponse.append(profileSearcher.toXMLContent() ); 
		XMLResponse.append( ServletDocument.getFilesXMLByOwnerID( intProfileId )); //append Job Searcher's file paths (resume,etc.) to XML table
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);	
		
	}/**end of method getProfileSearcherById(HttpServletRequest request, HttpServletResponse response)**/
	
	
	
	private void getProfileBySessionKey(HttpServletRequest request, HttpServletResponse response, Session session)throws ServletException, IOException{
		
		
		//Initialize Return statements
		boolean isSuccessful = false;
		String message = "Failure to fetch profile by Session Key";
		
		Object profile = null;
		ProfileSearcher searcher = new ProfileSearcher();
		ProfilePoster poster = new ProfilePoster();
		
		Connection conn = null;	
		Statement stmt = null;
		
//		String documentList = "";
		// if invalid session, redirect to error page
//TODO REFACTOR
		earlyExit:
		{
		try{

			String acctType = "";
						
			if( session.getAccountType().equalsIgnoreCase("searcher") ) {
				acctType = "Searcher";
				profile = searcher;
			}
			else if ( session.getAccountType().equalsIgnoreCase( "poster") ) {
				acctType = "Poster";
				profile = poster;
			}

			
			System.out.println("Getting Job " + acctType + " Profile");
			
			conn = dbManager.getConnection();
			stmt = conn.createStatement();
			
			//TODO add join statement to retrieve email info
			String query = 
//				"SELECT * FROM tableProfile"+ acctType +" WHERE idAccount=" + currSession.getIdAccount();
				"SELECT * FROM tableProfile"+ acctType +" INNER JOIN tableAccount " + 
					"USING (idAccount) WHERE idAccount=" + session.getIdAccount();
			
			System.out.println("getProfileBySessionKey query:" + query);
			isSuccessful = stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			if (result.first()){

				System.out.println("getProfileBySessionKey successful");
				message = "getProfileBySessionKey successful";
				
				if ( session.getAccountType().equals("searcher") ) {
					searcher.accountID			= result.getInt("idAccount");
					searcher.name				= result.getString("name");
					searcher.phone		 		= result.getString("phone");
					searcher.selfDescription	= result.getString("selfDescription");
					searcher.educationLevel		= result.getInt("educationLevel");
					searcher.preferredStartDate = result.getLong("startingDate");
					searcher.email				= result.getString("email");
					searcher.secondaryEmail		= result.getString("secondaryEmail");
					
				}
				if ( session.getAccountType().equals("poster") ) {					
					poster.accountID		= result.getInt("idAccount");
					poster.name				= result.getString("name");
					poster.phone		 	= result.getString("phone");
					poster.selfDescription	= result.getString("selfDescription");
					poster.email			= result.getString("email");
					poster.secondaryEmail	= result.getString("secondaryEmail");

				}
					
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: Profile not found with ID=" + session.getIdAccount();
				System.out.println("Error: Profile not found with ID=" + session.getIdAccount());
			}
				
				
			/**Get Location values */
			query = "SELECT * FROM tableLocationProfile WHERE " +
						"idAccount= '" + session.getIdAccount() +"'";
			System.out.println(query);
			isSuccessful = stmt.execute(query);
			result = stmt.getResultSet();
			
			ArrayList<Location> fetchedAddressList = new ArrayList<Location>();
			
			Location address = new Location();
			while(result.next()){

				System.out.println("Location Found: " + result.getString("location"));
				//Get Address, Longitude, Latitude
				address.address = result.getString("location");
				address.longitude = result.getDouble("longitude");
				address.latitude = result.getDouble("latitude");	
			}
			fetchedAddressList.add(address);
			
			if ( session.getAccountType().equalsIgnoreCase("searcher") ) {
				searcher.addressList = fetchedAddressList;
			}
			else if ( session.getAccountType().equalsIgnoreCase("poster") ) {
				poster.addressList = fetchedAddressList;
			}
			
			
			/**Get Employment Preference values */
			if ( session.getAccountType().equals("searcher") ) {
				String empPref = "";
				int fullTime = 0;
				int partTime = 0;
				int internship = 0;
				
				query = "SELECT * FROM tableSearcherEmpPref WHERE " +
				"idAccount= '" + session.getIdAccount() +"'";
				System.out.println(query);
				isSuccessful = stmt.execute(query);
				result = stmt.getResultSet();
				
				if(!result.first()){
						System.out.println("No Employment Preference Found");
				}
				else{
					fullTime = result.getInt("fullTime");
					partTime = result.getInt("partTime");
					internship = result.getInt("internship");	

					if( (fullTime+partTime+internship) == 3   || (fullTime+partTime+internship) == 0)
						empPref = "N/A"; //no preference
					else{
						if( fullTime == 1)
							empPref += "Full-time ";
						if(partTime == 1)
							empPref += "Part-time ";
						if(internship == 1)
							empPref += "Internship";
					}
					
					System.out.println("Employment Preference: " + empPref);
					searcher.employmentPreference = empPref;
				}
			}// END OF EMPLOYMENT PREFERENCE
			
//			if( currSession.getAccountType().equalsIgnoreCase("searcher") ) {
//				documentList = ServletDocument.getXMLDocumentList( currSession );
//			}
			
			
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
	    System.out.println("Checkpoint: End of getPofileBySessionKey - Message: " + message);
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		
		if ( session.getAccountType().equalsIgnoreCase("searcher") ) {
			XMLResponse.append( ((ProfileSearcher)profile).toXMLContent() );
			XMLResponse.append( ServletDocument.getFilesXMLByOwnerID( session.getIdAccount() ));
//			XMLResponse.append( documentList );
		}
		else if ( session.getAccountType().equalsIgnoreCase("poster") ) {
			XMLResponse.append( ((ProfilePoster)profile).toXMLContent() );
		}
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	

	private void editProfile(HttpServletRequest request, HttpServletResponse response, Session session) 
								throws ServletException, IOException{
		
		int accountID 			= session.getIdAccount();
		String accountType 		= session.getAccountType();
		
		String name 			= request.getParameter("name");
		String phone 			= request.getParameter("phone");
		String selfDescription 	= request.getParameter("descripton");
		
		String address 			= request.getParameter("address");
		String latitude			= request.getParameter("latitude");
		String longitude		= request.getParameter("longitude");
		
		
		//Check new inputs and process linebreaks/spaces
		name = Utility.checkInputFormat( name );
		phone = Utility.checkInputFormat( phone );
		selfDescription = Utility.checkInputFormat( selfDescription );
		selfDescription = Utility.processLineBreaksWhiteSpaces(selfDescription);
		
		//Initialize return statements
		boolean isSuccessful = false;
		String message = "failed to edit profile";
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;

		try{
			stmt = conn.createStatement();
			
		//Edit Job Poster Profile
			if(accountType.equals("poster") ){
				System.out.println("Editting Poster Profile");
				
				String query = 
					"UPDATE tableProfilePoster SET " 
					+ "name='" 				+ name + 			"'," 
					+ "phone='" 			+ phone + 			"'," 
					+ "selfDescription='" 	+ selfDescription + "' " +
					"WHERE idAccount='" 	+ accountID + 		"' ";
					
				System.out.println("New PosterProfile Query:" + query);
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
				}
				else{
					message = "Edit Poster Profile success!";
					System.out.println("Edit Poster Profile success!");
				}
				
			}
		//Edit Job Searcher Profile
			else{
				System.out.println("Editting Searcher Profile");
				
				//Load Employment Preference Values
				String empPrefFT = request.getParameter("empPrefFT"); 
					if(empPrefFT.equals("true") )
						empPrefFT = "1"; 
					else
						empPrefFT = "0";
					
				String empPrefPT = request.getParameter("empPrefPT");
					if(empPrefPT.equals("true") )
						empPrefPT = "1"; 
					else
						empPrefPT = "0";
					
				String empPrefIn = request.getParameter("empPrefIn");
					if(empPrefIn.equals("true") )
						empPrefIn = "1"; 
					else
						empPrefIn = "0";
				
				String preferredStartDate = request.getParameter("preferredStartDate");
				int educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
				
				System.out.println("Starting Date String: " + preferredStartDate);
				
				long startDateMS = Utility.dateConvertor(preferredStartDate); //Convert to milliseconds
				
				//TODO: include address
				String query = 
					"UPDATE tableProfileSearcher SET " 
					+ "name= '" 			+ name 				+ "'," 
					+ "phone='" 			+ phone 			+ "'," 
					+ "selfDescription='" 	+ selfDescription 	+ "',"
					+ "educationLevel='" 	+ educationLevel 	+ "'," 
					+ "startingDate='"		+ startDateMS 		+ "' " +
					"WHERE idAccount='" 	+ accountID 		+ "' ";			
				
				// if successful, 1 row should be inserted
				System.out.println("New SearcherProfile Query (location):" + query);
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
				}
				else{
					message = "Edit Searcher Profile Success!";
					System.out.println("Edit Searcher Profile success!");
				}
				
				
				//Update Employment Type Preference 
				query = 
					"UPDATE tableSearcherEmpPref SET " 
					+ "fullTime= '" 		+ empPrefFT 	+ "'," 
					+ "partTime='" 			+ empPrefPT 	+ "'," 
					+ "internship='" 		+ empPrefIn 	+ "' " +
					"WHERE idAccount='" + accountID + "' ";
			
				System.out.println("Update Query: " + query);
				
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
				}
				else{
					message = "Edit Searcher Employment Preference success!";
					System.out.println("Edit Searcher Employment Preference success!");
				}

				
			}//END OF ELSE
			
		//Update Location Values
			String query = 
				"UPDATE tableLocationProfile SET " 
				+ "location= '" 	+ address 	+ "'," 
				+ "longitude='" 	+ longitude + "'," 
				+ "latitude='" 		+ latitude 	+ "' " +
				"WHERE idAccount='" + accountID + "' ";
		
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "Edit All Profile Fields success!";
				System.out.println("Edit Profile Location success!");
			}
			

		}//END OF TRY
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
	
	
/*********************************************************************************************************************
 * searchSearcherProfile(HttpServletRequest request, HttpServletResponse response)
 * 
 * Method to find Job Searchers based on the parameters the user (Job Poster) has selected or typed in the Search 
 * Job Searcher's page. A mySQL query is created from these parameters, and is passed to DBManager to process a
 * result set. The method creates an XML table from this result set and passes it as a HttpServletResponse 
 * to the XMLHttp Object that invoked this method.
 * 
 * Caller Function: searchSearcherProfile() in Profile.js
 * 
 * @param request - The HttpServletRequest that invokes this method
 * @param response - The HttpServletResponse that is passed back to the XMLHttp Object that calls this method
 * @throws IOException  
 ********************************************************************************************************************/
	private void searchSearcherProfile(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		//ArrayList that stores the result set, and later passes its content to the XML Response table
		ArrayList<ProfileSearcher> jsList = new ArrayList<ProfileSearcher>();
			
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
			
		try {		
			stmt = conn.createStatement();
			
			//Call helper method buildSearchSearcherQuery to add individual queries onto one total query
			String query = buildSearchSearcherQuery(request);				
			//DEBUG
			System.out.println(query);
			
			//Create ArrayList to store Job Searcher's location(s) in jsList as parameter "locationList"
			Location location = new Location();
			ArrayList<Location> locationList = new ArrayList<Location>();
			
			//Execute query and get result set
			stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			//Compile each result from the result set into jsList ArrayList
			while( result.next() ) {
				ProfileSearcher temp = new ProfileSearcher();
					
				temp.accountID 				= result.getInt("idAccount");
				temp.name					= result.getString("name");
				temp.educationLevel			= result.getInt("educationLevel");
				temp.preferredStartDate		= result.getLong("startingDate");

				location.address 			= result.getString("location");
				location.longitude 			= result.getDouble("longitude"); //long. and lat. to display in Google Map
				location.latitude 			= result.getDouble("latitude");		
				locationList.add(location);
				temp.addressList 			= locationList;
					
				jsList.add( temp ); //add temporary ProfileSearcher object into jsList
			}							
			System.out.println("Query Successfully Finished");	
			
		}/**end of try block**/ 
		
		catch (SQLException e1) { //Catch mySQL exception
			e1.printStackTrace();
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
		}/**end of finally block**/
		
		//Create XML table to store all results from result set
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		//iterate through jsList and append to xml
		Iterator<ProfileSearcher> itr = jsList.iterator();
	    while (itr.hasNext()) {
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}/**end of method searchSearcherProfile(HttpServletRequest request, HttpServletResponse response)**/



/*****************************************************************************************************************
 * buildSearchSearcherQuery(HttpServlet Request request)
 * 
 * Helper method that dynamically makes a query based on the user's interest in the Search Job Searcher's page. 
 *
 * Caller Function:  searchSearcherProfile(HttpServletRequest request, HttpServletResponse response) in 
 * ServletProfile.java
 *
 * @param request - The HttpServletRequest that invokes searchSearcherProfile()
 * @return - the query based on what the user's search in Search Job Searcher's page
 *****************************************************************************************************************/
	private String buildSearchSearcherQuery(HttpServletRequest request){
		
		//Create a Map that will store the Search Parameter names and Search Parameter value
		//ie.) location: USA, Hawaii --> Search Paramter name: location; Search Parameter value: USA, Hawaii
		Map<String, String>paraMap = new HashMap<String, String>();
		boolean qkSearch=false;
		
		//Get Map from DbDict that stores all the Search Parameter names (front-end and back-end)
		//Front-end: what it's called in the website
		//Back-end: what it's called in the code and database
		Map<String, String> paraColMap = DbDict.getDict(request.getParameter("action"));
		
		//A list (enumeration) of the search parameter names from the Search Job Searcher 
		Enumeration paramNames = request.getParameterNames();
		
		while (paramNames.hasMoreElements()) {			
			String paraName = (String) paramNames.nextElement();
			
			//If request parameter name is "action", this will cause colName to be null. Hence, do and write nothing.
			if( paraName.equals("action")){
				//do and write nothing
			}
			//Else if parameter name is something else i.e.) a request parameter name of an html input: "name", etc.
			else {
				//Look up corresponding search parameter names from paraColMap MAP
				String colName = paraColMap.get(paraName);
				//Put the parameters' names and values into the paraMap MAP
				paraMap.put(colName,request.getParameter(paraName) );
				
				//Debug
				System.out.println("Column: " + colName); 
				System.out.println("Value: " + paraMap.get(colName));
			}
		}/**end of while loop**/
		
		//CAUTION: NEED TO HAVE A SPACE AT THE END oF FOLLOWING Query!
		String query			= "SELECT * FROM tableProfileSearcher LEFT OUTER JOIN tableLocationProfile USING (idAccount) WHERE ";
		String andKeyword 		= " AND ";		//CAUTION: SPACE IMPORTANT
		String inKeyword 		= " IN ";		//CAUTION: SPACE IMPORTANT
		String orKeyword		= " OR "; 		//CAUTION: SPACE IMPORTANT
		String likeKeyword		= " LIKE "; 	//CAUTION: SPACE IMPORTANT
		String regExKeyword 	= " REGEXP ";	//CAUTION: SPACE IMPORTANT
		String whereKeyword		= " WHERE ";	//CAUTION: SPACE IMPORTANT
		String orderByKeyword	= " ORDER BY "; //CAUTION: SPACE IMPORTANT
		String limitKeyword		= " LIMIT "; 	//CAUTION: SPACE IMPORTANT
		String descKeyword		= " DESC "; 	//CAUTION: SPACE IMPORTANT
		StringBuffer wordRegExBuffer = new StringBuffer(" '[[:<:]][[:>:]]' "); //CAUTION: SPACE IMPORTANT, Middle pos:9
		
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(query);
		
		for(Map.Entry<String, String> entry : paraMap.entrySet()){
			//Get parameter's name and value
			String column = entry.getKey();
			String value = entry.getValue();

    		if(column.equals("name")){
    			queryBuf.append(column + likeKeyword + "\'%" +value+"%\'" + andKeyword);
    		}
    		else if(column.equals("educationLevel")){
   	    		queryBuf.append(column+ "=" + value + andKeyword);
    		}
    		else if(column.equals("startingDate")){
    			long dateInLong = Utility.dateStringToLong(value);
    			queryBuf.append(column+ ">=" + dateInLong +andKeyword);
    		}
    		else if(column.equals("location")){
    			queryBuf.append(column + likeKeyword + "\'%" +value+"%\'" + andKeyword);
    		} 
        }/**end of for loop**/
             			
	  	if(!qkSearch)
	  		queryBuf.delete(queryBuf.length() - andKeyword.length(), queryBuf.length()); //remove the last " AND "
	  	else
	  		queryBuf.delete(queryBuf.length() - orKeyword.length(), queryBuf.length()); //remove the last " OR "
	  		
	  	query = queryBuf.toString();
       
		return query;
		
	}/**end of method buildSearchSearcherQuery(HttpServletRequest request)**/

	
/******************************************************************************************************************** 
 * viewAllASearchers(HttpServletRequest request, HttpServletResponse response)
 * 
 * Method to view all Job Searchers in the database. A mySQL query is created from these parameters, and is passed to 
 * DBManager to process a result set. The method creates an XML table from this result set and passes it as a 
 * HttpServletResponse to the XMLHttp Object that invoked this method.
 * 
 * Caller function: viewAllSearchers() from Profile.js
 * 
 * @param request - The HttpServletRequest that invokes this method
 * @param response - The HttpServletResponse that is passed back to the XMLHttp Object that calls this method
 * @throws IOException
 ********************************************************************************************************************/
	private void viewAllSearchers(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		//ArrayList that stores the result set, and later passes its content to the XML Response table
		ArrayList<ProfileSearcher> jsList = new ArrayList<ProfileSearcher>();
		//Create connection database and create statement to store/execute query
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
			
		try {		
			stmt = conn.createStatement();
			
			//Query: Select all Job Searchers in the database
			String query = "SELECT * FROM tableProfileSearcher LEFT OUTER JOIN tableLocationProfile USING (idAccount)";
				
			//DEBUG
			System.out.println(query);
			
			//Execute the query and get result set
			stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			//Compile each result from the result set into jsList ArrayList
			while( result.next() ) {
				ProfileSearcher temp = new ProfileSearcher();
						
				temp.accountID 				= result.getInt("idAccount");
				temp.name					= result.getString("name");
				temp.educationLevel			= result.getInt("educationLevel");
				temp.preferredStartDate		= result.getLong("startingDate");
				
				//Create ArrayList to store Job Searcher's location(s) in jsList as parameter "locationList"
				Location location = new Location();
				ArrayList<Location> locationList = new ArrayList<Location>();
				location.address = result.getString("location");
				location.longitude = result.getDouble("longitude");
				location.latitude = result.getDouble("latitude");
				locationList.add(location);
				temp.addressList = locationList;
					
				jsList.add( temp ); //add temporary ProfileSearcher object into jsList
			}
				
			System.out.println("Query for View All Searchers Successfully Finished");
			
		}/**end of try block**/
		
		catch (SQLException e1) {
		e1.printStackTrace();
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
		}/**end of finally block**/
		
		//Create XML table to store all results from result set			
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		//iterate through jsList and append to xml
		Iterator<ProfileSearcher> itr = jsList.iterator();
	    while (itr.hasNext()) {
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	
	}/**end of method viewAllSearchers(HttpServletRequest request, HttpServletResponse response)**/

	
/**
 * 	
 */
	private void searcherProfileSummary(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException{
		
		
		String[]colToGet={"name"};//TODO MORE COLUMNS
		Map<String,Object>condMap=new HashMap<String, Object>();
		StringBuffer responseMsg =new StringBuffer();
		
		String sKey = session.getKey();
		
		StringBuffer qBuf = DBQ.sessAuthQuery(sKey, new String[]{"idAccount"}, "searcher");
		String authQuery =qBuf.substring(1, qBuf.length()-2) //Remove bracket	
						       .toString();
		int acctId =-1;
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {
			stmt=conn.createStatement();
			ResultSet authRs = stmt.executeQuery(authQuery);
			if(authRs.next()){ //authenticating
				acctId=authRs.getInt("idAccount");
			}
			if(acctId!=-1){
				condMap.put("idAccount=", acctId); //TODO put more
				String selQuery = DBQ.buidlSelQuery(new String[]{"tableProfileSearcher"}, colToGet, condMap)[0]
				                  .append(DBQ.buidlSelQuery(new String[]{"tableProfileSearcher"}, colToGet, condMap)[1])
				                  .toString();
				
				System.out.println("Summarizing Searcher Profile----->Processing " + selQuery);
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(selQuery);
				while(rs.next()){
					//CDATA
					responseMsg.append("Hello, " + rs.getString("name") + DBQ.BR);
				}
				File[]userFile =ServletDocument.getUserFiles(acctId);
				responseMsg.append("You have post " + userFile.length + " Resume Files");
			}
			else{
				System.out.println("Account Doesnot exist or session key expires");
			}
		}
		catch (SQLException e) {
			//TODO log SQL exception
			Utility.logError("SQL exception : " + e.getMessage());
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
	XMLResponse.append("\t<profileSmr>" + responseMsg.toString() + "</profileSmr>\n");
	XMLResponse.append("</response>\n");
	response.setContentType("application/xml");
	response.getWriter().println(XMLResponse);
		
		
	}

/****************************************************************************************************************************
 * saveCandidate(HttpServletRequest request, HttpServletResponse response, Session session )
 * 
 * Method to save favourite Job Searcher (by id) into Candidates List. A mySQL query is created from these parameters, and is 
 * passed to DBManager to process a result set. The method creates an XML table from this result set and passes it as 
 * a HttpServletResponse to the XMLHttp Object that invoked this method.
 * 
 * @param request - The HttpServletRequest that invokes this method
 * @param response - The HttpServletResponse that is passed back to the XMLHttp Object that calls this method
 * @param session - The current session the user is currently in
 * @throws IOException
 ****************************************************************************************************************************/
	private void saveCandidate(HttpServletRequest request, HttpServletResponse response, Session session )throws IOException{
						
		String msg = "";

		int accountId = -1;	//account id of user. Initialise
		int searcherId = -1;	//account id of Job Searcher. Initialise
		long dateAdded = -1;	//date and time (in milliseconds) the Job Searcher was saved. Initialise
		String query = "";	//mySQL query. Initialise
		
		//get account Id from selected Job Searcher and convert it from String to Integer
		String searcherIdInString = request.getParameter("searcherId");
		try{
			searcherId = Integer.parseInt(searcherIdInString);
		}
		catch(NumberFormatException e){
			response.sendRedirect("error.html");
		}
		
		//get user's account Id from the current session the user is in
		accountId = session.getIdAccount();	
		//get current data and time	
		dateAdded = Utility.getCurrentTime();
		
		//mySql query to save's user's account id, Job Searcher's account id, and current date&time into database
		query = "INSERT IGNORE INTO tablecandidate(idAccount, idSearcher, dateAdded) VALUES " +
				"('"  + accountId + "','" + searcherId + "','" + dateAdded + "')";		
		
		//Create connection with database and create Statement to store/execute query
		Connection conn = dbManager.getConnection();
		PreparedStatement preparedStmt = null;
		Statement stmt2 = null; //for debugging and diagnostics
		int isSucessful = -1;
		ResultSet rs = null;
		
		//for debugging and diagnostics. Check to see if query is executed correctly and whether
		//Job Searcher and user's Ids and current time are saved
		try {			
			System.out.println("Processing " + query);
			preparedStmt = conn.prepareStatement(query);
			isSucessful = preparedStmt.executeUpdate();
				
			query="SELECT * FROM tablefavouritejobad ORDER BY dateAdded LIMIT 1";
			stmt2 = conn.createStatement();
			stmt2.execute(query);
			msg = "Action Performed Successfully";
			System.out.println(msg);
			if(isSucessful != -1){
				rs = stmt2.getResultSet();
				if (rs.next()){
					accountId = rs.getInt("idAccount");
					System.out.println("Retrieved: Account ID:" + accountId);
					searcherId = rs.getInt("idJobAd"); 
					System.out.println("Retrieved: Job Ad ID:" + searcherId);
				}
				else
					System.out.println("Error: Inserted row not found after creation");
					
				if(accountId !=-1 && searcherId != -1)
					System.out.println("accountId and jobAdId are valid values");
				else
					System.out.println("accountId or jobAdId not valid");	
			}
			else
				System.out.println("preparedStmt not sucessful");
				
			}/**end of try block**/
		
			catch (SQLException e) {
				Utility.logError("SQL exception : " + e.getMessage());
			}
			
		    finally {
		        try{
		            if (preparedStmt != null)
		                preparedStmt.close();
		            if (stmt2 != null)
		            	stmt2.close();
		        }
		        catch (Exception e) {
		        	//TODO log "Cannot close Prepared Statement"
		        	System.out.println("Cannot close Prepared Statement : " + e.getMessage());
		        }
		        try {
		            if (conn  != null)
		                conn.close();
		        }
		        catch (SQLException e) {
		        	//TODO log Cannot close Connection
		        	System.out.println("Cannot close Connection : " + e.getMessage());
		        }
		    }/**end of finally block**/
			
		}/**end of method saveCandidate(HttpServletRequest request, HttpServletResponse response, Session session)**/

	
/***************************************************************************************************************
 * listCandidate()
 * 
 * @param request
 * @param response
 * @throws IOException
 ***************************************************************************************************************/
		private void listCandidate(HttpServletRequest request, HttpServletResponse response)throws IOException{
			ArrayList<JobAdvertisement> favJobAdList = new ArrayList<JobAdvertisement>();
			
			Session userSession = accManager.getSessionByKey(request.getParameter("sessionKey"));
			int accountId = userSession.getIdAccount();
			
			
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			try {		
				stmt = conn.createStatement();
				//Add individual queries onto total query
				String query = "SELECT * FROM tablecandidate INNER JOIN tableAccount " +
						"USING(idAccount) " +
						"INNER JOIN tableProfileSearcher USING (idAccount)" +
						"WHERE tablecandidate.idAccount =" +
						accountId;
						
				
				//DEBUG
				System.out.println(query);
				
				stmt.execute(query);
				ResultSet result = stmt.getResultSet();
				
				//Compile the result into the arraylist
				while( result.next() ) {
					JobAdvertisement temp = new JobAdvertisement();
					
					temp.jobAdId 				= result.getInt("idJobAd");
					temp.jobAdTitle				= result.getString("title");
					temp.creationDate		 	= result.getLong("datePosted");
					temp.contactInfo 			= result.getString("contactInfo");
					temp.educationReq	 		= result.getInt("educationRequired");
					temp.jobAvailability 		= Utility.jobTypeTranslator(false,result.getString("jobAvailability"));

					favJobAdList.add( temp ); //add to the temporary list
				}
				
				stmt.close();
				
				System.out.println("Query Successfully Finished");
				
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
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
			Iterator<JobAdvertisement> itr = favJobAdList.iterator();
		    while (itr.hasNext()) {//iterate through all list and append to xml
		    	XMLResponse.append(itr.next().toXMLContent() ); 
		    }
			
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
		}
		
	/*********************************************************************************************
	 *
	 * deleteCandidate()
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 ***********************************************************************************************/	
		private void deleteCandidate(HttpServletRequest request, HttpServletResponse response)throws IOException{
			
			Session userSession = accManager.getSessionByKey(request.getParameter("sessionKey"));
			int accountId = userSession.getIdAccount();
			
			String searcherIdInString = request.getParameter("idSearcher");
			System.out.println("TESTING"+searcherIdInString);
			int searcherId = Integer.parseInt(searcherIdInString);
			
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			try {		
				stmt = conn.createStatement();
				//Add individual queries onto total query
				String query = "DELETE FROM tablecandidate " +
						"WHERE tablecandidate.idAccount =" + accountId + 
						" AND tablecandidate.idSearcher =" + searcherId;
						
				
				//DEBUG
				System.out.println(query);
				
				stmt.execute(query);
				
				System.out.println("Query Successfully Finished");
				
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
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
			
			
		}
}


/**
 * 
 * DEPRECATED
 * 
 */

//private void createProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
//	System.out.println("Checkpoint: Inside createProfile");
//	
//	//Account Type: Poster = 1, Searcher = 2
//	int accountType = Integer.parseInt(request.getParameter("accountType")); 	
//	int accountID = Integer.parseInt(request.getParameter("accountID"));
//	
//	String name;
//	String phone;
//	String selfDescription;
//	String empPref;
//	long preferredStartDate;
//	int educationLevel;
//	
//	//Initialize Return statments
//	boolean isSuccessful = false;
//	String message = "Failure to create new profile";
//	
//	Connection conn = dbManager.getConnection();	
//	Statement stmt = null;
//	
//	try{
//	//Create Job Poster Profile
//		if(accountType == 1){
//			System.out.println("Creating Job Poster Profile");
//			stmt = conn.createStatement();
//			
//			name = request.getParameter("posterName");
//			phone = request.getParameter("posterPhone");
//			selfDescription = request.getParameter("posterDescription");
//
//			//Check format
//			name = Utility.checkInputFormat( name );
//			phone = Utility.checkInputFormat( phone );
//			selfDescription = Utility.checkInputFormat( selfDescription );
//			
//		//Add new entry with specified paramters into database
//			String query = 
//				"INSERT INTO tableProfilePoster(idAccount, name, phone, selfDescription) " + 
//				"VALUES ('"
//					+ accountID + "','"
//					+ name + "','"
//					+ phone + "','"
//					+ selfDescription + 
//				"')";
//			
//			System.out.println("New PosterProfile Query:" + query);
//			int rowsInserted = stmt.executeUpdate(query);
//			
//			if (rowsInserted == 1){
//				System.out.println("New Profile Creation success (DB)");
//			}
//			else{
//				System.out.println("Error: row not inserted");
//				stmt.close();
//			}
//			
//			//Check if profile is created successfully
//			query = "SELECT idAccount FROM tableProfilePoster WHERE " + " idAccount='" + accountID + "'"; 
//			ResultSet result = stmt.executeQuery(query);
//			if( result.first() ){
//				System.out.println("Profile Created in DB");
//				isSuccessful = true;
//				message = "Create new profile success";
//			}
//			else{
//				System.out.println("Error: result.first() is false ");
//			}
//		}
//		
//	//Create Job Searcher Profile
//		else{
//			System.out.println("Creating Job Searcher Profile");
//			
//			name = request.getParameter("searcherName");
//			phone = request.getParameter("searcherPhone");
//			selfDescription = request.getParameter("searcherDescripton");
//			empPref = request.getParameter("empPref");
//			educationLevel = Integer.parseInt(request.getParameter("educationLevel"));
//			preferredStartDate = Long.parseLong(request.getParameter("startingDate"));
//
//			stmt = conn.createStatement();
//			
//			//Check format
//			name = Utility.checkInputFormat( name );
//			phone = Utility.checkInputFormat( phone );
//			selfDescription = Utility.checkInputFormat( selfDescription );
//
//			//TODO: include address
//			String query = 
//				"INSERT INTO tableProfileSearcher(idAccount, name, phone, selfDescription, educationLevel, startingDate) " + 
//				"VALUES ('"
//					+ accountID + "','"
//					+ name + "','"
//					+ phone + "','"
//					+ selfDescription + 
//					+ educationLevel + "','"
//					+ preferredStartDate + 
//				"')";
//
//			// if successful, 1 row should be inserted
//			System.out.println("New SearcherProfile Query:" + query);
//			int rowsInserted = stmt.executeUpdate(query);
//			
//			if (rowsInserted == 1){
//				System.out.println("New Profile Creation success (DB)");
//			}
//			else{
//				stmt.close();
//			}
//			
//			//Check if profile is created successfully
//			query = "SELECT name FROM tableProfileSearcher WHERE " + " idAccount='" + accountID + "'"; 
//			ResultSet result = stmt.executeQuery(query);
//			if( result.first() ){
//				System.out.println("Profile Created in DB");
//				isSuccessful =  true;
//				message = "Create new profile success";
//			}
//			else{
//				System.out.println("Error: result.first() is false ");
//			}
//		}
//	}
//	catch (SQLException e) {
//		//TODO log SQL exception
//		System.out.println("SQL exception : " + e.getMessage());
//
//	}
//	// close DB objects
//    finally {
//        try{
//            if (stmt != null)
//                stmt.close();
//        }
//        catch (Exception e) {
//        	//TODO log "Cannot close Statement"
//        	System.out.println("Cannot close Statement : " + e.getMessage());
//        }
//        try {
//            if (conn  != null)
//                conn.close();
//        }
//        catch (SQLException e) {
//        	//TODO log Cannot close Connection
//        	System.out.println("Cannot close Connection : " + e.getMessage());
//        }
//    }
//    
//    System.out.println("Checkpoint: End of create profile - Message: " + message);
//    
//	StringBuffer XMLResponse = new StringBuffer();	
//	XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//	XMLResponse.append("<response>\n");
//	XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
//	XMLResponse.append("\t<message>" + message + "</message>\n");
//	XMLResponse.append("</response>\n");
//	response.setContentType("application/xml");
//	response.getWriter().println(XMLResponse);
// 
//}

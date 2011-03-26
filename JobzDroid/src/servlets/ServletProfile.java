package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
import classes.JobAdvertisement;
import classes.Location;
import classes.ProfilePoster;
import classes.ProfileSearcher;
import classes.Session;
import classes.Utility;

import servlets.ServletDocument;

import managers.DBManager;
import managers.EmailManager;

public class ServletProfile extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;
	private DBColName DbDict =	ServletInitializer.retDbColName();

	public ServletProfile() {
        super();
		dbManager = DBManager.getInstance();
    }
	
	private enum EnumAction
	{ 
		//createProfile,
		editProfile,
		getProfileById,
		getProfileBySessionKey,
		searchProfile,
		searchSearcherProfile,
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
				
//			case createProfile:
//				createProfile(request, response); //implement error checks
//				break;
				
			case editProfile:
				editProfile(request,response);
				break;
				
			case getProfileBySessionKey:
				getProfileBySessionKey(request, response);
				break;
			
			case searchProfile:
				searchProfile(request,response);
				break;
				
			case searchSearcherProfile:
				searchSearcherProfile(request, response);
				break;
				
			default:
				System.out.print("Dont you try to hack =D");
				break;
		
		}//ENDOF SWITCH
	
	}//ENDOF processReq Func
	
	
	private void getProfileById(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		String sessionKey = request.getParameter("sessionKey");
		
		int accountID = Integer.parseInt(request.getParameter("idAccount"));
		Session currSession = dbManager.getSessionByKey(sessionKey );
		
		//Initialize Return statments
		boolean isSuccessful = false;
		String message = "Failure to create new profile";
		
		Object profile = null;
		ProfileSearcher searcher = new ProfileSearcher();
		ProfilePoster poster = new ProfilePoster();
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		String accountType = "";
		
		earlyExit: {
		try{
			stmt = conn.createStatement();
		
		if ( currSession == null ) {
			message = "Invalid Session";
			break earlyExit;
		}
		
		if ( currSession.checkPrivilege("poster", "admin", "superAdmin") == false) {
			message = "User does not have privilige to get profile by ID.";
			break earlyExit;
		}

		
		String query = 		
			"SELECT * FROM tableAccount WHERE idAccount=" + accountID;
		
		System.out.println( query );
		stmt.executeQuery(query);
		
		ResultSet result = stmt.getResultSet();
		
		// get info out from account table
		if ( result.first() ) {
			accountType = result.getString("type");
			if( accountType.equalsIgnoreCase("searcher") ) {
				profile = searcher;
				searcher.accountID	= result.getInt("idAccount");
				searcher.email		= result.getString("email");
				accountType = "Searcher";
			}
			else if ( result.getString("accountType").equalsIgnoreCase("poster") ) {
				profile = poster;
				poster.accountID	= result.getInt("idAccount");
				poster.email		= result.getString("email");
				accountType = "Poster";
			}
			
		}
		
		query = "SELECT * FROM tableProfile"+ accountType +" INNER JOIN tableAccount " + 
		"USING (idAccount) WHERE idAccount=" + accountID;
		
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
			else if (accountType.equalsIgnoreCase("searcher") ){
				System.out.println("Getting Job Searcher Profile");
				
				/**Get field values */
					
					if (result.first()){
						System.out.println("got Job poster profile");
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
		} else if ( accountType.equalsIgnoreCase("poster") ) {
			XMLResponse.append(poster.toXMLContent() );
		}
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		
		System.out.println(XMLResponse);
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	private void getProfileBySessionKey(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		
		String sessionKey = request.getParameter("sessionKey");
		Session currSession = dbManager.getSessionByKey( sessionKey );
		
		//Initialize Return statements
		boolean isSuccessful = false;
		String message = "Failure to fetch profile by Session Key";
		
		Object profile = null;
		ProfileSearcher searcher = new ProfileSearcher();
		ProfilePoster poster = new ProfilePoster();
		
		Connection conn = null;	
		Statement stmt = null;
		
//		String documentList = "";
		
		earlyExit:
		{
		try{

			String acctType = "";
						
			if( currSession.checkPrivilege( "searcher") ) {
				acctType = "Searcher";
				profile = searcher;
			}
			else if ( currSession.checkPrivilege( "poster") ) {
				acctType = "Poster";
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
//				"SELECT * FROM tableProfile"+ acctType +" WHERE idAccount=" + currSession.getIdAccount();
				"SELECT * FROM tableProfile"+ acctType +" INNER JOIN tableAccount " + 
					"USING (idAccount) WHERE idAccount=" + currSession.getIdAccount();
			
			System.out.println("getProfileBySessionKey query:" + query);
			isSuccessful = stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			if (result.first()){

				System.out.println("getProfileBySessionKey successful");
				message = "getProfileBySessionKey successful";
				
				if ( currSession.getAccountType().equals("searcher") ) {
					searcher.accountID			= result.getInt("idAccount");
					searcher.name				= result.getString("name");
					searcher.phone		 		= result.getString("phone");
					searcher.selfDescription	= result.getString("selfDescription");
					searcher.educationLevel		= result.getInt("educationLevel");
					searcher.preferredStartDate = result.getLong("startingDate");
					searcher.email				= result.getString("email");
					searcher.secondaryEmail		= result.getString("secondaryEmail");
					
				}
				if ( currSession.getAccountType().equals("poster") ) {					
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
				message = "Error: Profile not found with ID=" + currSession.getIdAccount();
				System.out.println("Error: Profile not found with ID=" + currSession.getIdAccount());
			}
				
				
			/**Get Location values */
			query = "SELECT * FROM tableLocationProfile WHERE " +
						"idAccount= '" + currSession.getIdAccount() +"'";
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
			
			if ( currSession.getAccountType().equals("searcher") ) {
				searcher.addressList = fetchedAddressList;
			}
			else if ( currSession.getAccountType().equals("poster") ) {
				poster.addressList = fetchedAddressList;
			}
			
			
			/**Get Employment Preference values */
			if ( currSession.getAccountType().equals("searcher") ) {
				String empPref = "";
				int fullTime = 0;
				int partTime = 0;
				int internship = 0;
				
				query = "SELECT * FROM tableSearcherEmpPref WHERE " +
				"idAccount= '" + currSession.getIdAccount() +"'";
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

					if( (fullTime+partTime+internship) == 3 ){
						empPref = "N/A"; //no preference
					}
					else{
						if( fullTime == 1){
							empPref += "Full-time ";
						}
						
						if(partTime == 1){
							empPref += "Part-time ";
						}
						
						if(internship == 1){
							empPref += "Internship";
						}
					}
					
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
		
		if ( currSession.getAccountType().equals("searcher") ) {
			XMLResponse.append( ((ProfileSearcher)profile).toXMLContent() );
//			XMLResponse.append( documentList );
		}
		else if ( currSession.getAccountType().equals("poster") ) {
			XMLResponse.append( ((ProfilePoster)profile).toXMLContent() );
		}
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	

	private void editProfile(HttpServletRequest request, HttpServletResponse response) 
								throws ServletException, IOException{
		
		String sessionKey 		= request.getParameter("sessionKey");
		Session currSession 	= dbManager.getSessionByKey( sessionKey );
		
		int accountID 			= currSession.getIdAccount();
		String accountType 		= currSession.getAccountType();
		
		String name 			= request.getParameter("name");
		String phone 			= request.getParameter("phone");
		String selfDescription 	= request.getParameter("descripton");
		
		String address 			= request.getParameter("address");
		String latitude			= request.getParameter("latitude");
		String longitude		= request.getParameter("longitude");
		
		
		//Check new inputs
		name = Utility.checkInputFormat( name );

		phone = Utility.checkInputFormat( phone );
		selfDescription = Utility.checkInputFormat( selfDescription );
		
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
	
	
	
	
	
	
	
	
/*****************************************************************************************************************
 * 					searchProfile Function
 * 
 * FOR QUICK SEARCH AND ADVANCED SEARCH 
 * 
 *****************************************************************************************************************/
	private void searchProfile(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
	ArrayList<ProfileSearcher> profileList = new ArrayList<ProfileSearcher>();
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {		
			stmt = conn.createStatement();
			//Add individual queries onto total query
			String query = buildSearchQuery(request);
			
			//DEBUG
			System.out.println(query);
			
			stmt.execute(query);
			ResultSet result = stmt.getResultSet();
			
			//Compile the result into the arraylist
			while( result.next() ) {
				ProfileSearcher temp = new ProfileSearcher();
				
				temp.accountID				= result.getInt("idAccount");
				temp.name					= result.getString("name");
				temp.phone					= result.getString("phone");
				temp.educationLevel 		= result.getInt("educationLevel");
				
				//TODO: add other necessary fields

				profileList.add( temp ); //add to the temporary list
			}
			
			stmt.close();
			System.out.println("Query Successfully Finished");
			
		} catch (SQLException e1) {
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
		Iterator<ProfileSearcher> itr = profileList.iterator();
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	
	
	private String buildSearchQuery(HttpServletRequest request){
		
		Map<String, String>paraMap = new HashMap<String, String>();//col-value
		boolean qkSearch=false;
		
		Map<String, String> paraColMap = DbDict.getDict(request.getParameter("action")); //action=searchProfile
		Map<String, String> DbCols = DbDict.getColNameMap();
		
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			
			String paraName = (String) paramNames.nextElement();
			if( paraName.equals("action")){//Not Querying this
				//Do Nothing
			}
			else{
				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
			    //Put the parameters' names and values into the MAP
				
			//TODO: Check if this is needed
//				if(colName.equals("jobAvailability")){
//					if(paraMap.get(colName)!=null){
//						
//						// If it is search by job availability
//						//CAUTION: MIDDLE SPACE IMPORTANT
//						String curVal = paraMap.get(colName);
//						curVal = curVal.substring(0, curVal.length()-1);
//						String jobAvailTerm = curVal+ ", '" + request.getParameter(paraName) + "')";
//						paraMap.put(colName, jobAvailTerm);
//					}
//					else{
//						paraMap.put(colName,"(\'"+request.getParameter(paraName)+"\')" );
//					}
//				}
//				else{
////						paraMap.put(colName,request.getParameter(paraName) );
//						paraMap.put(colName,request.getParameter(paraName) );
//					
//				}
				
//				paraMap.put(colName,request.getParameter(paraName) );
				paraMap.put(colName,request.getParameter(paraName) );
			    
				//Debug
			    System.out.println("Column: " + colName + "\n" +
			    					"Value: " + paraMap.get(colName));
			}
		}
		//CATION: NEED TO HAVE A SPACE AT THE END oF FOLLOWING Query!
		String query 			="SELECT idAccount, name, educationLevel FROM tableProfileSearcher WHERE ";
		String panicQuery		="SELECT idAccount, name, educationLevel jobAvailability FROM tableProfileSearcher";
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
		boolean panic = false;
		
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(query);
		
       for(Map.Entry<String, String> entry : paraMap.entrySet()){
    	   String column = entry.getKey();
    	   String value = entry.getValue();
    	   if(!(column.equals("quickSearch"))){
    		   
    		   if(DbCols.get(column).equals("name")){
    			   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
    		   }
    		   else if(DbCols.get(column).equals("education")){
    			   //if search by the availability term use IN
   	    		    queryBuf.append(column+ inKeyword + value + andKeyword);
    		   }
    		   
    		 //TODO: add location
//    		   else if(DbCols.get(column).equals("employmentPref")){
//   	    		    queryBuf.append(column+ "=" + value + andKeyword);
//    		   }  
    		   //TODO: add location
//    		   else if(DbCols.get(column).equals("location")){
//    			   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
//    		   }
    		   else{//TODO NOT WORKING NEED TO FIX 
    			   panic= true;
	    		   System.out.println("PANIC ACTION!");
	    		   queryBuf.setLength(0);
	    		   queryBuf.append( panicQuery + orderByKeyword + "name" + descKeyword);
	    		   query = queryBuf.toString();
	    		   break;
    		   }
    	   }//ENDOF IF NOT QUICK SEARCH
    	   
    	   else{//Perform quick search, only one value but wild search on all
    		   qkSearch=true;
    		   wordRegExBuffer.insert(9,value);
    		   queryBuf.append(DbCols.get("title") 			+ regExKeyword + wordRegExBuffer.toString() + orKeyword);
    		   queryBuf.append(DbCols.get("education") 		+ regExKeyword + wordRegExBuffer.toString() + orKeyword);

//    		   queryBuf.append(DbCols.get("location") 		+ regExKeyword + wordRegExBuffer.toString() + orKeyword);
//    		   queryBuf.append(DbCols.get("employmentPref") + regExKeyword + wordRegExBuffer.toString() + orKeyword);

    		   if (Utility.degreeConvertor(value)>0){
    			   queryBuf.insert(query.length(), DbCols.get("education") + "=" + Utility.degreeConvertor(value) + orKeyword);
    		   }
    	   }
        }//ENDOF FOR-MAP LOOP
       
       
    	   if (!panic){
	  			
	  			if(!qkSearch){
	  				queryBuf.delete(queryBuf.length() - andKeyword.length(), queryBuf.length()); //remove the last " AND "
	  			}
	  			else{
	  				queryBuf.delete(queryBuf.length() - orKeyword.length(), queryBuf.length()); //remove the last " OR "
	  			}
	  			queryBuf.append(orderByKeyword + "name" + descKeyword);//TODO Can have pages using limited
	  			query = queryBuf.toString();
			}
       
		return query;
		
		
	}



/*******************************************************
 * searchSearcherProfile
 * 
 * 
 * 
 * @param
 * @param
 * 
 ********************************************************/
	public void searchSearcherProfile(HttpServletRequest request, HttpServletResponse response) throws IOException{

		ArrayList<ProfileSearcher> jsList = new ArrayList<ProfileSearcher>();
			
		//tags = Utility.checkInputFormat( tags );
		//location = Utility.checkInputFormat( location );
		//education = Utility.checkInputFormat( education );
			
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
			
		try {		
			stmt = conn.createStatement();
/**BUILD SEARCH QUERY(REQUEST)**/
			//Add individual queries onto total query
			String query = buildSearchQuery(request);
				
			//DEBUG
			System.out.println(query);
				
			stmt.execute(query);
			ResultSet result = stmt.getResultSet();
				
			//Compile the result into the arraylist
			while( result.next() ) {
				ProfileSearcher temp = new ProfileSearcher();
					
				temp.accountID 				= result.getInt("idAccount");
				temp.name					= result.getString("name");
				temp.educationLevel			= result.getInt("educationLevel");
				temp.preferredStartDate		= result.getLong("startingDate");
//				temp.tags 					= result.getString("tags");
					
				jsList.add( temp ); //add to the temporary list
			}
				
			stmt.close();
				
			System.out.println("Query Successfully Finished");
//			return jobAdList;
				
		} catch (SQLException e1) {
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
		Iterator<ProfileSearcher> itr = jsList.iterator();
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }
			
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
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

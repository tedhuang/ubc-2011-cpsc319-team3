package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.JobAdvertisement;
import classes.Location;
import classes.Session;
import classes.Utility;

import managers.DBManager;

/**
 * Servlet implementation class ServletJobAdvertisement
 */
public class ServletJobAd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBManager dbManager;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJobAd() {
        super();
        dbManager = DBManager.getInstance();
        // TODO Auto-generated constructor stub
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		//Add new functions here
		createJobAdvertisement,
		searchJobAdvertisement,
		editJobAdvertisement,
		deleteJobAd,
		getJobAdByOwner,
		getJobAdById,
		adminApprove,
		adminDeny,
		adminDeleteJobAd,
		changeJobAdStatus,
		submitJobAdForApproval,
		extendJobAdExpiry,		
		loadAdList //search loader(?)
		
	}
	


    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestProcess(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestProcess(request,response);
	}

	private void requestProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String action = request.getParameter("action");
		System.out.println(action);
		try{
			EnumAction.valueOf(action);
		}
		catch(IllegalArgumentException e){
			//TODO forward to error page
			PrintWriter out = response.getWriter();
			out.println("Error: Request Process Failed on ServletJobAdvertisement");
			return;
		}
		
		//Check which function the request is calling from the servlet
		switch( EnumAction.valueOf(action) ){
		
			case createJobAdvertisement:
				createJobAdvertisement(request, response);
				break;
				
			case searchJobAdvertisement:
				searchJobAd(request, response);
				break;
			
			case editJobAdvertisement:
				editJobAdvertisement(request,response);
			
			case getJobAdByOwner:
				getJobAdByOwner(request, response);
				break;
				
			case getJobAdById:
				getJobAdById(request, response);
				break;
				
			case loadAdList:
				searchJobAd(request, response);
				break;
				
			case adminApprove:
				adminApprove(request, response);
				break;
				
			case adminDeny:
				adminDeny(request, response);
				break;
				
			case adminDeleteJobAd:
				adminDeleteJobAd(request, response);
				break;
				
			case changeJobAdStatus:
				changeJobAdStatus(request, response);
				break;
				
			case extendJobAdExpiry:
				extendJobAdExpiry(request, response);
				break;
				
			case submitJobAdForApproval:
				submitJobAdForApproval(request, response);
				break;
				
			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
		}
		
	}
	
	


	/******************************************************************************************************************
	 * 					ADLISTLOAD
	 * LOAD JOB AD LIST BY CRITERIAs
	 ******************************************************************************************************************/
	private void adListLoader( HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		boolean isSuccessful=false;
		String message="";
		
	/*
		ArrayList <String> reqParaNameList = new ArrayList<String>();
		ArrayList <String> reqParaValList = new ArrayList<String>();
		
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
		    //Get the parameters' names
			String paraName = (String) paramNames.nextElement();
		    reqParaNameList.add(paraName);
		    //Get the parameters' values
		    reqParaValList.add(request.getParameter(paraName));
		    
		    System.out.println("Parameter: " + paraName + "\n" +
		    					"Value: " + request.getParameter(paraName));
		}
		String criteria = reqParaNameList.get(0);
		String condition = reqParaValList.get(0);
		*/
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
		
		String query = buildSearchQuery(request);
		
		try {
			stmt = conn.createStatement();
			
//			String query = 
//				"SELECT idJobAd, title, datePosted, contactInfo, educationRequired " +//TODO JOIN with LOCATION Table
//				"FROM tableJobAd WHERE " + criteria+"=" + condition;
			
			System.out.println("getJobAdById query:" + query);
			isSuccessful = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			
			if (result.first()){
				System.out.println("getJobAd successful by QUERY" + query);
				message = "getJobAd successful";
				
				//Fill in the fields of the jobAd object
				
				jobAd.contactInfo 		= result.getString("contactInfo");
				jobAd.educationReq 		= result.getInt("educationRequired");
				jobAd.jobAdTitle		= result.getString("title");
				jobAd.jobAdId 			= result.getInt("idJobAd");
				jobAd.creationDate 		= result.getLong("datePosted");
				
				
//				jobAd.ownerID 			= result.getInt("idAccount");
//				jobAd.jobAdDescription 	= result.getString("description");
//				jobAd.expiryDate		= result.getLong("expiryDate");
//				jobAd.startingDate 		= result.getLong("dateStarting");
//				jobAd.status 			= result.getString("status");
//				jobAd.tags 				= result.getString("tags");
//				jobAd.numberOfViews 	= result.getInt("numberOfViews");
//				jobAd.isApproved 		= result.getBoolean("isApproved");
				
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: AD not found with Query" + query;
				System.out.println(message);
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append(jobAd.toXMLContent() );
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	

	
	
	
	
	private void getJobAdByOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
	
		String message = "Create Job Advertisement Failed";
		boolean isSuccessful = false;
		
		JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		String ownerId = request.getParameter("ownerId");

		Connection conn = dbManager.getConnection();	
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			
			String query = 
				"SELECT * FROM tableJobAd " + 
				"WHERE idAccount=" + ownerId;
			
			System.out.println("getJobAdById query:" + query);
			isSuccessful = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			
			if(!result.first()){
				message = "Error: No Job Ad found with owner ID =" + ownerId;
				System.out.println("Error: No Job Ad found with owner ID =" + ownerId);
			}
			
			while(result.next()){
				
				//Fill in the fields of the jobAd object
				jobAd.jobAdId 			= result.getInt("idJobAd");
				jobAd.ownerID 			= result.getInt("idAccount");
				jobAd.jobAdTitle		= result.getString("title");
				jobAd.jobAdDescription 	= result.getString("description");
				jobAd.expiryDate		= result.getLong("expiryDate");
				jobAd.startingDate 		= result.getLong("dateStarting");
				jobAd.creationDate 		= result.getLong("datePosted");
				jobAd.status 			= result.getString("status");
				jobAd.contactInfo 		= result.getString("contactInfo");
				jobAd.educationReq 		= result.getInt("educationRequired");
				jobAd.tags 				= result.getString("tags");
				jobAd.numberOfViews 	= result.getInt("numberOfViews");
				jobAd.isApproved 		= result.getInt("isApproved");
				
			/**Get Location values */
				ArrayList<Location> locationList = new ArrayList<Location>();
				Location location = new Location();
				
				query = "SELECT * FROM tableLocationJobAd WHERE " +
						"idJobAd= '" + jobAd.jobAdId +"'";
				result = stmt.getResultSet();
				
				if(!result.first()){
					System.out.println("Error: failed to find the inserted location");
				}
				else{
					while(result.next()){
						//Get Address, Longitude, Latitude
						location.address = result.getString("location");
						location.longitude = result.getDouble("longitude");
						location.latitude = result.getDouble("latitude");	
					}
					locationList.add(location);
				}
				
				jobAd.locationList = locationList;
				
				jobAdList.add(jobAd);
			
			}//END OF WHILE LOOP
			
			System.out.println("getJobAdByStatus successful");
			message = "getJobAdByStatus successful";
			isSuccessful = true;

		} //END OF TRY
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		
		Iterator<JobAdvertisement> itr = jobAdList.iterator();
		
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	
	/*
	 * Extracts all information from the database of the targetted Job Ad
	 * and returns it in a JobAdvertisement object ported to XML format
	 */
	private void getJobAdById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String message = "Create Job Advertisement Failed";
		boolean isSuccessful = false;
		
		String jobAdId = request.getParameter("jobAdId");
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
		
		try {
			stmt = conn.createStatement();
			
			String query = 
				"SELECT * FROM tableJobAd WHERE idJobAd=" + jobAdId;
			
			System.out.println("getJobAdById query:" + query);
			isSuccessful = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			
			if (result.first()){
				System.out.println("getJobAd successful");
				message = "getJobAd successful";
				
				//Fill in the fields of the jobAd object
				
				jobAd.jobAdId 			= result.getInt("idJobAd");
				jobAd.ownerID 			= result.getInt("idAccount");
				jobAd.jobAdTitle		= result.getString("title");
				jobAd.jobAdDescription 	= result.getString("description");
				jobAd.expiryDate		= result.getLong("expiryDate");
				jobAd.startingDate 		= result.getLong("dateStarting");
				jobAd.creationDate 		= result.getLong("datePosted");
				jobAd.status 			= result.getString("status");
				jobAd.contactInfo 		= result.getString("contactInfo");
				jobAd.educationReq 		= result.getInt("educationRequired");
				jobAd.tags 				= result.getString("tags");
				jobAd.numberOfViews 	= result.getInt("numberOfViews");
				jobAd.isApproved 		= result.getInt("isApproved");
				
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: Job Ad not found with ID=" + jobAdId;
				System.out.println("Error: Job Ad not found with ID=" + jobAdId);
			}
			
	/**Get Location values */
			ArrayList<Location> locationList = new ArrayList<Location>();
			Location location = new Location();
			
			query = "SELECT * FROM tableLocationJobAd WHERE " +
					"idJobAd= '" + jobAdId +"'";
			result = stmt.getResultSet();
			
			if(!result.first()){
				System.out.println("Error: failed to find the inserted location");
			}
			else{
				while(result.next()){
					//Get Address, Longitude, Latitude
					location.address = result.getString("location");
					location.longitude = result.getDouble("longitude");
					location.latitude = result.getDouble("latitude");	
				}
				locationList.add(location);
			}
			
			jobAd.locationList = locationList;
			
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append(jobAd.toXMLContent() );
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}



	private void editJobAdvertisement(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		//initialize return statements
		String message = "Create Job Advertisement Failed";
		boolean isSuccessful = false;
		
		int jobAdId 				 = Integer.parseInt(request.getParameter("jobAdId"));
		
		String jobAdvertisementTitle = request.getParameter("strTitle");
		String jobDescription 		 = request.getParameter("strDescription");
		String contactInfo 			 = request.getParameter("strContactInfo");
		String strTags 				 = request.getParameter("strTags");
		String jobAvailability	 	 = request.getParameter("strJobAvailability");
		
		int educationRequirement 	 = Integer.parseInt(request.getParameter("educationRequirement"));
		
		int expiryYear 				 = Integer.parseInt( request.getParameter("expiryYear"));
		String expiryMonth 			 = request.getParameter("expiryMonth");
		int expiryDay 				 = Integer.parseInt( request.getParameter("expiryDay"));
		
		int startingYear 			 = Integer.parseInt(request.getParameter("startingYear"));
		String startingMonth 		 = request.getParameter("startingMonth");
		int startingDay 			 = Integer.parseInt(request.getParameter("startingDay"));
				
		long millisExpiryDate 		 = Utility.calculateDate(expiryYear,expiryMonth,expiryDay);
		long millisStartingDate 	 = Utility.calculateDate(startingYear, startingMonth, startingDay);
		
		String address 				 = request.getParameter("address");
		double longitude 			 = Double.parseDouble(request.getParameter("longitude"));
		double latitude 			 = Double.parseDouble(request.getParameter("latitude"));
		

		System.out.println(jobAdvertisementTitle);
		System.out.println(jobDescription);
		System.out.println(contactInfo);
		System.out.println(strTags);
		System.out.println(" Expire On: " + millisExpiryDate);
		System.out.println("Location: " + address + " Long: " + longitude + " Lat: " + latitude);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try{
			stmt = conn.createStatement();
			
			String query = 
				"UPDATE tableJobAd SET " 
				+ "title='" 			+ jobAdvertisementTitle + "','" 
				+ "description='" 		+ jobDescription + "','" 
				+ "expiryDate='" 		+ millisExpiryDate + "','" 
				+ "dateStarting='" 		+ millisStartingDate + "','" 
				+ "contactInfo='" 		+ contactInfo + "','" 
				+ "educationRequired='"	+ educationRequirement + "','" 
				+ "jobAvailability='" 	+ jobAvailability + "','" 
				+ "tags='" 				+ strTags + "' " +
				"WHERE idJobAd='" 		+ jobAdId + "' ";
				
			System.out.println("Edit JobAd Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "Edit Job Ad success!";
				System.out.println("Edit Job Ad success!");
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	/**
	 * Creates a new job advertisement entry in the database with the given values
	 * @param jobAdvertisementTitle
	 * @param jobDescription
	 * @param jobLocation
	 * @param contactInfo
	 * @param strTags
	 * @return idJobAd
	 * @throws IOException 
	 */
	private void createJobAdvertisement(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("Entered createJobAdvertisement");
		
		//initialize return statements
		String message = "Create Job Advertisement Failed";
		boolean isSuccessful = false;
		
		System.out.println("sessionKey=" + request.getParameter("sessionKey"));
		
		//Checks the user's privilege
		Session userSession = dbManager.getSessionByKey(request.getParameter("sessionKey"));
		
		earlyExit: {
			System.out.println("Entered user sessionKey");
				
			if ( userSession == null ) {
				//TODO session invalid, handle error
				System.out.println("session is null");
				message = "Failed to authenticate user session";
				break earlyExit;
			}
			else {
				//TODO implmement this
				System.out.println("checking usertype");
				System.out.println("usertype = " + userSession.getAccountType());
				
				if( userSession.getAccountType().equals("poster") ||
						userSession.getAccountType().equals("admin")) {
					System.out.print("User has the correct priviliege");
				}
				else {
					message = "User does not have the right privilege";
					break earlyExit;
				}
	
			}
			
			System.out.print("User session sucessful for key " + userSession.getKey() );
			
			String jobAdvertisementTitle = request.getParameter("strTitle");
			String jobDescription 		 = request.getParameter("strDescription");
			String contactInfo 			 = request.getParameter("strContactInfo");
			String tags 				 = request.getParameter("strTags");
			String jobAvailability	 	 = request.getParameter("strJobAvailability");

			int educationRequirement 	 = Integer.parseInt(request.getParameter("strEducationReq"));
			
			int expiryYear 				 = Integer.parseInt( request.getParameter("expiryYear"));
			String expiryMonth 			 = request.getParameter("expiryMonth");
			int expiryDay 				 = Integer.parseInt( request.getParameter("expiryDay"));
			
			int startingYear 			 = Integer.parseInt(request.getParameter("startingYear"));
			String startingMonth 		 = request.getParameter("startingMonth");
			int startingDay 			 = Integer.parseInt(request.getParameter("startingDay"));
			
			Calendar cal 				 = Calendar.getInstance();
			long millisDateCreated 		 = cal.getTimeInMillis();
			long millisExpiryDate		 = Utility.calculateDate(expiryYear,expiryMonth,expiryDay);
			long millisStartingDate		 = Utility.calculateDate(startingYear, startingMonth, startingDay);

			String address 				 = request.getParameter("address");
			double longitude 			 = Double.parseDouble(request.getParameter("longitude"));
			double latitude 			 = Double.parseDouble(request.getParameter("latitude"));
			
			int jobAdId = -1;
			
			System.out.println(jobAdvertisementTitle);
			System.out.println(jobDescription);
			System.out.println(contactInfo);
			System.out.println(tags);
			System.out.println("Created On: " + millisDateCreated + " Expire On: " + millisExpiryDate);
			System.out.println("Location: " + address + " Long: " + longitude + " Lat: " + latitude);
			
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			try {
				System.out.println("Inserting new Job Ad into DB");
				
				stmt = conn.createStatement();
				
				jobAdvertisementTitle = Utility.checkInputFormat( jobAdvertisementTitle );
				jobDescription = Utility.checkInputFormat( jobDescription );
				contactInfo = Utility.checkInputFormat( contactInfo );
				tags = Utility.checkInputFormat( tags );
				
			//Add new entry with specified paramters into database
				String query = 
					"INSERT INTO tableJobAd(title, description, expiryDate, dateStarting, datePosted, contactInfo, educationRequired, jobAvailability, tags ) " +
					"VALUES " + "('" 
						+ jobAdvertisementTitle + "','" 
						+ jobDescription + "','" 
						+ millisExpiryDate + "','" 
						+ millisStartingDate + "','" 
						+ millisDateCreated + "','"
						+ contactInfo + "','" 
						+ educationRequirement + "','"
						+ jobAvailability + "','"
						+ tags + 
					"')";
				
				// if successful, 1 row should be inserted
				System.out.println("New Job Ad Query: " + query);
				int rowsInserted = stmt.executeUpdate(query);
				
				if (rowsInserted != 1){
					System.out.println("New JobAd Creation failed");
					Utility.logError("New JobAd insert in DB failed");
				}
				
			//Get jobAdId
				query = "SELECT idJobAd FROM tableJobAd WHERE " +
						"title='" + jobAdvertisementTitle + "' AND " +
						"datePosted='" + millisDateCreated + "'";
				ResultSet result = stmt.executeQuery(query);
				
				if(result.first()){
					jobAdId = result.getInt("idJobAd");
					System.out.println("Job Ad ID: " + jobAdId);
				}
				else{
					System.out.println("Error: Job Ad ID not found after creation");
				}
				
			//Insert location values into location table
				query = 
					"INSERT INTO tableLocationJobAd (location, longitude, latitude, idJobAd) " + 
					"VALUES " + "('" 
					+ address + "','" 
					+ longitude + "','" 
					+ latitude + "','" 
					+ jobAdId +
				"');";
				
				System.out.println("Location table query: " + query);
				
				rowsInserted = stmt.executeUpdate(query);
				
				System.out.println("Row Inserted: " + rowsInserted);
				
				if (rowsInserted == 1){
					System.out.println("New JobAd Creation success (DB)");
					isSuccessful = true;
					message = "Create Job Advertisement Successful";
				}
				else{
					System.out.println("Insert location for new job ad failed");
					Utility.logError("Insert location for new job ad failed");
				}

				//Debug print
				System.out.println("Location Query: "+ query);
				
				if( stmt.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Location Update Failed");
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
		}//earlyExit:
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	/*
	 * Changes the targetted Job ad status to pending
	 */
	private void submitJobAdForApproval(HttpServletRequest request,
			HttpServletResponse response) throws IOException {	
		/**
		 * TODO: implement session key check
		 */
		
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "submitJobAdForApproval failed";
		
		try {
			stmt = conn.createStatement();
			
			//Update isApproved
			String query = 
				"UPDATE tableJobAd " + 
				"SET status='" + "pending" +"' " +
				"WHERE idJobAd='" + jobAdId + "'";
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "submitJobAdForApproval worked!";
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}

	
	
	/*
	 * Updates the expiryDate DB entry of the Job Ad to the new one specified
	 */
	private void extendJobAdExpiry(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		/**
		 * TODO: implement session key check
		 */
				
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		String newExpireTime = request.getParameter("expiryDate");
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "extendJobAdExpiry failed";
		
		try {
			stmt = conn.createStatement();
			
			//Update isApproved
			String query = 
				"UPDATE tableJobAd " + 
				"SET expiryDate='" + newExpireTime +"' " +
				"WHERE idJobAd='" + jobAdId + "'";
			
			//Debug print
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				message = "extendJobAdExpiry worked!";
				System.out.println("extendJobAdExpiry worked!");
				isSuccessful = true;
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
		
	}
	
	
	
	
	/*
	 * Sets the status of the job ad to open and changes the isApproved value to true
	 */
	private void adminApprove(HttpServletRequest request, HttpServletResponse response) throws IOException{
		/**
		 * TODO: Implement check session key
		 */
		
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "adminApprove failed";
		
		try {
			stmt = conn.createStatement();
			
			String query = 
				"UPDATE tableJobAd " + 
				"SET isApproved='" + 1 +"', " +
					"status='" + "open" + "' " +
				"WHERE idJobAd='" + jobAdId + "'";
			
			//Debug print
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				System.out.println("adminApprove worked!");
				message = "adminApprove worked!";
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	
	
	
	
	
	/*
	 * Sets the status of the job ad to draft and changes the isApproved value to false
	 */
	private void adminDeny(HttpServletRequest request, HttpServletResponse response) throws IOException{
		/**
		 * TODO: Implement check session key
		 */
		
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "adminRevertApproval failed";
		
		try {
			stmt = conn.createStatement();
			
			String query = 
				"UPDATE tableJobAd " + 
				"SET isApproved='" + 0 +"', " +
					"status='" + "inactive" + "' " +
				"WHERE idJobAd='" + jobAdId + "'";
			
			//Debug print
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "adminDeny worked!";
				System.out.println("adminDeny worked!");
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	

	/*
	 * Permenently removes the job ad by ID from the database
	 */
	private void adminDeleteJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		/**
		 * TODO: Implement check session key
		 */
		
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));

		
		System.out.println(jobAdId);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "adminDeleteJobAd failed";
		
		try {
			stmt = conn.createStatement();
			
			//Delete designed job Ad
			String query = 
				"DELETE FROM tableJobAd " + 
				"WHERE idJobAd='" + jobAdId + "'";
			
			//Debug print
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "adminDeleteJobAd worked!";
				System.out.println("adminDeleteJobAd worked!");
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
		
	}
	
	/*
	 * Changes the status of the targetted job ad to the specified status
	 */
	private void changeJobAdStatus(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		String status = request.getParameter("status");
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		String message = "changeJobAdStatus failed";
		
		try {
			stmt = conn.createStatement();
			
			//Update status
			String query = 
				"UPDATE tableJobAd " + 
				"SET status='" + status +"' " +
				"WHERE idJobAd='" + jobAdId + "'";
			
			//Debug print
			System.out.println("Update Query: " + query);
			
			if( stmt.executeUpdate(query) != 1 ){ //Error Check
				System.out.println("Error: Update Query Failed");
			}
			else{
				isSuccessful = true;
				message = "changeJobAdStatus worked!";
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
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	
	
	
	
	
	public ArrayList<JobAdvertisement> searchJobAdvertisement(String keywords, //TODO: change keywords to array 
			  String location, 
			  String education
			  ){ //String tags //TODO: fix up tags search

		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		String totalQuery;
		String keywordQuery;
		String locationQuery;
		//String tagsQuery;
		String educationQuery;
		
		keywords = Utility.checkInputFormat( keywords );
		//tags = Utility.checkInputFormat( tags );
		//location = Utility.checkInputFormat( location );
		//education = Utility.checkInputFormat( education );

		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {		
			stmt = conn.createStatement();
			
			//Add individual queries onto total query
			if(keywords == "")
				keywordQuery = "";
			else
				keywordQuery = "AND title = '" + keywords + "'";
			
			if(location == "")
				locationQuery = "";
			else
				locationQuery = " AND location = '" + location + "'";
			
			if(education == "")
				educationQuery = "";
			else
				educationQuery = " AND education = '" + education + "'";
			
			totalQuery = "SELECT * FROM tableJobAd "
			+ "WHERE status = 'open' " 
			+ keywordQuery + locationQuery + educationQuery; 
			
			
			System.out.println(totalQuery);
			boolean success = stmt.execute(totalQuery);
			
			ResultSet result = stmt.getResultSet();
			
			//Compile the result into the arraylist
			while( result.next() ) {
				JobAdvertisement temp = new JobAdvertisement();
				
				temp.jobAdId = result.getInt("idJobAd");
				temp.ownerID = result.getInt("OwnerID");
				temp.numberOfViews = result.getInt("numberOfViews");
				temp.jobAdTitle = result.getString("title");
				//temp.location = result.getString("location");
				temp.tags = result.getString("tags");
				temp.contactInfo = result.getString("contactInfo");
				temp.expiryDate = result.getLong("ExpiryDate");
				temp.startingDate = result.getLong("startingDate");
				temp.creationDate = result.getLong("CreationDate");
				temp.status = result.getString("status");
				temp.jobAdDescription = result.getString("description");				
				
				jobAdList.add( temp ); //add to the temporary list
			}
			
			stmt.close();
			
			System.out.println("Searched Auctions");
			return jobAdList;
			
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
		
		return null;
		}
	/*****************************************************************************************************************
	 * 					searchJobAd Function
	 * 
	 * FOR QUICK SEARCH AND ADVANCED SEARCH 
	 * 
	 *****************************************************************************************************************/
	public void searchJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{

		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		
		//tags = Utility.checkInputFormat( tags );
		//location = Utility.checkInputFormat( location );
		//education = Utility.checkInputFormat( education );
		
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
				JobAdvertisement temp = new JobAdvertisement();
				
				temp.jobAdId 				= result.getInt("idJobAd");
				temp.jobAdTitle				= result.getString("title");
				temp.creationDate		 	= result.getLong("datePosted");
				temp.contactInfo 			= result.getString("contactInfo");
				temp.educationReq	 		= result.getInt("educationRequired");
				temp.jobAvailability 		= Utility.jobTypeTranslator(false,result.getString("jobAvailability"));
//				temp.tags 					= result.getString("tags");
				
				jobAdList.add( temp ); //add to the temporary list
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
		Iterator<JobAdvertisement> itr = jobAdList.iterator();
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		}
	
	/*****************************************************************************************************************
	 * 					buildSearchQuery Function
	 * Dynamically making the query as the user's interests
	 * Callee Function:  DBColConvertor
	 * Caller Function:  searchJobAd
	 *****************************************************************************************************************/
	private String buildSearchQuery(HttpServletRequest request){
		
		Map<String, String>paraMap = new HashMap<String, String>();
		boolean qkSearch=false;
		
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			
			String paraName = (String) paramNames.nextElement();
			if( paraName.equals("action")){//Not Querying this
				//Do Nothing
			}
			else{
				String colName = DBColConvertor(paraName); //convert to col names
			    //Put the parameters' names and values into the MAP
				if(colName.equals("jobAvailability")){
					if(paraMap.get(colName)!=null){
						
						// If it is search by job availability
						//CAUTION: MIDDLE SPACE IMPORTANT
						String curVal = paraMap.get(colName);
						curVal = curVal.substring(0, curVal.length()-1);
						String jobAvailTerm = curVal+ ", '" + request.getParameter(paraName) + "')";
						paraMap.put(colName, jobAvailTerm);
					}
					else{
						paraMap.put(colName,"(\'"+request.getParameter(paraName)+"\')" );
					}
				}
				else{
						paraMap.put(colName,request.getParameter(paraName) );
				}
			    
				//Debug
			    System.out.println("Column: " + colName + "\n" +
			    					"Value: " + paraMap.get(colName));
			}
		}
		//CATION: NEED TO HAVE A SPACE AT THE END oF FOLLOWING Query!
		String query 			="SELECT idJobAd, title, datePosted, contactInfo, educationRequired, jobAvailability FROM tableJobAd WHERE ";
		String panicQuery		="SELECT idJobAd, title, datePosted, contactInfo, educationRequired, jobAvailability FROM tableJobAd";
		String andKeyword 		= " AND ";		//CAUTION: SPACE IMPORTANT
		String inKeyword 		= " IN ";		//CAUTION: SPACE IMPORTANT
		String orKeyword		= " OR "; 		//CAUTION: SPACE IMPORTANT
		String likeKeyword		= " LIKE "; 	//CAUTION: SPACE IMPORTANT
		String regExKeyword 	= " REGEXP ";	//CAUTION: SPACE IMPORTANT
		String whereKeyword		= " WHERE ";	//CAUTION: SPACE IMPORTANT
		String orderByKeyword	= " ORDER BY "; //CAUTION: SPACE IMPORTANT
		String limitKeyword		= " LIMIT "; 	//CAUTION: SPACE IMPORTANT
		String descKeyword		= " DESC "; 	//CAUTION: SPACE IMPORTANT
		StringBuffer wordRegExBuffer = new StringBuffer(" '[[:<:]][[:>:]]' "); //CAUTION: SPACE IMPORTANT
		boolean panic = false;
		
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(query);
		
       for(Map.Entry<String, String> entry : paraMap.entrySet()){
    	   
    	   if(!(entry.getKey().equals("quickSearch"))){
	    	   switch(EnumJobTableCol.valueOf(entry.getKey())){
	    	   
	    	   case title:
	    			queryBuf.append(entry.getKey()+ regExKeyword + wordRegExBuffer.insert(9,entry.getValue()) + andKeyword);
	    			break;
	    			//CAUTION: SPACE IMPORTANT
	    	   case jobAvailability: //if search by the availability term use IN
	    		    queryBuf.append(entry.getKey()+ inKeyword + entry.getValue() + andKeyword);
	            	break;
	           
	    	   case educationRequired:
	    		    queryBuf.append(entry.getKey()+ "=" + entry.getValue() + andKeyword);
	    		    break;
	    	   case tags:
	    		   queryBuf.append(entry.getKey()+ regExKeyword + wordRegExBuffer.insert(9,entry.getValue()) + andKeyword);
	    		   break;
//	    	   case location:
//	    		   queryBuf.append(entry.getKey()+ regExKeyword + wordRegExBuffer.insert(9,entry.getValue()) + andKeyword);
//	    		   break;
	    		   
	    	   default: //panic //TODO NOT WORKING NEED TO FIX 
	    		   panic= true;
	    		   System.out.println("PANIC ACTION!");
	    		   queryBuf.setLength(0);
	    		   queryBuf.append( panicQuery + orderByKeyword + "datePosted" + descKeyword);
	    		   query = queryBuf.toString();
	    		   break;
	    	  }//ENDOF SWITCH
    	   }//ENDOF IF NOT QUICK SEARCH
    	   
    	   else{//Perform quick search
    		   qkSearch=true;
    		   wordRegExBuffer.insert(9,entry.getValue());
    		   for(EnumJobTableCol col : EnumJobTableCol.values()){
    			   
    			   switch (col){
    			   
    			   case undefine:
    				   break;
    			   	case idJobAd:
    				   break;
    				   
    			   	case educationRequired:
    			   		if (Utility.degreeConvertor(entry.getValue())!=0){
//    			   			queryBuf.delete(queryBuf.length() - andKeyword.length(), queryBuf.length()); //remove the last " AND "
    			   			queryBuf.insert(query.length(), col + "=" + Utility.degreeConvertor(entry.getValue()) + orKeyword); //Extract to the edu level
    			   		}
    			   	   break;
    			   	   
    			   	case jobAvailability:
    			   		if(Utility.jobTypeTranslator(true,entry.getValue())!=null){
    			   			queryBuf.insert(query.length(), col + "='" + Utility.jobTypeTranslator(true,entry.getValue())+"'" + orKeyword); //Extract to the Job Type
    			   			}
    			   		break;
    			   		
    			   	 default:
    			   		queryBuf.append(col + regExKeyword + wordRegExBuffer.toString() + orKeyword);
    			   		break;
    			   }
    		   }//ENDOF FOR EACH-ENUM-COL
		  
    	   }
        }//ENDOF FOR-MAP LOOP
       
       
    	   if (!panic){
	  			
	  			if(!qkSearch){
	  				queryBuf.delete(queryBuf.length() - andKeyword.length(), queryBuf.length()); //remove the last " AND "
	  			}
	  			else{
	  				queryBuf.delete(queryBuf.length() - orKeyword.length(), queryBuf.length()); //remove the last " OR "
	  			}
	  			queryBuf.append(orderByKeyword + "datePosted" + descKeyword);//TODO Can have pages using limited
	  			query = queryBuf.toString();
			}
       
		return query;
	}
	/**************************************************************************************
	 * 			DBColConvertor Function
	 *  Convert input form's name into corrsponding DB columns
	 * @param input
	 * @return
	 **************************************************************************************/
	private String DBColConvertor(String input){
		switch (EnumCriteria.valueOf(input)){
		
		case searchTitle:
			return "title";
			
		case searchEduReq:
			return "educationRequired";
			
		case searchCompany:
			return "company";
		
		case searchJobAdId:
			return "idJobAd";
			
		case searchPT:
			return "jobAvailability";
			
		case searchFT:
			return "jobAvailability";
			
		case searchIS:
			return "jobAvailability";
		
		case quickSearch:
			return "quickSearch";
		
		default:
		return "undefine";
		
		}
	}
	/************************************************************************************
	 * 	This is the input forms' name, be sure to be matched
	 ************************************************************************************/
	private enum EnumCriteria	{
		
		searchTitle,
		searchCompany,
		searchEduReq,
		searchJobAdId,
		searchJobLoc,
		searchFT,
		searchPT,
		searchIS,
		quickSearch,
		
		undefine
		
	}
	private enum EnumJobTableCol{
		
		title,
	//	company, 
		educationRequired,
	//	location,
		jobAvailability,
		tags,
		idJobAd,
		
		undefine
	}
	
	
	
	
}//ENDOF SERVLETJobAD
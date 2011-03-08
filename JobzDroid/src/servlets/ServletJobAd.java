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
		getJobAdById,
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
			case getJobAdById:
				getJobAdById(request, response);
				break;
				
			case loadAdList:
				searchJobAd(request, response);
				break;
				
			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
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
		searchIS
		
		
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
		

		
		default:
		return "";
		
		}
		
		
	}
	
	/*****************************************************************************************************************
	 * 					buildQuery Function
	 * Dynamically making the query as the user's interests
	 * @param request
	 * @return
	 *****************************************************************************************************************/
	private String buildQuery(HttpServletRequest request){
		
		Map<String, String>paraMap = new HashMap<String, String>();
		
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
		//CATION: NEED TO HAVE A SPACE AT THE END oF FOLLOWING listSelQuery!
		String query ="SELECT idJobAd, title, datePosted, contactInfo, educationRequired, jobAvailability FROM tableJobAd WHERE ";
		StringBuffer buf = new StringBuffer();
		buf.append(query);
		String andClause =" AND ";//CAUTION: SPACE IMPORTANT
		String inClause = " IN ";//CAUTION: SPACE IMPORTANT
       for(Map.Entry<String, String> entry : paraMap.entrySet()){
    	   
           if(entry.getKey().equals("jobAvailability")){//if search by the availability term use IN
        	   buf.append(entry.getKey()+ inClause + entry.getValue() + andClause);
           }
           else{//CAUTION: SPACE IMPORTANT
        	   buf.append(entry.getKey()+ " LIKE '%" +entry.getValue()+"%'" + andClause);
           }
        }
		query = buf.substring(0, buf.length()- andClause.length()); //remove the last " AND "
		return query;
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
		
		String query = buildQuery(request);
		
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
	
	
	
	private void getJobAdById(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
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
				jobAd.isApproved 		= result.getBoolean("isApproved");
				
				
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: AD not found with ID=" + jobAdId;
				System.out.println("Error: AD not found with ID=" + jobAdId);
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
		String jobDescription = request.getParameter("strDescription");
		//String jobLocation = request.getParameter("strJobLocation");
		String contactInfo = request.getParameter("strContactInfo");
		String strTags = request.getParameter("strTags");
		
		int educationRequirement = Integer.parseInt(request.getParameter("educationRequirement"));
		
		int expiryYear = Integer.parseInt( request.getParameter("expiryYear"));
		String expiryMonth = request.getParameter("expiryMonth");
		int expiryDay =  Integer.parseInt( request.getParameter("expiryDay"));
		
		int startingYear =  Integer.parseInt(request.getParameter("startingYear"));
		String startingMonth = request.getParameter("startingMonth");
		int startingDay = Integer.parseInt(request.getParameter("startingDay"));
		
		long millisExpiryDate = Utility.calculateDate(expiryYear,expiryMonth,expiryDay);
		long millisStartingDate = Utility.calculateDate(startingYear, startingMonth, startingDay);
		
		Calendar cal = Calendar.getInstance();
		
		long millisDateCreated = cal.getTimeInMillis();

		//TODO: add values for these:
		//int ownerID;
		
		System.out.println(jobAdvertisementTitle);
		System.out.println(jobDescription);
		//System.out.println(jobLocation);
		System.out.println(contactInfo);
		System.out.println(strTags);
		System.out.println("Created On: " + millisDateCreated + " Expire On: " + millisExpiryDate);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {
			System.out.print("Inserting new Job Ad into DB");
			
			stmt = conn.createStatement();
			
			jobAdvertisementTitle = Utility.checkInputFormat( jobAdvertisementTitle );
			jobDescription = Utility.checkInputFormat( jobDescription );
			//jobLocation = Utility.checkInputFormat( jobLocation );
			contactInfo = Utility.checkInputFormat( contactInfo );
			strTags = Utility.checkInputFormat( strTags );
			
			String query = 
				"INSERT INTO tableJobAd(title, description, expiryDate, dateStarting, datePosted, contactInfo, educationRequired, tags ) " +
				"VALUES " + "('" 
					+ jobAdvertisementTitle + "','" 
					+ jobDescription + "','" 
					+ millisExpiryDate + "','" 
					+ millisStartingDate + "','" 
					+ millisDateCreated + "','"
					+ contactInfo + "','" 
					+ educationRequirement + "','"
					+ strTags + 
				"')";
			
			// if successful, 1 row should be inserted
			System.out.println("New Job Ad Query:" + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if (rowsInserted == 1){
				System.out.println("New JobAd Creation success (DB)");
				isSuccessful = true;
				message = "Create Job Advertisement Successful";
			}
			else{
				System.out.println("New JobAd Creation failed");
				Utility.logError("New JobAd insert in DB failed");
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
				temp.location = result.getString("location");
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
			String query = buildQuery(request);
			System.out.println(query);
			boolean success = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			
			//Compile the result into the arraylist
			while( result.next() ) {
				JobAdvertisement temp = new JobAdvertisement();
				
				temp.jobAdId 				= result.getInt("idJobAd");
				temp.jobAdTitle				= result.getString("title");
				temp.creationDateFormatted 	= Utility.dateConvertor(result.getLong("datePosted"));
				temp.contactInfo 			= result.getString("contactInfo");
				temp.educationReq 			= result.getInt("educationRequired");
				temp.jobAvailability 		= result.getString("jobAvailability");
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
//		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
//		XMLResponse.append("\t<message>" + message + "</message>\n");
		Iterator<JobAdvertisement> itr = jobAdList.iterator();
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().toXMLContent() ); 
	    }
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		}
	
}













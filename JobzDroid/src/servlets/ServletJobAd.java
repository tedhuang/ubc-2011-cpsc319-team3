package servlets;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.DBColName;
import classes.DbQuery;
import classes.JobAdvertisement;
import classes.Location;
import classes.Session;
import classes.Utility;

import managers.AccountManager;
import managers.DBManager;
import managers.SystemManager;

/**
 * Servlet implementation class ServletJobAdvertisement
 */
public class ServletJobAd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBManager dbManager;
	private DbQuery DBQ =new DbQuery();
	private DBColName DbDict =	ServletInitializer.retDbColName();
	private AccountManager accManager;
	private Map<String, String>jobAdDBDict=DbDict.getClsDict("jobAd");
	private Map<String, String>jobAdLocDict=DbDict.getDict("jobAdLocation");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJobAd() {
        super();
        dbManager = DBManager.getInstance();
        accManager = new AccountManager();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		//Add new functions here
		createJobAdvertisement,
		saveJobAdDraft,
		updateDraftAd,
		updateOpenAd,
		
		searchJobAdvertisement,
		quickSearchJobAd,
		getJobAdByOwner,
		getJobAdById,
		deleteJobAd,
		getSomeJobAd,
		getSuggestions,
		changeJobAdStatus,
		
		saveFavouriteJobAd,
		listFavouriteJobAd,
		deleteFavouriteJobAd	
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestProcess(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		requestProcess(request,response);
	}

	private void requestProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String action = request.getParameter("action");
		System.out.println(action);
		try{
			EnumAction.valueOf(action);
		}
		catch(IllegalArgumentException e){
			throw new ServletException("Invalid job ad servlet action.");
		}
		
		
		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);

		boolean sessionCheck = true;
		
		//Check which function the request is calling from the servlet
		switch( EnumAction.valueOf(action) ){
		
		/*****************POST AD ACTIONS***********************/
			case createJobAdvertisement :
				createJobAd(request, response);
				break;
			
			case saveJobAdDraft:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "poster") )
					createJobAd(request, response);
				break;
				
			case updateOpenAd:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "poster" ) )
					updateJobAd(request, response);
				break;
				
			case updateDraftAd:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "poster") )
					updateJobAd(request, response);
				break;

			case deleteJobAd:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "poster", "admin", "superAdmin") )
					deleteJobAd(request, response);
				break;
			
			case changeJobAdStatus:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "poster" ) )
					changeJobAdStatus(request,response);
				break;
				
				
		/*****************Retrieve AD ACTIONS***********************/		
			case searchJobAdvertisement:
				searchJobAd(request, response);
				break;
//			case quickSearchJobAd:
//				quickSearchJobAd(request, response);
//				break;
			case getJobAdByOwner:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if(session.checkPrivilege( "poster" ) )
					getJobAdByOwner(request, response);
				break;
				
			case getJobAdById:
				getJobAdById(request, response);
				break;
							
			case getSomeJobAd:
				getSomeJobAd(request, response);
				break;	
			case getSuggestions:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "searcher" ) )
					getSuggestions(request, response);
				break;				
		/*****************FAVOURITE AD ACTIONS***********************/		
				
			case saveFavouriteJobAd:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "searcher") )
					saveFavouriteJobAd(request, response, session);
				break;
				
			case listFavouriteJobAd:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				else if( sessionCheck = session.checkPrivilege( "searcher" ) )
					listFavouriteJobAd(request, response);
				break;
				
			case deleteFavouriteJobAd:
				deleteFavouriteJobAd(request, response);
				break;
				

			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
		}
		
		if ( sessionCheck == false && session != null ) {
			response.sendRedirect("error.html");
		}
		
	}
	
	
	
	
	/*
	 * Method to browse job ad used by job searchers and guests
	 */
	private void getSomeJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("ServletJobAd: Inside getSomeJobAd");

		String message = "getSomeJobAd failed";
		boolean isSuccessful = false;
		
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		
		
		int numToGet = 20;
		int index = Integer.parseInt(request.getParameter("startingIndex"));
		
		System.out.println("Browse index: " + index);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		Statement stmtLoc = null;
		Statement stmtEmp = null;

		try {
			stmt = conn.createStatement();
			String query = 
				 	"SELECT * FROM tableJobAd " +
				 	"WHERE status = 'open' " +
					"ORDER BY datePosted DESC "+
				 	"LIMIT " + index +  ", " + numToGet + ";";
			
			System.out.println(query);
			isSuccessful = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			
			while(result.next()){
				JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
				
				//Fill in the fields of the jobAd object
				jobAd.jobAdId 			= result.getInt("idJobAd");
				jobAd.ownerID 			= result.getInt("idAccount");
				jobAd.creationDate 		= result.getLong("datePosted");
				jobAd.jobAdTitle		= result.getString("title");
				jobAd.expiryDate		= result.getLong("expiryDate");
				jobAd.jobAvailability	= result.getString("jobAvailability");
				jobAd.status 			= result.getString("status");
				jobAd.educationReq 		= result.getInt("educationRequired");
				jobAd.isApproved 		= result.getInt("isApproved");
				jobAd.numberOfViews 	= result.getInt("numberOfViews");

				jobAd.location		 	= result.getString("location");
				jobAd.startingDate 		= result.getLong("dateStarting");
				jobAd.contactInfo 		= result.getString("contactInfo");
				jobAd.tags 				= result.getString("tags");
				
				jobAdList.add(jobAd);

			}//END OF WHILE LOOP
			
			if(jobAdList.isEmpty()){
				message = "Error: No Job Ad found";
				System.out.println("Error: No Job Ad found");
			}
			else{
				System.out.println("getSomeJobAd successful");
				message = "getSomeJobAd successful";
				isSuccessful = true;
			}
					
			
		} //END OF TRY
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	            if(stmtLoc != null)
	            	stmtLoc.close();
	            if(stmtEmp != null)
	            	stmtEmp.close();
	        }
	        catch (Exception e) {
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
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
		
/*******************************************************************************************************************************
 * 									<===+==========GetJobAdByOwner================>
 * - Poster's functions, it retrieves all ads by the owner
 * @param request
 * @param response
 * @throws IOException
 ********************************************************************************************************************************/

	private void getJobAdByOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
	
		String message = "getJobAdByOwner failed";
		boolean isSuccessful = false;
		
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		
		//Extract ownerID from session key
		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);
		int ownerId = session.getIdAccount();

		Connection conn = dbManager.getConnection();	
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			
			String query = 
				"SELECT idJobAd, idAccount, datePosted, title, location, " +
					"expiryDate, jobAvailability, status ,educationRequired FROM tableJobAd " + 
				"WHERE idAccount='" + ownerId +"';";
			
			System.out.println("getJobAdByOwner query:" + query);
			isSuccessful = stmt.execute(query);
			
			ResultSet result = stmt.getResultSet();
			ResultSetMetaData rsmd=null;
		    
			while(result.next()){
				JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
				jobAd.resetValueMap(); //make sure no data leftover
				rsmd = result.getMetaData();
				int numColumns = rsmd.getColumnCount();
				for(int i=1; i<=numColumns; i++){
					String colName = rsmd.getColumnName(i);
					jobAd.valueMap.put(jobAdDBDict.get(colName), result.getObject(colName));
					
				}
				
				jobAdList.add(jobAd);
			
			}//END OF WHILE LOOP
			
			if(jobAdList.isEmpty()){
				message = "Error: No Job Ad found with owner ID =" + ownerId;
				System.out.println("Error: No Job Ad found with owner ID =" + ownerId);
			}
			System.out.println("getJobAdByOwner successful");
			message = "getJobAdByOwner successful";
			isSuccessful = true;

		} //END OF TRY
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
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
	    	XMLResponse.append(itr.next().xmlParser() ); 
	    }
		
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	
	/*
	 * Method used by job poster to change the status of job advertisements
	 */
	private void changeJobAdStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String message = "There is a problem getting the detail, please try again.";
		String jobAdId = request.getParameter("jobAdId");
		String toStatus = request.getParameter("status");
		Connection conn = dbManager.getConnection();	
		
		String[]colToGet = {"idJobAd", "status"};
		ArrayList<String>cond = new ArrayList<String>();
		cond.add("idJobAd="+jobAdId);
		StringBuffer []qBuf = DBQ.buidlSelQuery(new String[]{"tableJobAd"}, colToGet, cond);
		
		try {
			
			String query = 
				//"SELECT idJobAd, status FROM tableJobAd WHERE idJobAd='" + jobAdId + "'";
				qBuf[0].append(qBuf[1]).toString();
			System.out.println("changeJobAdStatus query: " + query);
			
			ResultSet result = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
			   if(result.first()){
				   result.updateString("status", toStatus);
				   result.updateRow();
				   message = "Update job ad status successful";
				   System.out.println(message);
				}
			   else{ //if the job ad doesn't specify  a location
				}
		 }//eof try		 
	    
		catch (SQLException e) {
			System.out.println("SQL exception : " + e.getMessage());
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	
	/*
	 * Extracts all information from the database of the targetted Job Ad
	 * and returns it in a JobAdvertisement object ported to XML format
	 */
	private void getJobAdById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String message = "There is a problem getting the detail, please try again.";
		String jobAdId = request.getParameter("jobAdId");
		
		String[]colToGet={"adTb.idJobAd", "idAccount", "title", "description", "expiryDate", "dateStarting", "datePosted", 
							"contactInfo", "educationRequired", "jobAvailability", "tags", "hasGradFunding",
							"addr0", "addr1", "addr2", "latlng0", "latlng1","latlng2"};
		String[]tables={"tableJobAd as adTb", "tableLocationJobAd as adLoc"};
		String[]onCond={"adTb.idJobAd=adLoc.idJobAd", DBQ.AND+"adTb.idJobAd="+jobAdId };
		
		Connection conn = dbManager.getConnection();	
		JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
		StringBuffer[]qBuf=DBQ.buildJoinQuery(tables, colToGet, "join", onCond);
		
		try {
			
			String query = qBuf[0].append(qBuf[1]).toString();
			System.out.println("getJobAdById query:" + query);
			
			ResultSetMetaData rsmd=null;
			ResultSet result = conn.createStatement().executeQuery(query);
			Map<String, String>locationMap = new HashMap<String, String>();
			   if(result.next()){
				   rsmd = result.getMetaData();
					int numColumns = rsmd.getColumnCount();
					for(int i=1; i<=numColumns; i++){
						String colName = rsmd.getColumnName(i);
						if(colName.matches("(?i)addr.*")|| colName.matches("(?i)latlng.*")){
							if(result.getString(colName)!=null){ //get the locations
								locationMap.put(jobAdLocDict.get(colName), result.getString(colName));
							}
						}
						else{ // get the ad info
							 jobAd.valueMap.put(jobAdDBDict.get(colName), result.getObject(colName));
						}
					}
					jobAd.adLocation=new Location(result.getInt("idJobAd"), locationMap);
				}
			   else{ //if the job ad doesn't specify  a location
				   String[]adOnly={"idJobAd", "idAccount", "title", "description", "expiryDate", "dateStarting", "datePosted", 
							"contactInfo", "educationRequired", "jobAvailability", "tags", "hasGradFunding"};
				   Map<String, Object>conditionMap=new HashMap<String, Object>();
				   conditionMap.put("idJobAd=", jobAdId);
				   qBuf=DBQ.buidlSelQuery(new String[]{"tableJobAd"}, adOnly, conditionMap);
				   query=qBuf[0].append(qBuf[1]).toString();
				   result = conn.createStatement().executeQuery(query);
				   if(result.next()){
					   rsmd = result.getMetaData();
						int numColumns = rsmd.getColumnCount();
						for(int i=1; i<=numColumns; i++){
							String colName = rsmd.getColumnName(i);
								 jobAd.valueMap.put(jobAdDBDict.get(colName), result.getObject(colName));
						}
				   }
			   }
		 }//eof try
			
//			ResultSet result = stmt.getResultSet();
			
//			if (result.first()){
//				System.out.println("getJobAd successful");
//				message = "getJobAd successful";
				
				//Fill in the fields of the jobAd object
				
//				jobAd.jobAdId 			= result.getInt("idJobAd");
//				jobAd.ownerID 			= result.getInt("idAccount");
//				jobAd.jobAdTitle		= result.getString("title");
//				jobAd.jobAdDescription 	= result.getString("description");
//				jobAd.expiryDate		= result.getLong("expiryDate");
//				jobAd.startingDate 		= result.getLong("dateStarting");
//				jobAd.creationDate 		= result.getLong("datePosted");
//				jobAd.status 			= result.getString("status");
//				jobAd.contactInfo 		= result.getString("contactInfo");
//				jobAd.educationReq 		= result.getInt("educationRequired");
//				jobAd.tags 				= result.getString("tags");
//				jobAd.hasGradFunding	= result.getInt("hasGradFunding");
				
				
//			}
//			else{ //Error case
//				isSuccessful = false;
//				message = "Error: Job Ad not found with ID=" + jobAdId;
//				System.out.println("Error: Job Ad not found with ID=" + jobAdId);
//			}
			
			/**Get Employment Availability values */
//				String empAvail = "";
//				int fullTime = 0;
//				int partTime = 0;
//				int internship = 0;
//				
//				query = "SELECT * FROM tableEmpTypeJobAd WHERE " +
//				"idJobAd= '" + jobAdId + "'";
//				System.out.println(query);
//				
//				isSuccessful = stmt.execute(query);
//				result = stmt.getResultSet();
//				
//				if(!result.first()){
//						System.out.println("No Employment Type Found");
//				}
//				else{
//					fullTime = result.getInt("fullTime");
//					partTime = result.getInt("partTime");
//					internship = result.getInt("internship");	
//
//					if( (fullTime+partTime+internship) == 0 ){
//						empAvail = "Not Specified"; //no preference
//					}
//					else{
//						if( fullTime == 1){
//							empAvail += "Full-time ";
//						}
//						if(partTime == 1){
//							empAvail += "Part-time ";
//						}
//						if(internship == 1){
//							empAvail += "Internship";
//						}
//					}
//					System.out.println("Job Positions Available: " + empAvail);
//					jobAd.jobAvailability = empAvail;
//				}
		 
	    
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		if(jobAd.valueMap.size()>0){
			XMLResponse.append(jobAd.xmlParser() );
		}
		else{
			XMLResponse.append("\t<message>" + message + "</message>\n");
		}
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	/*****************************************************************************************************************
 * 					searchJobAd Function
 * 
 * FOR ADVANCED SEARCH 
 * 
 *****************************************************************************************************************/
	private void searchJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{

		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		ArrayList<Location> adLocationList = new ArrayList<Location>();
		ArrayList <String> locQueryCond = new ArrayList<String>();
		String locCond="idJobAd=";
		String locCol[]={"idJobAd","addr0", "latlng0","addr1", "latlng1","addr2", "latlng2"};
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
			ResultSetMetaData rsmd = null;
			
			//Compile the result into the arraylist
			while( result.next() ) {
				JobAdvertisement temp = new JobAdvertisement();
				rsmd = result.getMetaData();
				int numColumns = rsmd.getColumnCount();
				for(int i=1; i<=numColumns; i++){
					String colName = rsmd.getColumnName(i);
						 temp.valueMap.put(jobAdDBDict.get(colName), result.getObject(colName));
				}
				jobAdList.add( temp ); //add to the temporary list
				temp.jobAdId = result.getInt("idJobAd");
				locQueryCond.add(locCond+result.getInt("idJobAd")+DBQ.OR); //prepare the jobId to locInfo 
				
			}
			if(result.first()){ // if we have hits
				StringBuffer[]qBuf=DBQ.buidlSelQuery(new String[]{"tableLocationJobAd"}, locCol, locQueryCond);
				query=qBuf[0].append(qBuf[1].delete(qBuf[1].length()-DBQ.OR.length(), qBuf[1].length())).toString();//get rid of last "OR"
				
				result=conn.createStatement().executeQuery(query);
				Map<String, String>locationMap;
				while(result.next()){ //if we have locations
					rsmd = result.getMetaData();
					int numColumns = rsmd.getColumnCount();
					locationMap=new HashMap<String, String>();
					for(int i=1; i<=numColumns; i++){
						String colName = rsmd.getColumnName(i);
						if(result.getString(colName)!=null ){ //not getting the null values
							locationMap.put(colName, result.getString(colName));
						}
					}
					Location temp = new Location(result.getInt("idJobAd"), locationMap);
					adLocationList.add(temp);
				}
				
				for(JobAdvertisement job_ad : jobAdList){
					for (Location location : adLocationList){
						if (job_ad.jobAdId==location.masterJobId){
							job_ad.adLocation=location;
	//						return;
						}
					}
				}
			}//eof query location base on non-null result
			System.out.println("Search Query Successfully Finished");
			
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
				Utility.logError("Cannot close Statement : " + e.getMessage());
			}
			try {
				if (conn  != null)
					conn.close();
			}
			catch (SQLException e) {
				Utility.logError("Cannot close Connection : " + e.getMessage());
			}
		}
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		Iterator<JobAdvertisement> itr = jobAdList.iterator();
	    while (itr.hasNext()) {//iterate through all list and append to xml
	    	XMLResponse.append(itr.next().xmlParser() ); 
	    }
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		}
	
/*****************************************************************************************************************
 * 					buildSearchQuery Function
 * Dynamically making the query as the user's interests
 * Caller Function:  searchJobAd
 *****************************************************************************************************************/
	private String buildSearchQuery(HttpServletRequest request){
		Map<String, String>paraMap = new HashMap<String, String>();//col-value
		boolean qkSearch=false;
		
		Map<String, String> paraColMap = DbDict.getDict(request.getParameter("action"));
		StringBuffer qLocBuf = new StringBuffer();
		
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			
			String paraName = (String) paramNames.nextElement();
			if( paraName.equals("action")){//Not Querying this
				//Do Nothing
			}
			else{
				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
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
				else if(colName.equals("location")){
					 qLocBuf.append(colName+ DBQ.LIKE + DBQ.SQUO+DBQ.PCNT+request.getParameter(paraName)+DBQ.PCNT+DBQ.SQUO + DBQ.AND);
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
		String query =DBQ.SELECT + 
					  "idJobAd, title, datePosted, contactInfo, " +
					  "educationRequired, jobAvailability, location FROM tableJobAd" +  DBQ.WHERE;
		boolean panic = false;
		
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(query);
		
       for(Map.Entry<String, String> entry : paraMap.entrySet()){
    	   String column = entry.getKey();
    	   String value = Utility.checkInputFormat(entry.getValue());
    	   
    	   if(!(column.equals("quickSearch"))){
    		   
    		   if(column.equals("title")||column.equals("tags")){
    			   StringBuffer sb = new StringBuffer(DBQ.wordRegExBuffer);
    			   queryBuf.append(column+ DBQ.REGEXP + sb.insert(8,value) + DBQ.AND);
    		   }
    		   else if(column.equals("jobAvailability")){
    			   //if search by the availability term use IN
   	    		    queryBuf.append(column+ DBQ.IN + value + DBQ.AND);
    		   }
    		   else if(column.equals("educationRequired")){
   	    		    queryBuf.append(column+ DBQ.EQ + value + DBQ.AND);
    		   }
//    		   else if(column.equals("location")){
//    			   qLocBuf.append(column+ DBQ.LIKE + DBQ.SQUO+DBQ.PCNT+value+DBQ.PCNT+DBQ.SQUO + DBQ.AND);
//    		   }
    		   else{
	    		   System.out.println("DON'T YOU TRY TO HACK!");
	    		   panic=true;
	    		   break;
    		   }
    	   }
        }//ENDOF FOR-MAP LOOP
       
       
    	   if (!panic){
	  			
	  			if(!qkSearch){
	  				//remove the last " AND "
	  				if(qLocBuf.length()>0){
	  					qLocBuf.delete(qLocBuf.length() - DBQ.AND.length(), qLocBuf.length());
		  				queryBuf.append(qLocBuf);
	  				}
	  				else{
	  					queryBuf.delete(queryBuf.length() - DBQ.AND.length(), queryBuf.length()); 
	  				}
	  				
	  			}
	  			else{
	  				queryBuf.delete(queryBuf.length() - DBQ.OR.length(), queryBuf.length()); //remove the last " OR "
	  			}
	  			queryBuf.append(DBQ.ORDERBY + "datePosted" + DBQ.DESC);//TODO Can have pages using limited
	  			query = queryBuf.toString();
			}//eof IF-NOT-PANIC
       
		return query;
	}
	
/****************************************************************************************************************
 * 
 * @param request	
 * @param response
 * @throws IOException
 ****************************************************************************************************************/
private void createJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		mysqlCmd qcmd = new mysqlCmd();
		//Debug
		System.out.println("Entered createJobAdvertisement");
		//initialize return statements
		boolean isSuccessful = false;
		String msg = "";
		
		System.out.println("sessionKey=" + request.getParameter("sessionKey"));
		
		String sKey=request.getParameter("sessionKey");
		System.out.println("sessionKey=" +sKey );
		//Checks the user's privilege
		int acctId=-1;
		
		StringBuffer qBuf =sessPosterAuthQuery(sKey,qcmd); // authenticate user
		String query=qBuf.substring(1, qBuf.length()-2); //Remove bracket
		System.out.println(query);

//			String address 				 = request.getParameter("address");
//			double longitude 			 = Double.parseDouble(request.getParameter("longitude"));
//			double latitude 			 = Double.parseDouble(request.getParameter("latitude"));
			
//			System.out.println("Created On: " + millisDateCreated + " Expire On: " + millisExpiryDate);
//			System.out.println("Location: " + address + " Long: " + longitude + " Lat: " + latitude);
			
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			try {
				
				System.out.println("Processing " + query);
				stmt = conn.createStatement();
				ResultSet authRs = stmt.executeQuery(query);
				if(authRs.next()){
					acctId=authRs.getInt("idAccount");
				}
				if(acctId==-1){
					stmt.close();
					conn.close();
				}
				else{
					stmt=conn.createStatement(); //create a new statement
					StringBuffer[]postQuery=buildPostAdQuery(request, acctId);
					query = postQuery[0].toString(); //The Post Ad Query
					
					int success = stmt.executeUpdate(query);
					if (success != 1){
						System.out.println("New JobAd Creation failed");
						Utility.logError("New JobAd insert in DB failed");
					}
					
					msg = "Action Performed Successfully";
					//Get jobAdId By Using select the last inserted ID
					PreparedStatement getLastInsertId = conn.prepareStatement("SELECT LAST_INSERT_ID()");
					ResultSet rs = getLastInsertId.executeQuery();
					
					int jobAdId = -1;
					if (rs.next()){
						jobAdId = rs.getInt("last_insert_id()"); 
						System.out.println("Retrived: Job Ad ID: " + jobAdId);
					}
					else{
						System.out.println("Error: Job Ad ID not found after creation");
					}
				   if(jobAdId != -1 && postQuery[1]!=null){	
					   stmt=conn.createStatement(); //create a new statement maybe not needed
					   PreparedStatement postLocation = conn.prepareStatement(postQuery[1].toString());
					   postLocation.setInt(1, jobAdId);
//					   query = postQuery[1].toString(); //Insert location values into location table
					   //DEBUG
					   System.out.println("Location table query: " + postLocation.toString());
					
					   postLocation.executeUpdate();
					   
					   rs = getLastInsertId.executeQuery();
					   if(rs.next()){
					  	 System.out.println("Location Inserted With Job Id: " + rs.getInt("last_insert_id()"));
					   }
					   
					   else{
						Utility.logError("No Location Specified or Inserting location for new job ad failed");
					   }
	
				  }//ENDOF INSERT INTO LOCATION TABLE 
				}
			}
			catch (SQLException e) {
				Utility.logError("SQL exception : " + e.getMessage());
			}
			// close DB objects
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement : " + e.getMessage());
		        }
		        try {
		            if (conn  != null)
		                conn.close();
		        }
		        catch (SQLException e) {
		        	Utility.logError("Cannot close Connection : " + e.getMessage());
		        }
		    }
		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
//		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + msg + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
/***************************************************************************************************************
 * 					Build Post Job Ad Query
 * - In charge of Creating and Drafting Job Ad
 * @param request
 * @param IdAcct
 * @return
 ***************************************************************************************************************/
private StringBuffer[] buildPostAdQuery(HttpServletRequest request, int IdAcct){
	StringBuffer [] queriesBuffer = new StringBuffer[2];//size 2
	mysqlCmd qcmd 	 			= new mysqlCmd(); 
	Map<String, Object>paraMap	= new HashMap<String, Object>();//JobAd Table Mapping
	Map<String, String>locMap	= new HashMap<String, String>();//JobAd Location Table Mapping
	StringBuffer queryBuf 		= new StringBuffer();
	StringBuffer locQueryBuf	= new StringBuffer();
	String action = request.getParameter("action");
	
	Map<String, String> paraColMap = DbDict.getDict(action);
	Map<String, String> jobLocMap  = DbDict.getDict("jobAdLocation");
	
	Enumeration paramNames = request.getParameterNames();
	while (paramNames.hasMoreElements()) {
		
		String paraName = (String) paramNames.nextElement();
		if( !paraName.equals("action") && !paraName.equals("sessionKey")){//Not query "action"&"sKey"
			//debug
			System.out.println("Request: "  + paraName + " Value: " +request.getParameter(paraName));
			
			if(paraName.matches("(?i).*addr.*") || paraName.matches("(?i).*latlng.*")){//Distinguish for the location query 
				String colName = jobLocMap.get(paraName);
				locMap.put(colName, Utility.checkInputFormat(request.getParameter(paraName)) );
				//Debug
			    System.out.println("Column: " + colName + " Value: " + locMap.get(colName)+"\n");
			}
			
			else{
				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
				
				//Put the parameters' names and values into the MAP
				if(colName.equals("dateStarting")||colName.equals("expiryDate")){
					long aDate=Utility.dateConvertor(request.getParameter(paraName)); //TODO Double Check convertor
					paraMap.put(colName, aDate);
				}
				else if(colName.equals("title")){
					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
					paraMap.put(colName, strFormatted);
				}
				else if(colName.equals("description")){
					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
					strFormatted = Utility.processLineBreaksWhiteSpaces(strFormatted);
					paraMap.put(colName, strFormatted);
				}
				else{
					paraMap.put(colName, Utility.checkInputFormat(request.getParameter(paraName)) );//will fail if no value is actually passed
				}
				//Debug
			    System.out.println("Column: " + colName + " Value: " + paraMap.get(colName)+"\n");
			}
			
			
		} //ENDOF IF SET COL-VAL IN PARAMAP
		
	  	
	  }//ENDOF WHILE paramNames.hasMoreElements()
	paraMap.put("idAccount", IdAcct);//set the owner id
	paraMap.put("datePosted", Calendar.getInstance().getTimeInMillis());//set the creation timeStamp
	
	/*
	 * Entering Query making
	 * CAUTION: SPACE IMPORTANT
	 */
	StringBuffer stm1 = new StringBuffer();
	StringBuffer stm2 = new StringBuffer();
	
	stm1.append( qcmd.INSERT + qcmd.INTO + "tableJobAd " + qcmd.PRNTHS);
	stm2.append( qcmd.VALUES + qcmd.PRNTHS);
	stm1.insert(stm1.length()-2, "status"+qcmd.COMA);
    
	if(action.equals("saveJobAdDraft")){//status is draft
		stm2.insert(stm2.length()-2, "'draft'" + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
	  }
	else if(action.equals("createJobAdvertisement")){
		//status is pending
		if(SystemManager.enableJobAdApproval)
			stm2.insert(stm2.length()-2, "'pending'" + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
		else
			stm2.insert(stm2.length()-2, "'open'" + qcmd.COMA);
	}
	
	for(Map.Entry<String, Object> entry : paraMap.entrySet()){
		String column= entry.getKey();
		Object value = entry.getValue();
		stm1.insert(stm1.length()-2 , column+ qcmd.COMA);//insert col into the parentheses
		stm2.insert(stm2.length()-2,  qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
	}
		stm1.delete(stm1.length() - qcmd.COMA.length()-2, stm1.length()-2);//get rid of last coma
		stm2.delete(stm2.length() - qcmd.COMA.length()-2, stm2.length()-2);
		queryBuf.append(stm1.append(stm2));//merge the query
		queriesBuffer[0]=queryBuf;
	
	if(locMap.size()>0){
		//Handle Location query
		stm1.setLength(0);
		stm2.setLength(0);
		stm1.append( qcmd.INSERT + qcmd.INTO + "tableLocationJobAd " + qcmd.PRNTHS);
	//	stm1.insert(stm1.length()-2, "idJobAd, "); //Prepare for the job id
		stm2.append( qcmd.VALUES + qcmd.PRNTHS);
		locMap.put("idJobAd", "?");//This will be used in the preparedStatement
		
		for(Map.Entry<String, String> entry : locMap.entrySet()){
			String column= entry.getKey();
			String value = entry.getValue();
			stm1.insert(stm1.length()-2 , column+ qcmd.COMA);//insert col into the parentheses
			if(column.equals("idJobAd")){
			 stm2.insert(stm2.length()-2,  value + qcmd.COMA);
			}
			else{
				stm2.insert(stm2.length()-2,  qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
			}
		}
			stm1.delete(stm1.length() - qcmd.COMA.length()-2, stm1.length()-2);//get rid of last coma
			stm2.delete(stm2.length() - qcmd.COMA.length()-2, stm2.length()-2);
			locQueryBuf.append(stm1.append(stm2));//merge the query
			queriesBuffer[1]=locQueryBuf;
	}
	
	return queriesBuffer;
}

private void updateJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
	//Debug
	System.out.println("<====================Entered updateJobAd===========================>");
	//initialize return statements
	String msg = "Opps, Apearently There Is Some Problem When Updating your Ad. Please Try Again Later.";
	
	//Checks the user's privilege
	System.out.println("User with sessionKey: " + request.getParameter("sessionKey"));
	String sKey=request.getParameter("sessionKey");
	int jobAdId = Integer.parseInt(request.getParameter("adId-field"));
	int acctId=-1;
	StringBuffer qBuf =DBQ.sessAuthQuery(sKey,new String[]{"idAccount"},"poster"); // authenticate user
	String query=qBuf.substring(1, qBuf.length()-2); //Remove bracket
	//DEBUG
	System.out.println(query);

	Connection conn = dbManager.getConnection();	
		
	try {
			
		System.out.println("Processing " + query);
		ResultSet authRs = conn.createStatement().executeQuery(query);
		if(authRs.next()){
			acctId=authRs.getInt("idAccount");
		}
		if(acctId==-1){
			conn.close(); //user-auth failed
		}
		else{
			
			StringBuffer[]postQuery=buildUpdateAdQuery(request, acctId);
			
			query = postQuery[0].toString(); //The update input-form Query
			if(query.length()!=0){
				int success = conn.createStatement().executeUpdate(query);
				if (success != 1){
					System.out.println("Job Ad Input Form Update failed");
					Utility.logError("Job Ad Input Form Update failed");
				}
				else{
					String chkAddionalMode = request.getParameter("updateDead");
					if(chkAddionalMode!=null){
						msg = "Ad Updated Successfully";
						ArrayList<String>cond=new ArrayList<String>();
						cond.add("idJobAd"+DBQ.EQ+jobAdId);
						StringBuffer[] statusQuery=DBQ.buidlSelQuery(new String[]{"tableJobAd"}, new String[]{"idJobAd","status"}, cond);
						query = statusQuery[0].append(statusQuery[1]).toString();
						ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
						if(rs.first()){
							if(rs.getString("status").matches("(?i)inactive|pending")){
								if(chkAddionalMode.matches("(?i)publish.*")){
									rs.updateString("status", "pending");
								}
								else if(chkAddionalMode.matches("(?i).*todraft.*")){
									rs.updateString("status", "draft");
								}
									
									rs.updateRow();
							}
						}
					}
//					StringBuffer stm1=new StringBuffer( DBQ.UPDATE + "tableJobAd" +DBQ.SET);
//					StringBuffer stm2=new StringBuffer( DBQ.WHERE + "idJobAd"+DBQ.EQ+DBQ.BRKT.insert(0, "SELECT LAST_INSERT_ID()")+
//														DBQ.AND+"status"+DBQ.EQ+"inactive"+DBQ.OR+"status"+DBQ.EQ+"pending");
				}
			}
			
			query = postQuery[1].toString(); //The update location Query
			if(query.length()!=0){
				int success = conn.createStatement().executeUpdate(query);
				if (success != 1){
					System.out.println("Job Ad Location Update failed");
					Utility.logError("Job Ad Location Update failed");
				}
				else{
					msg = "Ad Updated Successfully";
				}
			}
			

		  }//ENDOF INSERT INTO LOCATION TABLE 
		}
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (conn != null)
	                conn.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Connection : " + e.getMessage());
	        }
	    }
	  //Debug
	System.out.println("<====================EOF updateJobAd===========================>");
	
	StringBuffer XMLResponse = new StringBuffer();	
	XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
	XMLResponse.append("<response>\n");
	XMLResponse.append("\t<message>" + msg + "</message>\n");
	XMLResponse.append("</response>\n");
	response.setContentType("application/xml");
	response.getWriter().println(XMLResponse);
	
}

/***************************************************************************************************************
 * 					Build Post Job Ad Query
 * - In charge of Creating and Drafting Job Ad
 * @param request
 * @param IdAcct
 * @return
 ***************************************************************************************************************/
private StringBuffer[] buildUpdateAdQuery(HttpServletRequest request, int IdAcct){
	StringBuffer [] queriesBuffer = new StringBuffer[2];//size 2
	Map<String, Object>paraMap	= new HashMap<String, Object>();//JobAd Table Mapping
	Map<String, String>locMap	= new HashMap<String, String>();//JobAd Location Table Mapping
	StringBuffer queryBuf 		= new StringBuffer();
	StringBuffer locQueryBuf	= new StringBuffer();
	String action = request.getParameter("action");
	
	Map<String, String> paraColMap = DbDict.getDict(action);
	Map<String, String> jobLocMap  = DbDict.getDict("jobAdLocation");
	int jobAdId=-1;
	
	Enumeration paramNames = request.getParameterNames();
	
	 while (paramNames.hasMoreElements()) {
		
		String paraName = (String) paramNames.nextElement();
		if( !paraName.equals("action") && !paraName.equals("sessionKey")&&!paraName.equals("updateDead")){//Not querying "action"&"sKey"
			//debug
			System.out.println("Request: "  + paraName + " Value: " +request.getParameter(paraName));
			
			if(paraName.matches("(?i).*addr.*") || paraName.matches("(?i).*latlng.*")){//Distinguish for the location query 
				String colName = jobLocMap.get(paraName);
				locMap.put(colName, Utility.checkInputFormat(request.getParameter(paraName)) );
				//Debug
			    System.out.println("Column: " + colName + " Value: " + locMap.get(colName)+"\n");
			}
			
			else{
				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
				
				//Put the parameters' names and values into the MAP
				if(colName.matches("(?i)expirydate|datestarting|.*date.*")){//if it is a field about "date" convert to Long
					long aDate=Utility.dateConvertor(request.getParameter(paraName)); //TODO Double Check convertor
					paraMap.put(colName, aDate);
				}
				else if(colName.equals("idJobAd")){
					String str = request.getParameter(paraName);
					jobAdId = Integer.parseInt(str);
				}
				else if(colName.equals("title")){
					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
					paraMap.put(colName, strFormatted);
				}
				else if(colName.equals("description")){
					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
					strFormatted = Utility.processLineBreaksWhiteSpaces(strFormatted);
					paraMap.put(colName, strFormatted);
				}
				else{
					paraMap.put(colName, Utility.checkInputFormat(request.getParameter(paraName)) );//will fail if no value is actually passed
				}
				//Debug
			    System.out.println("Column: " + colName + " Value: " + paraMap.get(colName)+"\n");
			}
			
			
		} //ENDOF IF SET COL-VAL IN PARAMAP
		
	  	
	  }//ENDOF WHILE paramNames.hasMoreElements()
	 StringBuffer stm1 = new StringBuffer();
	 StringBuffer stm2 = new StringBuffer();
	 
	 if(!paraMap.isEmpty()){ //only location is updated
		 
		paraMap.put("datePosted", Calendar.getInstance().getTimeInMillis());//set the creation timeStamp
		
		/*
		 * Entering Query making
		 * CAUTION: SPACE IMPORTANT
		 */
		
		stm1.append( DBQ.UPDATE + "tableJobAd" +DBQ.SET);
		stm2.append( DBQ.WHERE + "idAccount" + DBQ.EQ + IdAcct + DBQ.AND +"idJobAd" +DBQ.EQ + jobAdId);
	    
		for(Map.Entry<String, Object> entry : paraMap.entrySet()){
			String column= entry.getKey();
			Object value = entry.getValue();
			
			if(value instanceof String){
				stm1.append(column +DBQ.EQ + DBQ.SQUO+ value + DBQ.SQUO + DBQ.COMA);
			}
			else if((value instanceof Long) || (value instanceof Integer)){
				stm1.append(column +DBQ.EQ + value + DBQ.COMA);
			}
		}
			stm1.delete(stm1.length() - DBQ.COMA.length(), stm1.length());//get rid of last coma
			queryBuf.append(stm1.append(stm2));//merge the query
	 }
	 
	if(!locMap.isEmpty()){
		//Handle Location query
		stm1.setLength(0);
		stm2.setLength(0);
		stm1.append( DBQ.UPDATE + "tableLocationJobAd " + DBQ.SET);
	//	stm1.insert(stm1.length()-2, "idJobAd, "); //Prepare for the job id
		stm2.append( DBQ.WHERE + "idJobAd" + DBQ.EQ + jobAdId);
		
		for(Map.Entry<String, String> entry : locMap.entrySet()){
			String column= entry.getKey();
			String value = entry.getValue();
			stm1.insert(stm1.length()-2 , column+ DBQ.COMA);//insert col into the parentheses
				stm1.append(DBQ.SQUO + value + DBQ.SQUO + DBQ.COMA);
		}
			stm1.delete(stm1.length() - DBQ.COMA.length()-2, stm1.length()-2);//get rid of last coma
			locQueryBuf.append(stm1.append(stm2));//merge the query
	}
	queriesBuffer[0]=queryBuf;
	queriesBuffer[1]=locQueryBuf;
	return queriesBuffer;
}

	

	/*
	 * Permenently removes the job ad by ID from the database
	 */
	private void deleteJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		mysqlCmd qcmd = new mysqlCmd(); 
		String feedback="adminDeleteJobAd failed";
		String sessKey = request.getParameter("sessionKey");
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
		
		StringBuffer qBuf = sessPosterAuthQuery(sessKey, qcmd); 
		qBuf.insert(0, qcmd.SELECT + "tbAd.idJobAd, tbAd.idAccount" + 
					   qcmd.FROM + "tableJobAd tbAd" + qcmd.WHERE + "idAccount" + qcmd.EQ);
		qBuf.append(qcmd.AND +"idJobAd" + qcmd.EQ + jobAdId);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		
		try {
//			stmt = conn.createStatement();
			
			//Delete designed job Ad
			String query = qBuf.toString();
			System.out.println("Processin Query: " + query);
			ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							   .executeQuery(query);
			
//			stmt.executeQuery(query);
			
//			ResultSet rs = stmt.getResultSet();
			while (rs.next()){
				System.out.println("JobAd: ID-" + rs.getInt("idJobAd") +"was deleted by " + 
						   			"Use: ID-" + rs.getInt("idAccount") +" with sessionKey-" + sessKey +
						   			"At time:" + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
				feedback="The Ad was deleted!";
				rs.deleteRow();
				
			}
			
		}
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	Utility.logError("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + feedback + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	

	private StringBuffer sessPosterAuthQuery(String sKey, mysqlCmd qcmd){
		
		//TODO refactor this???? Harry
		
		StringBuffer qSessTb =new StringBuffer();
		StringBuffer qAcctTb =new StringBuffer();
		
		qSessTb.append(qcmd.PRNTHS);
		qAcctTb.append(qcmd.PRNTHS); //append brackets
		
		qSessTb.insert(1, qcmd.SELECT + "idAccount" + qcmd.FROM + "tableSession" +  
						  qcmd.WHERE +"sessionKey" + qcmd.EQ + qcmd.SQUO +sKey + qcmd.SQUO);
		qAcctTb.insert(1, qcmd.SELECT + "idAccount" + qcmd.FROM + "tableAccount " + 
				  		  qcmd.WHERE +"idAccount" + qcmd.EQ);
		qAcctTb.insert(qAcctTb.length()-2, qSessTb + qcmd.AND + "type" + qcmd.EQ + "'poster'" + 
										   qcmd.AND + "status" +qcmd.EQ +"'active'");
		return qAcctTb;
	}
	private StringBuffer sessSearcherAuthQuery(String sKey, mysqlCmd qcmd, String[]getArr){
		//TODO check this for refactor
		StringBuffer qSessTb =new StringBuffer();
		StringBuffer qAcctTb =new StringBuffer();
		StringBuffer infoToGet =new StringBuffer();
		for(String str:getArr){
			infoToGet.append(str+qcmd.COMA);
		}
		infoToGet.delete(infoToGet.length()-qcmd.COMA.length(), infoToGet.length());
		qSessTb.append(qcmd.PRNTHS);
		qAcctTb.append(qcmd.PRNTHS); //append brackets
		
		qSessTb.insert(1, qcmd.SELECT + "idAccount" + qcmd.FROM + "tableSession" +  
						  qcmd.WHERE +"sessionKey" + qcmd.EQ + qcmd.SQUO +sKey + qcmd.SQUO);
		qAcctTb.insert(1, qcmd.SELECT + infoToGet + qcmd.FROM + "tableAccount " + 
				  		  qcmd.WHERE +"idAccount" + qcmd.EQ);
		qAcctTb.insert(qAcctTb.length()-2, qSessTb + qcmd.AND + "type" + qcmd.EQ + "'searcher'" + 
										   qcmd.AND + "status" +qcmd.EQ +"'active'");
		return qAcctTb;
	}
	
	
	 public final class mysqlCmd{//THIS STRUCTURE REDUCES THE POTENTIAL ERROR BY SPACE INSIDE THE QUERY 
		 //TODO refactor this?? -harry
		String SELECT			="SELECT ";		//CAUTION: SPACE IMPORTANT
		String INSERT			="INSERT ";		//CAUTION: SPACE IMPORTANT
		String UPDATE			="UPDATE ";		//CAUTION: SPACE IMPORTANT
		String DEL				="DELETE ";		//CAUTION: SPACE IMPORTANT
		
		String AS				= " AS "; 		//CAUTION: SPACE IMPORTANT
		String IN 				= " IN ";		//CAUTION: SPACE IMPORTANT
		String OR				= " OR "; 		//CAUTION: SPACE IMPORTANT
		String ON				= " ON ";		//CAUTION: SPACE IMPORTANT
		String AND 				= " AND ";		//CAUTION: SPACE IMPORTANT
		String SET				= " SET ";		//CAUTION: SPACE IMPORTANT
		String LIKE				= " LIKE "; 	//CAUTION: SPACE IMPORTANT
		String REGEXP		 	= " REGEXP ";	//CAUTION: SPACE IMPORTANT
		String WHERE			= " WHERE ";	//CAUTION: SPACE IMPORTANT
		String ORDER			= " ORDER BY "; //CAUTION: SPACE IMPORTANT
		String LIMIT			= " LIMIT "; 	//CAUTION: SPACE IMPORTANT
		String DESC				= " DESC "; 		//CAUTION: SPACE IMPORTANT
		String INTO				= " INTO "; 		//CAUTION: SPACE IMPORTANT
		String FROM				= " FROM "; 		//CAUTION: SPACE IMPORTANT
		String VALUES			= " VALUES "; 	//CAUTION: SPACE IMPORTANT
		
		String COMA				= ", ";		//CAUTION: SPACE IMPORTANT
		String PRNTHS			= "() ";	//CAUTION: SPACE IMPORTANT, insert-pos:1
//		String RPRNTHS			= ") ";
		String SQUO				= "'";
		String EQ				= "=";
		String GRTR				= ">";
		String SMLR				= "<";
		StringBuffer wordRegExBuffer = new StringBuffer("'[[:<:]][[:>:]]' "); //CAUTION: SPACE IMPORTANT, insert-pos:8
		StringBuffer SQ				 =  new StringBuffer("'' ");//CAUTION: SPACE IMPORTANT, insert-pos:1
//		StringBuffer 
		private mysqlCmd(){
		}
	}
	 private StringBuffer[] buildSuggestionQuery(boolean initSugg, String[]userInfo, String[]adInfo){
			
		 	Map<String, String> colMatchMap = DbDict.getDict("getSuggestions");
		 	StringBuffer[] suggQueryArr=new StringBuffer[3];
		 	StringBuffer mostMatched = new StringBuffer(); //for most match ed suggestions
		 	StringBuffer wildSugg   = new StringBuffer();
		 	
			StringBuffer qProfileTb = new StringBuffer(); 
			StringBuffer uInfoBuf   = new StringBuffer();
			StringBuffer adInfoBuf  = new StringBuffer();
			for(String str:userInfo){
				uInfoBuf.append(str+DBQ.COMA);
			}
			for(String str:adInfo){
				adInfoBuf.append(str+DBQ.COMA);
			}
			//remove extra coma
			uInfoBuf.delete( uInfoBuf.length()-DBQ.COMA.length(), uInfoBuf.length());
			adInfoBuf.delete(adInfoBuf.length()-DBQ.COMA.length(), adInfoBuf.length());
			
			qProfileTb.append(DBQ.SELECT + uInfoBuf + DBQ.FROM + "tableProfileSearcher " + 
								DBQ.WHERE +"idAccount" + DBQ.EQ + "?");
			suggQueryArr[0]=qProfileTb;
			
			//query for most matched suggestions
			mostMatched.append(DBQ.SELECT + adInfoBuf + DBQ.FROM + "tableJobAd" + DBQ.WHERE );
			for(String str:userInfo){
				if(str.equals("startingDate")){
					mostMatched.append(colMatchMap.get(str)+DBQ.GRTR +"?" +DBQ.AND);
				}
				else if(str.equals("location")){
					mostMatched.append(colMatchMap.get(str) + DBQ.LIKE + "?" +
										DBQ.AND + colMatchMap.get(str) + DBQ.LIKE + "?" +DBQ.AND); //location 
				}
				else{
					mostMatched.append(colMatchMap.get(str)+DBQ.EQ +"?" +DBQ.AND);
				}
			}
			mostMatched.delete(mostMatched.length()-DBQ.AND.length(), mostMatched.length()); //rm extra "AND"
			mostMatched.append(DBQ.ORDERBY + "datePosted" + DBQ.DESC);
			if(initSugg){
				mostMatched.append(DBQ.LIMIT + 0 + DBQ.COMA + 5);
			}
			
			suggQueryArr[1]=mostMatched;
			
			//prepare for the search on table job ad
			wildSugg.append(DBQ.SELECT + adInfoBuf + DBQ.FROM + "tableJobAd" +  
							DBQ.WHERE );
			for(String str:userInfo){
				
				wildSugg.append(colMatchMap.get(str)+DBQ.EQ +"?" +DBQ.OR);
			}
			wildSugg.delete(wildSugg.length()-DBQ.OR.length(), wildSugg.length()); //rm extra "OR"
			suggQueryArr[2]=wildSugg;
			return suggQueryArr;
		}
	 
	 private void getSuggestions(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 	ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
			mysqlCmd qcmd = new mysqlCmd();
			String[]authInfo={"idAccount"};
			String[]uInfo={"educationLevel", "startingDate", "location"};
			String[]adInfo={"idJobAd","title","datePosted","location","contactInfo","educationRequired"};
			//Debug
			System.out.println("Entered get Suggestions");
			//initialize return statements
			boolean isSuccessful = false;
			String msg = "";
			
			System.out.println("sessionKey=" + request.getParameter("sessionKey"));
			
			String sKey=request.getParameter("sessionKey");
			System.out.println("sessionKey=" +sKey );
			String moreSugg=request.getParameter("mode");
			
			//Checks the user's privilege
			int acctId=-1;
			
			StringBuffer qBuf =sessSearcherAuthQuery(sKey,qcmd, authInfo); // authenticate user
			String query=qBuf.substring(1, qBuf.length()-2); //Remove bracket
			System.out.println(query);
				
				Connection conn = dbManager.getConnection();	
				Statement stmt = null;
				
				try {
					
					System.out.println("Processing " + query);
					stmt = conn.createStatement();
					ResultSet authRs = stmt.executeQuery(query);
					if(authRs.next()){ //authenticating
						acctId=authRs.getInt("idAccount");
					}
					if(acctId==-1){
						stmt.close();
						conn.close();
					}
					else{
						stmt=conn.createStatement(); //create a new statement
						StringBuffer[]suggQueryBuf = null;
						if(moreSugg.equals("true")){ // if it's a init load only 5 results pop up
							suggQueryBuf=buildSuggestionQuery(true, uInfo, adInfo); // extract prepared query buffers
						}
						else{
							suggQueryBuf=buildSuggestionQuery(false, uInfo, adInfo); // extract prepared query buffers
						}
						//first get the user info
						PreparedStatement suggQuery = conn.prepareStatement(suggQueryBuf[0].toString());
						suggQuery.setInt(1, acctId);
						
						ResultSet suggRs = suggQuery.executeQuery();
						
						//now get the suggestions that MOST MATCHED user's info
						suggQuery = conn.prepareStatement(suggQueryBuf[1].toString());
						while(suggRs.next()){
							//THE ORDER NEEDS TO BE CONSISTENT WITH THE USERINFO ARRAY
							//TODO NULL CHECK
							String[]locInfo=Utility.locationParser(suggRs.getString("location"));
							suggQuery.setInt(1, suggRs.getInt("educationLevel"));
							suggQuery.setLong(2, suggRs.getLong("startingDate"));
							suggQuery.setString(3, "%"+locInfo[0]+"%");
							suggQuery.setString(4, "%"+locInfo[1]+"%");
						}
						
					   stmt=conn.createStatement(); //create a new statement maybe not needed
					   //DEBUG
					   System.out.println("suggestion querying on job ad table: " + suggQuery.toString());
					
					   suggRs = suggQuery.executeQuery();
					   ResultSetMetaData rsmd=null;
					   
					   while(suggRs.next()){
						   JobAdvertisement temp = new JobAdvertisement();
						   
						   rsmd = suggRs.getMetaData();
							int numColumns = rsmd.getColumnCount();
							for(int i=1; i<=numColumns; i++){
								String colName = rsmd.getColumnName(i);
								temp.valueMap.put(jobAdDBDict.get(colName), suggRs.getObject(colName));
								
							}
//							temp.jobAdId 				= suggRs.getInt("idJobAd");
//							temp.jobAdTitle				= suggRs.getString("title");
//							temp.creationDate		 	= suggRs.getLong("datePosted");
//							temp.location				= suggRs.getString("location");
//							temp.contactInfo 			= suggRs.getString("contactInfo");
//							temp.educationReq	 		= suggRs.getInt("educationRequired");
//							temp.jobAvailability 		= Utility.jobTypeTranslator(false,suggRs.getString("jobAvailability"));
//							temp.tags 					= result.getString("tags");
							
							jobAdList.add( temp ); //add to the temporary list
					   }
					   
					  }//ENDOF INSERT INTO LOCATION TABLE 
				}
				catch (SQLException e) {
					Utility.logError("SQL exception : " + e.getMessage());
				}
				// close DB objects
			    finally {
			        try{
			            if (stmt != null)
			                stmt.close();
			        }
			        catch (Exception e) {
			        	Utility.logError("Cannot close Statement : " + e.getMessage());
			        }
			        try {
			            if (conn  != null)
			                conn.close();
			        }
			        catch (SQLException e) {
			        	Utility.logError("Cannot close Connection : " + e.getMessage());
			        }
			    }
			
			StringBuffer XMLResponse = new StringBuffer();	
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
//			XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
			Iterator<JobAdvertisement> itr = jobAdList.iterator();
		    while (itr.hasNext()) {//iterate through all list and append to xml
		    	XMLResponse.append(itr.next().xmlParser() ); 
		    }
			XMLResponse.append("\t<message>" + msg + "</message>\n");
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
			
		}
/****
 * saveFavouriteJobAd()
 * 
 * 
 ****/
	private void saveFavouriteJobAd(HttpServletRequest request, HttpServletResponse response, Session session)throws IOException{

		
		String msg = "";

		int accountId = session.getIdAccount();
		int jobAdId = -1;
		long dateAdded = -1;
		String query = "";
		
		String jobAdIdInString = request.getParameter("jobAdId");
		try{
			jobAdId = Integer.parseInt(jobAdIdInString);
		}
		catch(NumberFormatException e){
			response.sendRedirect("error.html");
		}
		
		
		dateAdded = Utility.getCurrentTime();
		
		query = "INSERT IGNORE INTO tablefavouritejobad(idAccount, idJobAd, dateAdded) VALUES " +
			"('"  + accountId + "','" + jobAdId + "','" + dateAdded + "')";		
		
		Connection conn = dbManager.getConnection();
		PreparedStatement preparedStmt = null;
		Statement stmt2 = null;
		int isSucessful = -1;
		ResultSet rs = null;
		
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
					jobAdId = rs.getInt("idJobAd"); 
					System.out.println("Retrieved: Job Ad ID:" + jobAdId);
				}
				else
					System.out.println("Error: Inserted row not found after creation");
				
				if(accountId !=-1 && jobAdId != -1){
						System.out.println("accountId and jobAdId are valid values");
				}
				else{
					System.out.println("accountId or jobAdId not valid");
				}
			}
			else{
				System.out.println("preparedStmt not sucessful");
			}
		}
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
	        	System.out.println("Cannot close Prepared Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		
	}	

/*********************************************************************************************
 *
 * listFavouriteJobAd()
 * 
 * @param request
 * @param response
 * @throws IOException
 ***********************************************************************************************/
	private void listFavouriteJobAd(HttpServletRequest request, HttpServletResponse response)throws IOException{
		ArrayList<JobAdvertisement> favJobAdList = new ArrayList<JobAdvertisement>();
		
		Session userSession = accManager.getSessionByKey(request.getParameter("sessionKey"));
		int accountId = userSession.getIdAccount();
		
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {		
			stmt = conn.createStatement();
			//Add individual queries onto total query
			String query = "SELECT * FROM tablefavouritejobad INNER JOIN tablejobad " +
					"USING(idJobAd) WHERE tablefavouritejobad.idAccount =" +
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
				Utility.logError("Cannot close Statement : " + e.getMessage());
			}
			try {
				if (conn  != null)
					conn.close();
			}
			catch (SQLException e) {
				Utility.logError("Cannot close Connection : " + e.getMessage());
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
 * deleteFavouriteJobAd()
 * 
 * @param request
 * @param response
 * @throws IOException
 ***********************************************************************************************/	
	private void deleteFavouriteJobAd(HttpServletRequest request, HttpServletResponse response)throws IOException{
		
		Session userSession = accManager.getSessionByKey(request.getParameter("sessionKey"));
		int accountId = userSession.getIdAccount();
		
		String jobAdIdInString = request.getParameter("jobAdId");
		System.out.println("jobIDINSTRING"+jobAdIdInString);
		int jobAdId = Integer.parseInt(jobAdIdInString);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {		
			stmt = conn.createStatement();
			//Add individual queries onto total query
			String query = "DELETE FROM tablefavouritejobad " +
					"WHERE tablefavouritejobad.idAccount =" + accountId + 
					" AND tablefavouritejobad.idJobAd =" + jobAdId;
					
			
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
				Utility.logError("Cannot close Statement : " + e.getMessage());
			}
			try {
				if (conn  != null)
					conn.close();
			}
			catch (SQLException e) {
				Utility.logError("Cannot close Connection : " + e.getMessage());
			}
		}
		
		
	}
	

	private void addNewTag(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException{
		
		//Debug
		System.out.println("Entered PostJobAdvertisement");
		//initialize return statements
		boolean isSuccessful = false;
		String msg = "";
		
		System.out.println("sessionKey=" + request.getParameter("sessionKey"));
		
		//Checks the user's privilege
		Session userSession = accManager.getSessionByKey(request.getParameter("sessionKey"));
		int acctId =  session.getIdAccount();

		System.out.print("User Access Granted for key: \n" + userSession.getKey() +"\n" );

		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {
			
			System.out.println("Processing " + "");
			stmt = conn.createStatement();
			
			int success = stmt.executeUpdate("");
			if (success != 1){
				System.out.println("New JobAd Creation failed");
				Utility.logError("New JobAd insert in DB failed");
			}
			
			msg = "Action Performed Successfully";
			//Get jobAdId By Using select the last inserted ID
			PreparedStatement getLastInsertId = conn.prepareStatement("SELECT LAST_INSERT_ID()");
			ResultSet rs = getLastInsertId.executeQuery();
			int jobAdId = -1;
			if (rs.next())
			{
				jobAdId = rs.getInt("last_insert_id()"); 
				System.out.println("Retrived: Job Ad ID: " + jobAdId);
			}
			else{
				System.out.println("Error: Job Ad ID not found after creation");
			}
		   if(jobAdId != -1){
		   	//Insert location values into location table

			if (success == 1){
				System.out.println("New JobAd Creation success (DB)");
				isSuccessful = true;

//					message = "Create Job Advertisement Successful";
			}
			else{
				System.out.println("Insert location for new job ad failed");
				Utility.logError("Insert location for new job ad failed");
			}

		  }//ENDOF INSERT INTO LOCATION TABLE 
		}
		catch (SQLException e) {
			Utility.logError("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	Utility.logError("Cannot close Connection : " + e.getMessage());
	        }
	    }

		
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
//		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + msg + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	

		private void getTagList(HttpServletRequest request, HttpServletResponse response) throws IOException{
			System.out.println("ServletJobAd: Inside getAllJobAd");

			String message = "getAllJobAd failed";
			boolean isSuccessful = false;
			
			ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
			
//			String sessionKey = request.getParameter("sessionKey");
//			Session session = dbManager.getSessionByKey(sessionKey);

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			Statement stmtLoc = null;
			Statement stmtEmp = null;

			try {
				stmt = conn.createStatement();
				
				String query = "SELECT * FROM tableJobAd" +
								" ORDER BY datePosted DESC";
				
				System.out.println("getAllJobAd query:" + query);
				isSuccessful = stmt.execute(query);
				
				ResultSet result = stmt.getResultSet();
				
				while(result.next()){
//                jobAd.tags 				= result.getString("tags");
					//d.tags = result.getString("tags");

				}//END OF WHILE LOOP
				
				if(jobAdList.isEmpty()){
					message = "Error: No Job Ad found";
					System.out.println("Error: No Job Ad found");
				}
				else{
					System.out.println("getAllJobAd successful");
					message = "getAllJobAd successful";
					isSuccessful = true;
				}
						
				
			} //END OF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception : " + e.getMessage());
			}
			// close DB objects
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		            if(stmtLoc != null)
		            	stmtLoc.close();
		            if(stmtEmp != null)
		            	stmtEmp.close();
		        }
		        catch (Exception e) {
		        	System.out.println("Cannot close Statement : " + e.getMessage());
		        }
		        try {
		            if (conn  != null)
		                conn.close();
		        }
		        catch (SQLException e) {
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
	
		
		private boolean addTagsToJobAd( int idJobAd, String[] tags ) {
			
//			System.out.println("ServletJobAd: Inside getAllJobAd");


			boolean isSuccessful = false;
			
			LinkedList<Integer> tagIDs = new LinkedList<Integer>();
			
//			String sessionKey = request.getParameter("sessionKey");
//			Session session = dbManager.getSessionByKey(sessionKey);

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			Statement stmtLoc = null;
			Statement stmtEmp = null;
			
			LinkedList<String> inputTagsList = new LinkedList<String>();
			
			for( String oneTag: tags) {
				inputTagsList.add(oneTag);
			}

			Iterator inputTagIterator = inputTagsList.iterator();
			
			while ( inputTagIterator.hasNext() ) {
				inputTagIterator.next();
			}
			
			String partialQuery = new String();
			try {
				stmt = conn.createStatement();
				
				String query = "SELECT * FROM tableTags" +
								" Where BY datePosted DESC";
				
				System.out.println("getAllJobAd query:" + query);
				isSuccessful = stmt.execute(query);
				
				ResultSet result = stmt.getResultSet();
			
						
				
			} //END OF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception : " + e.getMessage());
			}
			// close DB objects
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		            if(stmtLoc != null)
		            	stmtLoc.close();
		            if(stmtEmp != null)
		            	stmtEmp.close();
		        }
		        catch (Exception e) {
		        	System.out.println("Cannot close Statement : " + e.getMessage());
		        }
		        try {
		            if (conn  != null)
		                conn.close();
		        }
		        catch (SQLException e) {
		        	System.out.println("Cannot close Connection : " + e.getMessage());
		        }
		    }
		    
			
			return false;
		
		}

}//ENDOF SERVLETJobAD


/*=====================================================================================================================================
 * 		"MAYBE" FUNCTION--QUICK SEARCH
 *======================================================================================================================================/
//private void quickSearchJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
//ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
//ArrayList<Location> adLocationList = new ArrayList<Location>();
//ArrayList <String> queryCond = new ArrayList<String>();
//String locCond="idJobAd=";
//String locCol[]={"idJobAd","addr0", "latlng0","addr1", "latlng1","addr2", "latlng2"};
//String[]ColToGet={"idJobAd", "title", "datePosted", "contactInfo","educationRequired", "jobAvailability", "location"};
//
//Connection conn = dbManager.getConnection();	
//Statement stmt = null;
//Map<String, String> paraColMap = DbDict.getDict(action);
//
//for(String )
//try {		
//	stmt = conn.createStatement();
//	
//		
//	String query = DBQ.buildSelQuery(new String[]{"tableJobAd"}, colToGet, );
//	
//	//DEBUG
//	System.out.println(query);
//	
//	stmt.execute(query);
//	ResultSet result = stmt.getResultSet();
//	ResultSetMetaData rsmd = null;
//	
//	//Compile the result into the arraylist
//	while( result.next() ) {
//		JobAdvertisement temp = new JobAdvertisement();
//		rsmd = result.getMetaData();
//		int numColumns = rsmd.getColumnCount();
//		for(int i=1; i<=numColumns; i++){
//			String colName = rsmd.getColumnName(i);
//				 temp.valueMap.put(jobAdDBDict.get(colName), result.getObject(colName));
//		}
//		jobAdList.add( temp ); //add to the temporary list
//		temp.jobAdId = result.getInt("idJobAd");
//		locQueryCond.add(locCond+result.getInt("idJobAd")+DBQ.OR); //prepare the jobId to locInfo 
//		
//	}
//	if(result.first()){ // if we have hits
//		StringBuffer[]qBuf=DBQ.buidlSelQuery(new String[]{"tableLocationJobAd"}, locCol, locQueryCond);
//		query=qBuf[0].append(qBuf[1].delete(qBuf[1].length()-DBQ.OR.length(), qBuf[1].length())).toString();//get rid of last "OR"
//		
//		result=conn.createStatement().executeQuery(query);
//		Map<String, String>locationMap;
//		while(result.next()){ //if we have locations
//			rsmd = result.getMetaData();
//			int numColumns = rsmd.getColumnCount();
//			locationMap=new HashMap<String, String>();
//			for(int i=1; i<=numColumns; i++){
//				String colName = rsmd.getColumnName(i);
//				if(result.getString(colName)!=null){ //not getting the null values
//					locationMap.put(colName, result.getString(colName));
//				}
//			}
//			Location temp = new Location(result.getInt("idJobAd"), locationMap);
//			adLocationList.add(temp);
//		}
//		
//		for(JobAdvertisement job_ad : jobAdList){
//			for (Location location : adLocationList){
//				if (job_ad.jobAdId==location.masterJobId){
//					job_ad.adLocation=location;
////						return;
//				}
//			}
//		}
//	}//eof query location base on non-null result
//	System.out.println("Search Query Successfully Finished");
//	
//} catch (SQLException e1) {
//e1.printStackTrace();
//}
//
//// close DB objects
//finally {
//	try{
//		if (stmt != null)
//			stmt.close();
//	}
//	catch (Exception e) {
//		Utility.logError("Cannot close Statement : " + e.getMessage());
//	}
//	try {
//		if (conn  != null)
//			conn.close();
//	}
//	catch (SQLException e) {
//		Utility.logError("Cannot close Connection : " + e.getMessage());
//	}
//}
//
//StringBuffer XMLResponse = new StringBuffer();	
//XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//XMLResponse.append("<response>\n");
//Iterator<JobAdvertisement> itr = jobAdList.iterator();
//while (itr.hasNext()) {//iterate through all list and append to xml
//	XMLResponse.append(itr.next().xmlParser() ); 
//}
//XMLResponse.append("</response>\n");
//response.setContentType("application/xml");
//response.getWriter().println(XMLResponse);
//}



/***********************************************************************
 ********************** DEPRECATED FUNCTIONS: **************************
 ***********************************************************************/
/*********************************************************************************************************************
 * 							Post a job advertisement
 * Mode:SAVE DRAFT, NEW AD and EDIT AD
 *
 *********************************************************************************************************************/
//	private void postJobAd(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException{
//		
//		//Debug
//		System.out.println("Entered PostJobAdvertisement");
//		//initialize return statements
//		boolean isSuccessful = false;
//		String msg = "";
//			
//		String query = buildPostQuery(request, session.getIdAccount() );
//		System.out.println(query);
//		
//		Connection conn = dbManager.getConnection();	
//		Statement stmt = null;
//		
//		try {
//			
//			System.out.println("Processing " + query);
//			stmt = conn.createStatement();
//			
//			int success = stmt.executeUpdate(query);
//			if (success != 1){
//				System.out.println("New JobAd Creation failed");
//				Utility.logError("New JobAd insert in DB failed");
//			}
//			
//			msg = "Action Performed Successfully";
//			//Get jobAdId By Using select the last inserted ID
//			PreparedStatement getLastInsertId = conn.prepareStatement("SELECT LAST_INSERT_ID()");
//			ResultSet rs = getLastInsertId.executeQuery();
//			int jobAdId = -1;
//			if (rs.next())
//			{
//				jobAdId = rs.getInt("last_insert_id()"); 
//				System.out.println("Retrived: Job Ad ID: " + jobAdId);
//			}
//			else{
//				System.out.println("Error: Job Ad ID not found after creation");
//			}
//			if(jobAdId != -1){
//		   	//Insert location values into location table
//
//			
//			if (success == 1){
//				System.out.println("New JobAd Creation success (DB)");
//				isSuccessful = true;
//
//			}
//			else{
//				System.out.println("Insert location for new job ad failed");
//				Utility.logError("Insert location for new job ad failed");
//			}
//
//		  }//ENDOF INSERT INTO LOCATION TABLE 
//		}
//		catch (SQLException e) {
//			//TODO log SQL exception
//			Utility.logError("SQL exception : " + e.getMessage());
//		}
//		// close DB objects
//	    finally {
//	        try{
//	            if (stmt != null)
//	                stmt.close();
//	        }
//	        catch (Exception e) {
//	        	//TODO log "Cannot close Statement"
//	        	System.out.println("Cannot close Statement : " + e.getMessage());
//	        }
//	        try {
//	            if (conn  != null)
//	                conn.close();
//	        }
//	        catch (SQLException e) {
//	        	//TODO log Cannot close Connection
//	        	System.out.println("Cannot close Connection : " + e.getMessage());
//	        }
//		}
//
//		
//		StringBuffer XMLResponse = new StringBuffer();	
//		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//		XMLResponse.append("<response>\n");
////		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
//		XMLResponse.append("\t<message>" + msg + "</message>\n");
//		XMLResponse.append("</response>\n");
//		response.setContentType("application/xml");
//		response.getWriter().println(XMLResponse);
//		
//	}

/**********************************************************************************************************************
 * 								build Post Query (@deprecated but still needed) Replaced by buildPostAdQuery
 * This function will build query for SUBMIT AD AND SAVE DARFT
 * @param request
 * @param IdAcct
 * @return
 ***********************************************************************************************************************/
//	private String buildPostQuery(HttpServletRequest request, int IdAcct){
// 		String query=null;
//		mysqlCmd qcmd 	 			= new mysqlCmd(); 
//		Map<String, Object>paraMap	= new HashMap<String, Object>();//col-val
//		StringBuffer queryBuf 		= new StringBuffer();
//		String action = request.getParameter("action");
//		
//		Map<String, String> paraColMap = DbDict.getDict(action);
//		
//		
//		Enumeration paramNames = request.getParameterNames();
//		while (paramNames.hasMoreElements()) {
//			
//			String paraName = (String) paramNames.nextElement();
//			if( !paraName.equals("action") && !paraName.equals("sessionKey") ){//Not query "action"&"sKey"
//				//debug
//				System.out.println("Request: "  + paraName 
//									+ " Value: " +request.getParameter(paraName));
//				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
//				
//				//Put the parameters' names and values into the MAP
//				if(colName.equals("dateStarting")||colName.equals("expiryDate")){
//					long aDate=Utility.dateConvertor(request.getParameter(paraName));
//					paraMap.put(colName, aDate);
//				}
//				else if(colName.equals("title")){
//					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
//					paraMap.put(colName, strFormatted);
//				}
//				else if(colName.equals("description")){
//					String strFormatted = Utility.replaceNonAsciiChars(request.getParameter(paraName));
//					strFormatted = Utility.processLineBreaksWhiteSpaces(strFormatted);
//					paraMap.put(colName, strFormatted);
//				}
//				else{
//					paraMap.put(colName, Utility.checkInputFormat(request.getParameter(paraName)) );//will fail if no value is actually passed
//				}
//				//Debug
//			    System.out.println("Column: " + colName + " Value: " + paraMap.get(colName)+"\n");
//				}//ENDOF IF SET COL-VAL IN PARAMAP
//		  	
//		  }//ENDOF WHILE paramNames.hasMoreElements()
//		paraMap.put("idAccount", IdAcct);//set the owner
//		paraMap.put("datePosted", Calendar.getInstance().getTimeInMillis());//set the modified time
//		
//		/*
//		 * Entering Query making
//		 * CAUTION: SPACE IMPORTANT
//		 */
//		StringBuffer stm1 = new StringBuffer();
//		StringBuffer stm2 = new StringBuffer();
//		if(action.equals("editJobAd")){
//			stm1.append( qcmd.UPDATE + "tableJobAd " + qcmd.SET );
//			stm2.append( qcmd.WHERE);
//		}  
//		
//		else if( ( action.equals("saveJobAdDraft") || action.equals("createJobAdvertisement")) ){
//			stm1.append( qcmd.INSERT + qcmd.INTO + "tableJobAd " + qcmd.PRNTHS);
//			stm2.append( qcmd.VALUES + qcmd.PRNTHS);
//			stm1.insert(stm1.length()-2, "status"+qcmd.COMA);
//		}
//		else if( ( action.equals("updateDraft") )){
//			stm1.append( qcmd.UPDATE + "tableJobAd " + qcmd.SET );
//			stm2.append( qcmd.WHERE);
//		}
//	    
//		/*
//		 * Job Ad modes dependent on "action"
//		 * createJobAdvertisement --> "draft"
//		 * 
//		 */
//		
//		int mode=-1;
//		if(action.equals("saveJobAdDraft")){
//			stm2.insert(stm2.length()-2, qcmd.SQUO+ "draft" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
//			mode=1;
//		  }
//		else if(action.equals("createJobAdvertisement")){//TODO CHCK SYSTEM SETTING IF NEEDS APPROVAL
//			stm2.insert(stm2.length()-2, qcmd.SQUO+ "pending" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
//			mode=1;
//		  }
//		else if(action.equals("editJobAd")){
//			stm1.append("status" + qcmd.EQ+ qcmd.SQUO+ "pending" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
//			mode=2;
//		}
//		else if(action.equals("updateDraft")){
//			mode=2;
//		  }
//		switch (mode){
//		
//		case 1:
//			for(Map.Entry<String, Object> entry : paraMap.entrySet()){
//				String column= entry.getKey();
//				Object value = entry.getValue();
//				stm1.insert(stm1.length()-2 , column+ qcmd.COMA);//insert col into the parentheses
//	    		stm2.insert(stm2.length()-2,  qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
//			}
//			stm1.delete(stm1.length() - qcmd.COMA.length()-2, stm1.length()-2);//get rid of last coma
//			stm2.delete(stm2.length() - qcmd.COMA.length()-2, stm2.length()-2);
//			queryBuf.append(stm1.append(stm2));//merge the query
//			break;
//			
//		case 2:
//			for(Map.Entry<String, Object> entry : paraMap.entrySet()){
//				String column = entry.getKey();
//	    		Object value = entry.getValue();
//	    		
//	    		if(column.equals("idJobAd")){
//	    			stm2.append( qcmd.AND + column +qcmd.EQ + value);
//	    		}
//	    		else if(column.equals("idAccount")){
//	    			stm2.append(column + qcmd.EQ +(Integer) value);
//	    		}
////	    		if(!column.equals("jobAdId")|| !column.equals("idAccount")){
//	    		else{
//	    			if(value instanceof String){
//	    				stm1.append(column +qcmd.EQ + qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
//	    			}
//	    			else if((value instanceof Long) || (value instanceof Integer)){
//	    				stm1.append(column +qcmd.EQ + value + qcmd.COMA);
//	    			}
//	    		}
//	    		
//			}
//			stm1.delete(stm1.length() - qcmd.COMA.length(), stm1.length());
//			queryBuf.append(stm1.append(stm2));
//			break;
//			
//		default:
//			System.out.println("DONT YOU TRY TO HACK =D");
//			break;
//		}
//
//		query = queryBuf.toString();
//		return query;
//	}
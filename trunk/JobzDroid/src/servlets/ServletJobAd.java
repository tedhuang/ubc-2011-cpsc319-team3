package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import classes.DBColName;
import classes.JobAdvertisement;
import classes.Location;
import classes.Session;
import classes.Utility;

import managers.AccountManager;
import managers.DBManager;
import managers.RSSManager;
import managers.SystemManager;

/**
 * Servlet implementation class ServletJobAdvertisement
 */
public class ServletJobAd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBManager dbManager;
	private DBColName DbDict =	ServletInitializer.retDbColName();
	private AccountManager accManager;
	
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
		editJobAd,
		saveJobAdDraft,
		updateDraft,
		
		searchJobAdvertisement,
		getJobAdByOwner,
		getJobAdById,
		deleteJobAd,
		getSomeJobAd,
		getSuggestions,
		
		saveFavouriteJobAd,
		listFavouriteJobAd,
		deleteFavouriteJobAd,
		
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
		
		
		String sessionKey = request.getParameter("sessionKey");
		sessionKey = Utility.checkInputFormat(sessionKey);
		Session session = accManager.getSessionByKey(sessionKey);
		
		//Check which function the request is calling from the servlet
		switch( EnumAction.valueOf(action) ){
		
		/*****************POST AD ACTIONS***********************/
			case createJobAdvertisement :
//				createJobAdvertisement(request, response);
				createJobAd(request, response);
				break;
				
			case editJobAd:
				if(session.checkPrivilege( response, "poster" ) )
					postJobAd(request, response, session);
				break;
				
			case saveJobAdDraft:
				if(session.checkPrivilege( response, "poster") )
					postJobAd(request, response, session);
				break;
			case updateDraft:
				if(session.checkPrivilege( response, "poster") )
					postJobAd(request, response, session);
				break;

			case deleteJobAd:
				deleteJobAd(request, response);
				break;
				
//			case deactivateJobAd:
//				deactivateJobAd(request, response);
//				break;
				
		/*****************Retrieve AD ACTIONS***********************/		
			case searchJobAdvertisement:
				searchJobAd(request, response);
				break;
			case getJobAdByOwner:
				getJobAdByOwner(request, response);
				break;
				
			case getJobAdById:
				getJobAdById(request, response);
				break;
			
			case loadAdList:
				searchJobAd(request, response);
				break;
				
			case getSomeJobAd:
				getSomeJobAd(request, response);
				break;	
			case getSuggestions:
				getSuggestions(request, response);
				break;				
		/*****************FAVOURITE AD ACTIONS***********************/		
				
			case saveFavouriteJobAd:
				if(session.checkPrivilege( response, "searcher") )
					saveFavouriteJobAd(request, response, session);
				break;
				
			case listFavouriteJobAd:
				listFavouriteJobAd(request, response);
				break;
				
			case deleteFavouriteJobAd:
				deleteFavouriteJobAd(request, response);
				break;
				

			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
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

				//jobAd.jobAdDescription 	= result.getString("description");
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
			//TODO log SQL exception
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
		
	

	private void getJobAdByOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
	
		String message = "getJobAdByOwner failed";
		boolean isSuccessful = false;
		Map<String, String>jobAdDBDict=DbDict.getClsDict("jobAd");
		
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		
		//Extract ownerID from session key
		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);
		int ownerId = session.getIdAccount();

		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		Statement stmtLoc = null;

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
				jobAd.hasGradFunding	= result.getInt("hasGradFunding");
				
				
			}
			else{ //Error case
				isSuccessful = false;
				message = "Error: Job Ad not found with ID=" + jobAdId;
				System.out.println("Error: Job Ad not found with ID=" + jobAdId);
			}
			
			/**Get Employment Availability values */
				String empAvail = "";
				int fullTime = 0;
				int partTime = 0;
				int internship = 0;
				
				query = "SELECT * FROM tableEmpTypeJobAd WHERE " +
				"idJobAd= '" + jobAdId + "'";
				System.out.println(query);
				
				isSuccessful = stmt.execute(query);
				result = stmt.getResultSet();
				
				if(!result.first()){
						System.out.println("No Employment Type Found");
				}
				else{
					fullTime = result.getInt("fullTime");
					partTime = result.getInt("partTime");
					internship = result.getInt("internship");	

					if( (fullTime+partTime+internship) == 0 ){
						empAvail = "Not Specified"; //no preference
					}
					else{
						if( fullTime == 1){
							empAvail += "Full-time ";
						}
						if(partTime == 1){
							empAvail += "Part-time ";
						}
						if(internship == 1){
							empAvail += "Internship";
						}
					}
					System.out.println("Job Positions Available: " + empAvail);
					jobAd.jobAvailability = empAvail;
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
		XMLResponse.append(jobAd.toXMLContent() );
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
 * Caller Function:  searchJobAd
 *****************************************************************************************************************/
	private String buildSearchQuery(HttpServletRequest request){
		Map<String, String>paraMap = new HashMap<String, String>();//col-value
		boolean qkSearch=false;
		
		Map<String, String> paraColMap = DbDict.getDict(request.getParameter("action"));
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
//						paraMap.put(colName,request.getParameter(paraName) );
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
		StringBuffer wordRegExBuffer = new StringBuffer(" '[[:<:]][[:>:]]' "); //CAUTION: SPACE IMPORTANT, Middle pos:9
		boolean panic = false;
		
		StringBuffer queryBuf = new StringBuffer();
		queryBuf.append(query);
		
       for(Map.Entry<String, String> entry : paraMap.entrySet()){
    	   String column = entry.getKey();
    	   String value = entry.getValue();
    	   if(!(column.equals("quickSearch"))){
    		   
    		   if(column.equals("title")||column.equals("tags")){
    			   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
    		   }
    		   else if(column.equals("jobAvailability")){
    			   //if search by the availability term use IN
   	    		    queryBuf.append(column+ inKeyword + value + andKeyword);
    		   }
    		   else if(column.equals("educationRequired")){
   	    		    queryBuf.append(column+ "=" + value + andKeyword);
    		   }
//    		   else if(column.equals("location")){
//    			   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
//    		   }
    		   else{//TODO NOT WORKING NEED TO FIX 
    			   panic= true;
	    		   System.out.println("PANIC ACTION!");
	    		   queryBuf.setLength(0);
	    		   queryBuf.append( panicQuery + orderByKeyword + "datePosted" + descKeyword);
	    		   query = queryBuf.toString();
	    		   break;
    		   }
    		   /*
	    	   switch(EnumJobTableCol.valueOf(column)){
	    	   
	    	   case title:
	    			queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
	    			break;
	    			//CAUTION: SPACE IMPORTANT
	    	   case jobAvailability: //if search by the availability term use IN
	    		    queryBuf.append(column+ inKeyword + value + andKeyword);
	            	break;
	           
	    	   case educationRequired:
	    		    queryBuf.append(column+ "=" + value + andKeyword);
	    		    break;
	    	   case tags:
	    		   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
	    		   break;
//	    	   case location:
//	    		   queryBuf.append(column+ regExKeyword + wordRegExBuffer.insert(9,value) + andKeyword);
//	    		   break;
	    		   
	    	   default: //panic //TODO NOT WORKING NEED TO FIX 
	    		   panic= true;
	    		   System.out.println("PANIC ACTION!");
	    		   queryBuf.setLength(0);
	    		   queryBuf.append( panicQuery + orderByKeyword + "datePosted" + descKeyword);
	    		   query = queryBuf.toString();
	    		   break;
	    	  }//ENDOF SWITCH*/
    	   }//ENDOF IF NOT QUICK SEARCH
    	   
    	   else{//Perform quick search, only one value but wild search on all
    		   qkSearch=true;
    		   wordRegExBuffer.insert(9,value);
    		   queryBuf.append(DbCols.get("title") 		+ regExKeyword + wordRegExBuffer.toString() + orKeyword);//title
    		   queryBuf.append(DbCols.get("tags")  		+ regExKeyword + wordRegExBuffer.toString() + orKeyword);//tags
    		   queryBuf.append(DbCols.get("company")  	+ regExKeyword + wordRegExBuffer.toString() + orKeyword);//tags
//    		   queryBuf.append(DbCols.get("location") 	+ regExKeyword + wordRegExBuffer.toString() + orKeyword);//location
    		   if (Utility.degreeConvertor(value)>0){
    			   queryBuf.insert(query.length(), DbCols.get("educationRequired") + "=" + Utility.degreeConvertor(value) + orKeyword);
    		   }
    		   if(Utility.jobTypeTranslator(true,value)!=null){
		   			queryBuf.insert(query.length(), DbCols.get("jobAvailability") + "='" + Utility.jobTypeTranslator(true,value)+"'" + orKeyword); //Extract to the Job Type
		   		}
    		   /*
    		   for(EnumJobTableCol col : EnumJobTableCol.values()){
    			   
    			   switch (col){
    			   
    			   case undefine:
    				   break;
    			   	case idJobAd:
    				   break;
    				   
    			   	case educationRequired:
    			   		if (Utility.degreeConvertor(value)!=0){
//    			   			queryBuf.delete(queryBuf.length() - andKeyword.length(), queryBuf.length()); //remove the last " AND "
    			   			queryBuf.insert(query.length(), col + "=" + Utility.degreeConvertor(value) + orKeyword); //Extract to the edu level
    			   		}
    			   	   break;
    			   	   
    			   	case jobAvailability:
    			   		if(Utility.jobTypeTranslator(true,value)!=null){
    			   			queryBuf.insert(query.length(), col + "='" + Utility.jobTypeTranslator(true,value)+"'" + orKeyword); //Extract to the Job Type
    			   			}
    			   		break;
    			   		
    			   	 default:
    			   		queryBuf.append(col + regExKeyword + wordRegExBuffer.toString() + orKeyword);
    			   		break;
    			   
    		   }//ENDOF FOR EACH-ENUM-COL}*/
		  
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

/*********************************************************************************************************************
 * 							Post a job advertisement
 * Mode:SAVE DRAFT, NEW AD and EDIT AD
 *
 *********************************************************************************************************************/
	private void postJobAd(HttpServletRequest request, HttpServletResponse response, Session session) throws IOException{
		
		//Debug
		System.out.println("Entered PostJobAdvertisement");
		//initialize return statements
		boolean isSuccessful = false;
		String msg = "";
			
		String query = buildPostQuery(request, session.getIdAccount() );
		System.out.println(query);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {
			
			System.out.println("Processing " + query);
			stmt = conn.createStatement();
			
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

			}
			else{
				System.out.println("Insert location for new job ad failed");
				Utility.logError("Insert location for new job ad failed");
			}

		  }//ENDOF INSERT INTO LOCATION TABLE 
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
//		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + msg + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
/****************************************************************************************************************
 * 
 * @param request	
 * @param response
 * @throws IOException
 */
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
				   if(jobAdId != -1){	
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
						System.out.println("Insert location for new job ad failed");
						Utility.logError("Insert location for new job ad failed");
					}
	
				  }//ENDOF INSERT INTO LOCATION TABLE 
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
	else if(action.equals("createJobAdvertisement")){//TODO CHCK SYSTEM SETTING IF NEEDS APPROVAL
		//status is pending
		stm2.insert(stm2.length()-2, "'pending'" + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
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
	
	queriesBuffer[0]=queryBuf;
	queriesBuffer[1]=locQueryBuf;
	return queriesBuffer;
}
/**********************************************************************************************************************
 * 								build Post Query (@deprecated but still needed) Replaced by buildPostAdQuery
 * This function will build query for SUBMIT AD AND SAVE DARFT
 * @param request
 * @param IdAcct
 * @return
 ***********************************************************************************************************************/
	private String buildPostQuery(HttpServletRequest request, int IdAcct){
 		String query=null;
		mysqlCmd qcmd 	 			= new mysqlCmd(); 
		Map<String, Object>paraMap	= new HashMap<String, Object>();//col-val
		StringBuffer queryBuf 		= new StringBuffer();
		String action = request.getParameter("action");
		
		Map<String, String> paraColMap = DbDict.getDict(action);
		
		
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			
			String paraName = (String) paramNames.nextElement();
			if( !paraName.equals("action") && !paraName.equals("sessionKey") ){//Not query "action"&"sKey"
				//debug
				System.out.println("Request: "  + paraName 
									+ " Value: " +request.getParameter(paraName));
				String colName = paraColMap.get(paraName);//Look Up Corresponding col names
				
				//Put the parameters' names and values into the MAP
				if(colName.equals("dateStarting")||colName.equals("expiryDate")){
					long aDate=Utility.dateConvertor(request.getParameter(paraName));
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
				}//ENDOF IF SET COL-VAL IN PARAMAP
		  	
		  }//ENDOF WHILE paramNames.hasMoreElements()
		paraMap.put("idAccount", IdAcct);//set the owner
		paraMap.put("datePosted", Calendar.getInstance().getTimeInMillis());//set the modified time
		
		/*
		 * Entering Query making
		 * CAUTION: SPACE IMPORTANT
		 */
		StringBuffer stm1 = new StringBuffer();
		StringBuffer stm2 = new StringBuffer();
		if(action.equals("editJobAd")){
			stm1.append( qcmd.UPDATE + "tableJobAd " + qcmd.SET );
			stm2.append( qcmd.WHERE);
		}  
		
		else if( ( action.equals("saveJobAdDraft") || action.equals("createJobAdvertisement")) ){
			stm1.append( qcmd.INSERT + qcmd.INTO + "tableJobAd " + qcmd.PRNTHS);
			stm2.append( qcmd.VALUES + qcmd.PRNTHS);
			stm1.insert(stm1.length()-2, "status"+qcmd.COMA);
		}
		else if( ( action.equals("updateDraft") )){
			stm1.append( qcmd.UPDATE + "tableJobAd " + qcmd.SET );
			stm2.append( qcmd.WHERE);
		}
	    
		/*
		 * Job Ad modes dependent on "action"
		 * createJobAdvertisement --> "draft"
		 * 
		 */
		
		int mode=-1;
		if(action.equals("saveJobAdDraft")){
			stm2.insert(stm2.length()-2, qcmd.SQUO+ "draft" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
			mode=1;
		  }
		else if(action.equals("createJobAdvertisement")){//TODO CHCK SYSTEM SETTING IF NEEDS APPROVAL
			stm2.insert(stm2.length()-2, qcmd.SQUO+ "pending" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
			mode=1;
		  }
		else if(action.equals("editJobAd")){
			stm1.append("status" + qcmd.EQ+ qcmd.SQUO+ "pending" + qcmd.SQUO + qcmd.COMA);//CAUTION: SIGLE QUO & COMA IMPORTANT
			mode=2;
		}
		else if(action.equals("updateDraft")){
			mode=2;
		  }
		switch (mode){
		
		case 1:
			for(Map.Entry<String, Object> entry : paraMap.entrySet()){
				String column= entry.getKey();
				Object value = entry.getValue();
				stm1.insert(stm1.length()-2 , column+ qcmd.COMA);//insert col into the parentheses
	    		stm2.insert(stm2.length()-2,  qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
			}
			stm1.delete(stm1.length() - qcmd.COMA.length()-2, stm1.length()-2);//get rid of last coma
			stm2.delete(stm2.length() - qcmd.COMA.length()-2, stm2.length()-2);
			queryBuf.append(stm1.append(stm2));//merge the query
			break;
			
		case 2:
			for(Map.Entry<String, Object> entry : paraMap.entrySet()){
				String column = entry.getKey();
	    		Object value = entry.getValue();
	    		
	    		if(column.equals("idJobAd")){
	    			stm2.append( qcmd.AND + column +qcmd.EQ + value);
	    		}
	    		else if(column.equals("idAccount")){
	    			stm2.append(column + qcmd.EQ +(Integer) value);
	    		}
//	    		if(!column.equals("jobAdId")|| !column.equals("idAccount")){
	    		else{
	    			if(value instanceof String){
	    				stm1.append(column +qcmd.EQ + qcmd.SQUO+ value + qcmd.SQUO + qcmd.COMA);
	    			}
	    			else if((value instanceof Long) || (value instanceof Integer)){
	    				stm1.append(column +qcmd.EQ + value + qcmd.COMA);
	    			}
	    		}
	    		
			}
			stm1.delete(stm1.length() - qcmd.COMA.length(), stm1.length());
			queryBuf.append(stm1.append(stm2));
			break;
			
		default:
			System.out.println("DONT YOU TRY TO HACK =D");
			break;
		}

		query = queryBuf.toString();
		return query;
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
		XMLResponse.append("\t<message>" + feedback + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	

	private StringBuffer sessPosterAuthQuery(String sKey, mysqlCmd qcmd){
		
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
	 private StringBuffer[] buildSuggestionQuery(mysqlCmd qcmd, String[]userInfo, String[]adInfo){
			
		 	StringBuffer[] suggQueryArr=new StringBuffer[2];
			StringBuffer qProfileTb = new StringBuffer();
			StringBuffer qJobAdTb   = new StringBuffer();
			StringBuffer uInfoBuf   = new StringBuffer();
			StringBuffer adInfoBuf  = new StringBuffer();
			for(String str:userInfo){
				uInfoBuf.append(str+qcmd.COMA);
			}
			for(String str:adInfo){
				adInfoBuf.append(str+qcmd.COMA);
			}
			//remove extra coma
			uInfoBuf.delete( uInfoBuf.length()-qcmd.COMA.length(), uInfoBuf.length());
			adInfoBuf.delete(adInfoBuf.length()-qcmd.COMA.length(), adInfoBuf.length());
			
			qProfileTb.append(qcmd.SELECT + uInfoBuf + qcmd.FROM + "tableProfileSearcher " + 
			  		  qcmd.WHERE +"idAccount" + qcmd.EQ + "?");
			suggQueryArr[0]=qProfileTb;
			
			//prepare for the search on table job ad
			qJobAdTb.append(qcmd.SELECT + adInfoBuf + qcmd.FROM + "tableJobAd" +  
							  qcmd.WHERE );
			for(String str:userInfo){
				if(str.equals("educationLevel")){
					qJobAdTb.append("educationRequired"+qcmd.EQ +"?" +qcmd.OR);
				}
			}
			qJobAdTb.delete(qJobAdTb.length()-qcmd.OR.length(), qJobAdTb.length()); //rm extra "OR"
			suggQueryArr[1]=qJobAdTb;
			return suggQueryArr;
		}
	 
	 private void getSuggestions(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 	ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
			mysqlCmd qcmd = new mysqlCmd();
			String[]authInfo={"idAccount"};
			String[]uInfo={"educationLevel"};
			String[]adInfo={"*"};//The order of the col must consistent with the prepared stmt
			//Debug
			System.out.println("Entered get Suggestions");
			//initialize return statements
			boolean isSuccessful = false;
			String msg = "";
			
			System.out.println("sessionKey=" + request.getParameter("sessionKey"));
			
			String sKey=request.getParameter("sessionKey");
			System.out.println("sessionKey=" +sKey );
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
						StringBuffer[]suggQueryBuf=buildSuggestionQuery(qcmd, uInfo, adInfo);
						
						//first get the user info
						PreparedStatement suggQuery = conn.prepareStatement(suggQueryBuf[0].toString());
						suggQuery.setInt(1, acctId);
						
						ResultSet suggRs = suggQuery.executeQuery();
						
						//now get the suggestions from job ad based on user's info
						suggQuery = conn.prepareStatement(suggQueryBuf[1].toString());
						while(suggRs.next()){
							suggQuery.setInt(1, suggRs.getInt("educationLevel"));//be sure to consistent with the array
						}
						
					   stmt=conn.createStatement(); //create a new statement maybe not needed
					   //DEBUG
					   System.out.println("suggestion querying on job ad table: " + suggQuery.toString());
					
					   suggRs = suggQuery.executeQuery();
					   
					   while(suggRs.next()){
						   JobAdvertisement temp = new JobAdvertisement();
							
							temp.jobAdId 				= suggRs.getInt("idJobAd");
							temp.jobAdTitle				= suggRs.getString("title");
							temp.creationDate		 	= suggRs.getLong("datePosted");
							temp.contactInfo 			= suggRs.getString("contactInfo");
							temp.educationReq	 		= suggRs.getInt("educationRequired");
							temp.jobAvailability 		= Utility.jobTypeTranslator(false,suggRs.getString("jobAvailability"));
//							temp.tags 					= result.getString("tags");
							
							jobAdList.add( temp ); //add to the temporary list
					   }
					   
					  }//ENDOF INSERT INTO LOCATION TABLE 
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
//			XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
			Iterator<JobAdvertisement> itr = jobAdList.iterator();
		    while (itr.hasNext()) {//iterate through all list and append to xml
		    	XMLResponse.append(itr.next().toXMLContent() ); 
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
	public void saveFavouriteJobAd(HttpServletRequest request, HttpServletResponse response, Session session)throws IOException{

		
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
	public void listFavouriteJobAd(HttpServletRequest request, HttpServletResponse response)throws IOException{
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
 * deleteFavouriteJobAd()
 * 
 * @param request
 * @param response
 * @throws IOException
 ***********************************************************************************************/	
	public void deleteFavouriteJobAd(HttpServletRequest request, HttpServletResponse response)throws IOException{
		
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
		
		String query = buildPostQuery(request, acctId);

		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		try {
			
			System.out.println("Processing " + query);
			stmt = conn.createStatement();
			
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
				//TODO log SQL exception
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
				//TODO log SQL exception
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




/***********************************************************************
 ********************** DEPRECATED FUNCTIONS: **************************
 ***********************************************************************/
//
///******************************************************************************************************************
// * 					ADLISTLOAD
// * LOAD JOB AD LIST BY CRITERIAs
// ******************************************************************************************************************/
//private void adListLoader( HttpServletRequest request, HttpServletResponse response) throws IOException{
//	
//	boolean isSuccessful=false;
//	String message="";
//	
///*
//	ArrayList <String> reqParaNameList = new ArrayList<String>();
//	ArrayList <String> reqParaValList = new ArrayList<String>();
//	
//	Enumeration paramNames = request.getParameterNames();
//	while (paramNames.hasMoreElements()) {
//	    //Get the parameters' names
//		String paraName = (String) paramNames.nextElement();
//	    reqParaNameList.add(paraName);
//	    //Get the parameters' values
//	    reqParaValList.add(request.getParameter(paraName));
//	    
//	    System.out.println("Parameter: " + paraName + "\n" +
//	    					"Value: " + request.getParameter(paraName));
//	}
//	String criteria = reqParaNameList.get(0);
//	String condition = reqParaValList.get(0);
//	*/
//	
//	Connection conn = dbManager.getConnection();	
//	Statement stmt = null;
//	JobAdvertisement jobAd = new JobAdvertisement(); //create a new job ad object to hold the info
//	
//	String query = buildSearchQuery(request);
//	
//	try {
//		stmt = conn.createStatement();
//		
////		String query = 
////			"SELECT idJobAd, title, datePosted, contactInfo, educationRequired " +//TODO JOIN with LOCATION Table
////			"FROM tableJobAd WHERE " + criteria+"=" + condition;
//		
//		System.out.println("getJobAdById query:" + query);
//		isSuccessful = stmt.execute(query);
//		
//		ResultSet result = stmt.getResultSet();
//		
//		if (result.first()){
//			System.out.println("getJobAd successful by QUERY" + query);
//			message = "getJobAd successful";
//			
//			//Fill in the fields of the jobAd object
//			
//			jobAd.contactInfo 		= result.getString("contactInfo");
//			jobAd.educationReq 		= result.getInt("educationRequired");
//			jobAd.jobAdTitle		= result.getString("title");
//			jobAd.jobAdId 			= result.getInt("idJobAd");
//			jobAd.creationDate 		= result.getLong("datePosted");
//			jobAd.hasGradFunding	= result.getInt("hasGradFunding");
//			jobAd.isApproved 		= result.getInt("isApproved");
//			
////			jobAd.ownerID 			= result.getInt("idAccount");
////			jobAd.jobAdDescription 	= result.getString("description");
////			jobAd.expiryDate		= result.getLong("expiryDate");
////			jobAd.startingDate 		= result.getLong("dateStarting");
////			jobAd.status 			= result.getString("status");
////			jobAd.tags 				= result.getString("tags");
////			jobAd.numberOfViews 	= result.getInt("numberOfViews");
//			
//		}
//		else{ //Error case
//			isSuccessful = false;
//			message = "Error: AD not found with Query" + query;
//			System.out.println(message);
//		}
//		
//		
//	}
//	catch (SQLException e) {
//		//TODO log SQL exception
//		Utility.logError("SQL exception : " + e.getMessage());
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
//    StringBuffer XMLResponse = new StringBuffer();	
//	XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//	XMLResponse.append("<response>\n");
//	XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
//	XMLResponse.append("\t<message>" + message + "</message>\n");
//	XMLResponse.append(jobAd.toXMLContent() );
//	XMLResponse.append("</response>\n");
//	response.setContentType("application/xml");
//	response.getWriter().println(XMLResponse);
//}
//
//
//
//
//private void editJobAdvertisement(HttpServletRequest request, HttpServletResponse response) throws IOException{
//	
//	//initialize return statements
//	String message = "Create Job Advertisement Failed";
//	boolean isSuccessful = false;
//	
//	int jobAdId 				 = Integer.parseInt(request.getParameter("jobAdId"));
//	
//	String jobAdvertisementTitle = request.getParameter("strTitle");
//	String jobDescription 		 = request.getParameter("strDescription");
//	String contactInfo 			 = request.getParameter("strContactInfo");
//	String strTags 				 = request.getParameter("strTags");
//	String jobAvailability	 	 = request.getParameter("strJobAvailability");
//	
//	int educationRequirement 	 = Integer.parseInt(request.getParameter("educationRequirement"));
//	
//	int expiryYear 				 = Integer.parseInt( request.getParameter("expiryYear"));
//	String expiryMonth 			 = request.getParameter("expiryMonth");
//	int expiryDay 				 = Integer.parseInt( request.getParameter("expiryDay"));
//	
//	int startingYear 			 = Integer.parseInt(request.getParameter("startingYear"));
//	String startingMonth 		 = request.getParameter("startingMonth");
//	int startingDay 			 = Integer.parseInt(request.getParameter("startingDay"));
//			
//	long millisExpiryDate 		 = Utility.calculateDate(expiryYear,expiryMonth,expiryDay);
//	long millisStartingDate 	 = Utility.calculateDate(startingYear, startingMonth, startingDay);
//	
//	String address 				 = request.getParameter("address");
//	double longitude 			 = Double.parseDouble(request.getParameter("longitude"));
//	double latitude 			 = Double.parseDouble(request.getParameter("latitude"));
//	
//
//	System.out.println(jobAdvertisementTitle);
//	System.out.println(jobDescription);
//	System.out.println(contactInfo);
//	System.out.println(strTags);
//	System.out.println(" Expire On: " + millisExpiryDate);
//	System.out.println("Location: " + address + " Long: " + longitude + " Lat: " + latitude);
//	
//	Connection conn = dbManager.getConnection();	
//	Statement stmt = null;
//	
//	try{
//		stmt = conn.createStatement();
//		
//		String query = 
//			"UPDATE tableJobAd SET " 
//			+ "title='" 			+ jobAdvertisementTitle + "','" 
//			+ "description='" 		+ jobDescription + "','" 
//			+ "expiryDate='" 		+ millisExpiryDate + "','" 
//			+ "dateStarting='" 		+ millisStartingDate + "','" 
//			+ "contactInfo='" 		+ contactInfo + "','" 
//			+ "educationRequired='"	+ educationRequirement + "','" 
//			+ "jobAvailability='" 	+ jobAvailability + "','" 
//			+ "tags='" 				+ strTags + "' " +
//			"WHERE idJobAd='" 		+ jobAdId + "' ";
//			
//		System.out.println("Edit JobAd Query: " + query);
//		
//		if( stmt.executeUpdate(query) != 1 ){ //Error Check
//			System.out.println("Error: Update Query Failed");
//		}
//		else{
//			isSuccessful = true;
//			message = "Edit Job Ad success!";
//			System.out.println("Edit Job Ad success!");
//		}
//
//		
//	}
//	catch (SQLException e) {
//		Utility.logError("SQL exception : " + e.getMessage());
//	}
//	// close DB objects
//    finally {
//        try{
//            if (stmt != null)
//                stmt.close();
//        }
//        catch (Exception e) {
//        	System.out.println("Cannot close Statement : " + e.getMessage());
//        }
//        try {
//            if (conn  != null)
//                conn.close();
//        }
//        catch (SQLException e) {
//        	System.out.println("Cannot close Connection : " + e.getMessage());
//        }
//    }
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
//
//
///**
// * Creates a new job advertisement entry in the database with the given values
// * @param jobAdvertisementTitle
// * @param jobDescription
// * @param jobLocation
// * @param contactInfo
// * @param strTags
// * @return idJobAd
// * @throws IOException 
// */
//private void createJobAdvertisement(HttpServletRequest request, HttpServletResponse response) throws IOException{
//	System.out.println("Entered createJobAdvertisement");
//	
//	//initialize return statements
//	String message = "Create Job Advertisement Failed";
//	boolean isSuccessful = false;
//	
//	System.out.println("sessionKey=" + request.getParameter("sessionKey"));
//	
//	//Checks the user's privilege
//	Session userSession = dbManager.getSessionByKey(request.getParameter("sessionKey"));
//	
//	earlyExit: {
//		System.out.println("Entered user sessionKey");
//			
//		if ( userSession == null ) {
//			//TODO session invalid, handle error
//			System.out.println("session is null");
//			message = "Failed to authenticate user session";
//			break earlyExit;
//		}
//		else {
//			//TODO implmement this
//			System.out.println("checking usertype");
//			System.out.println("usertype = " + userSession.getAccountType());
//			
//			if( userSession.getAccountType().equals("poster") ||
//					userSession.getAccountType().equals("admin")) {
//				System.out.print("User has the correct priviliege");
//			}
//			else {
//				message = "User does not have the right privilege";
//				break earlyExit;
//			}
//
//		}
//		
//		System.out.print("User session sucessful for key " + userSession.getKey() );
//		
//		String jobAdvertisementTitle = request.getParameter("strTitle");
//		String jobDescription 		 = request.getParameter("strDescription");
//		String contactInfo 			 = request.getParameter("strContactInfo");
//		String tags 				 = request.getParameter("strTags");
//		String jobAvailability	 	 = request.getParameter("strJobAvailability");
//
//		int educationRequirement 	 = Integer.parseInt(request.getParameter("strEducationReq"));
//		
//		int expiryYear 				 = Integer.parseInt( request.getParameter("expiryYear"));
//		String expiryMonth 			 = request.getParameter("expiryMonth");
//		int expiryDay 				 = Integer.parseInt( request.getParameter("expiryDay"));
//		
//		int startingYear 			 = Integer.parseInt(request.getParameter("startingYear"));
//		String startingMonth 		 = request.getParameter("startingMonth");
//		int startingDay 			 = Integer.parseInt(request.getParameter("startingDay"));
//		
//		Calendar cal 				 = Calendar.getInstance();
//		long millisDateCreated 		 = cal.getTimeInMillis();
//		long millisExpiryDate		 = Utility.calculateDate(expiryYear,expiryMonth,expiryDay);
//		long millisStartingDate		 = Utility.calculateDate(startingYear, startingMonth, startingDay);
//
//		String address 				 = request.getParameter("address");
//		double longitude 			 = Double.parseDouble(request.getParameter("longitude"));
//		double latitude 			 = Double.parseDouble(request.getParameter("latitude"));
//		
//		int jobAdId = -1;
//		
//		System.out.println(jobAdvertisementTitle);
//		System.out.println(jobDescription);
//		System.out.println(contactInfo);
//		System.out.println(tags);
//		System.out.println("Created On: " + millisDateCreated + " Expire On: " + millisExpiryDate);
//		System.out.println("Location: " + address + " Long: " + longitude + " Lat: " + latitude);
//		
//		Connection conn = dbManager.getConnection();	
//		Statement stmt = null;
//		
//		try {
//			System.out.println("Inserting new Job Ad into DB");
//			
//			stmt = conn.createStatement();
//			
//			jobAdvertisementTitle = Utility.checkInputFormat( jobAdvertisementTitle );
//			jobDescription = Utility.checkInputFormat( jobDescription );
//			contactInfo = Utility.checkInputFormat( contactInfo );
//			tags = Utility.checkInputFormat( tags );
//			
//		//Add new entry with specified paramters into database
//			String query = 
//				"INSERT INTO tableJobAd(title, description, expiryDate, dateStarting, datePosted, contactInfo, educationRequired, jobAvailability, tags ) " +
//				"VALUES " + "('" 
//					+ jobAdvertisementTitle + "','" 
//					+ jobDescription + "','" 
//					+ millisExpiryDate + "','" 
//					+ millisStartingDate + "','" 
//					+ millisDateCreated + "','"
//					+ contactInfo + "','" 
//					+ educationRequirement + "','"
//					+ jobAvailability + "','"
//					+ tags + 
//				"')";
//			
//			// if successful, 1 row should be inserted
//			System.out.println("New Job Ad Query: " + query);
//			int rowsInserted = stmt.executeUpdate(query);
//			
//			if (rowsInserted != 1){
//				System.out.println("New JobAd Creation failed");
//				Utility.logError("New JobAd insert in DB failed");
//			}
//			
//		//Get jobAdId
//			query = "SELECT idJobAd FROM tableJobAd WHERE " +
//					"title='" + jobAdvertisementTitle + "' AND " +
//					"datePosted='" + millisDateCreated + "'";
//			ResultSet result = stmt.executeQuery(query);
//			
//			if(result.first()){
//				jobAdId = result.getInt("idJobAd");
//				System.out.println("Job Ad ID: " + jobAdId);
//			}
//			else{
//				System.out.println("Error: Job Ad ID not found after creation");
//			}
//			
//		//Insert location values into location table
//			query = 
//				"INSERT INTO tableLocationJobAd (location, longitude, latitude, idJobAd) " + 
//				"VALUES " + "('" 
//				+ address + "','" 
//				+ longitude + "','" 
//				+ latitude + "','" 
//				+ jobAdId +
//			"');";
//			
//			System.out.println("Location table query: " + query);
//			
//			rowsInserted = stmt.executeUpdate(query);
//			
//			System.out.println("Row Inserted: " + rowsInserted);
//			
//			if (rowsInserted == 1){
//				System.out.println("New JobAd Creation success (DB)");
//				isSuccessful = true;
//				message = "Create Job Advertisement Successful";
//			}
//			else{
//				System.out.println("Insert location for new job ad failed");
//				Utility.logError("Insert location for new job ad failed");
//			}
//
//			//Debug print
//			System.out.println("Location Query: "+ query);
//			
//			if( stmt.executeUpdate(query) != 1 ){ //Error Check
//				System.out.println("Error: Location Update Failed");
//			}
//			
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
//	    }
//	}//earlyExit:
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

/*
 * Changes the status of the targetted job ad to the specified status
 */
//private void changeJobAdStatus(HttpServletRequest request, HttpServletResponse response) throws IOException{
//	
//	int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));
//	String status = request.getParameter("status");
//	
//	Connection conn = dbManager.getConnection();	
//	Statement stmt = null;
//	
//	boolean isSuccessful = false;
//	String message = "changeJobAdStatus failed";
//	
//	// check if the request is to deactivate a Job Ad, we need to acquire new expiryTime if true
//	boolean setToInactive = false;
//	if( status.equalsIgnoreCase("inactive")) {
//		setToInactive = true;
//	}
//	
//	try {
//		stmt = conn.createStatement();
//		
//		String setExpiryTime = "";
//		if ( setToInactive ) {
//			setExpiryTime = "expiryDate='" + Utility.getCurrentTime() + "' ";
//		}
//		
//		//Update status
//		//setExpiryTime empty unless setting Job Ad to "inactive"
//		String query = 
//			"UPDATE tableJobAd " + 
//			"SET status='" + status +"' " + setExpiryTime +
//			"WHERE idJobAd='" + jobAdId + "'";
//		
//		//Debug print
//		System.out.println("Update Query: " + query);
//		
//		if( stmt.executeUpdate(query) != 1 ){ //Error Check
//			System.out.println("Error: Update Query Failed");
//		}
//		else{
//			isSuccessful = true;
//			message = "changeJobAdStatus to " + status + " worked";
//		}
//	}
//	catch (SQLException e) {
//		//TODO log SQL exception
//		Utility.logError("SQL exception : " + e.getMessage());
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
//	StringBuffer XMLResponse = new StringBuffer();	
//	XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
//	XMLResponse.append("<response>\n");
//	XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
//	XMLResponse.append("\t<message>" + message + "</message>\n");
//	XMLResponse.append("</response>\n");
//	response.setContentType("application/xml");
//	response.getWriter().println(XMLResponse);
//	
//	
//}














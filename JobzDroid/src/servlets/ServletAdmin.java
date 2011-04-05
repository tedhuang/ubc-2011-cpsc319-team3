package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import classes.Account;
import classes.DbQuery;
import classes.JobAdvertisement;
import classes.NewsEntry;
import classes.Session;
import classes.Utility;

import managers.AccountManager;
import managers.DBManager;
import managers.EmailManager;
import managers.NewsManager;
import managers.RSSManager;
import managers.SystemManager;

/**
 * Servlet implementation class ServletAdmin
 * Handles all admin actions, including ban, unban, create/delete admin account(super admin), post news and so on
 */
public class ServletAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;
	private NewsManager newsManager;
	private EmailManager emailManager;
	private AccountManager accManager;
	private DbQuery DBQ =new DbQuery();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAdmin() {
        super();
		newsManager = NewsManager.getInstance();
		dbManager = DBManager.getInstance();
		emailManager = new EmailManager();
		accManager = new AccountManager();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		requestForAdminLogin,
		adminGetJobAd,
		ban,
		adminApprove,
		adminDeny,
		adminDeleteJobAd,
		unban,
		createAdmin,
		deleteAccount,
		postNews,
		deleteNews,
		postRSSEntry,
		removeRSSEntry
	}

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
		// throw error if request is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(Exception e){
			throw new ServletException("Invalid account servlet action.");
		}

		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);
		
		
		switch( EnumAction.valueOf(action) ){
			case requestForAdminLogin:
				adminLoginReqTaker(request, response);
				break;
			case adminGetJobAd:
				adminGetJobAd(request, response);
				break;
			case adminApprove:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					adminApprove(request, response);
				break;
			case adminDeny:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					adminDeny(request, response);
				break;
			case adminDeleteJobAd:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					adminDeleteJobAd(request, response);
				break;
			case ban:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					banHandler(request, response);
				break;
			case unban:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					unbanHandler(request, response);
				break;
			case createAdmin:
				if(session.checkPrivilege( response, "superAdmin") )
					createAdminHandler(request, response);
				break;
			case deleteAccount:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					deleteAccountHandler(request, response, session );
				break;
			case postNews:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					postNewsHandler(request, response);
				break;
			case deleteNews:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					deleteNewsHandler(request, response);
				break;
			case postRSSEntry:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					postRSSEntryHandler(request, response);
				break;
			case removeRSSEntry:
				if(session.checkPrivilege( response, "admin", "superAdmin") )
					removeRSSEntryHandler(request, response);
				break;
		}
	}
	
	private void adminLoginReqTaker(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		String pw = request.getParameter("password");
		Session currSession = null;
		String action = "";
		Account acc = accManager.getAccountFromEmail(email);
		if( acc != null ){
			String accType = acc.getType();
			if( accType.equals("admin") || accType.equals("superAdmin"))
				currSession = accManager.startSession(email, pw);
		}
		
		if(currSession != null){
			// if login successful, go to manage job ad (default) page
			// Write XML to response if DB has return message
			action = "manageJobAd.jsp";
			
			StringBuffer XMLResponse = new StringBuffer();
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
			XMLResponse.append("\t<sessionKey>" + currSession.getKey() + "</sessionKey>\n");
			XMLResponse.append("\t<action>" + action + "</action>\n");
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
			
		}
		else{
			StringBuffer XMLResponse = new StringBuffer();	
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
			XMLResponse.append("\t<sessionKey>" + null + "</sessionKey>\n");
			XMLResponse.append("\t<action>" + action + "</action>\n");
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
		}
		
	}
	
	
	/**
	 * Returns an xml formatted arraylist of all job advertisements
	 */
	private void adminGetJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("ServletJobAd: Inside getAllJobAd");

		int numToGet = 20;
		int index = Integer.parseInt(request.getParameter("startingIndex"));
		String filter = request.getParameter("filter");
		
		String message = "adminGetJobAd failed";
		boolean isSuccessful = false;
			
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		
//		String sessionKey = request.getParameter("sessionKey");
//		Session session = dbManager.getSessionByKey(sessionKey);

		Connection conn = dbManager.getConnection();	
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			
			String query = 
			 	"SELECT * FROM tableJobAd ";
			
			if(!filter.equals("all")){ //Check if status filter is set
				query += "WHERE status = '" + filter + "' ";
			}
			
			query+=
				"ORDER BY datePosted DESC "+
			 	"LIMIT " + index +  ", " + numToGet + ";";
			
			
			System.out.println("getAllJobAd query:" + query);
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
				System.out.println("getAllJobAd successful");
				message = "adminGetJobAd successful";
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
	
	
	
	/*
	 * Sets the status of the job ad to open and changes the isApproved value to true
	 */
	private void adminApprove(HttpServletRequest request, HttpServletResponse response) throws IOException{	
		
		String feedback="adminApprove failed";
		String sessKey = request.getParameter("sessionKey");
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));

		StringBuffer qBuf = DBQ.sessAdminAuthQuery(sessKey, new String[]{"idAccount"});
				
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		
		try {			
			String query = qBuf.toString();
			System.out.println("Processin Query: " + query);
			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()){
				qBuf.setLength(0);
				qBuf.append(DBQ.SELECT + "*" + 
							DBQ.FROM + "tableJobAd tbAd" + 
							DBQ.WHERE + "idJobAd" + DBQ.EQ + jobAdId);
				query=qBuf.toString();
				
				ResultSet adRs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
					.executeQuery(query);
				
				
				if(adRs.next()){
					System.out.println("JobAd: ID-" + adRs.getInt("idJobAd") +"was denied by " + 
			   			"Use: ID-" + adRs.getInt("idAccount") +" with sessionKey-" + sessKey +
			   			"At time:" + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
					adRs.updateString("status", "open");
					adRs.updateInt("isApproved", 1);
					adRs.updateRow();
					
					feedback="The Ad was approved!";
					isSuccessful = true;
					
					System.out.println("adminApprove worked!");

				// Update job ad RSS feed
					query = "SELECT * FROM tableJobAd WHERE idJobAd = '" + jobAdId +"';";
					ResultSet newRs = conn.createStatement().executeQuery(query);
					if(rs.first()){
						String title = newRs.getString("title");
						String desc = newRs.getString("description");
						long datePosted = newRs.getLong("datePosted");
						String tags = newRs.getString("tags");
						String[] feedCategory = null;					
						if(tags != null){
							feedCategory = tags.split(",");
							// remove spaces before and after each category
							for(int i = 0; i < feedCategory.length; i++)
								feedCategory[i] = feedCategory[i].trim();
						}
						try {
							SyndFeed feed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + "jobAd.xml");
							//TODO add link
							SyndEntry entry = RSSManager.createFeedEntry(title, new java.util.Date(datePosted),
									desc, null, feedCategory);
							RSSManager.addEntryToFeed(feed, entry, 0);
							
							String jobAdRSSPath = getServletContext().getRealPath("jobAd.xml");
							RSSManager.writeFeedToFile(feed, jobAdRSSPath);
						} 
						catch (Exception e) {
							Utility.logError("Failed to post RSS entry '" + title + "' to Job Ad RSS: " + e.getMessage());
						}
					}
				}
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
	
	
	
	/*
	 * Sets the status of the job ad to draft and changes the isApproved value to false
	 */
	private void adminDeny(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		String feedback="adminDeny failed";
		String sessKey = request.getParameter("sessionKey");
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));

		StringBuffer qBuf = DBQ.sessAdminAuthQuery(sessKey, new String[]{"idAccount"});
				
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		
		try {			
			String query = qBuf.toString();
			System.out.println("Processin Query: " + query);
			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()){
				qBuf.setLength(0);
				qBuf.append(DBQ.SELECT + "*" + 
							DBQ.FROM + "tableJobAd tbAd" + 
							DBQ.WHERE + "idJobAd" + DBQ.EQ + jobAdId);
				query=qBuf.toString();
				
				ResultSet adRs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
				   .executeQuery(query);
				if(adRs.next()){
					System.out.println("JobAd: ID-" + adRs.getInt("idJobAd") +"was denied by " + 
			   			"Use: ID-" + adRs.getInt("idAccount") +" with sessionKey-" + sessKey +
			   			"At time:" + new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
					adRs.updateString("status", "draft");
					adRs.updateInt("isApproved", 0);
					adRs.updateRow();
					feedback="The Ad was denied!";
				}
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
	
	
/*
 * Method used by the admin or super admin to permanently delete job ads
 */
private void adminDeleteJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		String feedback="adminDeleteJobAd failed";
		String sessKey = request.getParameter("sessionKey");
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));

		StringBuffer qBuf = DBQ.sessAdminAuthQuery(sessKey, new String[]{"idAccount"});
				
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		
		try {			
			String query = qBuf.toString();
			System.out.println("Processin Query: " + query);
			ResultSet rs = conn.createStatement().executeQuery(query);
			while (rs.next()){
				qBuf.setLength(0);
				qBuf.append(DBQ.SELECT + "*" + 
							DBQ.FROM + "tableJobAd tbAd" + 
							DBQ.WHERE + "idJobAd" + DBQ.EQ + jobAdId);
				query=qBuf.toString();
				
				ResultSet adRs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
				   .executeQuery(query);
				if(adRs.next()){
					adRs.deleteRow(); //Deletes the Job Ad Entry from the database					
					feedback="The Ad was deleted!";
				}
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
	
	
	
	
	/***
	 * Handles account ban requests.
	 */
	private void banHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		String reason = request.getParameter("reason");	
		email = Utility.checkInputFormat(email);
		reason = Utility.checkInputFormat(reason);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if email exists
		boolean emailExists = accManager.checkEmailExists(email);
		if(!emailExists){
			allGood = false;
			message = "Account does not exist.";
		}
		else {
			// read account information
			Account accountToBan = accManager.getAccountFromEmail(email);
			if(accountToBan == null){
				allGood = false;
				message = "Error reading account information.";
			}
			else{
				String accountTypeToBan = accountToBan.getType();
				// check if account to ban is searcher or poster
				if( !accountTypeToBan.equals("searcher") && !accountTypeToBan.equals("poster") ){
					allGood = false;
					message = "Cannot ban account type " + accountTypeToBan + ".";
				}

			}
		}
		
		if(allGood){
			if(accManager.banAccount(email)){
				result = true;
				message = "Ban successful.";
				// send email to inform banned user
				emailManager.informBan(email, reason);
			}
			else
				message = "An error has occured while performing ban action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles account unban requests.
	 */
	private void unbanHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		String reason = request.getParameter("reason");	
		email = Utility.checkInputFormat(email);
		reason = Utility.checkInputFormat(reason);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if email exists
		boolean emailExists = accManager.checkEmailExists(email);
		if(!emailExists){
			allGood = false;
			message = "Account " + email + " does not exist.";
		}
		else {
			// read account information
			Account accountToUnban = accManager.getAccountFromEmail(email);
			if(accountToUnban == null){
				allGood = false;
				message = "Error reading account information.";
			}
			else{
				String accountStatusToUnban = accountToUnban.getStatus();
				// check if account status is banned
				if( !accountStatusToUnban.equals("banned") ){
					allGood = false;
					message = "Account " + email + " is not banned.";
				}
				
			}
		}
		
		if(allGood){
			if(accManager.unbanAccount(email)){
				result = true;
				message = "Unban successful.";
				emailManager.informUnban(email, reason);
			}
			else
				message = "An error has occured while performing unban action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	

	/***
	 * Handles create admin requests from the super admin.
	 */
	private void createAdminHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String accountName = request.getParameter("accountName");
		String password = request.getParameter("password");	
		String passwordRepeat = request.getParameter("passwordRepeat");	
		accountName = Utility.checkInputFormat(accountName);
		password = Utility.checkInputFormat(password);
		passwordRepeat = Utility.checkInputFormat(passwordRepeat);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if account name exists
		if( accountName == null ){
			message = "Account name is required.";
			allGood = false;
		}
		else if( !Utility.validate(accountName, "^[_A-Za-z0-9-\\.]{5,15}$") ){
			message = "Invalid account name.";
			allGood = false;
		}
		else if(accManager.checkEmailExists(accountName)){
			allGood = false;
			message = "This account already exists.";
		}
		else if( password == null ){
			message = "Password is required.";
			allGood = false;
		}	
		else if( !Utility.validate(password, SystemManager.pwPattern) ){
			message = "Invalid password format.";
			allGood = false;
		}	
		else if( passwordRepeat == null ){
			message = "Please repeat your password.";
			allGood = false;
		}	
		else if( !password.equals(passwordRepeat) ){
			message = "Passwords do not match.";
			allGood = false;
		}

		
		if(allGood){
			if(accManager.createAdminAccount(accountName, password)){
				result = true;
				message = "Admin creation successful.";
			}
			else
				message = "An error has occured while performing create admin action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles delete account requests from the super admin.
	 */
	private void deleteAccountHandler(HttpServletRequest request, HttpServletResponse response, Session session) throws ServletException, IOException{
		String accountName = request.getParameter("accountName");
		String reason = request.getParameter("reason");
		accountName = Utility.checkInputFormat(accountName);
		reason = Utility.checkInputFormat(reason);
		boolean allGood = true;
		boolean result = false;
		String message = "";
		String userType = "";
		String accToDeleteType = "";
		int accToDeleteID = -1;
		
		// check if account name exists
		boolean emailExists = accManager.checkEmailExists(accountName);
		if(!emailExists){
			allGood = false;
			message = "This account does not exist.";
		}
		else {
			// if check if user is authorized


			Account accToDelete = accManager.getAccountFromEmail(accountName);
			if( accToDelete == null ){
				allGood = false;
				message = "Error reading account information.";
			}
			else{
				accToDeleteType = accToDelete.getType();
				accToDeleteID = accToDelete.getIdAccount();
				userType = session.getAccountType();
				// only super admins can delete admin accounts
				if( accToDeleteType.equals("admin") && !userType.equals("superAdmin")){
					allGood = false;
					message = "Unauthorized delete account action.";
				}
				// requires at least admin privilege to delete normal accounts
				else if( ( accToDeleteType.equals("searcher") || accToDeleteType.equals("searcher") ) 
						&& (!userType.equals("admin") && !userType.equals("superAdmin")) ){
					allGood = false;
					message = "Unauthorized delete account action.";
				}
				else if(accToDeleteType.equals("superAdmin")){
					allGood = false;
					message = "Invalid delete account action.";
				}
			}

		}
		
		if(allGood){
			if( accManager.wipeUserDocuments(accToDeleteID) && accManager.deleteAccount(accountName)){				
				result = true;
				message = "Account deletion successful.";
				// inform the user
				if( !accToDeleteType.equals("admin") ){
					emailManager.informDeletion(accountName, reason);
				}
			}
			else
				message = "An error has occured while performing delete account action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles post news requests from admins.
	 */
	private void postNewsHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String title = request.getParameter("title");
		String content = request.getParameter("content");		
		
		boolean allGood = true;
		boolean result = false;
		String message = "";


		if( title == null || title.equals("") ){
			allGood = false;
			message = "Title cannot be empty.";
		}

		// check input
		title = Utility.replaceNonAsciiChars(title);
		title = Utility.checkInputFormat(title);
		if(content != null){
			content = Utility.replaceNonAsciiChars(content);
			content = Utility.checkInputFormat(content);
			content = Utility.processLineBreaksWhiteSpaces(content);
		}		
		else
			content = "";
		
		if(allGood){
			long currentTime = Utility.getCurrentTime();
			// reduce precision to 1 second to work with ROME library's pubDate 
			currentTime = currentTime / 1000 * 1000;			
			
			if(newsManager.addNewsEntry(title, content, currentTime)){
				result = true;
				message = "Post news successful.";
				// add entry to the top of news RSS
				try {
					SyndFeed newsFeed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + "news.xml");
					SyndEntry newsEntry = RSSManager.createFeedEntry(title, new java.util.Date(currentTime), content);
					RSSManager.addEntryToFeed(newsFeed, newsEntry, 0);
					String newsPath = getServletContext().getRealPath("news.xml");
					RSSManager.writeFeedToFile(newsFeed, newsPath);
				} 
				catch (Exception e) {
					Utility.logError("Failed to add news entry '" + title + "' to RSS: " + e.getMessage());
					message = "Post news successful, but failed to update News RSS.";
				}				
			}
			else
				message = "An error has occured while performing post news action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles delete news requests from admins.
	 */
	private void deleteNewsHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String strIdNews = request.getParameter("idNews");
		int idNews = -1;
		boolean allGood = true;
		boolean result = false;
		String message = "";

		try{
			idNews = Integer.parseInt(strIdNews);
		}
		catch(NumberFormatException ex){
			allGood = false;
			message = "Invalid News ID.";
		}


		
		if(allGood){
			// load news before we delete 
			NewsEntry newsEntry = newsManager.getNewsEntryById(idNews);
			
			if(newsManager.deleteNewsEntry(idNews)){
				result = true;
				message = "News deletion successful.";
				// find and remove the first matching news entry in the RSS
				try {
					String newsPath = getServletContext().getRealPath("news.xml");
					SyndFeed newsFeed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + "news.xml");
					int entryIndex = RSSManager.searchEntry( newsFeed, newsEntry.getTitle(), newsEntry.getContent(), new java.util.Date(newsEntry.getDateTimePublished()) );
					newsFeed = RSSManager.removeEntryFromFeed(newsFeed, entryIndex);
					RSSManager.writeFeedToFile(newsFeed, newsPath);
				} 
				catch (Exception e) {
					Utility.logError("Failed to remove news (Title: " + newsEntry.getTitle() + ") from RSS: " + e.getMessage());
					message = "News deletion successful, but there was an error updating News RSS: " + e.getMessage();
				}				
			}
			else
				message = "An error has occured while performing delete news action. Please try again later.";
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles add RSS entry requests from admins.
	 */
	private void postRSSEntryHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String feedType = request.getParameter("type");
		String title = request.getParameter("title");
		String link = request.getParameter("link");
		String content = request.getParameter("content");	
		String categories = request.getParameter("categories");	
				
		boolean allGood = true;
		boolean result = false;
		String message = "";
		String[] categoriesArray = {};

		if( title == null || title.equals("") ){
			allGood = false;
			message = "Title cannot be empty.";
		}			
		else if( feedType == null || ( !feedType.equals("news") && !feedType.equals("jobAd") ) ){
			allGood = false;
			message = "Invalid feed type.";
		}
		
		// process inputs
		title = Utility.replaceNonAsciiChars(title);
		if(content != null){
			content = Utility.replaceNonAsciiChars(content);
			content = Utility.processLineBreaksWhiteSpaces(content);
		}
		if(categories != null){
			categories = Utility.replaceNonAsciiChars(categories);
			categoriesArray = categories.split(",");
		}
		
		if(allGood){
			long currentTime = Utility.getCurrentTime();
			// reduce precision to 1 second to work with ROME library's pubDate 
			currentTime = currentTime / 1000 * 1000;			
		
			// add entry to the top of selected RSS
			try {
				String feedFile;
				if( feedType.equals("news") )
					feedFile = "news.xml";
				else
					feedFile = "jobAd.xml";
					
				SyndFeed feed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + feedFile);
				SyndEntry entry = RSSManager.createFeedEntry(title, new java.util.Date(currentTime), content, link, categoriesArray);
				RSSManager.addEntryToFeed(feed, entry, 0);
				String filePath = getServletContext().getRealPath(feedFile);
				RSSManager.writeFeedToFile(feed, filePath);
				result = true;
				message = "Post RSS entry successful.";
			} 
			catch (Exception e) {
				Utility.logError("Failed to post RSS entry '" + title + "' to RSS: " + e.getMessage());
				message = "Failed to post RSS entry '" + title + "' to " + feedType + " RSS: " + e.getMessage();
			}
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles delete RSS entry requests from admins.
	 */
	private void removeRSSEntryHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String strEntryIndex = request.getParameter("entryIndex");
		String feedType = request.getParameter("feedType");
		int entryIndex = -1;
		boolean allGood = true;
		boolean result = false;
		String message = "";


		if( feedType == null || ( !feedType.equals("news") && !feedType.equals("jobAd") ) ){
			allGood = false;
			message = "Invalid feed type.";
		}
		else{
			try{
				entryIndex = Integer.parseInt(strEntryIndex);
			}
			catch(NumberFormatException e){}
			// report error if index failed to be parsed, or an invalid index was passed in
			if(entryIndex < 0){
				allGood = false;
				message = "Invalid entry index.";
				}
		}
		
		if(allGood){		
			// delete RSS entry with the given index
			try {
				String feedFile;
				if( feedType.equals("news") )
					feedFile = "news.xml";
				else
					feedFile = "jobAd.xml";
					
				SyndFeed feed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + feedFile);
				RSSManager.removeEntryFromFeed(feed, entryIndex);
				String filePath = getServletContext().getRealPath(feedFile);
				RSSManager.writeFeedToFile(feed, filePath);
				result = true;
				message = "RSS entry deletion successful.";
			} 
			catch (Exception e) {
				Utility.logError("Failed to remove RSS entry with index '" + entryIndex + "' from RSS: " + e.getMessage());
				message = "Failed to remove RSS entry with index '" + entryIndex + "' from RSS: " + e.getMessage();
			}
		}
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	/**************************************HELPER FUNCTIONS*******************************************************/
	

}

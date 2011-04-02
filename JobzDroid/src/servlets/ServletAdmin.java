package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import classes.Account;
import classes.DbQuery;
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
		
		switch( EnumAction.valueOf(action) ){
			case requestForAdminLogin:
				adminLoginReqTaker(request, response);
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
			case ban:
				banHandler(request, response);
				break;
			case unban:
				unbanHandler(request, response);
				break;
			case createAdmin:
				createAdminHandler(request, response);
				break;
			case deleteAccount:
				deleteAccountHandler(request, response);
				break;
			case postNews:
				postNewsHandler(request, response);
				break;
			case deleteNews:
				deleteNewsHandler(request, response);
				break;
			case postRSSEntry:
				postRSSEntryHandler(request, response);
				break;
			case removeRSSEntry:
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

				// get new job ad info and update job ad RSS feed
				query = "SELECT * FROM tableJobAd WHERE idJobAd = '" + jobAdId +"';";
				stmt.executeQuery(query);
				ResultSet rs = stmt.getResultSet();
				if(rs.first()){
					String title = rs.getString("title");
					String desc = rs.getString("description");
					long datePosted = rs.getLong("datePosted");
					String tags = rs.getString("tags");
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
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
		
	}
	
	
	
private void adminDeleteJobAd(HttpServletRequest request, HttpServletResponse response) throws IOException{
		

		String feedback="adminDeleteJobAd failed";
		String sessKey = request.getParameter("sessionKey");
		int jobAdId = Integer.parseInt(request.getParameter("jobAdId"));

		StringBuffer qBuf = DBQ.sessAuthQuery(sessKey, new String[]{"idAccount"}, "admin");
		
		qBuf.insert(0, DBQ.SELECT + "tbAd.idJobAd, tbAd.idAccount" + 
				DBQ.FROM + "tableJobAd tbAd" + DBQ.WHERE + "idAccount" + DBQ.EQ);
		qBuf.append(DBQ.AND +"idJobAd" + DBQ.EQ + jobAdId);
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;
		
		boolean isSuccessful = false;
		
		try {			
			//Delete designed job Ad
			String query = qBuf.toString();
			System.out.println("Processin Query: " + query);
			ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
							   .executeQuery(query);
			
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
	
	
	
	
	/***
	 * Handles account ban requests.
	 */
	private void banHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String email = request.getParameter("email");
		String reason = request.getParameter("reason");	
		sessionKey = Utility.checkInputFormat(sessionKey);
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
				else{
					// check session key
					Session session = accManager.getSessionByKey(sessionKey);
					if(session == null){			
						allGood = false;
						message = "Unauthorized ban action.";
					}
					else{
						// check if user is authorized
						String userType = session.getAccountType();
						if( !userType.equals("admin") && !userType.equals("superAdmin")){
							allGood = false;
							message = "Unauthorized ban action.";
						}
					}
				}
			}
		}
		
		if(allGood){
			if(banAccount(email)){
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
		String sessionKey = request.getParameter("sessionKey");
		String email = request.getParameter("email");
		String reason = request.getParameter("reason");	
		sessionKey = Utility.checkInputFormat(sessionKey);
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
				else{
					// check session key
					Session session = accManager.getSessionByKey(sessionKey);
					if(session == null){			
						allGood = false;
						message = "Unauthorized unban action.";
					}
					else{
						// check if user is authorized
						String userType = session.getAccountType();
						if( !userType.equals("admin") && !userType.equals("superAdmin")){
							allGood = false;
							message = "Unauthorized unban action.";
						}
					}
				}
			}
		}
		
		if(allGood){
			if(unbanAccount(email)){
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
		String sessionKey = request.getParameter("sessionKey");
		String accountName = request.getParameter("accountName");
		String password = request.getParameter("password");	
		String passwordRepeat = request.getParameter("passwordRepeat");	
		sessionKey = Utility.checkInputFormat(sessionKey);
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
		else {
			Session session = accManager.getSessionByKey(sessionKey);
			if(session == null){			
					allGood = false;
					message = "Unauthorized create admin action.";
			}
			else{
				// check if user is authorized
				String userType = session.getAccountType();
				if( !userType.equals("superAdmin")){
					allGood = false;
					message = "Unauthorized create admin action.";
				}
			}
		}
		
		if(allGood){
			if(createAdminAccount(accountName, password)){
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
	private void deleteAccountHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String accountName = request.getParameter("accountName");
		String reason = request.getParameter("reason");
		sessionKey = Utility.checkInputFormat(sessionKey);
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
			Session session = accManager.getSessionByKey(sessionKey);
			if(session == null){			
					allGood = false;
					message = "Unauthorized delete account action.";
			}
			else{
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
		String sessionKey = request.getParameter("sessionKey");
		String title = request.getParameter("title");
		String content = request.getParameter("content");		
		sessionKey = Utility.checkInputFormat(sessionKey);
		
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// session check
		Session session = accManager.getSessionByKey(sessionKey);
		if(session == null){
			allGood = false;
			message = "Unauthorized post news action.";
		}
		else{
			// check if user is authorized
			String userType = session.getAccountType();
			if( !userType.equals("superAdmin") && !userType.equals("admin") ){
				allGood = false;
				message = "Unauthorized post news action.";
			}
			else if( title == null || title.equals("") ){
				allGood = false;
				message = "Title cannot be empty.";
			}
		}
		// check input
		title = Utility.checkInputFormat(title);
		if(content != null){
			content = Utility.checkInputFormat(content);
			// process line breaks and white spaces in content
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
		String sessionKey = request.getParameter("sessionKey");
		String strIdNews = request.getParameter("idNews");
		int idNews = -1;
		sessionKey = Utility.checkInputFormat(sessionKey);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// session check
		Session session = accManager.getSessionByKey(sessionKey);
		if(session == null){
			allGood = false;
			message = "Unauthorized delete news action.";
		}
		else{
			// check if user is authorized
			String userType = session.getAccountType();
			if( !userType.equals("superAdmin") && !userType.equals("admin") ){
				allGood = false;
				message = "Unauthorized delete news action.";
			}
			else{
				try{
					idNews = Integer.parseInt(strIdNews);
				}
				catch(NumberFormatException ex){
					allGood = false;
					message = "Invalid News ID.";
				}
			}
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
		String sessionKey = request.getParameter("sessionKey");
		String feedType = request.getParameter("type");
		String title = request.getParameter("title");
		String link = request.getParameter("link");
		String content = request.getParameter("content");	
		String categories = request.getParameter("categories");	
		
		sessionKey = Utility.checkInputFormat(sessionKey);
		
		boolean allGood = true;
		boolean result = false;
		String message = "";
		String[] categoriesArray = {};

		// session check
		Session session = accManager.getSessionByKey(sessionKey);
		if(session == null){
			allGood = false;
			message = "Unauthorized post RSS action.";
		}
		else{
			String userType = session.getAccountType();
			if( !userType.equals("superAdmin") && !userType.equals("admin") ){
				allGood = false;
				message = "Unauthorized post RSS action.";
			}
			else if( title == null || title.equals("") ){
				allGood = false;
				message = "Title cannot be empty.";
			}			
			else if( feedType == null || ( !feedType.equals("news") && !feedType.equals("jobAd") ) ){
				allGood = false;
				message = "Invalid feed type.";
			}
		}
		
		// process inputs
		if(content != null)
			content = Utility.processLineBreaksWhiteSpaces(content);
		if(categories != null)
			categoriesArray = categories.split(",");
		
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
		String sessionKey = request.getParameter("sessionKey");
		String strEntryIndex = request.getParameter("entryIndex");
		String feedType = request.getParameter("feedType");
		int entryIndex = -1;
		sessionKey = Utility.checkInputFormat(sessionKey);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// session check
		Session session = accManager.getSessionByKey(sessionKey);
		if(session == null){
			allGood = false;
			message = "Unauthorized post RSS action.";
		}
		else{
			String userType = session.getAccountType();
			if( !userType.equals("superAdmin") && !userType.equals("admin") ){
				allGood = false;
				message = "Unauthorized delete RSS action.";
			}
			else if( feedType == null || ( !feedType.equals("news") && !feedType.equals("jobAd") ) ){
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
	
	/***
	 * Bans the given account.
	 * @param email Account name to ban.
	 * @return boolean indicating whether the ban was successful.
	 */
	private boolean banAccount(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;		
		
		try {
			stmt = conn.createStatement();
			String query = "UPDATE tableAccount SET status='banned' WHERE email='" + email + "';";
            
			int rowsInserted = stmt.executeUpdate(query);
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	}
	
	/***
	 * Unbans the given account.
	 * @param email Account name to unban.
	 * @return boolean indicating whether the unban was successful.
	 */
	private boolean unbanAccount(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;		
		
		try {
			stmt = conn.createStatement();
			String query = "UPDATE tableAccount SET status='active' WHERE email='" + email + "';";
            
			int rowsInserted = stmt.executeUpdate(query);
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	}
	
	/***
	 * Creates an admin account with given account name and password.
	 * @param email Account name to create.
	 * @param password Password to use.
	 * @return boolean indicating whether the admin creation was successful.
	 */
	private boolean createAdminAccount(String email, String password){
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;		
		long currentTime = Utility.getCurrentTime();
		try {
			String query = "INSERT INTO tableAccount(email, password, type, status, dateTimeCreated)" +
            		" VALUES(?,md5(?),'admin','active',?);";
			
			pst = conn.prepareStatement(query);
			pst.setString(1, email);
            pst.setString(2, password);
            pst.setLong(3, currentTime);
            
			int rowsInserted = pst.executeUpdate();
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (pst != null)
	                pst.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	}
}

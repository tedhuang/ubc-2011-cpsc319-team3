package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

import classes.Account;
import classes.Session;
import classes.Utility;

import managers.DBManager;
import managers.RSSManager;

/**
 * Servlet implementation class ServletAdmin
 * Handles all admin actions, including ban, unban, create/delete admin account(super admin), post news and so on
 */
public class ServletAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAdmin() {
        super();
		dbManager = DBManager.getInstance();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		requestForAdminLogin,
		ban,
		unban,
		createAdmin,
		deleteAdmin,
		postNews
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
			case ban:
				ban(request, response);
				break;
			case unban:
				unban(request, response);
				break;
			case createAdmin:
				createAdmin(request, response);
				break;
			case deleteAdmin:
				deleteAdmin(request, response);
				break;
			case postNews:
				postNews(request, response);
				break;
		}
	}
	
	private void adminLoginReqTaker(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		String pw = request.getParameter("password");
		Session currSession = null;
		String action = "";
		Account acc = dbManager.getAccountFromEmail(email);
		if( acc != null ){
			String accType = acc.getType();
			if( accType.equals("admin") || accType.equals("superAdmin"))
				currSession = dbManager.startSession(email, pw);
		}
		
		if(currSession != null){
			// if login successful, return credential and sucess message
			// Write XML to response if DB has return message
			action = "home.jsp";
			
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
	
	/***
	 * Handles account ban requests.
	 */
	private void ban(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String email = request.getParameter("email");		
		sessionKey = Utility.checkInputFormat(sessionKey);
		email = Utility.checkInputFormat(email);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if email exists
		boolean emailExists = dbManager.checkEmailExists(email);
		if(!emailExists){
			allGood = false;
			message = "Account does not exist.";
		}
		else {
			// read account information
			Account accountToBan = dbManager.getAccountFromEmail(email);
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
					Session session = dbManager.getSessionByKey(sessionKey);
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
				message = "Account " + email + " has been successfully banned.";
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
	private void unban(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String email = request.getParameter("email");	
		sessionKey = Utility.checkInputFormat(sessionKey);
		email = Utility.checkInputFormat(email);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if email exists
		boolean emailExists = dbManager.checkEmailExists(email);
		if(!emailExists){
			allGood = false;
			message = "Account does not exist.";
		}
		else {
			// read account information
			Account accountToUnban = dbManager.getAccountFromEmail(email);
			if(accountToUnban == null){
				allGood = false;
				message = "Error reading account information.";
			}
			else{
				String accountStatusToUnban = accountToUnban.getType();
				// check if account status is banned
				if( !accountStatusToUnban.equals("banned") ){
					allGood = false;
					message = "Account is not banned.";
				}
				else{
					// check session key
					Session session = dbManager.getSessionByKey(sessionKey);
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
				message = "Account " + email + " has been successfully unbanned.";
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
	private void createAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String accountName = request.getParameter("accountName");
		String password = request.getParameter("password");	
		sessionKey = Utility.checkInputFormat(sessionKey);
		accountName = Utility.checkInputFormat(accountName);
		password = Utility.checkInputFormat(password);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if account name exists
		boolean emailExists = dbManager.checkEmailExists(accountName);
		if(emailExists){
			allGood = false;
			message = "This account already exists.";
		}
		else {
			Session session = dbManager.getSessionByKey(sessionKey);
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
				message = "Admin account " + accountName + " has been successfully created.";
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
	 * Handles delete admin requests from the super admin.
	 */
	private void deleteAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String accountName = request.getParameter("accountName");
		sessionKey = Utility.checkInputFormat(sessionKey);
		accountName = Utility.checkInputFormat(accountName);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// check if account name exists
		boolean emailExists = dbManager.checkEmailExists(accountName);
		if(!emailExists){
			allGood = false;
			message = "This account does not exist.";
		}
		else {
			Session session = dbManager.getSessionByKey(sessionKey);
			if(session == null){			
					allGood = false;
					message = "Unauthorized delete admin action.";
			}
			else{
				// check if user is authorized
				String userType = session.getAccountType();
				if( !userType.equals("superAdmin")){
					allGood = false;
					message = "Unauthorized delete admin action.";
				}
			}
		}
		
		if(allGood){
			if(deleteAdminAccount(accountName)){
				result = true;
				message = "Admin account " + accountName + " has been successfully deleted.";
			}
			else
				message = "An error has occured while performing delete admin action. Please try again later.";
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
	private void postNews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionKey");
		String title = request.getParameter("title");
		String content = request.getParameter("content");	
		sessionKey = Utility.checkInputFormat(sessionKey);
		title = Utility.checkInputFormat(title);
		content = Utility.checkInputFormat(content);
		boolean allGood = true;
		boolean result = false;
		String message = "";

		// session check
		Session session = dbManager.getSessionByKey(sessionKey);
		if(session == null){
			allGood = false;
			message = "Unauthorized create admin action.";
		}
		else{
			// check if user is authorized
			String userType = session.getAccountType();
			if( !userType.equals("superAdmin") && !userType.equals("admin") ){
				allGood = false;
				message = "Unauthorized post news action.";
			}
		}
		
		if(allGood){
			if(addNewsEntry(title, content)){
				result = true;
				message = "News entry has been successfully posted.";
				// add entry to the top of news RSS
				try {
					SyndFeed newsFeed = RSSManager.readFeedFromFile("http://localhost:8080/JobzDroid/rss/news.xml");
					SyndEntry newsEntry = RSSManager.createFeedEntry(title, new java.util.Date(), content);
					RSSManager.addEntryToFeed(newsFeed, newsEntry, 0);
					RSSManager.writeFeedToFile(newsFeed, "rss/news.xml");
				} 
				catch (Exception e) {
					Utility.logError("Failed to add news entry '" + title + "' to RSS: " + e.getMessage());
					message = "News entry has been successfully posted, but there was an error while trying to update RSS for news.";
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
            
			int rowsInserted = pst.executeUpdate(query);
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
	
	/***
	 * Deletes an admin account with given account name.
	 * @param email Account name to delete.
	 * @return boolean indicating whether the deletion was successful.
	 */
	private boolean deleteAdminAccount(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "DELETE FROM tableAccount WHERE email='" + email + "';";
            
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
	private boolean addNewsEntry(String title, String content){
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;		
		long currentTime = Utility.getCurrentTime();
		try {
			String query = "INSERT INTO tableNews(title, content, dateTimePublished)" +
            		" VALUES(?,?,?);";
			
			pst = conn.prepareStatement(query);
			pst.setString(1, title);
            pst.setString(2, content);
            pst.setLong(3, currentTime);
            
			int rowsInserted = pst.executeUpdate(query);
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

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

import classes.Account;
import classes.Session;
import classes.Utility;

import managers.DBManager;
import managers.SystemManager;

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
		ban,
		unban,
		createAdmin,
		deleteAdmin
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
		// forward to error page if request is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(Exception e){
		//	forwardErrorPage(request, response);
			throw new ServletException("Invalid account servlet action.");
		}
		
		switch( EnumAction.valueOf(action) ){
			// account registration
			case ban:
				ban(request, response);
				break;
			case unban:
				unban(request, response);
				break;
			case createAdmin:
				ban(request, response);
				break;
			case deleteAdmin:
				ban(request, response);
				break;
		}
	}
	
	/***
	 * Handles account ban requests.
	 */
	private void ban(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String sessionKey = request.getParameter("sessionID");
		String email = request.getParameter("email");		
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
			Account accountToBan = dbManager.getAcccountFromEmail(email);
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
		String sessionKey = request.getParameter("sessionID");
		String email = request.getParameter("email");		
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
			Account accountToUnban = dbManager.getAcccountFromEmail(email);
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
	 * Bans the given account.
	 * @param email Account name to ban.
	 * @return boolean indicating whether the ban was successful.
	 */
	private boolean banAccount(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;		
		email = Utility.checkInputFormat(email);
		
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
		email = Utility.checkInputFormat(email);
		
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
}

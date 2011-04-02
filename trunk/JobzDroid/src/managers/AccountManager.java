package managers;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import servlets.ServletDocument;

import classes.Account;
import classes.Session;
import classes.Utility;
/***
 * Account functions
 */

public class AccountManager {
	DBManager dbManager;
	public AccountManager(){
		dbManager = DBManager.getInstance();
	}
	
	/***
	 * Checks whether the given primary email address already exists.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean checkEmailExists(String email) {
		System.out.println("Inside DBManager - checkEmailExists");
		Connection conn = dbManager.getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		email = Utility.checkInputFormat(email);
		try {
			stmt = conn.createStatement();
			String query = "SELECT idAccount FROM tableAccount " + "WHERE email='" + email + "';"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();			
			// check if ResultSet is empty  
			if (!rs.next())
				return false;
			else
				return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return false; 
	}
	
	/***
	 * Returns the account object with the input email address. 
	 * Returns null if email address doesn't exist in the account table.
	 * @param email Email address to be queried.
	 * @return Account object with the input email address. Returns null if not found.
	 */
	public Account getAccountFromEmail(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		Account account = null;
		int idAccount;
		String secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		
		email = Utility.checkInputFormat(email);
		try {
			stmt = conn.createStatement();
			query = "SELECT idAccount, secondaryEmail, type, status, dateTimeCreated, password FROM tableAccount WHERE email='" + email + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first()){
				idAccount = rs.getInt("idAccount");
				if(rs.getObject("secondaryEmail") == null)
					secondaryEmail = "";
				else
					secondaryEmail = rs.getString("secondaryEmail");
				type = rs.getString("type");
				status = rs.getString("status");
				dateTimeCreated = rs.getLong("dateTimeCreated");
				passwordMd5 = rs.getString("password");
				account = new Account(idAccount, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
				return account;
			}
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return account;
	}
	
	/***
	 * Returns the account object with the input account ID. 
	 * Returns null if email address doesn't exist in the account table.
	 * @param email Account ID to be queried.
	 * @return Account object with the input account ID. Returns null if not found.
	 */
	public Account getAccountFromId(int idAccount){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		Account account = null;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		
		try {
			stmt = conn.createStatement();
			query = "SELECT email, secondaryEmail, type, status, dateTimeCreated, password FROM tableAccount WHERE idAccount='" + idAccount + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first()){
				email = rs.getString("email");
				if(rs.getObject("secondaryEmail") == null)
					secondaryEmail = "";
				else
					secondaryEmail = rs.getString("secondaryEmail");
				type = rs.getString("type");
				status = rs.getString("status");
				dateTimeCreated = rs.getLong("dateTimeCreated");
				passwordMd5 = rs.getString("password");
				account = new Account(idAccount, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
				return account;
			}
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return account;
	}
	
	/***
	 * Returns an array list of all accounts of type searcher and poster
	 * @return An array list of all accounts of type searcher and poster.
	 */
	public ArrayList<Account> getSearcherPosterAccounts(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		ArrayList<Account> accounts = new ArrayList<Account>();
		int idNews;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		try {			
			stmt = conn.createStatement();
			query = "SELECT * FROM tableAccount WHERE type='searcher' OR type='poster';";            
			rs = stmt.executeQuery(query);
		    while (rs.next()) {
		    	idNews = rs.getInt("idAccount");
		    	email = rs.getString("email");
		    	secondaryEmail = rs.getString("secondaryEmail");
		    	type = rs.getString("type");
		    	status = rs.getString("status");
		    	dateTimeCreated = rs.getLong("dateTimeCreated");
		    	passwordMd5 = rs.getString("password");
		        Account acc = new Account(idNews, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
		        accounts.add(acc);
		    }
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return accounts;
	}
	
	/***
	 * Returns an array list of all accounts of type searcher and poster
	 * @return An array list of all accounts of type searcher and poster.
	 */
	public ArrayList<Account> getAdminAccounts(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		ArrayList<Account> accounts = new ArrayList<Account>();
		int idNews;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		try {			
			stmt = conn.createStatement();
			query = "SELECT * FROM tableAccount WHERE type='admin';";            
			rs = stmt.executeQuery(query);
		    while (rs.next()) {
		    	idNews = rs.getInt("idAccount");
		    	email = rs.getString("email");
		    	secondaryEmail = rs.getString("secondaryEmail");
		    	type = rs.getString("type");
		    	status = rs.getString("status");
		    	dateTimeCreated = rs.getLong("dateTimeCreated");
		    	passwordMd5 = rs.getString("password");
		        Account acc = new Account(idNews, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
		        accounts.add(acc);
		    }
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return accounts;
	}
	
	/**
	 * Returns the account ID associated with the password reset request ID
	 * @param idPasswordReset Password reset ID contained in the link set to the user requesting a password reset.
	 * @return Account ID associated with the the password reset. Returns -1 if the password reset ID is invalid or expired.
	 */
	public int getIdAccountFromIdPasswordReset(String idPasswordReset){
		Connection conn = dbManager.getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		long expiryTime, currentTime = Utility.getCurrentTime();
		int idAccount = -1;
		System.out.println(idPasswordReset);
		idPasswordReset = Utility.checkInputFormat(idPasswordReset);
		try {
			stmt = conn.createStatement();
			String query = "SELECT idAccount,expiryTime FROM tablePasswordReset " + "WHERE idPasswordReset='" + idPasswordReset + "';"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then return account id
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					return idAccount;
				}
				else
					return -1;
			}
			else
				return -1;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
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
		return -1;
	}
	
	/***
	 * Deletes an account with given account name.
	 * @param email Account name to delete.
	 * @return boolean indicating whether the deletion was successful.
	 */
	public boolean deleteAccount(String email){
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
	
	/***************************************************************************************************************************************
	 * 									SessionKey Generator FUNCTIONS
	 * should return a new unique session key for the account
	 * return null for failure
	***************************************************************************************************************************************/
		
		public Session startSession(String email, String pw){

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;		
			email = Utility.checkInputFormat(email);
			pw = Utility.checkInputFormat(pw);
			// md5 the password
			int idAccount = 0;
			String accountType = null;
			ResultSet rs = null;
			
			Session newSession = null;
			
			try{
				// retrieve the account ID from login information
				stmt = conn.createStatement();
				System.out.println("check email:" + email + " password" + pw);
				rs = stmt.executeQuery( "SELECT * FROM tableAccount "+
						   						  "WHERE email='"+ email + "' " +
						   						  "AND password = md5('" + pw + "')" +
				  								  "AND status = 'active';");
				
				System.out.println( Utility.md5(pw) );
				if(rs.first()){
					
					System.out.println( email +" Logged in");
					
					idAccount	= rs.getInt("idAccount");
					accountType	= rs.getString("Type");
					
					newSession = new Session( idAccount, accountType );
					
					System.out.println( idAccount + " " + accountType );
					
				}
				else{
					System.out.println( "could not find matching email / password info" );
					return null;
				}
				
				newSession = registerSessionKey( newSession );
				if(newSession.getKey()==null) {
					Utility.logError( "Failed to generate session key on request, returning null" );
					return null;
				}
				else {
					return newSession;
				}
			}//ENDOF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
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
		    
			return null;
		}
		
		private Session registerSessionKey( Session session ) {
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			UUID uuid = UUID.randomUUID();
			String newSessionKey = uuid.toString();
			long newExpiryTime = Utility.getCurrentTime() + SystemManager.expiryTimeSession;
			
			int rowsInserted = 0;
			
			try{
				//wipe previous sessionKey associated with the account
				cleanSessionKeyByID( session.getIdAccount() );
				
				stmt = conn.createStatement();			
				rowsInserted = stmt.executeUpdate("INSERT INTO tableSession (sessionKey, idAccount, expiryTime) VALUES " +
											"('" + newSessionKey + "','" + session.getIdAccount() + "','" + 
											newExpiryTime + "')");

			}//ENDOF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
			
			// free DB objects

			if(rowsInserted==1){	// if success, return session key
				session.registerNewSessionKey( newSessionKey, newExpiryTime);
				return session;
			}
			else {
				Utility.logError("could not insert new session key into DB");
			}
			
			return null;
		}
		
		private Session extendSessionExpiryTime( Session session ) {

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;		

			
			long newExpiryTime = session.getExpiryTime() + SystemManager.expiryTimeSession;
			
			try{
				// retrieve the account ID from login information
				stmt = conn.createStatement();
				int rowsUpdated = stmt.executeUpdate( "UPDATE tableSession SET expiryTime='" + newExpiryTime + "' " + 
											"WHERE sessionKey='"+ session.getKey() + "';");
				
				if( rowsUpdated != 1 ) {
					return null;
				}
				
				session.updateExpiryTime(newExpiryTime);
				return session;

			}//ENDOF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
		    
			return null;
		}
		
		private int cleanSessionKeyByID( int idAccount ) {
			// cleanup other sessionKey associated with this idAccount 

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			int cleanUpCount = 0;
			try {
				
				stmt=conn.createStatement();
				cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
					 	   "WHERE idAccount='" + idAccount +"'" );
				if(cleanUpCount==0){
					System.out.println("No other keys for account " + idAccount);
				}
				else{
					System.out.println( cleanUpCount + " keys were cleaned from DB");
				}
				
			}
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
			
			return cleanUpCount;
		}
		
		private int cleanSessionKeyByKey( String sessionKey ) {
			// cleanup other sessionKey associated with this idAccount 

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			int cleanUpCount = 0;
			try {
				stmt=conn.createStatement();
				cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
					 	   "WHERE sessionKey='" + sessionKey +"'" );
				if(cleanUpCount==0){
					System.out.println("No key matches " + sessionKey);
				}
				else{
					System.out.println( cleanUpCount + " keys were cleaned from DB");
				}
			}
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
			finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
			
			return cleanUpCount;
		}
		
		public Session getSessionByKey( String key ) { 
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			ResultSet rs = null;
			if(key == null)
				return null;
			key = Utility.checkInputFormat(key);
			
			try{
				Session currSession = null;
				stmt = conn.createStatement();
				String queryCode =
					"SELECT * FROM tableSession LEFT OUTER JOIN tableAccount USING (idAccount) " + 
					"WHERE sessionKey='" + key +"' " +
					"AND status='active';";
				
				
				rs = stmt.executeQuery( queryCode );
				
				if(rs.first()){
					currSession = new Session( 	key,
												rs.getInt("idAccount"),
												rs.getString("type"),
												rs.getLong("expiryTime") );
				}
				else{
					return null;
				}
				
				long currentTime = Utility.getCurrentTime();
				if( currSession.getExpiryTime() >= currentTime ) {
					// if session didn't expire, return the current session
					return currSession;
					
				}
				else {//if the key is expired but within 30min
					if( currSession.getExpiryTime() + SystemManager.sessionRenewPeriodAfterExpiry >= currentTime  ) {
						// renew user's sessionKey
						currSession = extendSessionExpiryTime(currSession);
					}
					else {
						// past renewal period, user must re-log in
						cleanSessionKeyByKey( key );
						return null;
					}
					
				}
			}
			catch (SQLException e) {
					Utility.logError("SQL exception: " + e.getMessage());
			}
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
		    
			return null;
		}
		
		
	/**********************************END OF SESSIONKEY GENERATION FUNCTIONS***************************************************************/


	/****************************************************************************************************************************************
	 * 									USER LOG-OUT FUNCTION
	 * return: T/F
	 ****************************************************************************************************************************************/
		public boolean userLogout(String sessionKey){
			Connection conn = dbManager.getConnection();
			Statement stmt = null;
			int done = -1; 
			sessionKey = Utility.checkInputFormat(sessionKey);
			try
			{
				stmt=conn.createStatement();
				
				
				done = stmt.executeUpdate("DELETE FROM tableSession " +
									 	   "WHERE sessionKey='" + sessionKey +"'" );
				if(done==0){
					System.out.println("LOGOUT ROW DELETION:No Entry found!");
					return false;
				}
				else{
					System.out.println("User Logged Out");
					return true;
				}
				
			}
			catch (SQLException e) {
				Utility.logError("There is a problem when logging out");
			}
		    finally {
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
			return false;
		}
		
		/**
		 * Deletes all of the specified user's docs.
		 * @param idAcct Account ID of the user.
		 * @return Whether the delete was successful or not.
		 */
		public boolean wipeUserDocuments(int idAcct) {
			File[] fileArry = ServletDocument.getUserFiles( idAcct );
			
			boolean success = true;
			
			File parentDir = null;
			for( File fileThis : fileArry ) {
				fileThis.getParentFile();
				parentDir = fileThis.getParentFile();
				if( !fileThis.delete()) {
					success = false;
				}
			}
			
			if ( parentDir != null ) {
				if( !parentDir.delete() ) {
					success = false;
				}
			}

			
			return success;
		}
}

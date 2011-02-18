package managers;
import java.sql.*;
import java.util.Calendar;
import java.util.UUID;

import classes.Utility;

public class DBManager {			
	/***
	 * Returns a JDBC connection object
	 * @return Connection object to the database
	 */
	private Connection getConnection() {	
		Connection dbConn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//TODO move to config
			
	//		dbConn = DriverManager.getConnection("jdbc:mysql://70.79.38.90/jobzdroid", "root", "cpsc410");
	//		dbConn = DriverManager.getConnection("jdbc:mysql://192.168.0.192:3306/jobzdroid", "root", "cs319CS#!(");
			dbConn = DriverManager.getConnection("jdbc:mysql://www.db4free.net:3306/dbjobzdriod", "blitzcriegteam", "cs319team3");
		}
		catch(Exception e){
			//TODO: log error
			System.out.println("Error creating DB connection : " + e.getMessage());
		}		
		return dbConn;
	}
	
	public DBManager() {}
	
	
	/***
	 * Checks whether the given primary email address already exists.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean checkEmailExists(String email) {
		Connection conn = getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT idAccount FROM tableAccount " + "WHERE Email='" + email + "';"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			
			// check if ResultSet is empty  
			if (!rs.next())
				return false;
			else
				return true;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
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
		return false; 
	}	
	
	
	
	/***
	 * Creates a new account with the given email, password, account type and person/company name
	 * with a uniquely generated verification number used for email verification.
	 * New accounts open with "Pending" status.
	 * @param email Primary email
	 * @param password User password
	 * @param accountType Account type
	 * @param name Person/Company name 
	 * @param uuid randomly generated unique verification number for email
	 * @param expiryTimeEmailRegistration Time before the registration verification expires
	 * @return boolean indicating whether account was successfully created
	 */
	public boolean createAccount(String email, String password, String accountType, String name, UUID uuid, long expiryTimeEmailRegistration) {
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			int idAccount;
					
			// update account table, and set account status to pending
			String query = "INSERT INTO tableAccount(Email, Password, Type, Status, dateTimeCreated) VALUES " + 
	  		"('" + email + "',md5('" + password + "'),'" + accountType + "','" + "pending" + "','" + currentTime + "');";			
			// if successful, 1 row should be inserted
			int rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			
			// get account id of the account just created
			idAccount = getIdAcccountFromEmail(email);
			if(idAccount == -1)
				return false;
						
			// add entry to email verification table
			long expiryTime = currentTime + expiryTimeEmailRegistration;			
			query = "INSERT INTO tableEmailVerification(idEmailVerification, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";			
			// if successful, 1 row should be inserted
			rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			// add entry to user profile table
			if(accountType.equals("searcher")){
				query = "INSERT INTO tableProfileSearcher(idAccount, name) VALUES " + 
		  		"('" + idAccount + "','" + name + "');";
				rowsInserted = stmt.executeUpdate(query);
				if (rowsInserted != 1)
					return false;
			}
			else if(accountType.equals("poster")){
				query = "INSERT INTO tableProfilePoster(idAccount, name) VALUES " + 
		  		"('" + idAccount + "','" + name + "');";
				rowsInserted = stmt.executeUpdate(query);
				if (rowsInserted != 1)
					return false;
			}
			return true;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
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
		return false;		
	}
	
	/***
	 * Updates account status from pending to active if the given verification number is valid.
	 * The verification number is created and linked to an account upon account creation, and deleted after it is used to activate the account.
	 * @param verificationNumber A UUID linked to an account id in tableEmailVerification
	 * @return boolean indicating whether the account activation was successful
	 */
	public boolean activateAccount(String verificationNumber){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			long expiryTime;
			int idAccount, rowsUpdated;
					
			// check if verification number is valid	
			query = "SELECT idAccount, expiryTime FROM tableEmailVerification WHERE idEmailVerification='" + 
	  		verificationNumber + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			// if valid, then check expiry time of verification number
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then activate account
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					query = "UPDATE tableAccount SET status='active' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
						rowsUpdated = stmt.executeUpdate(query);
						if(rowsUpdated != 1){
							//TODO log error
							System.out.println("Failed to delete row containing the verification number upon successful account activation.");
						}
						return true;
					}
				}
			}
			else
				return false;
			
			return true;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
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
		return false;	
	}
	
	/***
	 * Updates account primary email if the given verification number is valid.
	 * The new email address is stored in tableEmailVerification when user requests for the email change.
	 * The verification number is created and linked to user's account when user requests to change primary email, and deleted after it is used to change the email.
	 * @param verificationNumber A UUID in tableEmailVerification which is linked to an account ID that requested for a primary email change
	 * @return boolean indicating whether changing the primary email was successful
	 */
	public boolean verifyChangePrimaryEmail(String verificationNumber){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			long expiryTime;
			int idAccount, rowsUpdated;
			String emailPending;
					
			// check if verification number is valid	
			query = "SELECT idAccount, expiryTime, emailPending FROM tableEmailVerification WHERE idEmailVerification='" + 
	  		verificationNumber + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			// if valid, then check expiry time of verification number
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then update primary email
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					emailPending = rs.getString("emailPending");
					query = "UPDATE tableAccount SET email='" + emailPending + "' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
						rowsUpdated = stmt.executeUpdate(query);
						if(rowsUpdated != 1){
							//TODO log error
							System.out.println("Failed to delete row containing the verification number upon successfully changing primary email.");
						}
						return true;
					}
				}
			}
			else
				return false;
			
			return true;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
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
		return false;
	}
	
	public boolean createJobAdvertisement(String jobAdvertisementTitle, String jobDescription, 
									 	  String jobLocation, String contactInfo, 
									 	  String strTags){
		
		Connection conn = getConnection();	
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
					
			String query = 
				"INSERT INTO tableJobAd(title, description, location, contactInfo, tags) VALUES " + 
				"('" + jobAdvertisementTitle + "','" + jobDescription + "','" 
					 + jobLocation + "','" + contactInfo + "','" + strTags + "')";
			
			// if successful, 1 row should be inserted
			int rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted == 1){
				System.out.println("Checkpoint:" + jobAdvertisementTitle);
				return true;
			}
			else
				return false;					
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
		return false;		
	}
	
	/**
	 * Adds a password reset request entry into the DB.
	 * An entry contains a password request id, the primary email of the account, and expiry time of the request.
	 * @param email User's primary email address.
	 * @param uuid Unique password reset request id.
	 * @param expiryTimeForgetPasswordRequest Time before request expires.
	 * @return boolean indicating whether adding the password reset request was successful.
	 */
	public boolean addForgetPasswordRequest(String email, UUID uuid, long expiryTimeForgetPasswordRequest){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		int rowsInserted;
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			int idAccount = getIdAcccountFromEmail(email);
			// add entry to password reset table
			long expiryTime = currentTime + expiryTimeForgetPasswordRequest;			
			query = "INSERT INTO tablePasswordReset(idPasswordReset, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";			
			// if successful, 1 row should be inserted
			rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			return true;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
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
		return false;	
	}
	
	/**********************************************************************************************************************
	 * 											User LogIn FUNCTION
	 * @param name
	 * @param pw
	 * @return 1 if log in successfully
	 * 	       -1 otherwise
	 **********************************************************************************************************************/
	public int userLogIn(String name, String pw)
	{
		Connection conn = getConnection();	
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT UserID FROM UserTable"+
					   						  "WHERE UserName='"+name + "'" +
					   						  "&&Password ='md5(" + pw + ")'");
			if(rs.first()){
				
				System.out.println(name +"Logged in");
				stmt.close();
				return 1;
			}
			else{
				return -1;
			}
		}
		catch(SQLException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
		}
		return -1;
	}
	
	/***
	 * Returns the account ID accosiated with the input email address. 
	 * Returns -1 if email address doesn't exist in the account table.
	 * @param email Email address to be queried.
	 * @return Account ID associated with the input email address. Returns -1 if not found.
	 */
	public int getIdAcccountFromEmail(String email){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		int idAccount = -1;
		try {
			stmt = conn.createStatement();
			// get account id of the account just created
			query = "SELECT idAccount FROM tableAccount WHERE email='" + email + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first())
				idAccount = rs.getInt("idAccount");
			return idAccount;
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	            //TODO log "Cannot close ResultSet"
	        	System.out.println("Cannot close ResultSet : " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
		return idAccount;
	}
	

	/************************************************************************************************************
	 * 									SessionKey Generator FUNCTION
	 * ? => Should we bind it to user log-in
	 * return: sessionKey or null
	 * TODO sync DB name
	 *************************************************************************************************************/


	public String generateSession(String name, String pw)
	{
		Connection conn = getConnection();	
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT UserID FROM UserTable"+
					   						  "WHERE UserName='"+name + "'" +
					   						  "&&Password ='md5(" + pw + ")'");
			if(rs.first()){
				
				System.out.println(name +"Logged in");
				stmt.close();
				try{
					stmt.executeUpdate("UPDATE UserTable set sessionKey="+"where UserName= '"+name +"'");
				}
				catch(SQLException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(rs.first()){//TODO how to get sessionkey
					System.out.println("sessionKey Generated:"+"");
				}
//				return 1;
			}
			else{
//				return -1;
			}
		}
		catch(SQLException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	

	/************************************************************************************************************
	 * 									USER LOG-OUT FUNCTION
	 * return: T/F
	 * TODO sync DB name
	 *************************************************************************************************************/
	public boolean userLogout(String SessionKey){
		Connection conn=getConnection();
		Statement stmt=null;
		try
		{
			stmt=conn.createStatement();
			stmt.executeUpdate("UPDATE UserTable" +
								 "SET SessionKey="+ null +
								 ", ExpireTime=" + Calendar.getInstance().getTimeInMillis() +
								 "WHERE SessionKey='" + SessionKey+"'");
			System.out.println("User Logged Out");
			return true;
			
		}
		catch (SQLException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("There is a problem when logging out");
		}
		return false;
	}
	
	
}
	
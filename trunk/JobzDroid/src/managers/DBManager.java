package managers;
import java.sql.*;
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
			dbConn = DriverManager.getConnection("jdbc:mysql://192.168.0.192:3306/jobzdroid", "root", "cs319CS#!(");
	//		dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jobzdroid", "web", "somepw");
		}
		catch(Exception e){
			//TODO: log error
			System.out.println("Error creating DB connection : " + e.getMessage());
		}		
		return dbConn;
	}
	
	public DBManager() {}
	
	/***
	 * Checks whether the given primary email address is unique.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean checkEmailUnique(String email) {
		Connection conn = getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String query = "SELECT idAccount FROM TableAccount " + "WHERE Email='" + email + "';"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			
			// check if ResultSet is empty  
			if (!rs.next())
				return true;
			else
				return false;
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
			String query = "INSERT INTO TableAccount(Email, Password, Type, Status, dateTimeCreated) VALUES " + 
	  		"('" + email + "',md5('" + password + "'),'" + accountType + "','" + "pending" + "','" + currentTime + "');";			
			// if successful, 1 row should be inserted
			int rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			
			// get account id of the account just created
			query = "SELECT idAccount FROM TableAccount WHERE email='" + email + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first())
				idAccount = rs.getInt("idAccount");
			else
				return false;
						
			// add entry to email verification table
			long expiryTime = currentTime + expiryTimeEmailRegistration;			
			query = "INSERT INTO TableEmailVerification(idEmailVerification, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";			
			// if successful, 1 row should be inserted
			rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			// add entry to user profile table
			if(accountType.equals("searcher")){
				query = "INSERT INTO TableProfileSearcher(idAccount, name) VALUES " + 
		  		"('" + idAccount + "','" + name + "');";
				rowsInserted = stmt.executeUpdate(query);
				if (rowsInserted != 1)
					return false;
			}
			else if(accountType.equals("poster")){
				
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
	 * @param verificationNumber A UUID linked to an account id in TableEmailVerification
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
			query = "SELECT idAccount, expiryTime FROM TableEmailVerification WHERE idEmailVerification='" + 
	  		verificationNumber + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			// if valid, then check expiry time of verification number
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then activate account
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					query = "UPDATE TableAccount SET status='active' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from TableEmailVerification
						query = "DELETE FROM TableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
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
	 * The new email address is stored in TableEmailVerification when user requests for the email change.
	 * The verification number is created and linked to user's account when user requests to change primary email, and deleted after it is used to change the email.
	 * @param verificationNumber A UUID in TableEmailVerification which is linked to an account ID that requested for a primary email change
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
			query = "SELECT idAccount, expiryTime, emailPending FROM TableEmailVerification WHERE idEmailVerification='" + 
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
					query = "UPDATE TableAccount SET email='" + emailPending + "' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from TableEmailVerification
						query = "DELETE FROM TableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
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
				"INSERT INTO tablejobadvertisement(title, description, location, contactInfo, tags) VALUES " + 
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
	 * User LogIn Function
	 * @param name
	 * @param pw
	 * @return 1 if log in successfully
	 * 	       -1 otherwise
	 */
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
	
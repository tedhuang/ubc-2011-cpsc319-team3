package managers;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
			dbConn = DriverManager.getConnection("jdbc:mysql://70.79.38.90/jobzdroid", "root", "cpsc410");
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
					
			// update account table
			String query = "INSERT INTO TableAccount(Email, Password, Type, Status, dateTimeCreated) VALUES " + 
	  		"('" + email + "','" + password + "','" + accountType + "','" + "Pending" + "','" + currentTime + "');";			
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
			}
			else if(accountType.equals("poster")){
				
			}

			System.out.println("aaa");
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

}
	
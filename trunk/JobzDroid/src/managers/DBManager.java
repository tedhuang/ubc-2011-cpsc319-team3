package managers;

import java.security.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import classes.JobAdvertisement;
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
	
	// if the previous session key expired but is within 30 min, renew it
	final long sessionRenewTime	 = 15 * 60 * 1000;
	// session expires in 6 hours
	final long sessionExpireTime = 1 * 60 * 60 * 1000;
	
	public DBManager() {}
	
	
	
/*****************************************************************
 * 	Helper Methods												 *
 *****************************************************************/
	
	//Parse user input into SQL acceptable format
	private String checkInputFormat(String toBeChecked){
		
		String corrected;
		corrected = toBeChecked.replace("\'", "\\\'");
		corrected = toBeChecked.replace("\"", "\\\"");
		corrected = toBeChecked.replace(";", "");
		corrected = toBeChecked.replace("{", "");
		corrected = toBeChecked.replace("}", "");
		corrected = toBeChecked.replace("<", "");
		corrected = toBeChecked.replace(">", "");
		corrected = toBeChecked.replace("^", "");
		
		return corrected;		
	}
	
	
	
	
	
	
/*********************************************************************
 * DB Functions  													 *
 *********************************************************************/
	
	
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
	
	
	
	
	/**
	 * Creates a new job advertisement entry in the database with the given values
	 * @param jobAdvertisementTitle
	 * @param jobDescription
	 * @param jobLocation
	 * @param contactInfo
	 * @param strTags
	 * @return idJobAd
	 */
	public int createJobAdvertisement(String jobAdvertisementTitle, String jobDescription, 
									 	  String jobLocation, String contactInfo, int educationRequirement,
									 	  String strTags, long expiryDate, long startingDate,
									 	  long datePosted){
		
		Connection conn = getConnection();	
		Statement stmt = null;
		int idJobAd = -1;
		
		
		try {
			stmt = conn.createStatement();
			
			checkInputFormat( jobAdvertisementTitle );
			checkInputFormat( jobDescription );
			checkInputFormat( jobLocation );
			checkInputFormat( contactInfo );
			checkInputFormat( strTags );
			
			String query = 
				"INSERT INTO tableJobAd(title, description, expiryDate, dateStarting, datePosted, location, contactInfo, educationRequired, tags ) " +
				"VALUES " + "('" 
					+ jobAdvertisementTitle + "','" 
					+ jobDescription + "','" 
					+ expiryDate + "','" 
					+ startingDate + "','" 
					+ datePosted + "','"
					+ jobLocation + "','" 
					+ contactInfo + "','" 
					+ educationRequirement + "','"
					+ strTags + 
				"')";
			
			// if successful, 1 row should be inserted
			System.out.println("New Job Ad Query:" + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if (rowsInserted == 1){
				System.out.println("New JobAd Creation success (DB)");
			}
			else{
				stmt.close();
				return -1;
			}
			
			query = "SELECT idJobAd FROM tableJobAd WHERE " + " title='" + jobAdvertisementTitle + "'"; 
			ResultSet result = stmt.executeQuery(query);
			
			if( result.first() )
			{
				idJobAd = result.getInt("idJobAd");
				System.out.println("idJobAd: " + idJobAd);
			}
			else
			{
				System.out.println("Error: result.first() is false ");
			}
			
		}
		catch (SQLException e) {
			//TODO log SQL exception
			System.out.println("SQL exception : " + e.getMessage());
			idJobAd = -1;
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
	    
		return idJobAd;		
	}
	
	

	public ArrayList<JobAdvertisement> searchJobAdvertisement(String title, String location, String tags){
		
		ArrayList<JobAdvertisement> jobAdList = new ArrayList<JobAdvertisement>();
		String totalQuery;
		String titleQuery;
		String locationQuery;
		String tagsQuery;
		
		Connection conn = getConnection();	
		Statement stmt = null;
		
		try {
			
			stmt = conn.createStatement();
			
			if(title == ""){
				titleQuery = "";
			}
			else
			{
				titleQuery = "AND title = (SELECT title FROM UserTable WHERE title = '" + title + "')";
			}
			
			totalQuery = "SELECT * FROM tableJobAd "
					+ "WHERE title LIKE '" + title + "%' " 
					+ " AND Category LIKE '" + titleQuery;
			
			
			System.out.println(totalQuery);
			boolean success = stmt.execute(totalQuery);
			
			ResultSet result = stmt.getResultSet();
			
			while( result.next() ) {
				JobAdvertisement temp = new JobAdvertisement();
				
				temp.jobAdTitle = result.getString("title");
				temp.jobAdId = result.getInt("idJobAd");
				temp.expiryDate = result.getString("ExpiryDate");
				temp.creationDate = result.getString("CreationDate");
				temp.ownerID = result.getInt("OwnerID");
				temp.numberOfViews = result.getInt("numberOfViews");
				temp.jobAdDescription = result.getString("description");
				temp.location = result.getString("location");
				temp.status = result.getString("status");
				temp.tags = result.getString("tags");
				
				
				jobAdList.add( temp );
				
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
	
	
	public JobAdvertisement getJobAdById(int jobId){
		
		
		return null;
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
	
/**<<<<<<< .mine
//	/**********************************************************************************************************************
//	 * 											User LogIn FUNCTION
//	 * @param name
//	 * @param pw
//	 * @return 1 if log in successfully
//	 * 	       -1 otherwise
//	 **********************************************************************************************************************/
//	public int userLogIn(String name, String pw)
//	{
//		Connection conn = getConnection();	
//		Statement stmt = null;
//		try{
//			stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery( "SELECT UserID FROM UserTable"+
//					   						  "WHERE UserName='"+name + "'" +
//					   						  "&&Password ='md5(" + pw + ")'");
//			if(rs.first()){
//				
//				System.out.println(name +"Logged in");
//				stmt.close();
//				return 1;
//			}
//			else{
//				return -1;
//			}
//		}
//		catch(SQLException e) {
//				//TODO Auto-generated catch block
//				e.printStackTrace();
//		}
//		return -1;
//	}
//========	

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
					   						  "&&Password =md5('" + pw + "')");
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
	
//>>>>>>> .r71
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
	
/***************************************************************************************************************************************
 * 									SessionKey Generator FUNCTIONS
 * 
 * TODO sync DB name
 *
 * should return a new unique session key for the account
 * return null for failure
***************************************************************************************************************************************/
	private String md5(String input){
        String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] md5 = algorithm.digest();
            String tmp = "";
            for (int i = 0; i < md5.length; i++) {
                tmp = (Integer.toHexString(0xFF & md5[i]));
                if (tmp.length() == 1) {
                    res += "0" + tmp;
                } else {
                    res += tmp;
                }
            }
        } catch (NoSuchAlgorithmException ex) {}
        return res;
    }
	
	public String startSession(String email, String pw){
		
		int idAccount= -1;
		Connection conn = getConnection();	
		Statement stmt = null;
		// md5 the password
		String md5PW=md5(pw);
		
		try{
			// retrieve the account ID from login information
			stmt = conn.createStatement();
			System.out.println("check email:" + email +"password" + pw);
			ResultSet rs = stmt.executeQuery( "SELECT idAccount FROM tableAccount "+
					   						  "WHERE email='"+ email + "' " +
					   						  "AND password ='"+md5PW+"'");//TODO original md5 not working
			if(rs.first()){
				
				System.out.println( email +" Logged in");
				idAccount = rs.getInt("idAccount");
				System.out.println(idAccount);
				stmt.close();
				

			}
			else{
				// Error Handling if no id matches login info
				
				return null;
			}
			
			//cleanSessionKeyByID returns rows clean out of DB, don't need to check right now
			cleanSessionKeyByID( idAccount );
			
			String sessKey=registerSessionKey(idAccount);
			if(sessKey==null)
			{
				//TODO make error statement
				return null;
			}
			else
			{
				return sessKey;
			}
		}//ENDOF TRY
		catch(SQLException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	
	private String registerSessionKey( int idAccount ) {
		Connection conn = getConnection();	
		Statement stmt = null;
		try{
			//wipe previous sessionKey associated with the account
			stmt = conn.createStatement();

			UUID uuid = UUID.randomUUID();
			String sessionKey = uuid.toString();
			int rowsInserted=stmt.executeUpdate("INSERT INTO tableSession (sessionKey, idAccount, expiryTime) VALUES " +
										"('" + sessionKey + "','" + idAccount + "','" + 
										(Calendar.getInstance().getTimeInMillis() + sessionExpireTime) + "')");
				
//			if( rs.rowInserted() ) {
			if(rowsInserted==1){	// if success, return session key
				stmt.close();
				return sessionKey;
			}
			else {
				System.out.println("There is a problem when generating the session Key");
				stmt.close();
			}
		}//ENDOF TRY
		catch(SQLException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	
	private int cleanSessionKeyByID( int idAccount ) {
		// cleanup other sessionKey associated with this idAccount 

		Connection conn = getConnection();	
		Statement stmt = null;
		
		int cleanUpCount = 0;
		try {
			stmt=conn.createStatement();
			cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
				 	   "WHERE idAccount='" + idAccount +"'" );
			if(cleanUpCount==0){//TODO add user-end response
				System.out.println("No other keys for account " + idAccount);
			}
			else{
				System.out.println( cleanUpCount + " keys were cleaned from DB");
			}
			stmt.close();
		}
		catch(SQLException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cleanUpCount;
	}
	
	private int cleanSessionKeyByKey( String sessionKey ) {
		// cleanup other sessionKey associated with this idAccount 

		Connection conn = getConnection();	
		Statement stmt = null;
		
		int cleanUpCount = 0;
		try {
			stmt=conn.createStatement();
			cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
				 	   "WHERE sessionKey='" + sessionKey +"'" );
			if(cleanUpCount==0){//TODO add user-end response
				System.out.println("No key matches " + sessionKey);
			}
			else{
				System.out.println( cleanUpCount + " keys were cleaned from DB");
			}
			stmt.close();
		}
		catch(SQLException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cleanUpCount;
	}
	// returns null if session is expired
//	public String checkSessionKey( String sessionKey ) {//HARRY's VERSION
//		int idAccount = -1;
//		long expiryTime = 0;
//		String curKey=null;
//		Connection conn = getConnection();	
//		Statement stmt = null;
//		try{
//			// retrieves idAccount
//			stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery( "SELECT * FROM tableSession "+
//					   						  "WHERE idAccount='"+ acctID +"'");
//			
//			if(rs.first()){//Check if session exists, get expiryTime to check it later
//				expiryTime = rs.getLong("expiryTime");
//				idAccount = rs.getInt("idAccount");
//				curKey = rs.getString("sessionKey");
//			}
//			else{
//				stmt.close();
//				return null;
//				//TODO Error Handling
//			}//ENDOF if session exists 
//			
//			stmt.close();
//			long currentTime = Calendar.getInstance().getTimeInMillis();
//			if( currentTime <= expiryTime) {// if session didn't expire, return the current key
//				return curKey;
//			}
//			else {//if the key is expired but within 30min
//				if( expiryTime <= currentTime + sessionRenewTime ) {
//					// renew user's sessionKey
//					return registerSessionKey( idAccount );
//				}
//				else {
//					// past renewal period, user must re-log in
//					rs = stmt.executeQuery( "DELETE FROM tableSession " +
//											"WHERE idAccount='" + idAccount +"'");
//					return null;
//				}
//			}
//		}//ENDOF TRY
//		catch(SQLException e) {
//				//TODO Auto-generated catch block
//				e.printStackTrace();
//		}
//		return null;
//	}
	
	public String checkSessionKey( String key ) { 
		Connection conn = getConnection();	
		Statement stmt = null;
		try{
			
			// retrieves idAccount
			int idAccount = -1;
			long expiryTime = 0;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM tableSession "+
					   						  "WHERE sessionKey='"+ key+"'");
			
			if(rs.first()){
				//get expiryTime to check it later
				expiryTime = rs.getLong("expiryTime");
				idAccount = rs.getInt("idAccount");
			}
			else{
				stmt.close();
				return null;
				// Error Handling
			}
			stmt.close();
			
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if( expiryTime >= currentTime ) {
				// if session didn't expire, return the current key
				return key;
				
			}
			else {//if the key is expired but within 30min
				//clean up the sessionKey
				cleanSessionKeyByKey( key );
				if( expiryTime <= currentTime + sessionRenewTime ) {
					// renew user's sessionKey
					return registerSessionKey( idAccount );
				}
				else {
					// past renewal period, user must re-log in
					//TODO delte old entry?
//					rs = stmt.executeQuery( "DELETE FROM tableSession " +
//							"WHERE idAccount='" + idAccount +"'");
					return null;
				}
				
			}
			
		}
		catch(SQLException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
/**********************************END OF SESSIONKEY GENERATION FUNCTIONS***************************************************************/


/****************************************************************************************************************************************
 * 									USER LOG-OUT FUNCTION
 * return: T/F
 * TODO sync DB name
 ****************************************************************************************************************************************/
	public boolean userLogout(String sessionKey){
		Connection conn=getConnection();
		Statement stmt=null;
		int done=-1; 
		
		try
		{
			//TODO same code as cleanSessionKeyByKey() merge code?
			stmt=conn.createStatement();
			
			
			done = stmt.executeUpdate("DELETE FROM tableSession " +
								 	   "WHERE sessionKey='" + sessionKey +"'" );
			if(done==0){//TODO add user-end response
				System.out.println("LOGOUT ROW DELETION:No Entry found!");
				return false;
			}
			else{
				System.out.println("User Logged Out");
				return true;
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("There is a problem when logging out");
		}
		return false;
	}
	
	/**
	 * Returns the account ID associated with the password reset request ID
	 * @param idPasswordReset Password reset ID contained in the link set to the user requesting a password reset.
	 * @return Account ID associated with the the password reset. Returns -1 if the password reset ID is invalid or expired.
	 */
	public int getIdAccountFromIdPasswordReset(String idPasswordReset){
		Connection conn = getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		long expiryTime, currentTime = Utility.getCurrentTime();
		int idAccount = -1;
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
		return -1;
	}
	
	/***
	 * Resets password
	 * @param idPasswordReset Unique password reset ID
	 * @param idAccount Account id
	 * @param newPassword The new password.
	 * @return boolean indicating whether the password reset was successful.
	 */
	public boolean resetPassword(String idPasswordReset, int idAccount, String newPassword){
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		int rowsUpdated;
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tablePasswordReset WHERE idPasswordReset='" + idPasswordReset + "';";
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated != 1){
				//TODO log error
				System.out.println("Failed to delete row in tablePasswordReset.");
				}
			query = "UPDATE tableAccount SET password=md5('" + newPassword + "') WHERE idAccount='" + idAccount + "';";
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated == 1)
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
}
	
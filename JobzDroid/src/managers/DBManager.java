package managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import classes.DBConnectionPool;
import classes.JobAdvertisement;
import classes.Session;
import classes.Utility;

public class DBManager {			
	private DBConnectionPool connectionPool;
	// singleton class constructor
	private static DBManager dbManagerInstance = null;
	
	protected DBManager() {
		// register jdbc driver
		try{
			Driver driver = (Driver) Class.forName(SystemManager.dbDriver).newInstance();
			DriverManager.registerDriver(driver);
		}
		catch(Exception e){
			Utility.getErrorLogger().severe("Failed to register JDBC driver: " + e.getMessage());
		}
		// create connection pool
		connectionPool = new DBConnectionPool
			(SystemManager.dbURL, SystemManager.dbUser, SystemManager.dbPassword, SystemManager.maxDBConnectionPoolSize);
	}
	
	public static DBManager getInstance() {
		if(dbManagerInstance == null) {
			dbManagerInstance = new DBManager();
		}
		return dbManagerInstance;
	}
	
	/***
	 * Gets an active connection from the connection pool.
	 * @return An active connection.
	 */
	public Connection getConnection(){
		return connectionPool.getConnection();
	}
	
	/***
	 * Frees and returns a connection to the connection pool.
	 */
	public void freeConnection(Connection connection){
		connectionPool.returnConnectionToPool(connection);
	}	
	
/*********************************************************************
 * Commonly used DB Functions in multiple classes												 *
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
		email = Utility.checkInputFormat(email);
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
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.getErrorLogger().severe("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
		return false; 
	}
	
	/***
	 * Returns the account ID associated with the input email address. 
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
		email = Utility.checkInputFormat(email);
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
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.getErrorLogger().severe("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
		return idAccount;
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
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.getErrorLogger().severe("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
		return -1;
	}
	
	/****************************************************************************************************/
	
	

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
		
		Connection conn = getConnection();	
		Statement stmt = null;
		
		keywords = Utility.checkInputFormat( keywords );
		location = Utility.checkInputFormat( location );
		education = Utility.checkInputFormat( education );
		//tags = Utility.checkInputFormat( tags );
		
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
			
//			if(title == "")
//				tagsQuery = "";
//			else
//				tagsQuery = "AND tags = (SELECT title FROM tableJobAd WHERE tags = '" + tags + "')";
			
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
				temp.location = result.getString("location");
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
	
	
	public JobAdvertisement getJobAdById(int jobId){
		
		
		return null;
	}

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
		
		name = Utility.checkInputFormat(name);
		pw = Utility.checkInputFormat(pw);
		try{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT UserID FROM UserTable"+
					   						  "WHERE UserName='"+name + "'" +
					   						  "&&Password = md5('" + pw + "')");
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
	
/***************************************************************************************************************************************
 * 									SessionKey Generator FUNCTIONS
 * 
 * TODO sync DB name
 *
 * should return a new unique session key for the account
 * return null for failure
***************************************************************************************************************************************/
	
	public Session startSession(String email, String pw){

		Connection conn = getConnection();	
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
			System.out.println("check email:" + email + "password" + pw);
			rs = stmt.executeQuery( "SELECT * FROM tableAccount "+
					   						  "WHERE email='"+ email + "' " +
					   						  "AND password = md5('" + pw + "')" );//TODO original md5 not working
			
			System.out.println( Utility.md5(pw) );
			if(rs.first()){
				
				System.out.println( email +" Logged in");
				
				idAccount	= rs.getInt("idAccount");
				accountType	= rs.getString("Type");
				
				newSession = new Session( idAccount, accountType );
				
				System.out.println( idAccount + " " + accountType );
				
			}
			else{
				//TODO Error Handling if no id matches login info
				Utility.getErrorLogger().info( "could not find matching email / password info" );
				return null;
			}
			
			//TODO maybe we can think about returning same session Key if not expired
			
			newSession = registerSessionKey( newSession );
			if(newSession.getKey()==null) {
				Utility.getErrorLogger().severe( "Failed to generate session key on request, returning null" );
				return null;
			}
			else {
				return newSession;
			}
		}//ENDOF TRY
		catch (SQLException e) {
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
		
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.getErrorLogger().severe("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
	    
		return null;
	}
	
	private Session registerSessionKey( Session session ) {
		Connection conn = getConnection();	
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
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
		
		// free DB objects

		if(rowsInserted==1){	// if success, return session key
			session.registerNewSessionKey( newSessionKey, newExpiryTime);
			return session;
		}
		else {
			Utility.getErrorLogger().severe("could not insert new session key into DB");
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
			
		}
		catch (SQLException e) {
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
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
		}
		catch (SQLException e) {
			Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
		finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
	    }
		
		return cleanUpCount;
	}
	
	public Session getSessionByKey( String key ) { 
		Connection conn = getConnection();	
		Statement stmt = null;
		ResultSet rs = null;
		key = Utility.checkInputFormat(key);
		
		try{
			
			// retrieves idAccount
//			int idAccount = -1;
//			long expiryTime = 0;
			Session currSession = null;
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM tableSession, tableAccount "+
					"idAccount.tableAccount=idAccount.tableSession" + 
					"WHERE sessionKey.tableSession='"+key+"' && " );
			
			if(rs.first()){
				//get expiryTime to check it later
//				expiryTime = rs.getLong("expiryTime");
//				idAccount = rs.getInt("idAccount");
				currSession = new Session( 	key,
											rs.getInt("idAccount"),
											rs.getString("type"),
											rs.getLong("expiryTime") );
			}
			else{
				stmt.close();
				return null;
				// Error Handling
			}
			stmt.close();
			
			long currentTime = Utility.getCurrentTime();
			if( currSession.getExpiryTime() >= currentTime ) {
				// if session didn't expire, return the current session
				return currSession;
				
			}
			else {//if the key is expired but within 30min
				if( currSession.getExpiryTime() <= currentTime + SystemManager.sessionRenewPeriodAfterExpiry ) {
					// renew user's sessionKey
					currSession = registerSessionKey(currSession);
				}
				else {
					// past renewal period, user must re-log in
					//TODO delte old entry?
					cleanSessionKeyByKey( key );
					return null;
				}
				
			}
		}
		catch (SQLException e) {
				Utility.getErrorLogger().severe("SQL exception: " + e.getMessage());
		}
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.getErrorLogger().severe("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.getErrorLogger().severe("Cannot close Statement: " + e.getMessage());
	        }
	        freeConnection(conn);
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
		sessionKey = Utility.checkInputFormat(sessionKey);
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

	
	
/*****MOVED TO SERVLET PROFILE - PROFILE FUNCTIONS **************************************/
	/* MOVED INTO SERVLETPROFILE
	 * Creates a profile for job poster
	 */
//	public boolean createProfilePoster(int accountID, String name, String secEmail,
//			String contactInfo, String selfDescription, String affiliation) {
//
//		Connection conn = getConnection();	
//		Statement stmt = null;
//		int idCheck = -1;
//		
//		
//		try {
//			stmt = conn.createStatement();
//			
//			
//			name = Utility.checkInputFormat( name );
//			secEmail = Utility.checkInputFormat( secEmail );
//			contactInfo = Utility.checkInputFormat( contactInfo );
//			selfDescription = Utility.checkInputFormat( selfDescription );
//			affiliation = Utility.checkInputFormat( affiliation );
//			
//			String query = 
//				"INSERT INTO tableProfilePoster(idAccount, name, secondaryEmail, contactInfo, selfDescription, affiliation ) " +
//				"VALUES " + "('" 
//					+ accountID + "','" 
//					+ name + "','" 
//					+ secEmail + "','" 
//					+ contactInfo + "','" 
//					+ selfDescription + "','"
//					+ affiliation + 
//				"')";
//			
//			// if successful, 1 row should be inserted
//			System.out.println("New PosterProfile Query:" + query);
//			int rowsInserted = stmt.executeUpdate(query);
//			
//			if (rowsInserted == 1){
//				System.out.println("New Profile Creation success (DB)");
//			}
//			else{
//				stmt.close();
//				return false;
//			}
//			
//			//Check if profile is created successfully
//			query = "SELECT accountID FROM tableProfilePoster WHERE " + " accountID='" + accountID + "'"; 
//			ResultSet result = stmt.executeQuery(query);
//			if( result.first() )
//			{
//				idCheck = result.getInt("accountID");
//				System.out.println("Profile Created in DB with accountID: " + idCheck);
//				return true;
//			}
//			else
//			{
//				System.out.println("Error: result.first() is false ");
//				return false;
//			}
//			
//		}
//		catch (SQLException e) {
//			//TODO log SQL exception
//			System.out.println("SQL exception : " + e.getMessage());
//
//		}
//		
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
//	    
//		return false;
//	}
//	
//	
//	/* MOVED TO SERVLETPROFILE
//	 * Creates a profile for job searcher
//	 */
//	public boolean createProfileSearcher(int accountID, String name, String secEmail,
//			String contactInfo, String selfDescription, String empPref,
//			int educationLevel) {
//
//		Connection conn = getConnection();	
//		Statement stmt = null;
//		int idCheck = -1;
//		
//		
//		try {
//			stmt = conn.createStatement();
//			
//			name = Utility.checkInputFormat( name );
//			secEmail = Utility.checkInputFormat( secEmail );
//			contactInfo = Utility.checkInputFormat( contactInfo );
//			selfDescription = Utility.checkInputFormat( selfDescription );
//
//			//TODO: include location
//			String query = 
//				"INSERT INTO tableProfileSearcher(idAccount, name, secondaryEmail, contactInfo, selfDescription, empPref, educationLevel) " +
//				"VALUES " + "('" 
//					+ accountID + "','" 
//					+ name + "','" 
//					+ secEmail + "','" 
//					+ contactInfo + "','" 
//					+ selfDescription + "','"
//					+ empPref + "','"
//					+ 
//				"')";
//			
//			// if successful, 1 row should be inserted
//			System.out.println("New SearcherProfile Query:" + query);
//			int rowsInserted = stmt.executeUpdate(query);
//			
//			if (rowsInserted == 1){
//				System.out.println("New Profile Creation success (DB)");
//			}
//			else{
//				stmt.close();
//				return false;
//			}
//			
//			//Check if profile is created successfully
//			query = "SELECT accountID FROM tableProfileSearcher WHERE " + " accountID='" + accountID + "'"; 
//			ResultSet result = stmt.executeQuery(query);
//			if( result.first() )
//			{
//				idCheck = result.getInt("accountID");
//				System.out.println("Profile Created in DB with accountID: " + idCheck);
//				return true;
//			}
//			else
//			{
//				System.out.println("Error: result.first() is false ");
//				return false;
//			}
//			
//		}
//		catch (SQLException e) {
//			//TODO log SQL exception
//			System.out.println("SQL exception : " + e.getMessage());
//
//		}
//		
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
//	    
//		return false;
//		
//	}
//
//	
//	/*
//	 * TODO: FINISH THIS
//	 * 
//	 */
//	public boolean editProfilePoster(int accountID, String name, String secEmail,
//			String contactInfo, String selfDescription, String affiliation){
//		
//		Connection conn = getConnection();	
//		Statement stmt = null;
//		
//		try {
//			stmt = conn.createStatement();
//			
//			
//			name = Utility.checkInputFormat( name );
//			secEmail = Utility.checkInputFormat( secEmail );
//			contactInfo = Utility.checkInputFormat( contactInfo );
//			selfDescription = Utility.checkInputFormat( selfDescription );
//			affiliation = Utility.checkInputFormat( affiliation );
//			
//			String query = 
//				"UPDATE tableProfilePoster(idAccount, name, secondaryEmail, contactInfo, selfDescription, affiliation ) " +
//				"VALUES " + "('" 
//					+ accountID + "','" 
//					+ name + "','" 
//					+ secEmail + "','" 
//					+ contactInfo + "','" 
//					+ selfDescription + "','"
//					+ affiliation + 
//				"')";
//			
//			// if successful, 1 row should be inserted
//			System.out.println("New PosterProfile Query:" + query);
//			stmt.executeUpdate(query);
//			//TODO: implement error check
//			
//		}
//		catch (SQLException e) {
//			//TODO log SQL exception
//			System.out.println("SQL exception : " + e.getMessage());
//
//		}
//		
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
//	    
//		return false;
//		
//	}
//	
//	public boolean editProfileSearcher(int accountID, String name, String secEmail,
//			String contactInfo, String selfDescription, String empPref,
//			int educationLevel){
//		
//	Connection conn = getConnection();	
//	Statement stmt = null;
//	
//	
//	try {
//		stmt = conn.createStatement();
//		
//		name = Utility.checkInputFormat( name );
//		secEmail = Utility.checkInputFormat( secEmail );
//		contactInfo = Utility.checkInputFormat( contactInfo );
//		selfDescription = Utility.checkInputFormat( selfDescription );
//
//		//TODO: include location
//		String query = 
//			"UPDATE tableProfileSearcher(idAccount, name, secondaryEmail, contactInfo, selfDescription, empPref, educationLevel) " +
//			"VALUES " + "('" 
//				+ accountID + "','" 
//				+ name + "','" 
//				+ secEmail + "','" 
//				+ contactInfo + "','" 
//				+ selfDescription + "','"
//				+ empPref + "','"
//				+ 
//			"')";
//		
//		// if successful, 1 row should be inserted
//		System.out.println("New SearcherProfile Query:" + query);
//		stmt.executeUpdate(query);
//		//TODO: implment error check
//		
//	}
//	catch (SQLException e) {
//		//TODO log SQL exception
//		System.out.println("SQL exception : " + e.getMessage());
//
//	}
//	
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
//		return false;
//	}
//	
//	
//
///************************ END OF PROFILE FUNCTIONS *********************************/
//


}
















	
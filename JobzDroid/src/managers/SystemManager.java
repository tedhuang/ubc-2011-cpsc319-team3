package managers;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import classes.Utility;

/***
 * Provides functions to execute automated tasks and loading system variables from configuration file.
 * Singleton class.
 */
public class SystemManager {
	/***
	 * Global system variables with their default values. Values are overwritten after loading the configuration file.
	 */
	public static String    serverBaseURL                           = "http://localhost:8080/JobzDroid/";
	
	public static long 		sessionRenewPeriodAfterExpiry 			= 30 * 60 * 1000;				// default 15 mins
	public static long 		expiryTimeSession 						= 1 * 60 * 60 * 1000; 			// default 1 hour
	public static long 		expiryTimeEmailVerification 			= 60 * 60 * 1000;				// default 60 minutes
	public static long 		expiryTimeForgetPasswordReset 			= 60 * 60 * 1000;  				// default 60 minutes
	public static long 		expiryTimePendingAccount 				= 60 * 60 * 1000;  				// default 60 minutes
	public static long 		timeBeforeRemovingExpiredInactiveJobAds = 7 * 24 * 60 * 60 * 1000;		// default 7 days
	public static boolean 	autoRemoveExpiredInactiveJobAds 		= true;							// default auto remove expired inactive job ads
	public static boolean   enableJobAdApproval						= true;							// default enable admin job ad approval
	public static long 		timeIntervalAutomatedTasks 				= 10 * 60 * 1000;				// default 10 minutes
	public static long		timeIntervalAutomatedDBWorldTasks		= 1* 60 * 60 * 1000;			// default 1 hour
	public static long      expiryTimeJobAdDefault					= 30 * 24 * 60 * 60 * 1000;		// default 1 month
	//TODO add maximum expiry time for each value
	
	// default string patterns
	public static String emailPattern 		= "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	public static String pwPattern 			= "^[_A-Za-z0-9-\\.]{5,15}$";
	public static String namePattern 		= "^[_A-Za-z0-9-\\.]+$";
	public static String phonePattern 		= "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
	public static String fileNamePattern 	= "^[_A-Za-z0-9-\\.]+\\.+[A-Za-z0-9]+$";
	
	// DB connection variables
	public static String dbDriver 	= "com.mysql.jdbc.Driver";
	public static String dbURL 	    = "jdbc:mysql://70.79.38.90:3306/dbjobzdroid"; //jdbc:mysql://www.db4free.net:3306/dbjobzdriod";
	public static String dbUser 	= "root";//"blitzcriegteam";
	public static String dbPassword	= "cs319CS#!(";//"cs319team3";
	
	
//	public static String dbURL 		= "jdbc:mysql://www.db4free.net:3306/dbjobzdriod";
//	public static String dbUser 	= "blitzcriegteam";
//	public static String dbPassword	= "cs319team3";
	
	public static int maxDBConnectionPoolSize = 0;									// 0 means no limit
	
	// system email variables
	public static String systemEmailAddress		= "jobzdroid@gmail.com";
	public static String systemEmailPw			= "cs319CS#!(";
	public static String systemEmailSMTPHost	= "smtp.gmail.com";
	public static String systemEmailPort		= "465";
	
	// dbworld email variables
	public static String dbWorldEmailAddress  	= "jobzdroidtestemail@gmail.com" ;
	public static String dbWorldEmailPw			= "cpsc319team3";
	
	// document and file system management variables
	public static String[] validFileExtensions
						= { ".doc", ".docx", ".pdf" , ".rtf", ".txt" }; //TODO check SRS for file extensions
	public static String documentDirectory		= "/JobzDroid/Documents/"; //TODO maybe change to default user path
	public final static long bytesInMB			=  1000 * 1000;
	public static long fileStorageSizeLimit		= 5 * bytesInMB; // limit is 5 Mb, base unit is byte
	
	/***********************************************************************************************************************/
	
	// singleton instance
	private DBManager dbManager;
	private static SystemManager systemManagerInstance = null;
	protected SystemManager() {
		dbManager = DBManager.getInstance();
	}	
	public static SystemManager getInstance() {
		if(systemManagerInstance == null)
			systemManagerInstance = new SystemManager();
		return systemManagerInstance;
	}

	/***
	 * Loads system variables from configuration file
	 * @param filename Path of the configuration file.
	 */
	public void loadConfigFile(String filename){
		Properties config = new Properties();		
		// load file
		try {
			config.load(new FileInputStream(filename));
	        } 
		catch (Exception ex) {
			// log error
			Utility.logError("Error loading configuration file: " + ex.getMessage() + ". Using default values instead.");
			return;
	    }
		// update system variables
		serverBaseURL       = config.getProperty("serverBaseURL");
		dbDriver			= config.getProperty("dbDriver");
		dbURL				= config.getProperty("dbURL");
		dbUser 				= config.getProperty("dbUser");
		dbPassword 			= config.getProperty("dbPassword");
		emailPattern 		= config.getProperty("emailPattern");
		pwPattern 			= config.getProperty("pwPattern");
		namePattern 		= config.getProperty("namePattern");	
		phonePattern 		= config.getProperty("phonePattern");
		systemEmailAddress 	= config.getProperty("systemEmailAddress");
		systemEmailPw 		= config.getProperty("systemEmailPw");
		systemEmailSMTPHost = config.getProperty("systemEmailSMTPHost");
		systemEmailPort 	= config.getProperty("systemEmailPort");
		dbWorldEmailAddress = config.getProperty("dbWorldEmailAddress");
		dbWorldEmailPw		= config.getProperty("dbWorldEmailPw");
		try{
			sessionRenewPeriodAfterExpiry 	= Long.parseLong(config.getProperty("sessionRenewPeriodAfterExpiry").trim()) * 60 * 1000;	// unit: minutes -> millisecond
			expiryTimeSession 				= Long.parseLong(config.getProperty("expiryTimeSession").trim()) * 60 * 1000;
			expiryTimeEmailVerification 	= Long.parseLong(config.getProperty("expiryTimeEmailVerification").trim()) * 60 * 1000;
			expiryTimeForgetPasswordReset 	= Long.parseLong(config.getProperty("expiryTimeForgetPasswordReset").trim()) * 60 * 1000;
			expiryTimePendingAccount 		= Long.parseLong(config.getProperty("expiryTimePendingAccount").trim()) * 60 * 1000;
			timeIntervalAutomatedTasks 		= Long.parseLong(config.getProperty("timeIntervalAutomatedTasks").trim()) * 60 * 1000;
			timeBeforeRemovingExpiredInactiveJobAds =
				Long.parseLong(config.getProperty("timeBeforeRemovingExpiredInactiveJobAds").trim()) * 24 * 3600 * 1000;			// unit: day -> millisecond
						
			if (Integer.parseInt(config.getProperty("autoRemoveExpiredInactiveJobAds").trim()) == 0)								// 0 = false, true otherwise
				autoRemoveExpiredInactiveJobAds = false;
			else
				autoRemoveExpiredInactiveJobAds = true;		
			
			if (Integer.parseInt(config.getProperty("enableJobAdApproval").trim()) == 0)											// 0 = false, true otherwise
				enableJobAdApproval = false;
			else
				enableJobAdApproval = true;	
			
			maxDBConnectionPoolSize = Integer.parseInt(config.getProperty("maxDBConnectionPoolSize").trim());
		}
		catch(NumberFormatException e){
			// log error
			Utility.logError("Error parsing configuration file due to number format exception: " + e.getMessage() + ". Some settings will be using the default values.");
		}
	}
	
	/***
	 * Removes all entries inside email verification table with expiry time < current time.
	 */
	public void removeExpiredEmailVerifications(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tableEmailVerification WHERE expiryTime<'" + currentTime + "';";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}
	
	/***
	 * Removes all entries inside password reset table with expiry time < current time.
	 */
	public void removeExpiredPwResetRequests(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tablePasswordReset WHERE expiryTime<'" + currentTime + "';";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}
	
	/***
	 * Removes all entries inside session key table with (expiry time + sessionRenewPeriodAfterExpiry) < current time 
	 * (session keys that have passed the latest possible time to renew).
	 */
	public void removeExpiredSessionKeys(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tableSession WHERE expiryTime<'" + (currentTime + SystemManager.sessionRenewPeriodAfterExpiry) + "';";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}
	
	/***
	 * Removes all entries inside account table with status “pending” AND dateTimeCreated < (current time - expiryTimePendingAccount).
	 */
	public void removeExpiredPendingAccounts(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tableAccount WHERE dateTimeCreated<'" + (currentTime - expiryTimePendingAccount) + "'&& status='pending'" + ";";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}
	
	/***
	 * Removes all entries inside job ads table with status “inactive” AND (expiry time < current time).
	 */
	public void removeExpiredInactiveJobAds(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tableJobAd WHERE expiryDate<'" + currentTime + "'&& status='inactive'" + ";";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}

	/***
	 * Updates all entries with status NOT “inactive” AND (expiry time < current time) inside job ads table,
	 *  to status “inactive” and expiry time = (currentTime + timeBeforeRemovingExpiredInactiveJobAds)
	 */
	public void makeInactiveExpiredJobAds(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		String query = "";
		long currentTime = Utility.getCurrentTime();
		long newExpiryTime = currentTime + SystemManager.timeBeforeRemovingExpiredInactiveJobAds;
		try {
			stmt = conn.createStatement();
			query = "UPDATE tableJobAd SET status='inactive', expiryDate='" + newExpiryTime + "' WHERE status!='inactive' && expiryDate<'" + currentTime + "';";
			stmt.executeUpdate(query);
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
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
	}	
}

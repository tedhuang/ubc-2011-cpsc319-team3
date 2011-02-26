package managers;

import java.io.FileInputStream;
import java.util.Properties;

/***
 * Provides functions to execute automated tasks and loading system variables from configuration file.
 * Singleton class.
 */
public class SystemManager {
	/***
	 * Global system variables with their default values. Values are overwritten after loading the configuration file.
	 */
	public static long sessionRenewPeriodAfterExpiry = 15 * 60 * 1000;				// default 15 mins
	public static long expiryTimeSession = 1 * 60 * 60 * 1000; 						// default 1 hour
	public static long expiryTimeEmailVerification = 60 * 60 * 1000;				// default 60 minutes
	public static long expiryTimeForgetPasswordReset = 60 * 60 * 1000;  			// default 60 minutes
	public static long timeBeforeRemovingExpiredInactiveJobAds = 7 * 24 * 60 * 60 * 1000;	// default 7 days
	public static boolean autoRemoveExpiredInactiveJobAds = true;					// default auto remove expired inactive job ads
	public static long timeIntervalAutomatedTasks = 10 * 60 * 1000;					// default 10 minutes
	
	// default string patterns
	public static String emailPattern = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	public static String pwPattern = "^[_A-Za-z0-9-\\.]{5,15}$";
	public static String namePattern = "^[_A-Za-z0-9-\\.]+$";
	
	// DB connection variables
	public static String dbURL = "jdbc:mysql://www.db4free.net:3306/dbjobzdriod";
	public static String dbUser = "blitzcriegteam";
	public static String dbPassword = "cs319team3";
	
	/***********************************************************************************************************************/
	
	// singleton instance
	private static SystemManager systemManagerInstance = null;
	protected SystemManager() {}	
	public static SystemManager getInstance() {
		if(systemManagerInstance == null) {
			systemManagerInstance = new SystemManager();
		}
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
			//TODO log error
			System.out.println("Error loading configuration file: " + ex.getMessage());
			return;
	    }
		// update system variables
		dbURL = config.getProperty("dbURL");
		dbUser = config.getProperty("dbUser");
		dbPassword = config.getProperty("dbPassword");
		emailPattern = config.getProperty("emailPattern");
		pwPattern = config.getProperty("pwPattern");
		namePattern = config.getProperty("namePattern");		
		try{
			sessionRenewPeriodAfterExpiry = Long.parseLong(config.getProperty("sessionRenewPeriodAfterExpiry").trim()) * 60 * 1000;	// unit: minutes -> millisecond
			expiryTimeSession = Long.parseLong(config.getProperty("expiryTimeSession").trim()) * 60 * 1000;
			expiryTimeEmailVerification = Long.parseLong(config.getProperty("expiryTimeEmailVerification").trim()) * 60 * 1000;
			expiryTimeForgetPasswordReset = Long.parseLong(config.getProperty("expiryTimeForgetPasswordReset").trim()) * 60 * 1000;
			timeIntervalAutomatedTasks = Long.parseLong(config.getProperty("timeIntervalAutomatedTasks").trim()) * 60 * 1000;
			timeBeforeRemovingExpiredInactiveJobAds =
				Long.parseLong(config.getProperty("timeBeforeRemovingExpiredInactiveJobAds").trim()) * 24 * 3600 * 1000;			// unit: day -> millisecond
			
			if (Integer.parseInt(config.getProperty("autoRemoveExpiredInactiveJobAds").trim()) == 0)								// 0 = false, true otherwise
				autoRemoveExpiredInactiveJobAds = false;
			else
				autoRemoveExpiredInactiveJobAds = true;
		}
		catch(NumberFormatException e){
			//TODO log error
			System.out.println("Error parsing configuration file due to number format exception: " + e.getMessage());
		}
	}
	
	/***
	 * Removes all expired em
	 */
	public void removeExpiredEmailVerifications(){
		
	}
	
	public void removeExpiredPwResetRequests(){
		
	}
	
	public void removeExpiredSessionKeys(){
		
	}
	
	public void removeExpiredInactiveJobAds(){
		
	}
	
	public void makeInactiveExpiredJobAds(){
		
	}
}

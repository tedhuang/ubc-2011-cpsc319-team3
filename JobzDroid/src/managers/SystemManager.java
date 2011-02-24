package managers;
/***
 * Provides functions to execute automated tasks and loading system variables from configuration file.	
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
	
	public SystemManager() {}
	
	public void loadConfigFile(String filename){
		
	}
	
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

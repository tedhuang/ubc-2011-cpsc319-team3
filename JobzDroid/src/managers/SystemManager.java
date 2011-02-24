package managers;

public class SystemManager {
	
	public static long sessionRenewTime = 15 * 60 * 1000;							// default 15 mins
	public static long sessionExpireTime = 1 * 60 * 60 * 1000; 						// default 1 hour
	public static long expiryTimeEmailVerification = 60 * 60 * 1000;				// default 60 minutes
	public static long expiryTimeForgetPasswordReset = 60 * 60 * 1000;  			// default 60 minutes
	public static long autoDeleteExpiredEntriesTimer = 5 * 60 * 1000;				// default 5 minutes
	
	// default string patterns
	public static String emailPattern = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	public static String pwPattern = "^[_A-Za-z0-9-\\.]{5,15}$";
	public static String namePattern = "^[_A-Za-z0-9-\\.]+$";
	
	public SystemManager() {}
	
	public void deleteExpiredEntries(){
		
	}
}

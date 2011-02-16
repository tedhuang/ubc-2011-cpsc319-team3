package managers;


import java.util.UUID;

/***
 * Manages account related actions, and calls DBManager to perform DB operations
 */
public class AccountManager {
	private DBManager dbManager;
	
	private final long EXPIRY_TIME_EMAIL_VERIFICATION = 60 * 60 * 1000; // 60 minutes
	
	public AccountManager(){
		dbManager = new DBManager();
	}
	
	/***
	 * Checks whether the given primary email address is unique.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public boolean checkEmailUnique(String email){
		return dbManager.checkEmailUnique(email);
	}
	
	/***
	 * Creates a new account with the given email, password, account type and person/company name
	 * with a uniquely generated verification number used for email verification.
	 * New accounts open with "Pending" status.
	 * @param email Primary email
	 * @param password User password
	 * @param accType Account type
	 * @param name Person/Company name
	 * @return boolean indicating whether account was successfully created
	 */
	public boolean createAccount(String email, String password, String accType, String name, UUID uuid){
		return dbManager.createAccount(email, password, accType, name, uuid, EXPIRY_TIME_EMAIL_VERIFICATION);
	}
	
}
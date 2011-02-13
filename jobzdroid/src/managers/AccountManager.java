package managers;

/***
 * Manages account related actions, and calls DBManager to perform DB operations
 */
public class AccountManager {
	private DBManager dbManager;
	
	public AccountManager(){
		dbManager = new DBManager();
	}
	
	/***
	 * Checks whether the given primary email address is unique
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean isUniqueEmailAddr(String email){
		return dbManager.isUniqueEmailAddr(email);
	}
	
	/***
	 * Creates a new account with the given email, password, account type and person/company name
	 * @param email Primary email
	 * @param password User password
	 * @param accType Account type
	 * @param name Person/Company name
	 * @return boolean indicating whether account was successfully created
	 */
	public boolean createAccount(String email, String password, String accType, String name){
		return dbManager.createAccount(email, password, accType, name);
	}
	
}

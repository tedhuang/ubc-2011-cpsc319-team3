package classes;

public class Session {

	String	sessionKey;
	int		idAccount;
	String	accountType;
	long	expiryTime;
	
	public Session( String key, int id, String type, long expiry ) {
		sessionKey	= key;
		idAccount	= id;
		accountType	= type;
		expiryTime	= expiry;
	}
	
	public Session( int id, String type ) {
		idAccount	= id;
		accountType	= type;
	}
	
	public String getKey() {
		return sessionKey;
	}
	
	public int getIdAccount() {
		return idAccount;
	}
	
	public String getAccountType() {
		return accountType;
	}
	
	public long getExpiryTime() {
		return expiryTime;
	}
	
	public void registerNewSessionKey( String key, long expiry ) {
		sessionKey = key;
		expiryTime = expiry;
	}
	
}

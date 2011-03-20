package classes;

public class Session {

	private String sessionKey;
	private int	idAccount;
	private String accountType;
	private long expiryTime;
	
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
	
	
	public boolean checkPrivilege( String authorizedUserType0 ) {
		if( accountType.equalsIgnoreCase(authorizedUserType0) ) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkPrivilege( String authorizedUserType0, String authorizedUserType1 ) {
		if( accountType.equalsIgnoreCase(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1) ) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkPrivilege( String authorizedUserType0, String authorizedUserType1, String authorizedUserType2 ) {
		if( accountType.equalsIgnoreCase(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1)
				|| accountType.equalsIgnoreCase(authorizedUserType2) ) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkPrivilege( String authorizedUserType0, String authorizedUserType1, String authorizedUserType2,
									String authorizedUserType3 ) {
		if( accountType.equals(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1)
				|| accountType.equalsIgnoreCase(authorizedUserType2) 
				|| accountType.equalsIgnoreCase(authorizedUserType3) ) {
			return true;
		}
		
		return false;
	}
	
	public void updateExpiryTime(long newExpiryTime ) {
		expiryTime = newExpiryTime;
	}
}

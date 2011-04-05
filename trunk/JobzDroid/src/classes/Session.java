package classes;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
	
	
	public boolean checkPrivilege( HttpServletResponse response, String authorizedUserType0 ) throws IOException {
		if( this != null
				|| accountType.equalsIgnoreCase(authorizedUserType0) ) {
			return true;
		}
		
		if( this == null ) {
			response.sendRedirect("sessionExpired.html");
		}
		else {
			response.sendRedirect("error.html");
		}
		
		return false;
	}
	
	public boolean checkPrivilege( HttpServletResponse response, String authorizedUserType0, String authorizedUserType1 ) throws IOException {
		if( this != null
				|| accountType.equalsIgnoreCase(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1) ) {
			return true;
		}
		
		//TODO add redirect html page
		if( this == null ) {
			response.sendRedirect("sessionExpired.html");
		}
		else {
			response.sendRedirect("error.html");
		}
		
		return false;
	}
	
	public boolean checkPrivilege( HttpServletResponse response, String authorizedUserType0, String authorizedUserType1,
									String authorizedUserType2 ) throws IOException {
		if( this != null
				|| accountType.equalsIgnoreCase(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1)
				|| accountType.equalsIgnoreCase(authorizedUserType2) ) {
			return true;
		}
		
		if( this == null ) {
			response.sendRedirect("sessionExpired.html");
		}
		else {
			response.sendRedirect("error.html");
		}
		
		return false;
	}
	
	public boolean checkPrivilege( HttpServletResponse response, String authorizedUserType0, String authorizedUserType1, 
									String authorizedUserType2,	String authorizedUserType3 ) throws IOException {
		if( this != null
				|| accountType.equals(authorizedUserType0) 
				|| accountType.equalsIgnoreCase(authorizedUserType1)
				|| accountType.equalsIgnoreCase(authorizedUserType2) 
				|| accountType.equalsIgnoreCase(authorizedUserType3) ) {
			return true;
		}
		
		if( this == null ) {
			response.sendRedirect("sessionExpired.html");
		}
		else {
			response.sendRedirect("error.html");
		}
		
		return false;
	}
	
	public void updateExpiryTime(long newExpiryTime ) {
		expiryTime = newExpiryTime;
	}
}

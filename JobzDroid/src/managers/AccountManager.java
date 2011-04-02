package managers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import servlets.ServletDocument;

import classes.Account;
import classes.Session;
import classes.Utility;
/***
 * Account functions
 */

public class AccountManager {
	DBManager dbManager;
	public AccountManager(){
		dbManager = DBManager.getInstance();
	}
	
	/***
	 * Checks whether the given primary email address already exists.
	 * @param email email address to be checked
	 * @return boolean indicating whether the email address is unique
	 */
	public boolean checkEmailExists(String email) {
		System.out.println("Inside DBManager - checkEmailExists");
		Connection conn = dbManager.getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		email = Utility.checkInputFormat(email);
		try {
			stmt = conn.createStatement();
			String query = "SELECT idAccount FROM tableAccount " + "WHERE email='" + email + "';"; 			
			stmt.executeQuery(query);
			rs = stmt.getResultSet();			
			// check if ResultSet is empty  
			if (!rs.next())
				return false;
			else
				return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false; 
	}
	
	/***
	 * Returns the account object with the input email address. 
	 * Returns null if email address doesn't exist in the account table.
	 * @param email Email address to be queried.
	 * @return Account object with the input email address. Returns null if not found.
	 */
	public Account getAccountFromEmail(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		Account account = null;
		int idAccount;
		String secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		
		email = Utility.checkInputFormat(email);
		try {
			stmt = conn.createStatement();
			query = "SELECT idAccount, secondaryEmail, type, status, dateTimeCreated, password FROM tableAccount WHERE email='" + email + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first()){
				idAccount = rs.getInt("idAccount");
				if(rs.getObject("secondaryEmail") == null)
					secondaryEmail = "";
				else
					secondaryEmail = rs.getString("secondaryEmail");
				type = rs.getString("type");
				status = rs.getString("status");
				dateTimeCreated = rs.getLong("dateTimeCreated");
				passwordMd5 = rs.getString("password");
				account = new Account(idAccount, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
				return account;
			}
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return account;
	}
	
	/***
	 * Returns the account object with the input account ID. 
	 * Returns null if email address doesn't exist in the account table.
	 * @param email Account ID to be queried.
	 * @return Account object with the input account ID. Returns null if not found.
	 */
	public Account getAccountFromId(int idAccount){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		Account account = null;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		
		try {
			stmt = conn.createStatement();
			query = "SELECT email, secondaryEmail, type, status, dateTimeCreated, password FROM tableAccount WHERE idAccount='" + idAccount + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			if(rs.first()){
				email = rs.getString("email");
				if(rs.getObject("secondaryEmail") == null)
					secondaryEmail = "";
				else
					secondaryEmail = rs.getString("secondaryEmail");
				type = rs.getString("type");
				status = rs.getString("status");
				dateTimeCreated = rs.getLong("dateTimeCreated");
				passwordMd5 = rs.getString("password");
				account = new Account(idAccount, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
				return account;
			}
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return account;
	}
	
	/***
	 * Returns an array list of all accounts of type searcher and poster
	 * @return An array list of all accounts of type searcher and poster.
	 */
	public ArrayList<Account> getSearcherPosterAccounts(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		ArrayList<Account> accounts = new ArrayList<Account>();
		int idNews;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		try {			
			stmt = conn.createStatement();
			query = "SELECT * FROM tableAccount WHERE type='searcher' OR type='poster';";            
			rs = stmt.executeQuery(query);
		    while (rs.next()) {
		    	idNews = rs.getInt("idAccount");
		    	email = rs.getString("email");
		    	secondaryEmail = rs.getString("secondaryEmail");
		    	type = rs.getString("type");
		    	status = rs.getString("status");
		    	dateTimeCreated = rs.getLong("dateTimeCreated");
		    	passwordMd5 = rs.getString("password");
		        Account acc = new Account(idNews, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
		        accounts.add(acc);
		    }
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return accounts;
	}
	
	/***
	 * Returns an array list of all accounts of type searcher and poster
	 * @return An array list of all accounts of type searcher and poster.
	 */
	public ArrayList<Account> getAdminAccounts(){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		
		ArrayList<Account> accounts = new ArrayList<Account>();
		int idNews;
		String email, secondaryEmail, type, status, passwordMd5;
		long dateTimeCreated;
		try {			
			stmt = conn.createStatement();
			query = "SELECT * FROM tableAccount WHERE type='admin';";            
			rs = stmt.executeQuery(query);
		    while (rs.next()) {
		    	idNews = rs.getInt("idAccount");
		    	email = rs.getString("email");
		    	secondaryEmail = rs.getString("secondaryEmail");
		    	type = rs.getString("type");
		    	status = rs.getString("status");
		    	dateTimeCreated = rs.getLong("dateTimeCreated");
		    	passwordMd5 = rs.getString("password");
		        Account acc = new Account(idNews, email, secondaryEmail, type, status, dateTimeCreated, passwordMd5);
		        accounts.add(acc);
		    }
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return accounts;
	}
	
	/**
	 * Returns the account ID associated with the password reset request ID
	 * @param idPasswordReset Password reset ID contained in the link set to the user requesting a password reset.
	 * @return Account ID associated with the the password reset. Returns -1 if the password reset ID is invalid or expired.
	 */
	public int getIdAccountFromIdPasswordReset(String idPasswordReset){
		Connection conn = dbManager.getConnection();	
		ResultSet rs = null;
		Statement stmt = null;
		long expiryTime, currentTime = Utility.getCurrentTime();
		int idAccount = -1;
		System.out.println(idPasswordReset);
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
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return -1;
	}
	
	/***
	 * Deletes an account with given account name.
	 * @param email Account name to delete.
	 * @return boolean indicating whether the deletion was successful.
	 */
	public boolean deleteAccount(String email){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String query = "DELETE FROM tableAccount WHERE email='" + email + "';";
            
			int rowsInserted = stmt.executeUpdate(query);
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
			return false;	
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
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
	 * Creates a new account with the given email, password, account type and person/company name
	 * with a uniquely generated verification number used for email verification.
	 * New accounts open with "Pending" status.
	 * @param email Primary email
	 * @param password User password
	 * @param accountType Account type
	 * @param name Person/Company name 
	 * @param uuid randomly generated unique verification number for email
	 * @param latitude 
	 * @param longitude 
	 * @param address 
	 * @param expiryTimeEmailVerification Time before the registration verification expires
	 * @return boolean indicating whether account was successfully created
	 */
	public boolean createAccount(String email, String secondaryEmail, String password, String accountType, 
								  String name, String phone, UUID uuid, String description, int eduLevel, 
								  String startingDate, String address, String longitude, String latitude,
								  String empPrefFT, String empPrefPT, String empPrefIn) {
		System.out.println("Creating Account..");
		
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		email = Utility.checkInputFormat(email);
		password = Utility.checkInputFormat(password);
		accountType = Utility.checkInputFormat(accountType);
		name = Utility.checkInputFormat(name);
		address = Utility.checkInputFormat(address);
		
		if(secondaryEmail != null)
			secondaryEmail = Utility.checkInputFormat(secondaryEmail);
		
		if(description != null){
			description = Utility.checkInputFormat(description);
			description = Utility.processLineBreaksWhiteSpaces(description);
		}
		if(phone != null)
			phone = Utility.checkInputFormat(phone);
		if(address != null)
			address = Utility.checkInputFormat(address);
		
		try {
			long currentTime = Utility.getCurrentTime();
			int idAccount = -1;
			long startingDateLong = -1;
			// update account table, and set account status to pending
            String query = "INSERT INTO tableAccount(email, secondaryEmail, password, type, status, dateTimeCreated)" +
            		" VALUES(?,?,md5(?),?,'pending',?);";
            pst = conn.prepareStatement(query);
            pst.setString(1, email);
            if(secondaryEmail == null)
            	pst.setNull(2, java.sql.Types.VARCHAR);
            else
            	pst.setString(2, secondaryEmail);
            pst.setString(3, password);
            pst.setString(4, accountType);
            pst.setLong(5, currentTime);
            
            System.out.println(query);
			int rowsInserted = pst.executeUpdate();
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			// get account id of the account just created
			Account acc = getAccountFromEmail(email);
			if(acc != null)
				idAccount = acc.getIdAccount();
			if(idAccount == -1)
				return false;
						
			// add entry to email verification table
			long expiryTime = currentTime + SystemManager.expiryTimeEmailVerification;			
			query = "INSERT INTO tableEmailVerification(idEmailVerification, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";	
			pst = conn.prepareStatement(query);
			
			// if successful, 1 row should be inserted
            System.out.println(query);
			rowsInserted = pst.executeUpdate(query);
			if (rowsInserted != 1)
				return false;

			
			// add entry to user profile table
			if(accountType.equals("searcher")){
				// update profile table
				if(startingDate != null){
					startingDateLong = Utility.dateStringToLong(startingDate);
					if(startingDateLong == -1)
						return false;
				}
				query = "INSERT INTO tableProfileSearcher(idAccount, name, phone, selfDescription, educationLevel, startingDate) VALUES " + 
		  		"(?,?,?,?,?,?);";
	            pst = conn.prepareStatement(query);
	            pst.setInt(1, idAccount);
	            pst.setString(2, name);
	            if(phone == null)
	            	pst.setNull(3, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(3, phone);
	            if(description == null)
	            	pst.setNull(4, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(4, description);
	            
	            pst.setInt(5, eduLevel);
	            
	            if(startingDate == null)
	            	pst.setNull(6, java.sql.Types.BIGINT);
	            else
	            	pst.setLong(6, startingDateLong);
	            
	            System.out.println(query);
				rowsInserted = pst.executeUpdate();
				
				if (rowsInserted != 1)
					return false;
				
				// update employment Preference table
				if(empPrefFT.equals("true") )
					empPrefFT = "1"; 
				else
					empPrefFT = "0";
				
				if(empPrefPT.equals("true") )
					empPrefPT = "1"; 
				else
					empPrefPT = "0";
				
				if(empPrefIn.equals("true") )
					empPrefIn = "1"; 
				else
					empPrefIn = "0";

				query = 
					"INSERT INTO tableSearcherEmpPref(idAccount, partTime, fullTime, internship) " + 
					"VALUES ('"
						+ idAccount + "','"
						+ empPrefPT + "','"
						+ empPrefFT + "','"
						+ empPrefIn + 
					"');";
	            System.out.println(query);
	            
				if( pst.executeUpdate(query) != 1 ){ //Error Check
					System.out.println("Error: Update Query Failed");
					return false;
				}

			}
			else if(accountType.equals("poster")){
				query = "INSERT INTO tableProfilePoster(idAccount, name, phone, selfDescription) VALUES " + 
		  		"(?,?,?,?);";
	            pst = conn.prepareStatement(query);
	            pst.setInt(1, idAccount);
	            pst.setString(2, name);
	            
	            if(phone == null)
	            	pst.setNull(3, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(3, phone);
	            
	            if(description == null)
	            	pst.setNull(4, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(4, description);
	            
	            System.out.println(query);
				rowsInserted = pst.executeUpdate();
				if (rowsInserted != 1)
					return false;
			}
			
			
			
			//add entry to location table
			System.out.println("address: " + address);
			if( address != null){
				if( !insertAddress(idAccount, address, longitude, latitude) )
					return false;
			}
			else 
				return true;

			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (pst != null)
	                pst.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close PreparedStatement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false;		
	}
	
	/**
	 * Adds a password reset request entry into the DB.
	 * An entry contains a password request id, the primary email of the account, and expiry time of the request.
	 * @param email User's primary email address.
	 * @param uuid Unique password reset request id.
	 * @param expiryTimeForgetPasswordRequest Time before request expires.
	 * @return boolean indicating whether adding the password reset request was successful.
	 */
	public boolean addForgetPasswordRequest(String email, UUID uuid, long expiryTimeForgetPasswordRequest){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		email = Utility.checkInputFormat(email);
		String query = "";
		int rowsInserted;
		int idAccount = -1;
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			Account acc = getAccountFromEmail(email);
			if(acc != null)
				idAccount = acc.getIdAccount();
			if(idAccount == -1)
				return false;
			// add entry to password reset table
			long expiryTime = currentTime + expiryTimeForgetPasswordRequest;			
			query = "INSERT INTO tablePasswordReset(idPasswordReset, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";			
			// if successful, 1 row should be inserted
			System.out.println(query);
			rowsInserted = stmt.executeUpdate(query);
			if (rowsInserted != 1)
				return false;
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false;	
	}
	
	/***
	 * Updates account status from pending to active if the given verification number is valid.
	 * The verification number is created and linked to an account upon account creation, and deleted after it is used to activate the account.
	 * @param verificationNumber A UUID linked to an account id in tableEmailVerification
	 * @return boolean indicating whether the account activation was successful
	 */
	public boolean activateAccount(String verificationNumber){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		verificationNumber = Utility.checkInputFormat(verificationNumber);
		String query = "";
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			long expiryTime;
			int idAccount, rowsUpdated;
					
			// check if verification number is valid	
			query = "SELECT idAccount, expiryTime FROM tableEmailVerification WHERE idEmailVerification='" + 
	  		verificationNumber + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();
			// if valid, then check expiry time of verification number
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then activate account
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					query = "UPDATE tableAccount SET status='active' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					System.out.println(query);
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
						System.out.println(query);
						rowsUpdated = stmt.executeUpdate(query);
						if(rowsUpdated != 1)
							Utility.logError("Failed to delete row containing the verification number upon successful account activation.");
						return true;
					}
				}
			}
			else
				return false;
			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false;	
	}
	
	/***
	 * Identifies the user with the session key, and then adds an entry inside email verification table with the verification number
	 * and the pending email.
	 * @param sessionKey Session key of the user requesting for email change.
	 * @param newEmail New email address to change to.
	 * @return boolean indicating whether adding the email change was successful
	 */
	public boolean addEmailChangeRequest(String sessionKey, String newEmail, UUID uuid){
		System.out.println("Inside ServletAccount: addEmailChangeRequest");
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		sessionKey = Utility.checkInputFormat(sessionKey);
		newEmail = Utility.checkInputFormat(newEmail);
		long currentTime = Utility.getCurrentTime();
		
		try {
			// get accout id from sessionKey
			int idAccount;
			Session session = getSessionByKey(sessionKey);
			if(session != null)
				idAccount = session.getIdAccount();
			else
				return false;
			// add entry to email verification table
			long expiryTime = currentTime + SystemManager.expiryTimeEmailVerification;	
            String query = "INSERT INTO tableEmailVerification(idEmailVerification, idAccount, expiryTime, emailPending)" +
    		" VALUES(?,?,?,?);";
            
            
            pst = conn.prepareStatement(query);
		    pst.setString(1, uuid.toString());
		    pst.setInt(2, idAccount);
		    pst.setLong(3, expiryTime);
		    pst.setString(4, newEmail);
			// if successful, 1 row should be inserted
		    
		    System.out.println(query + "values: uuid=" + uuid.toString() + " idAccount=" + idAccount + " expiryTime=" + expiryTime + " newEmail=" + newEmail);
		    
			int rowsInserted = pst.executeUpdate();
			
			if (rowsInserted != 1){
				System.out.println("addEmailChangeRequest failed");
				return false;
			}
			System.out.println("addEmailChangeRequest success");
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (pst != null)
	                pst.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close PreparedStatement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false;		
	}
	
	/***
	 * Updates account primary email if the given verification number is valid.
	 * The new email address is stored in tableEmailVerification when user requests for the email change.
	 * The verification number is created and linked to user's account when user requests to change primary email, and deleted after it is used to change the email.
	 * @param verificationNumber A UUID in tableEmailVerification which is linked to an account ID that requested for a primary email change
	 * @return boolean indicating whether changing the primary email was successful
	 */
	public boolean verifyChangePrimaryEmail(String verificationNumber){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		verificationNumber = Utility.checkInputFormat(verificationNumber);
		String query = "";
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			long expiryTime;
			int idAccount, rowsUpdated;
			String emailPending;
			
			// check if verification number is valid	
			query = "SELECT idAccount, expiryTime, emailPending FROM tableEmailVerification WHERE idEmailVerification='" + 
	  		verificationNumber + "';";
			stmt.executeQuery(query);
			rs = stmt.getResultSet();			
			
			// if valid, then check expiry time of verification number
			if(rs.first()){
				expiryTime = rs.getLong("expiryTime");
				// if not expired, then update primary email
				if( currentTime < expiryTime){
					idAccount = rs.getInt("idAccount");
					emailPending = rs.getString("emailPending");
					query = "UPDATE tableAccount SET email='" + emailPending + "' WHERE idAccount='" + idAccount + "';";
					// if successful, 1 row should be updated
					System.out.println(query);
					rowsUpdated = stmt.executeUpdate(query);					
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
						System.out.println(query);
						rowsUpdated = stmt.executeUpdate(query);
						if(rowsUpdated != 1){
							Utility.logError("Failed to delete row containing the verification number " +
									verificationNumber + "in tableEmailVerification upon successfully changing primary email.");
						}
						return true;
					}					
				}				
			}
			else
				return false;			
			return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		return false;
	}
	
	/***
	 * Resets password and deletes associated entry from tablePasswordReset
	 * @param idPasswordReset Unique password reset ID
	 * @param idAccount Account id
	 * @param newPassword The new password.
	 * @return boolean indicating whether the password reset was successful.
	 */
	public boolean resetPassword(String idPasswordReset, int idAccount, String newPassword){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		int rowsUpdated;
		idPasswordReset = Utility.checkInputFormat(idPasswordReset);
		newPassword = Utility.checkInputFormat(newPassword);
		try {
			stmt = conn.createStatement();
			query = "DELETE FROM tablePasswordReset WHERE idPasswordReset='" + idPasswordReset + "';";
			System.out.println(query);
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated != 1){
				Utility.logError("Failed to delete entry from tablePasswordReset while resetting password for account" +
						"ID:" + idAccount +".");
				}
			query = "UPDATE tableAccount SET password=md5('" + newPassword + "') WHERE idAccount='" + idAccount + "';";
			System.out.println(query);
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated == 1)
				return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	    return false;
	}	
	
	/***
	 * Resets password and deletes associated entry from tablePasswordReset
	 * @param idAccount Account id
	 * @param newPassword The new password.
	 * @return boolean indicating whether the password change was successful.
	 */
	public boolean changePassword(int idAccount, String newPassword){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		int rowsUpdated;
		newPassword = Utility.checkInputFormat(newPassword);
		try {
			stmt = conn.createStatement();
			query = "UPDATE tableAccount SET password=md5('" + newPassword + "') WHERE idAccount='" + idAccount + "';";
			System.out.println(query);
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated == 1)
				return true;
		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		// free DB objects
	    finally {
	        try {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close Statement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
	    return false;
	}
	
	private boolean insertAddress(int idAccount, String address, String longitude, String latitude) {		
		boolean isSuccessful = false;
		
		Connection conn = dbManager.getConnection();	
		Statement stmt = null;

		try{
			stmt = conn.createStatement();
			
			String query = 
				"INSERT INTO tableLocationProfile(idAccount, location, latitude, longitude) " + 
				"VALUES ('"
					+ idAccount + "','"
					+ address + "','"
					+ longitude + "','"
					+ latitude + 
				"')";
			
			// if successful, 1 row should be inserted
			System.out.println("New location insert query: " + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if (rowsInserted == 1){
				System.out.println("Insert Profile address success");
				isSuccessful = true;				
			}

		}
		catch (SQLException e) {
			Utility.logError("SQL exception: " + e.getMessage());
		}
		
		// free DB objects
	    finally {
	        try {
	            if (stmt != null)
	            	stmt.close();
	        }
	        catch (Exception e){
	        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
	        }
	        try{
	            if (conn != null)
	            	conn.close();
	        }
	        catch (Exception e) {
	        	Utility.logError("Cannot close PreparedStatement: " + e.getMessage());
	        }
	        dbManager.freeConnection(conn);
	    }
		
		return isSuccessful;
	}
	
	/***************************************************************************************************************************************
	 * 									SessionKey Generator FUNCTIONS
	 * should return a new unique session key for the account
	 * return null for failure
	***************************************************************************************************************************************/
		
		public Session startSession(String email, String pw){

			Connection conn = dbManager.getConnection();	
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
				System.out.println("check email:" + email + " password" + pw);
				rs = stmt.executeQuery( "SELECT * FROM tableAccount "+
						   						  "WHERE email='"+ email + "' " +
						   						  "AND password = md5('" + pw + "')" +
				  								  "AND status = 'active';");
				
				System.out.println( Utility.md5(pw) );
				if(rs.first()){
					
					System.out.println( email +" Logged in");
					
					idAccount	= rs.getInt("idAccount");
					accountType	= rs.getString("Type");
					
					newSession = new Session( idAccount, accountType );
					
					System.out.println( idAccount + " " + accountType );
					
				}
				else{
					System.out.println( "could not find matching email / password info" );
					return null;
				}
				
				newSession = registerSessionKey( newSession );
				if(newSession.getKey()==null) {
					Utility.logError( "Failed to generate session key on request, returning null" );
					return null;
				}
				else {
					return newSession;
				}
			}//ENDOF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
			
			// free DB objects
		    finally {
		        try {
		            if (rs != null)
		                rs.close();
		        }
		        catch (Exception e){
		        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
		        }
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
		    
			return null;
		}
		
		private Session registerSessionKey( Session session ) {
			Connection conn = dbManager.getConnection();	
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
				Utility.logError("SQL exception: " + e.getMessage());
			}
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
			
			// free DB objects

			if(rowsInserted==1){	// if success, return session key
				session.registerNewSessionKey( newSessionKey, newExpiryTime);
				return session;
			}
			else {
				Utility.logError("could not insert new session key into DB");
			}
			
			return null;
		}
		
		private Session extendSessionExpiryTime( Session session ) {

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;		

			
			long newExpiryTime = session.getExpiryTime() + SystemManager.expiryTimeSession;
			
			try{
				// retrieve the account ID from login information
				stmt = conn.createStatement();
				int rowsUpdated = stmt.executeUpdate( "UPDATE tableSession SET expiryTime='" + newExpiryTime + "' " + 
											"WHERE sessionKey='"+ session.getKey() + "';");
				
				if( rowsUpdated != 1 ) {
					return null;
				}
				
				session.updateExpiryTime(newExpiryTime);
				return session;

			}//ENDOF TRY
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
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
		    
			return null;
		}
		
		private int cleanSessionKeyByID( int idAccount ) {
			// cleanup other sessionKey associated with this idAccount 

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			int cleanUpCount = 0;
			try {
				
				stmt=conn.createStatement();
				cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
					 	   "WHERE idAccount='" + idAccount +"'" );
				if(cleanUpCount==0){
					System.out.println("No other keys for account " + idAccount);
				}
				else{
					System.out.println( cleanUpCount + " keys were cleaned from DB");
				}
				
			}
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
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
			
			return cleanUpCount;
		}
		
		private int cleanSessionKeyByKey( String sessionKey ) {
			// cleanup other sessionKey associated with this idAccount 

			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			
			int cleanUpCount = 0;
			try {
				stmt=conn.createStatement();
				cleanUpCount = stmt.executeUpdate("DELETE FROM tableSession " +
					 	   "WHERE sessionKey='" + sessionKey +"'" );
				if(cleanUpCount==0){
					System.out.println("No key matches " + sessionKey);
				}
				else{
					System.out.println( cleanUpCount + " keys were cleaned from DB");
				}
			}
			catch (SQLException e) {
				Utility.logError("SQL exception: " + e.getMessage());
			}
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
			
			return cleanUpCount;
		}
		
		public Session getSessionByKey( String key ) { 
			Connection conn = dbManager.getConnection();	
			Statement stmt = null;
			ResultSet rs = null;
			if(key == null)
				return null;
			key = Utility.checkInputFormat(key);
			
			try{
				Session currSession = null;
				stmt = conn.createStatement();
				String queryCode =
					"SELECT * FROM tableSession LEFT OUTER JOIN tableAccount USING (idAccount) " + 
					"WHERE sessionKey='" + key +"' " +
					"AND status='active';";
				
				
				rs = stmt.executeQuery( queryCode );
				
				if(rs.first()){
					currSession = new Session( 	key,
												rs.getInt("idAccount"),
												rs.getString("type"),
												rs.getLong("expiryTime") );
				}
				else{
					return null;
				}
				
				long currentTime = Utility.getCurrentTime();
				if( currSession.getExpiryTime() >= currentTime ) {
					// if session didn't expire, return the current session
					return currSession;
					
				}
				else {//if the key is expired but within 30min
					if( currSession.getExpiryTime() + SystemManager.sessionRenewPeriodAfterExpiry >= currentTime  ) {
						// renew user's sessionKey
						currSession = extendSessionExpiryTime(currSession);
					}
					else {
						// past renewal period, user must re-log in
						cleanSessionKeyByKey( key );
						return null;
					}
					
				}
			}
			catch (SQLException e) {
					Utility.logError("SQL exception: " + e.getMessage());
			}
		    finally {
		        try {
		            if (rs != null)
		                rs.close();
		        }
		        catch (Exception e){
		        	Utility.logError("Cannot close ResultSet: " + e.getMessage());
		        }
		        try{
		            if (stmt != null)
		                stmt.close();
		        }
		        catch (Exception e) {
		        	Utility.logError("Cannot close Statement: " + e.getMessage());
		        }
		        dbManager.freeConnection(conn);
		    }
		    
			return null;
		}
		
		
	/**********************************END OF SESSIONKEY GENERATION FUNCTIONS***************************************************************/


	/****************************************************************************************************************************************
	 * 									USER LOG-OUT FUNCTION
	 * return: T/F
	 ****************************************************************************************************************************************/
		public boolean userLogout(String sessionKey){
			Connection conn = dbManager.getConnection();
			Statement stmt = null;
			int done = -1; 
			sessionKey = Utility.checkInputFormat(sessionKey);
			try
			{
				stmt=conn.createStatement();
				
				
				done = stmt.executeUpdate("DELETE FROM tableSession " +
									 	   "WHERE sessionKey='" + sessionKey +"'" );
				if(done==0){
					System.out.println("LOGOUT ROW DELETION:No Entry found!");
					return false;
				}
				else{
					System.out.println("User Logged Out");
					return true;
				}
				
			}
			catch (SQLException e) {
				Utility.logError("There is a problem when logging out");
			}
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
			return false;
		}
		
		/**
		 * Deletes all of the specified user's docs.
		 * @param idAcct Account ID of the user.
		 * @return Whether the delete was successful or not.
		 */
		public boolean wipeUserDocuments(int idAcct) {
			File[] fileArry = ServletDocument.getUserFiles( idAcct );
			
			boolean success = true;
			
			File parentDir = null;
			for( File fileThis : fileArry ) {
				fileThis.getParentFile();
				parentDir = fileThis.getParentFile();
				if( !fileThis.delete()) {
					success = false;
				}
			}
			
			if ( parentDir != null ) {
				if( !parentDir.delete() ) {
					success = false;
				}
			}			
			return success;
		}
}

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Session;
import classes.Utility;

import managers.DBManager;
import managers.EmailManager;
import managers.SystemManager;

/**
 * Servlet implementation class ServletAccount
 * Handles all account related requests, including registration, log-in, forget password, email verification
 */
public class ServletAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EmailManager emailManager;	
	private DBManager dbManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAccount() {
        super();
		dbManager = DBManager.getInstance();
		emailManager = new EmailManager();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		register,
		activate,
		requestEmailChange,
		verifyEmailChange,
		requestForgetPassword,
		emailLinkForgetPassword,
		resetForgetPassword,
		requestForLogin,
		requestForLogout
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("action");
		// forward to error page if request is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(Exception e){
			forwardErrorPage(request, response);
			return;
		}
		
		switch( EnumAction.valueOf(action) ){
			// account registration
			case register:
				registerAccount(request, response);
				break;
			// verify email for account activation
			case activate:
				activateAccount(request, response);
				break;
			// request for a primary email change
			case requestEmailChange:
			
				break;				
			// verify email for changing primary email
			case verifyEmailChange:
				verifyEmailChange(request, response);
				break;				
			// requests a unique link to be sent to user's email to reset password
			case requestForgetPassword:
				requestForgetPassword(request, response);
				break;		
			// handles email link click from a forget password request
			case emailLinkForgetPassword:
				emailLinkForgetPassword(request, response);
				break;
			// reset password
			case resetForgetPassword:
				resetForgetPassword(request, response);
				break;
			case requestForLogin:
				loginReqTaker(request, response);
				break;
			
			case requestForLogout:
				logoutReqTaker(request, response);
				break;
				//dbManager.userLogout("request.getParameter("SessionKey").toString());
			
			default:
				forwardErrorPage(request, response);
				break;
		}
	}
	
	/***
	 * Performs server-side checks on user registration inputs. If successful, then calls db manager update account tables in DB.
	 * Finally calls the email manager to send a verification email to the new user, and sends result and message back to user.
	 */
	private void registerAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		boolean result = false;
		String message = "";
		boolean allGood = true;
		boolean accountCreated = false;
		UUID uuid = UUID.randomUUID();; // verification number
		// get request parameters
		String email = request.getParameter("email");
		String secondaryEmail = request.getParameter("secondaryEmail");
		String password = request.getParameter("password");
		String passwordRepeat = request.getParameter("passwordRepeat");
		String accountType = request.getParameter("accountType");
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		String description = request.getParameter("description");
		String startingDate = request.getParameter("startingDate");
		String empPref = request.getParameter("empPref");
		int eduLevel = -1;
		if( accountType.equals("searcher") ){
			try{
				eduLevel = Integer.parseInt(request.getParameter("eduLevel"));
			}
			catch(NumberFormatException e){
				message = "Bad education level input.";
				allGood = false;
			}
		}
		else{
			// no poster specific fields currently
		}
		// validate data
		if( email == null ){
			message = "Email address is required.";
			allGood = false;
		}
		else if( !Utility.validate(email, SystemManager.emailPattern) ){
			message = "Invalid email address.";
			allGood = false;
		}
		else if( secondaryEmail != null ){
			if( !Utility.validate(secondaryEmail, SystemManager.emailPattern) ){
				message = "Invalid secondary email address format.";
				allGood = false;
			}
		}
		else if( password == null ){
			message = "Password is required.";
			allGood = false;
		}	
		else if( !Utility.validate(password, SystemManager.pwPattern) ){
			message = "Invalid password format.";
			allGood = false;
		}	
		else if( passwordRepeat == null ){
			message = "Please repeat your password.";
			allGood = false;
		}	
		else if( !password.equals(passwordRepeat) ){
			message = "Passwords do not match.";
			allGood = false;
		}
		else if( !accountType.equals("searcher") && !accountType.equals("poster") ){
			message = "Invalid account type.";
			allGood = false;
		}
		else if( name == null ){
			allGood = false;
			message = "Name is required.";
		}
		else if( !Utility.validate(name, SystemManager.namePattern) ){
			allGood = false;
			message = "Invalid name.";
		}		
		else if( phone != null){
			if( !Utility.validate(phone, SystemManager.phonePattern) ){
				allGood = false;
				message = "Invalid phone number.";
			}
		}
		else if( accountType.equals("searcher")){
			if( eduLevel < 0 || eduLevel > 3 ){
				allGood = false;
				message = "Invalid education level.";
			}
		}
		
		// if info are all valid, then proceed to do DB updates
		if(allGood){
			// check if email is unique
			boolean isUnique = !dbManager.checkEmailExists(email);
			if(isUnique){
				accountCreated = createAccount(email, secondaryEmail, password, accountType, name, phone, uuid, 
						SystemManager.expiryTimeEmailVerification, address, description, eduLevel, startingDate, empPref);

				if(accountCreated){
					//send verification email to new user
					//TODO
				//	emailManager.sendAccountActivationEmail(email, name, uuid);
					emailManager.sendAccountActivationEmail("luolw123@hotmail.com", name, uuid);
					message = "Account creation successful! An email has been sent to your inbox, " +
							"please follow the instructions to activate your account within "
					+ (int)Math.floor(SystemManager.expiryTimeEmailVerification/(1000*60)) + " minutes.";
					result = true;
				}
				else
					message = "Failed to create account. Please try again later.";
			}
			else{
				message = "This email address has already been used. Please choose another one.";
			}			
		}		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Calls DB manager to activate account.
	 */
	private void activateAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String verificationNumber = request.getParameter("id");
		boolean accountActivated = activateAccount(verificationNumber);
		if(accountActivated){
			//TODO redirect to actual page
			PrintWriter out = response.getWriter();
		    out.println("Congratulations, your account has successfully been activated!");
		    out.close();
		}
		else
			forwardErrorPage(request, response);
	}
	
	/***
	 * Calls DB manager to verify email change
	 */
	private void verifyEmailChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String verificationNumber = request.getParameter("id");
		boolean emailChanged = verifyChangePrimaryEmail(verificationNumber);
		if(emailChanged){
			//TODO redirect to actual page
			PrintWriter out = response.getWriter();
		    out.println("Congratulations, your primary email has successfully been changed!");
		    out.close();
		}
		else
			forwardErrorPage(request, response);
	}
	

/**************************************************************************************************************************************
 * 								USER LOG IN/OUT Function
 * 
 * @param req
 * @param response
 * @return
 * @throws ServletException
 * @throws IOException
 **************************************************************************************************************************************/
	private void loginReqTaker(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		String pw = request.getParameter("password");
		System.out.println("user="+ email+ "Password="+ pw);
		Session currSession = dbManager.startSession(email, pw);
		if(currSession != null){
			// if login successful, return credential and sucess message
			// Write XML to response if DB has return message
			StringBuffer XMLResponse = new StringBuffer();	
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
			XMLResponse.append("\t<sessionKey>" + currSession.getKey() + "</sessionKey>\n");
//			XMLResponse.append("\t<userID>" + userID + "</userID>\n");
			
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
			
		}
		else{
			System.out.println("sessionKey is null");
			StringBuffer XMLResponse = new StringBuffer();	
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
			XMLResponse.append("\t<sessionKey>" + null + "</sessionKey>\n");
//			XMLResponse.append("\t<userID>" + null + "</userID>\n");
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
		}
		
	}
/***************************/
	private void logoutReqTaker(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		
		if(dbManager.userLogout(req.getParameter("sessKey").toString())){
			System.out.println("Logout Successfully");
		}
		else{
			System.out.println("Logout failed");
		}
		
	}
/**************************************************************************************************************************************/
	

	/***
	 * Calls DB manager to add a password request entry, sends an email containing the password reset link
	 * to the user, and then returns the results to the user in the response.
	 */
	private void requestForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		boolean result = false;
		String message = "";
		boolean emailExists = dbManager.checkEmailExists(email);
		if(emailExists){
			UUID uuid = UUID.randomUUID();
			boolean requestAdded = addForgetPasswordRequest(email, uuid, SystemManager.expiryTimeForgetPasswordReset);
			if(requestAdded){
				//send verification email to new user
				//TODO
			//	emailManager.sendAccountActivationEmail(email, name, uuid);
				emailManager.sendPasswordResetEmail("luolw123@hotmail.com", uuid);
				message = "An email has been sent to your mail box to reset your password. " +
					"Please follow the link in your email to reset your password within "
					+ (int)Math.floor(SystemManager.expiryTimeForgetPasswordReset/(1000*60)) + " minutes.";
				result = true;
			}
		}
		else{
			result = false;
			message = "The Email address does not exist.";
		}
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Handles email link click from a forget password request
	 */
	private void emailLinkForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		getServletConfig().getServletContext().getRequestDispatcher("/ResetForgetPassword.jsp").forward(request,response);
	}
	
	/***
	 * Final step of forget password procedure. Final validation of request and updates user password.
	 */
	private void resetForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// get request parameters
		String password = request.getParameter("password");
		String passwordRepeat = request.getParameter("passwordRepeat");
		String idPasswordReset = request.getParameter("idPasswordReset");
		
		String message = "";
		boolean result = false;
		
		// validate new password	
		if( !Utility.validate(password, SystemManager.pwPattern) )
			message = "Invalid password format.";
		else if( !password.equals(passwordRepeat) )
			message = "Passwords do not match.";
		else{
			int idAccount = dbManager.getIdAccountFromIdPasswordReset(idPasswordReset);
			if (idAccount == -1)
				message = "Invalid or expired request.";
			else{
				boolean updateSuccessful = resetPassword(idPasswordReset, idAccount, password);
				if(updateSuccessful){
					message = "Password change successful!";
					result = true;
				}
				else
					message = "An error has been encountered during the request.";
			}
		}
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	/***
	 * Forwards current request to error page.
	 */
	private void forwardErrorPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		getServletConfig().getServletContext().getRequestDispatcher("/error.html").forward(request,response);
	}
	
	/********************************* HELPER FUNCTIONS *************************************************/

	/***
	 * Creates a new account with the given email, password, account type and person/company name
	 * with a uniquely generated verification number used for email verification.
	 * New accounts open with "Pending" status.
	 * @param email Primary email
	 * @param password User password
	 * @param accountType Account type
	 * @param name Person/Company name 
	 * @param uuid randomly generated unique verification number for email
	 * @param expiryTimeEmailRegistration Time before the registration verification expires
	 * @return boolean indicating whether account was successfully created
	 */
	private boolean createAccount(String email, String secondaryEmail, String password, String accountType, String name, String phone, UUID uuid, 
			long expiryTimeEmailRegistration, String address, String description, int eduLevel, String startingDate, String empPref) {
		Connection conn = dbManager.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		email = Utility.checkInputFormat(email);
		password = Utility.checkInputFormat(password);
		accountType = Utility.checkInputFormat(accountType);
		name = Utility.checkInputFormat(name);
		if(secondaryEmail != null)
			secondaryEmail = Utility.checkInputFormat(secondaryEmail);
		if(address != null)
			address = Utility.checkInputFormat(address);
		if(description != null)
			description = Utility.checkInputFormat(description);
		if(phone != null)
			phone = Utility.checkInputFormat(phone);
		
		try {
			long currentTime = Utility.getCurrentTime();
			int idAccount;
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
            
			int rowsInserted = pst.executeUpdate();
			// if successful, 1 row should be inserted
			if (rowsInserted != 1)
				return false;
			
			// get account id of the account just created
			idAccount = dbManager.getIdAcccountFromEmail(email);
			if(idAccount == -1)
				return false;
						
			// add entry to email verification table
			long expiryTime = currentTime + expiryTimeEmailRegistration;			
			query = "INSERT INTO tableEmailVerification(idEmailVerification, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";	
			pst = conn.prepareStatement(query);
			// if successful, 1 row should be inserted
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
				query = "INSERT INTO tableProfileSearcher(idAccount, name, phone, selfDescription, educationLevel, address, startingDate) VALUES " + 
		  		"(?,?,?,?,?,?,?);";
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
	            
	            if(address == null)
	            	pst.setNull(6, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(6, address);
	            
	            if(startingDate == null)
	            	pst.setNull(7, java.sql.Types.BIGINT);
	            else
	            	pst.setLong(7, startingDateLong);
	            
				rowsInserted = pst.executeUpdate();
				if (rowsInserted != 1)
					return false;
				
				// update employment Preference table
				if(empPref != null){
					String prefs[] = empPref.split("_");
					String pref;
					for(int i = 0; i < prefs.length; i++){
						pref = prefs[i].trim();
						if(!pref.equals("")){
							query = "INSERT INTO tableSearcherEmpPref(idAccount, empPref) VALUES " + 
					  		"(?,?);";
				            pst = conn.prepareStatement(query);
				            pst.setInt(1, idAccount);
				            pst.setString(2, pref);
				            pst.executeUpdate();
						}
					}
				}
			}
			else if(accountType.equals("poster")){
				query = "INSERT INTO tableProfilePoster(idAccount, name, phone, selfDescription, address) VALUES " + 
		  		"(?,?,?,?,?);";
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
	            
	            if(address == null)
	            	pst.setNull(5, java.sql.Types.VARCHAR);
	            else
	            	pst.setString(5, address);
	            
				rowsInserted = pst.executeUpdate();
				if (rowsInserted != 1)
					return false;
			}
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
	private boolean addForgetPasswordRequest(String email, UUID uuid, long expiryTimeForgetPasswordRequest){
		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		email = Utility.checkInputFormat(email);
		String query = "";
		int rowsInserted;
		try {
			stmt = conn.createStatement();
			long currentTime = Utility.getCurrentTime();
			int idAccount = dbManager.getIdAcccountFromEmail(email);
			// add entry to password reset table
			long expiryTime = currentTime + expiryTimeForgetPasswordRequest;			
			query = "INSERT INTO tablePasswordReset(idPasswordReset, idAccount, expiryTime) VALUES " + 
	  		"('" + uuid + "','" + idAccount + "','" + expiryTime + "');";			
			// if successful, 1 row should be inserted
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
	private boolean activateAccount(String verificationNumber){
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
					rowsUpdated = stmt.executeUpdate(query);
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
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
	 * Updates account primary email if the given verification number is valid.
	 * The new email address is stored in tableEmailVerification when user requests for the email change.
	 * The verification number is created and linked to user's account when user requests to change primary email, and deleted after it is used to change the email.
	 * @param verificationNumber A UUID in tableEmailVerification which is linked to an account ID that requested for a primary email change
	 * @return boolean indicating whether changing the primary email was successful
	 */
	private boolean verifyChangePrimaryEmail(String verificationNumber){
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
					rowsUpdated = stmt.executeUpdate(query);					
					if (rowsUpdated != 1)
						return false;
					else {
						// finally, delete row containing the verification number from tableEmailVerification
						query = "DELETE FROM tableEmailVerification WHERE idEmailVerification='" + verificationNumber + "';";
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
	private boolean resetPassword(String idPasswordReset, int idAccount, String newPassword){
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
			rowsUpdated = stmt.executeUpdate(query);
			if(rowsUpdated != 1){
				Utility.logError("Failed to delete entry from tablePasswordReset while resetting password for account" +
						"ID:" + idAccount +".");
				}
			query = "UPDATE tableAccount SET password=md5('" + newPassword + "') WHERE idAccount='" + idAccount + "';";
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
}

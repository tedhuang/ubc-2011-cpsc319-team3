package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Account;
import classes.Session;
import classes.Utility;

import managers.AccountManager;
import managers.DBManager;
import managers.EmailManager;
import managers.SystemManager;

/**
 * Servlet implementation class ServletAccount
 * Handles all account related requests, including registration, log-in, forget password, email verification and so on.
 */
public class ServletAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EmailManager emailManager;	
	private DBManager dbManager;
	private AccountManager accManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAccount() {
        super();
		dbManager = DBManager.getInstance();
		emailManager = new EmailManager();
		accManager = new AccountManager();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		register,
		activate,
		requestEmailChange,
		requestSecondaryEmailChange,
		verifyEmailChange,
		requestForgetPassword,
		emailLinkForgetPassword,
		resetForgetPassword,
		requestForLogin,
		requestForLogout,
		requestPasswordChange
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
		
		// throw error if action is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(Exception e){
			throw new ServletException("Invalid account servlet action.");
		}
		
		String sessionKey = request.getParameter("sessionKey");
		Session session = accManager.getSessionByKey(sessionKey);

		boolean sessionCheck = true;
		
		switch( EnumAction.valueOf(action) ){
			// account registration
			case register:
				registerHandler(request, response);
				break;
			// verify email for account activation
			case activate:
				activateAccountHandler(request, response);
				break;
			// request for a primary email change
			case requestEmailChange:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				if(sessionCheck = session.checkPrivilege( "searcher", "poster") )
					requestEmailChangeHandler(request, response, session);
				break;
			//request for a secondary email change
			case requestSecondaryEmailChange:
				requestSecondaryEmailChangeHandler(request,response);
				break;
			// verify email for changing primary email
			case verifyEmailChange:
				verifyEmailChangeHandler(request, response);
				break;				
			// requests a unique link to be sent to user's email to reset password
			case requestForgetPassword:
				requestForgetPasswordHandler(request, response);
				break;		
			// handles email link click from a forget password request
			case emailLinkForgetPassword:
				emailLinkForgetPasswordHandler(request, response);
				break;
			// reset password
			case resetForgetPassword:
				resetForgetPasswordHandler(request, response);
				break;
			case requestForLogin:
				loginReqTaker(request, response);
				break;			
			case requestForLogout:
				logoutReqTaker(request, response);
				break;
			case requestPasswordChange:
				if( session == null ) {
					response.sendRedirect("sessionExpired.html");
					return;
				}
				if (sessionCheck = session.checkPrivilege( "searcher", "poster", "admin", "superAdmin") ) 
					requestChangePasswordHandler(request,response, session);

						
				break;
		}
		
		if( sessionCheck == false && session != null  ) {
			response.sendRedirect("error.html");
		}
		
	}
	
	/***
	 * Performs server-side checks on user registration inputs. If successful, then calls db manager update account tables in DB.
	 * Finally calls the email manager to send a verification email to the new user, and sends result and message back to user.
	 */
	private void registerHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		boolean result 		   = false;
		String message 		   = "";
		boolean allGood 	   = true;
		boolean accountCreated = false;
		UUID uuid = UUID.randomUUID();; // verification number
		
		// get request parameters
		String email 			= request.getParameter("email");
		String secondaryEmail 	= request.getParameter("secondaryEmail");
		String password 		= request.getParameter("password");
		String passwordRepeat 	= request.getParameter("passwordRepeat");
		String accountType		= request.getParameter("accountType");
		String name				= request.getParameter("name");

		String phone 			= request.getParameter("phone");
		String description 		= request.getParameter("description");
		String startingDate 	= request.getParameter("startingDate");
		
		String address 			= request.getParameter("address");
		String latitude			= request.getParameter("latitude");
		String longitude		= request.getParameter("longitude");
		
		String empPrefFT 		= request.getParameter("empPrefFT"); 
		String empPrefPT 		= request.getParameter("empPrefPT");
		String empPrefIn 		= request.getParameter("empPrefIn");
		
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
			email = Utility.checkInputFormat(email);
			boolean isUnique = !accManager.checkEmailExists(email);
			if(isUnique){
				accountCreated = accManager.createAccount(email, secondaryEmail, password, accountType, name, phone, uuid,
						description, eduLevel, startingDate, address, longitude, latitude, empPrefFT, empPrefPT, empPrefIn);
				
				if(accountCreated){
					//send verification email to new user
					emailManager.sendAccountActivationEmail(email, name, uuid);
					message = "Account was successfully created! Please follow the instructions sent to your email to activate your account within "
					+ (int)Math.floor(SystemManager.expiryTimeEmailVerification/(1000*60)) + " minutes.";
					result = true;
					
				}
				else{
					// delete the account if there was any error during registration
					accManager.deleteAccount(email);
					message = "Failed to create account. Please try again later.";
				}
				
				
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
	 * Handles account activation link click from an email.
	 */
	private void activateAccountHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String verificationNumber = request.getParameter("id");
		boolean accountActivated = accManager.activateAccount(verificationNumber);
		if(accountActivated)
			response.sendRedirect("account/accountActivationSuccess.html");	
		else
			response.sendRedirect("error.html");
	}
	
	
	/**
	 * Handles Secondary E-mail changes
	 */
	private void requestSecondaryEmailChangeHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("Inside ServletAccount: Secondary Email Change Handler");
		
		boolean isSuccessful = false;
		String message = "Change secondary e-mail failed";
		
		String sessionKey 		= request.getParameter("sessionKey");
		Session currSession 	= accManager.getSessionByKey( sessionKey );
		int accountID 			= currSession.getIdAccount();
		String secEmail 		= request.getParameter("secEmail");
		Connection conn 		= dbManager.getConnection();	
		Statement stmt 			= null;

		secEmail = Utility.checkInputFormat( secEmail );

		try{
			stmt = conn.createStatement();
			String query = 
				"UPDATE tableAccount SET " 
				+ "secondaryEmail='" 	+ secEmail +  "' " +
				"WHERE idAccount='" 	+ accountID + "' ";

			// if successful, 1 row should be inserted
			System.out.println("Change Secondary Email query: " + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if (rowsInserted == 1){
				System.out.println("Change Secondary Email success");
				isSuccessful = true;
				message = "Change secondary e-mail successful";
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
		
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
	
	
	/***
	 * Handles primary email change requests.
	 */
	private void requestEmailChangeHandler(HttpServletRequest request, HttpServletResponse response, Session session) throws ServletException, IOException{
		

		String newEmail = request.getParameter("newEmail");		
		System.out.println("Inside ServletAccount: requestEmailChangeHandler - New Email: " + newEmail + ", Session Key: " + session.getKey());
		
		UUID uuid = UUID.randomUUID();; // verification number
		boolean result = false;
		String message = "";
		
		// check if email is unique
		boolean isUnique = !accManager.checkEmailExists(newEmail);
		
		if(isUnique){
			boolean requestAdded = accManager.addEmailChangeRequest(session.getIdAccount(), newEmail, uuid);			
			if(requestAdded){
				//send verification email to new email
				emailManager.sendPrimaryEmailChangeVerificationEmail(newEmail, uuid);
				message = "Email change request successful! An email has been sent to your inbox, " +
						"please follow the instructions to change your primary Email within "
				+ (int)Math.floor(SystemManager.expiryTimeEmailVerification/(1000*60)) + " minutes.";
				result = true;
			}
			else
				message = "Failed email change request. Please try again later.";
		}
		else{
			message = "This Email address has already been used. Please choose another one.";
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
	 * Verifies an email change link click from an email.
	 */
	private void verifyEmailChangeHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String verificationNumber = request.getParameter("id");
		boolean emailChanged = accManager.verifyChangePrimaryEmail(verificationNumber);
		if(emailChanged){
			response.sendRedirect("account/emailChangeSuccess.html");	
		}
		else{
			response.sendRedirect("error.html");
			System.out.println("Failed to verify email change.");
		}
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
		System.out.println("Inside ServletAccount: loginRequTaker");
		
		String email = request.getParameter("email");
		String pw = request.getParameter("password");
		System.out.println("user="+ email+ "Password="+ pw);
		
		String action = "";
		String message = "";

		Connection conn = dbManager.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		String accountStatus = "active";
		
		try {
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery( "SELECT * FROM tableAccount "+
						  "WHERE email='"+ email + "' " +
						  "AND password = md5('" + pw + "');");
			
			if( rs.first() ) {
				accountStatus = rs.getString("status");
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
		
	    if( accountStatus.equals("active")) {

		    Session currSession = accManager.startSession(email, pw);
			//Following IF/ELSE STMT IS THE FIRST XML WILL BE RETURN TO CLIENT WITH THE SESSION INFO
			if(currSession != null){
				// if login successful, return credential and sucess message
				// Write XML to response if DB has return message
				
				if( currSession.getAccountType().equals("poster")) {
					action = "./poster/";
				}
				else if( currSession.getAccountType().equals("searcher")) {
					action = "./searcher/";
				}
				else if( currSession.getAccountType().equals("admin") || currSession.getAccountType().equals("superAdmin")) {
					action = "./admin/manageJobAd.jsp";
				}
				message = "User logged in";
				
				StringBuffer XMLResponse = new StringBuffer();
				XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
				XMLResponse.append("<response>\n");
				XMLResponse.append("\t<sessionKey>" + currSession.getKey() + "</sessionKey>\n");
				XMLResponse.append("\t<action>" + action + "</action>\n");
				XMLResponse.append("\t<message>" + message + "</message>\n");
				XMLResponse.append("</response>\n");
				response.setContentType("application/xml");
				response.getWriter().println(XMLResponse);
				
			}
			else{
				message = "Login failed: Account name and password do not match.";
				
				System.out.println("sessionKey is null");
				StringBuffer XMLResponse = new StringBuffer();	
				XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
				XMLResponse.append("<response>\n");
				XMLResponse.append("\t<sessionKey>" + null + "</sessionKey>\n");
				XMLResponse.append("\t<action>" + action + "</action>\n");
				XMLResponse.append("\t<message>" + message + "</message>\n");
				XMLResponse.append("</response>\n");
				response.setContentType("application/xml");
				response.getWriter().println(XMLResponse);
			}
	    }
	    else {
	    	message = "Login failed: User account status is " + accountStatus + ".";
	    	
			System.out.println(message);
			StringBuffer XMLResponse = new StringBuffer();	
			XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			XMLResponse.append("<response>\n");
			XMLResponse.append("\t<sessionKey>" + null + "</sessionKey>\n");
			XMLResponse.append("\t<action>" + action + "</action>\n");
			XMLResponse.append("\t<message>" + message + "</message>\n");
			XMLResponse.append("</response>\n");
			response.setContentType("application/xml");
			response.getWriter().println(XMLResponse);
	    }
	    
		
	}
/***************************/
	private void logoutReqTaker(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		//TODO make this happen
		if(accManager.userLogout(req.getParameter("sessionKey").toString())){
			System.out.println("Logout Successfully");
		}
		else{
			System.out.println("Logout failed");
		}
		
	}
/**************************************************************************************************************************************/
	

	/***
	 * Adds a password request entry, sends an email containing the password reset link
	 * to the user, and then returns the results to the user in the response.
	 */
	private void requestForgetPasswordHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String email = request.getParameter("email");
		boolean result = false;
		String message = "";
		boolean emailExists = accManager.checkEmailExists(email);
		if(emailExists){
			UUID uuid = UUID.randomUUID();
			boolean requestAdded = accManager.addForgetPasswordRequest(email, uuid, SystemManager.expiryTimeForgetPasswordReset);
			if(requestAdded){
				//send verification email to new user
				emailManager.sendPasswordResetEmail(email, uuid);
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
	private void emailLinkForgetPasswordHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		RequestDispatcher dispatcher = request.getRequestDispatcher("/account/ResetForgetPassword.jsp");
		dispatcher.forward(request, response);
	}
	
	/***
	 * Final step of forget password procedure. Final validation of request and updates user password.
	 * Updates password if all checks pass.
	 */
	private void resetForgetPasswordHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
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
			int idAccount = accManager.getIdAccountFromIdPasswordReset(idPasswordReset);
			if (idAccount == -1)
				message = "Invalid or expired request.";
			else{
				boolean updateSuccessful = accManager.resetPassword(idPasswordReset, idAccount, password);
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
	 * Handles user change password requests.
	 */
	private void requestChangePasswordHandler(HttpServletRequest request, HttpServletResponse response, Session session) throws ServletException, IOException{
		// get request parameters
		String sessionKey = request.getParameter("sessionKey");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		String newPasswordRepeat = request.getParameter("newPasswordRepeat");
		
		//TODO refactor out session key from here
		String message = "";
		boolean result = false;
		int idAccount = -1;
		sessionKey = Utility.checkInputFormat(sessionKey);
		oldPassword = Utility.checkInputFormat(oldPassword);
		newPassword = Utility.checkInputFormat(newPassword);

		idAccount = session.getIdAccount();
		Account userAcc = accManager.getAccountFromId(idAccount);
		if( userAcc == null )
			message = "Invalid request.";
		// check old password
		else if( !Utility.md5(oldPassword).equals(userAcc.getPasswordMd5()) )
			message = "Incorrect old password.";
		// validate new password	
		else if( !Utility.validate(newPassword, SystemManager.pwPattern) )
			message = "Invalid new password format.";
		else if( !newPassword.equals(newPasswordRepeat) )
			message = "New passwords do not match.";
		else{
			boolean updateSuccessful = accManager.changePassword(idAccount, newPassword);
			if(updateSuccessful){
				message = "Password change successful!";
				result = true;
			}
			else
				message = "An error has been encountered during the request.";
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
}
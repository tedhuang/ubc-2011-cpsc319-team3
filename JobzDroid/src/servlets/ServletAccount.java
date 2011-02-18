package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Utility;

import managers.DBManager;
import managers.EmailManager;

/**
 * Servlet implementation class ServletAccount
 * Handles all account related requests, including registration, log-in, forget password, email verification
 */
public class ServletAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//TODO: move these constants to the config file
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	private static final String PW_PATTERN = "^\\S{5,15}$";
	private final long EXPIRY_TIME_EMAIL_VERIFICATION = 60 * 60 * 1000; // 60 minutes
	private final long EXPIRY_TIME_FORGET_PASSWORD_RESET = 60 * 60 * 1000; // 60 minutes
	
	private EmailManager emailManager;	
	private DBManager dbManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAccount() {
        super();
		dbManager = new DBManager();
		emailManager = new EmailManager();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		register,
		activate,
		requestEmailChange,
		verifyEmailChange,
		requestForgetPassword,
		resetForgetPassword
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
		// return error message if action parameter is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(IllegalArgumentException e){
			//TODO forward to error page
			PrintWriter out = response.getWriter();
			out.println("Grats, you broke the server.");
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
			// reset password
			case resetForgetPassword:
				resetForgetPassword(request, response);
				break;				
			default:
				break;
		}
	}
	
	/***
	 * Performs server-side checks on user registration inputs. If successful, then calls account manager update account tables in DB.
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
		String password = request.getParameter("password");
		String passwordRepeat = request.getParameter("passwordRepeat");
		String accountType = request.getParameter("accountType");
		String name = request.getParameter("name");
				
		// validate data
		if( !Utility.validate(email, EMAIL_PATTERN) ){
			message = "Invalid email address format.";
			allGood = false;
		}
		else if( !Utility.validate(password, PW_PATTERN) ){
			message = "Invalid password format.";
			allGood = false;
		}		
		else if( !accountType.equals("searcher") && !accountType.equals("poster") ){
			message = "Invalid account type.";
			allGood = false;
		}
		else if( !password.equals(passwordRepeat) ){
			message = "Passwords do not match.";
			allGood = false;
		}
		else if( name.length() < 1 ){
			allGood = false;
			if(accountType.equals("searcher"))
				message = "Name must not be empty.";
			else if(accountType.equals("poster"))
				message = "Company/organization must not be empty.";
		}
		
		// if info are all valid, then proceed to do DB updates
		if(allGood){
			// check if email is unique
			boolean isUnique = !dbManager.checkEmailExists(email);
			if(isUnique){
				accountCreated = dbManager.createAccount(email, password, accountType, name, uuid, EXPIRY_TIME_EMAIL_VERIFICATION);
				if(accountCreated){
					//send verification email to new user
					//TODO
				//	emailManager.sendAccountActivationEmail(email, name, uuid);
					emailManager.sendAccountActivationEmail("luolw123@hotmail.com", name, uuid);
					message = "Account creation successful! An email has been sent to your inbox, " +
							"please follow the instructions to activate your account within "
					+ (int)Math.floor(EXPIRY_TIME_EMAIL_VERIFICATION/(1000*60)) + " minutes.";
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
		PrintWriter out = response.getWriter();
		boolean accountActivated = dbManager.activateAccount(verificationNumber);
		if(accountActivated){
		    out.println("Congratulations, your account has successfully been activated!");
		    out.close();
		}
		else{
		    out.println("Invalid or expired account activation request.");
		    out.close();
		}
	}
	
	/***
	 * Calls DB manager to verify email change
	 */
	private void verifyEmailChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String verificationNumber = request.getParameter("id");
		PrintWriter out = response.getWriter();
		boolean emailChanged = dbManager.verifyChangePrimaryEmail(verificationNumber);
		if(emailChanged){
		    out.println("Congratulations, your primary email has successfully been changed!");
		    out.close();
		}
		else{
		    out.println("Invalid or expired account email change request.");
		    out.close();
		}
	}
	
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
			boolean requestAdded = dbManager.addForgetPasswordRequest(email, uuid, EXPIRY_TIME_FORGET_PASSWORD_RESET);
			if(requestAdded){
				//send verification email to new user
				//TODO
			//	emailManager.sendAccountActivationEmail(email, name, uuid);
				emailManager.sendPasswordResetEmail("luolw123@hotmail.com", uuid);
				message = "An email has been sent to your mail box to reset your password. " +
					"Please follow the link in your email to reset your password within "
					+ (int)Math.floor(EXPIRY_TIME_FORGET_PASSWORD_RESET/(1000*60)) + " minutes.";
				result = true;
			}
		}
		else{
			result = false;
			message = "Your entered email address does not exist as an account.";
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
	
	private void resetForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		getServletConfig().getServletContext().getRequestDispatcher("/test_pages/ResetForgetPassword.jsp").forward(request,response);
	}
	
	private String generateSessionKey( String email, String password) {
		UUID uuid = UUID.randomUUID();
		
		return null;
	}
}

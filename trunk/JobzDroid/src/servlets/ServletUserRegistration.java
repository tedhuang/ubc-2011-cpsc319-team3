package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.Utility;

import managers.AccountManager;
import managers.EmailManager;

/**
 * Servlet implementation class Servlet_User_Registration
 * Performs server-side checks on user registration inputs. If successful, then calls account manager update account tables in DB.
 * Finally calls the email manager to send a verification email to the new user.
 */
public class ServletUserRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	//TODO: move these constants to the config file
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	private static final String PW_PATTERN = "^\\S{5,15}$";
	private AccountManager accountManager;   
	private EmailManager emailManager;	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletUserRegistration() {
        super();
        accountManager = new AccountManager();
        emailManager = new EmailManager();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * Performs server-side checks on user registration inputs. If successful, then calls account manager update account tables in DB.
	 * Finally calls the email manager to send a verification email to the new user.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean result = false;
		String message = "";
		boolean allGood = true;
		boolean accountCreated = false;
		// get request parameters
		String email = request.getParameter("Email");
		String password = request.getParameter("Password");
		String passwordRepeat = request.getParameter("PasswordRepeat");
		String accountType = request.getParameter("AccountType");
		String name = request.getParameter("Name");
				
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
			boolean isUnique = accountManager.checkEmailUnique(email);
			if(isUnique){
				accountCreated = accountManager.createAccount(email, password, accountType, name);
				if(accountCreated){
					message = "Account creation successful! An email has been sent to your inbox, please follow the instructions to activate your account.";
					// send verification email to new user
				//	emailManager.sendAccountActivationEmail(email, name);
					emailManager.sendAccountActivationEmail("luolw123@hotmail.com", name);
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
}



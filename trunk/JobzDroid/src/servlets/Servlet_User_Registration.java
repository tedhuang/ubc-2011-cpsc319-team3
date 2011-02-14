package servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.Account_Manager;

/**
 * Servlet implementation class Servlet_User_Registration
 */
public class Servlet_User_Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	//TODO: move these constants to the config file
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	private static final String PW_PATTERN = "^\\S{5,15}$";
	private Account_Manager accManager;   
	
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet_User_Registration() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean result = false;
		String msg = "";
		boolean allGood = true;
		// get request parameters
		String email = request.getParameter("Email");
		String pw = request.getParameter("Password");
		String pwRepeat = request.getParameter("PasswordRepeat");
		String accType = request.getParameter("AccountType");
		String name = request.getParameter("Name");
				
		// validate data
		if( !validate(email, EMAIL_PATTERN) ){
			msg = "Invalid email address format.";
			allGood = false;
		}
		else if( !validate(pw, PW_PATTERN) ){
			msg = "Invalid password format.";
			allGood = false;
		}		
		else if( !accType.equals("searcher") && !accType.equals("poster") ){
			msg = "Invalid account type.";
			allGood = false;
		}
		else if( !pw.equals(pwRepeat) ){
			msg = "Passwords do not match.";
			allGood = false;
		}
		else if( name.length() < 1 ){
			allGood = false;
			if(accType.equals("searcher"))
				msg = "Name must not be empty.";
			else if(accType.equals("poster"))
				msg = "Company/organization must not be empty.";
		}
		
		if(allGood){
			boolean isUnique = accManager.isUniqueEmailAddr(email);
			
			if(isUnique){
				boolean accCreated = accManager.createAccount(email, pw, accType, name);
				if(accCreated){
					msg = "Account creation successful! An email has been sent to your inbox, please follow the instructions to activate your account.";
					result = true;
				}
				else
					msg = "Failed to created account. Please try again later.";
			}
			else{
				msg = "This email address has already been used. Please choose another one.";
			}			
		}
		
		// Write XML to response if DB has return message
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + msg + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}


	/**
	 * Determines whether a given string matches a regular expression pattern.
	 */
	private boolean validate(final String string, final String pattern){
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(string);
		return matcher.matches();
	
	}
}



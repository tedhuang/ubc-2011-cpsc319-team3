package servlets;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.AccountManager;

/**
 * Servlet implementation class UserRegisterationServlet
 * Handles user registration requests by validating user data, calling AccountManager and subsequently 
 * DBManager to perform DB operations, and finally returns the DB update results back to the user
 */
public class UserRegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\.]+@[_A-Za-z0-9-\\.]+(\\.[A-Za-z]{2,})$";
	private static final String PW_PATTERN = "^\\S{5,15}$";
	private AccountManager accManager;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRegistrationServlet() {
        super();
		accManager = new AccountManager();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		String temp = request.getPathInfo();
		System.out.println(temp);
		response.setContentType("text/html");  
		PrintWriter out = response.getWriter();  
		out.println("<html>");  
		out.println("<head><title> Simple Servlet </title></head>");  
		out.println("<body>");  
		out.println("<p>This is a simple Servlet!</p>");  
		out.println("</body></html>");  
		*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String result = "";
		boolean allGood = true;
		// get request parameters
		String email = request.getParameter("Email");
		String pw = request.getParameter("Password");
		String pwRepeat = request.getParameter("PasswordRepeat");
		String accType = request.getParameter("AccountType");
		String name = request.getParameter("Name");
				
		// validate data
		if( !validate(email, EMAIL_PATTERN) ){
			result = "Invalid email address format.";
			allGood = false;
		}
		else if( !validate(pw, PW_PATTERN) ){
			result = "Invalid password format.";
			allGood = false;
		}		
		else if( !accType.equals("searcher") && !accType.equals("poster") ){
			result = "Invalid account type.";
			allGood = false;
		}
		else if( !pw.equals(pwRepeat) ){
			result = "Passwords do not match.";
			allGood = false;
		}
		else if( name.length() < 1 ){
			allGood = false;
			if(accType.equals("searcher"))
				result = "Name must not be empty.";
			else if(accType.equals("poster"))
				result = "Company/organization must not be empty.";
		}
		
		if(allGood){
			boolean isUnique = accManager.isUniqueEmailAddr(email);
			System.out.println("account name is unique? " + isUnique);
			
			if(isUnique){
				boolean accCreated = accManager.createAccount(email, pw, accType, name);
				if(accCreated)
					result = "Account creation successful!";
				else
					result = "Failed to created account. Please try again later.";
			}
			else{
				result = "This email address has already been used. Please choose another one.";
			}			
		}
		
		// Write XML to response if DB has return message
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
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

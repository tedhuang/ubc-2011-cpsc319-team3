package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import classes.JobAdvertisement;
import classes.Utility;

import managers.DBManager;

/**
 * Servlet implementation class ServletJobAdvertisement
 */
public class ServletJobAd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBManager dbManager;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJobAd() {
        super();
        dbManager = DBManager.getInstance();
        // TODO Auto-generated constructor stub
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		//Add new functions here
		createJobAdvertisement,
		searchJobAdvertisement
		
	}
	
	private enum strMonths {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sept, Oct, Nov, Dec

	}
	
	private long calculateDate(int year, String month, int day){
		
		long resultTime = -1;
		int monthNum = -1;
		int daysInMonth;
		Calendar cal = Calendar.getInstance();
		
		//Convert string month representation to numerical representation
		switch( strMonths.valueOf(month) ){
		case Jan: monthNum = Calendar.JANUARY;
		case Feb: monthNum = Calendar.FEBRUARY;
		case Mar: monthNum = Calendar.MARCH;
		case Apr: monthNum = Calendar.APRIL;
		case May: monthNum = Calendar.MAY;
		case Jun: monthNum = Calendar.JUNE;
		case Jul: monthNum = Calendar.JULY;
		case Aug: monthNum = Calendar.AUGUST;
		case Sept: monthNum = Calendar.SEPTEMBER;
		case Oct: monthNum = Calendar.OCTOBER;
		case Nov: monthNum = Calendar.NOVEMBER;
		case Dec: monthNum = Calendar.DECEMBER;
		}
		//Calculate the number of days in that specific month
		cal.set(year, monthNum, day);

		//Calculate the total time in milliseconds (starting at unix time)
		resultTime = cal.getTimeInMillis();
		
		System.out.println("Result time: " + resultTime);
		
		return resultTime;
	}
    
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestProcess(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestProcess(request,response);
	}

	private void requestProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String action = request.getParameter("action");
		System.out.println(action);
		try{
			EnumAction.valueOf(action);
		}
		catch(IllegalArgumentException e){
			//TODO forward to error page
			PrintWriter out = response.getWriter();
			out.println("Error: Request Process Failed on ServletJobAdvertisement");
			return;
		}
		
		//Check which function the request is calling from the servlet
		switch( EnumAction.valueOf(action) ){
			case createJobAdvertisement:
				createJobAdvertisement(request, response);
				
				break;
			case searchJobAdvertisement:
				
				break;
			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
		}
		
		
	}
	
	
	/**
	 * Creates a new job advertisement entry in the database with the given values
	 * @param jobAdvertisementTitle
	 * @param jobDescription
	 * @param jobLocation
	 * @param contactInfo
	 * @param strTags
	 * @return idJobAd
	 * @throws IOException 
	 */
	private void createJobAdvertisement(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		//initialize return statements
		String message = "Create Job Advertisement Failed";
		boolean isSuccessful = false;
		
		String jobAdvertisementTitle = request.getParameter("strTitle");
		String jobDescription = request.getParameter("strDescription");
		//String jobLocation = request.getParameter("strJobLocation");
		String contactInfo = request.getParameter("strContactInfo");
		String strTags = request.getParameter("strTags");
		
		int educationRequirement = Integer.parseInt(request.getParameter("educationRequirement"));
		
		int expiryYear = Integer.parseInt( request.getParameter("expiryYear"));
		String expiryMonth = request.getParameter("expiryMonth");
		int expiryDay =  Integer.parseInt( request.getParameter("expiryDay"));
		
		int startingYear =  Integer.parseInt(request.getParameter("startingYear"));
		String startingMonth = request.getParameter("startingMonth");
		int startingDay = Integer.parseInt(request.getParameter("startingDay"));
		
		long millisExpiryDate = calculateDate(expiryYear,expiryMonth,expiryDay);
		long millisStartingDate = calculateDate(startingYear, startingMonth, startingDay);
		
		Calendar cal = Calendar.getInstance();
		
		long millisDateCreated = cal.getTimeInMillis();

		//TODO: add values for these:
		//int ownerID;
		
		System.out.println(jobAdvertisementTitle);
		System.out.println(jobDescription);
		//System.out.println(jobLocation);
		System.out.println(contactInfo);
		System.out.println(strTags);
		System.out.println("Created On: " + millisDateCreated + " Expire On: " + millisExpiryDate);
		
		Connection conn = dbManager.getConnection();	
		
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			
			jobAdvertisementTitle = Utility.checkInputFormat( jobAdvertisementTitle );
			jobDescription = Utility.checkInputFormat( jobDescription );
			//jobLocation = Utility.checkInputFormat( jobLocation );
			contactInfo = Utility.checkInputFormat( contactInfo );
			strTags = Utility.checkInputFormat( strTags );
			
			String query = 
				"INSERT INTO tableJobAd(title, description, expiryDate, dateStarting, datePosted, contactInfo, educationRequired, tags ) " +
				"VALUES " + "('" 
					+ jobAdvertisementTitle + "','" 
					+ jobDescription + "','" 
					+ millisExpiryDate + "','" 
					+ millisStartingDate + "','" 
					+ millisDateCreated + "','"
					+ contactInfo + "','" 
					+ educationRequirement + "','"
					+ strTags + 
				"')";
			
			// if successful, 1 row should be inserted
			System.out.println("New Job Ad Query:" + query);
			int rowsInserted = stmt.executeUpdate(query);
			
			if (rowsInserted == 1){
				System.out.println("New JobAd Creation success (DB)");
				isSuccessful = true;
				message = "Create Job Advertisement Successful";
			}
			else{
				Utility.getErrorLogger().warning("New JobAd insert in DB failed");
			}
			
			
		}
		catch (SQLException e) {
			//TODO log SQL exception
			Utility.getErrorLogger().severe("SQL exception : " + e.getMessage());
		}
		// close DB objects
	    finally {
	        try{
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e) {
	        	//TODO log "Cannot close Statement"
	        	System.out.println("Cannot close Statement : " + e.getMessage());
	        }
	        try {
	            if (conn  != null)
	                conn.close();
	        }
	        catch (SQLException e) {
	        	//TODO log Cannot close Connection
	        	System.out.println("Cannot close Connection : " + e.getMessage());
	        }
	    }
	    
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
		
	}
	
	
	
	public boolean searchJobAdvertisement(HttpServletRequest request, HttpServletResponse response){
		
		//String searchTitle = request.getParameter("strTitle");
		//String strTags = request.getParameter("strTags");
		
		String jobLocation = request.getParameter("strJobLocation");
		String searchText = request.getParameter("searchText");
		
		int educationRequirement = Integer.parseInt(request.getParameter("educationRequirement"));
		
		//TODO: implement parse "strSearchText" for title and keywords
		
		
		
		
		return false;
	}
		
	
	
	
	
	
}













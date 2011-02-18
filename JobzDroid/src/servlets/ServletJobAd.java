package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        dbManager = new DBManager();
        // TODO Auto-generated constructor stub
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		//Add new functions here
		createJobAdvertisement
		
	}
	
	private int calculateDate(int year, int month, int day){
		
		
		return 0;
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

	public void requestProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
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
				if( !createJobAdvertisement(request) ){
					//TODO: implement error handling
					System.out.println("Error: Create Job Advertisement Failed");
				}
				System.out.println("New Job Advertisement Created");
				break;
				
			default:
				System.out.println("Error: failed to process request - action not found");
				break;
				
		}
		
		
	}
	
	public boolean createJobAdvertisement(HttpServletRequest request){
		
		String jobAdvertisementTitle = request.getParameter("strTitle");
		String jobDescription = request.getParameter("strDescription");
		String jobLocation = request.getParameter("strJobLocation");
		String contactInfo = request.getParameter("strContactInfo");
		String strTags = request.getParameter("strTags");
		
		int expiryYear = Integer.parseInt( request.getParameter("expiryYear"));
		String expiryMonth = request.getParameter("expiryMonth");
		int expiryDay =  Integer.parseInt( request.getParameter("expiryDay"));
		
		int startingYear =  Integer.parseInt(request.getParameter("startingYear"));
		String startingMonth = request.getParameter("startingMonth");
		int startingDay = Integer.parseInt(request.getParameter("startingDay"));
		
		//TODO: add values for these:
		//int ownerID;
		//int jobAdID;
		
		System.out.println(jobAdvertisementTitle);
		System.out.println(jobDescription);
		System.out.println(jobLocation);
		System.out.println(contactInfo);
		System.out.println(strTags);
		
//		if( !dbManager.createJobAdvertisement(jobAdvertisementTitle, jobDescription, 
//										jobLocation, contactInfo, strTags) ){
//			//TODO: Implement Error Handling:
//			return false;
//		}
		
		dbManager.createJobAdvertisement(jobAdvertisementTitle, jobDescription, jobLocation, contactInfo, strTags);

		
		
		return true;
	}
	
	
}













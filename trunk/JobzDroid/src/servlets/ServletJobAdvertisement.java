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
public class ServletJobAdvertisement extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private DBManager dbManager;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletJobAdvertisement() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		//Add new functions here
		createJobAdvertisement
		
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
		
		System.out.println("Checkpoint: requestProcess function: " + request.getParameter("function").toString());
		String action = request.getParameter("action");
		
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
		
		String jobAdvertisementTitle = request.getParameter("strTitle").toString();
		String jobDescription = request.getParameter("strDescription").toString();
		String jobLocation = request.getParameter("strJobLocation").toString();
		String contactInfo = request.getParameter("strContactInfo").toString();
		String strTags = request.getParameter("strTags").toString();
		//int expiryYear = request.getParameter("expiryMonth");
		//int expiryMonth = request.getParameter("expiryMonth");
		//int expiryDay = request.getParameter("expiryDay");
		
		//TODO: add values for these:
		//int ownerID;
		//int jobAdID;
		
		if( !dbManager.createJobAdvertisement(jobAdvertisementTitle, jobDescription, 
										jobLocation, contactInfo, strTags) ){
			//TODO: Implement Error Handling:
			return false;
		}
		
		
		
		return true;
	}
	
}













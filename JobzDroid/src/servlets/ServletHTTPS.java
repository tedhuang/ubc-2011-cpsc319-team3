package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.DBManager;
import managers.JobAdvertisementManager;

/**
 * Servlet implementation class ServletHTTPS
 */
public class ServletHTTPS extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    //Add Manager Creation here
    JobAdvertisementManager jobAdvertisementManager = new JobAdvertisementManager();
    DBManager dbm = new DBManager();
    
//****************************************
//TODO: find out how to call multiple managers without having to pass all of them through parameters    
//****************************************    
    
    	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletHTTPS() {
        super();
    }
    
    
//	private enum manager
//	{
//		//Add more manager names here separated by a comma
//		JOB_ADVERTISEMENT_MANAGER;
//	}
//	
	private enum manager
	{ JobAdvertismentManager, UNKNOWN;
	
	public static manager fromString(String Str)
	{
		try {return valueOf(Str);}
		catch (Exception ex){return UNKNOWN;}
	}
	};

    
    
	/**s
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Checkpoint: " + 
				request.getParameter("manager").toString());
		
		switch( manager.fromString(request.getParameter("manager").toString()) ){
			case JobAdvertismentManager:
				
				System.out.println("Checkpoint: switch");
				jobAdvertisementManager.requestHandle(dbm, request);
				
				break;
				
			default:
				System.out.println("Failed HTTPS servlet post");
				break;
			//Add more manager cases here		
		}
			
		
		
	}

}















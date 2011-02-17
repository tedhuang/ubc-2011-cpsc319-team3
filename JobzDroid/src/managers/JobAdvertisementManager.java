package managers;

import javax.servlet.http.HttpServletRequest;


public class JobAdvertisementManager {
	

	public JobAdvertisementManager(){}
	
	private enum function
	{
		//Add more manager names here separated by a comma
		createJobAdvertisement, UNKNOWN;
		
		public static function fromString(String Str)
		{
			try {return valueOf(Str);}
			catch (Exception ex){return UNKNOWN;}
		}
	}

	
	DBManager dbManager;
	
	
	public String requestHandle(DBManager dbm, HttpServletRequest request){
		
		dbManager = dbm;
		
		System.out.println("Checkpoint: requestHandle function: " + request.getParameter("function").toString());
		
		
		switch(function.fromString(request.getParameter("function").toString())){
			case createJobAdvertisement:
				if( !createJobAdvertisement(request) ){
					//TODO: implement error handling
					System.out.println("Create Job Advertisement Failed");
				} 
				break;
			default:
				System.out.println("Failed JobAdvertisementManager requestHandle");
				break;
				
		}
		
		
		
		return null;
	}
	
	
	public boolean createJobAdvertisement(HttpServletRequest request){
		
		String jobAdvertisementTitle = request.getParameter("strTitle").toString();
		String jobDescription = request.getParameter("strDescription").toString();
		String jobLocation = request.getParameter("strJobLocation").toString();
		String contactInfo = request.getParameter("strContactInfo").toString();
		String strTags = request.getParameter("strTags").toString();
		//int expiryWeek = Integer.parseInt(request.getParameter("expiryWeek").toString());
		//int expiryDay = Integer.parseInt( request.getParameter("expiryDay").toString());
		//int ownerID;
		
		
		//int expiryTime = 
		
		
		if( !dbManager.createJobAdvertisement(jobAdvertisementTitle, jobDescription, 
										jobLocation, contactInfo, strTags) ){
			//TODO: Implement Error Handling:
			return false;
		}
		
		
		
		return true;
	}
	
	
	
	

}




;
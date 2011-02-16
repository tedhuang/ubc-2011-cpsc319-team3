package managers;

import javax.servlet.http.HttpServletRequest;




public class JobAdvertisementManager {
	

	public JobAdvertisementManager(){}
	
	private enum function
	{
		//Add more manager names here separated by a comma
		CREATE_JOB_ADVERTISEMENT;
	}

	
	DBManager dbManager;
	
	
	public String requestHandle(DBManager dbm, HttpServletRequest request){
		
		dbManager = dbm;
		
		
		switch(function.valueOf(request.getParameter("function").toString())){
			case CREATE_JOB_ADVERTISEMENT:
				if( !createJobAdvertisement(request) ){
					//TODO: implement error handling
					System.out.println("Create Job Advertisement Failed");
					break;
				} 
			
				
		}
		
		
		
		return null;
	}
	
	
	public boolean createJobAdvertisement(HttpServletRequest request){
		
		String jobAdvertisementTitle = request.getParameter("strTitle").toString();
		String jobDescription = request.getParameter("strDescription").toString();
		String jobLocation = request.getParameter("strJobLocation").toString();
		String contactInfo = request.getParameter("strContactInfo").toString();
		String strTags = request.getParameter("strTags").toString();
		int expiryWeek = Integer.parseInt(request.getParameter("expiryWeek").toString());
		int expiryDay = Integer.parseInt( request.getParameter("expiryDay").toString());
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
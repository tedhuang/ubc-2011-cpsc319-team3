package classes;

import java.util.ArrayList;

public class JobAdvertisement {

	public int 		jobAdId;
	public int		ownerID;
	public int		educationReq;
	public int		numberOfViews;
	public String	jobAdTitle;
	public String	tags;
	public String	contactInfo;
	public long		expiryDate;
	public long		startingDate;
	public long		creationDate;
	public String	status;
	public String	jobAdDescription;
	public boolean	isApproved;
	public String	jobAvailability;
	
	public ArrayList<Location> locationList;
	
	/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
	//public String	creationDateFormatted;
	//public String 	eduReqFormatted;

	
	public JobAdvertisement(){	}

	
	public String toXMLContent(){
		
		String eduReqFormatted;
		String creationDateFormatted;
		String startingDateFormatted;
		String expiryDateFormatted;
		locationList=new ArrayList<Location>();
		Location loc =new Location("Vancouver");
		locationList.add(loc);
		
		eduReqFormatted = Utility.degreeConvertor(educationReq);
		
		creationDateFormatted = Utility.dateConvertor(creationDate);
		expiryDateFormatted = Utility.dateConvertor(expiryDate);
		startingDateFormatted = Utility.dateConvertor(startingDate);
		
		String result =  "\t<jobAd" +
		" jobAdId=\"" 				+ jobAdId + "\"" +
		" jobAdTitle=\"" 			+ jobAdTitle + "\"" +
		" tags=\"" 					+ tags + "\"" +
		" jobAvail=\"" 				+ jobAvailability + "\"" +
		" contactInfo=\"" 			+ contactInfo + "\"" +
		
		//TODO: remove the non-formatted values from XML if not required
		" expiryDate=\"" 			+ expiryDate + "\"" +
		" startingDate=\"" 			+ startingDate + "\"" +
		" creationDate=\"" 			+ creationDate + "\"" +
		" educationReq=\"" 			+ educationReq + "\"" +
		
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
		" creationDateFormatted=\"" + creationDateFormatted + "\"" +
		" startingDateFormatted=\"" + startingDateFormatted + "\"" +
		" expiryDateFormatted=\"" + expiryDateFormatted + "\"" +
		" eduReqFormatted=\"" + eduReqFormatted + "\"" +
		/******************************************************************/
		
		" status=\"" 				+ status + "\"" +
		" numberOfViews=\"" 		+ numberOfViews + "\"" +
		" jobAdDescription=\"" 		+ jobAdDescription + "\"" +
		" isApproved=\"" 			+ isApproved + "\" >\n";
		
		/************ Add a list of location objects to XML ***********/
		for( int i = 0 ; i < locationList.size() ; i++ ){
			result=result.concat("\t\t<location address=\"" 	 + locationList.get(i).address + "\"" +
										 " latitude=\""  + locationList.get(i).latitude + "\"" +
										 " longitude=\"" + locationList.get(i).longitude + "\" >\n" );
		}
		
		result = result.concat("\t</jobAd>\n");
		
		System.out.println("JobAdvertisement Object XML:\n" + result);
		
		return result;

		
	}
 	
	
}

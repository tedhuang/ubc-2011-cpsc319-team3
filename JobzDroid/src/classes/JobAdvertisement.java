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
	/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
	public String	creationDateFormatted;
	public String 	eduReqFormatted;
	
	public ArrayList<Location> locationList;

	
	public JobAdvertisement(){	}

	
	public String toXMLContent(){
		
		String result =  "\t\t<jobAd" +
		" jobAdId=\"" 				+ jobAdId + "\"" +
		" jobAdTitle=\"" 			+ jobAdTitle + "\"" +
		" tags=\"" 					+ tags + "\"" +
		" jobAvail=\"" 				+ jobAvailability + "\"" +
		" contactInfo=\"" 			+ contactInfo + "\"" +
		" expiryDate=\"" 			+ expiryDate + "\"" +
		" startingDate=\"" 			+ startingDate + "\"" +
		" creationDate=\"" 			+ creationDate + "\"" +
		
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************///TODO DO WE NEED THE NON-FORMAT VAR?
		" creationDateFormatted=\"" + creationDateFormatted + "\"" +
		" eduReqFormatted=\"" + eduReqFormatted + "\"" +
		/******************************************************************/
		" status=\"" 				+ status + "\"" +
		" numberOfViews=\"" 		+ numberOfViews + "\"" +
		" educationReq=\"" 			+ educationReq + "\"" +
		" jobAdDescription=\"" 		+ jobAdDescription + "\"" +
		" isApproved=\"" 			+ isApproved + "\" + >\n";
		
		/************ Add a list of location objects to XML ***********/
		for( int i = 0 ; i < locationList.size() ; i++ ){
			result.concat("\t\t\t<location address=\"" 	 + locationList.get(i).address + "\"" +
										  "latitude=\""  + locationList.get(i).latitude + "\"" +
										  "longitude=\"" + locationList.get(i).longitude + "\" + >\n" );
		}
		
		result = result.concat("\t\t</jobAd>\n");
		
		return result;

		
	}
 	
	
}

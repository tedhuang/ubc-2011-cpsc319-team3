package classes;

public class JobAdvertisement {

	public int 		jobAdId;
	public int		ownerID;
	public int		educationReq;
	public int		numberOfViews;
	public String	jobAdTitle;
	public String	location;
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
	
	
	public JobAdvertisement() {
		jobAdId 				= -1;
		ownerID					= -1;
		educationReq			= 0;
		numberOfViews			= 0;
		jobAdTitle 				= null;
		location				= null;
		tags					= null;
		contactInfo				= null;
		expiryDate				= -1;
		startingDate			= -1;
		creationDate			= -1;
		status					= null;
		jobAdDescription		= null;
		isApproved				= false;
		jobAvailability 		= null;
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
		creationDateFormatted	= null;
		eduReqFormatted			= null;
	}
	
	public JobAdvertisement(int AdId,
							int opId,
							int numViews,
							int eduReq,
							String title,
							String loc,
							String t,
							String cInfo,
							long eDate,
							long sDate,
							long cDate,
							String initialStatus,
							String description,
							boolean isAppr,
							String jobAvail,
							String inDateFormatted,
							String eduFormatted
							){
		
		jobAdId 		= AdId;
		jobAdTitle 		= title;
		location		= loc;
		tags			= t;
		contactInfo		= cInfo;
		expiryDate		= eDate;
		startingDate 	= sDate;
		creationDate	= cDate;
		status			= initialStatus;
		numberOfViews	= numViews;
		educationReq	= eduReq;
		jobAdDescription= description;
		isApproved		= isAppr;
		jobAvailability = jobAvail;
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
		creationDateFormatted = inDateFormatted;
		eduReqFormatted		  = eduFormatted;
		
	}
	
	public String toXMLContent(){
		
		return "\t\t<jobAd" +
		" jobAdId=\"" + jobAdId + "\"" +
		" jobAdTitle=\"" + jobAdTitle + "\"" +
		" location=\"" + location  + "\"" +
		" tags=\"" + tags + "\"" +
		" jobAvail=\"" + jobAvailability + "\"" +
		" contactInfo=\"" + contactInfo + "\"" +
		" expiryDate=\"" + expiryDate + "\"" +
		" startingDate=\"" + startingDate + "\"" +
		" creationDate=\"" + creationDate + "\"" +
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************///TODO DO WE NEED THE NON-FORMAT VAR?
		" creationDateFormatted=\"" + creationDateFormatted + "\"" +
		" eduReqFormatted=\"" + eduReqFormatted + "\"" +
		/******************************************************************/
		" status=\"" + status + "\"" +
		" numberOfViews=\"" + numberOfViews + "\"" +
		" educationReq=\"" + educationReq + "\"" +
		" jobAdDescription=\"" + jobAdDescription + "\"" +
		" isApproved=\"" + isApproved + "\"" +
		"/>\n";
		
	}
 	
	
}

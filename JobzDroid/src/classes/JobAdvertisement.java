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
	public String	creationDateFormatted;
	
	
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
		creationDateFormatted	= null;
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
							String inDateFormatted
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
		creationDateFormatted = inDateFormatted;
		
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
		" creationDateFormatted=\"" + creationDateFormatted + "\"" +
		" status=\"" + status + "\"" +
		" numberOfViews=\"" + numberOfViews + "\"" +
		" educationReq=\"" + educationReq + "\"" +
		" jobAdDescription=\"" + jobAdDescription + "\"" +
		" isApproved=\"" + isApproved + "\"" +
		"/>\n";
		
	}
 	
	
}

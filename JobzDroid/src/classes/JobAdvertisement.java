package classes;

public class JobAdvertisement {

	public int 		jobAdId;
	public int		ownerID;
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
	
	
	public JobAdvertisement() {
		jobAdId 		= -1;
		ownerID			= -1;
		numberOfViews	= 0;
		jobAdTitle 		= null;
		location		= null;
		tags			= null;
		contactInfo		= null;
		expiryDate		= -1;
		startingDate	= -1;
		creationDate	= -1;
		status			= null;
		jobAdDescription= null;
		isApproved		= false;
	}
	
	public JobAdvertisement(int AdId,
							int opId,
							int numViews,
							String title,
							String loc,
							String t,
							String cInfo,
							long eDate,
							long sDate,
							long cDate,
							String initialStatus,
							String description,
							boolean isAppr
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
		jobAdDescription= description;
		isApproved		= isAppr;
		
	}
 	
	
}

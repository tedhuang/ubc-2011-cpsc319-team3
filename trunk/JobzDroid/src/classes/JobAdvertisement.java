package classes;

public class JobAdvertisement {

	public int 		jobAdId;
	public String	jobAdTitle;
	public String	location;
	public String	tags;
	public String	contactInfo;
	public String	expiryDate;
	public String	startingDate;
	public String	creationDate;
	public int		ownerID;
	public String	status;
	public int		numberOfViews;
	public String	jobAdDescription;
	
	public JobAdvertisement() {
		jobAdId 		= -1;
		jobAdTitle 		= null;
		location		= null;
		tags			= null;
		contactInfo		= null;
		expiryDate		= "";
		creationDate	= "";
		status			= null;
		numberOfViews	= 0;
		jobAdDescription="";
	}
 	
	
}

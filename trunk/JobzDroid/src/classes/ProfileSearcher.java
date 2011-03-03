package classes;

public class ProfileSearcher {

	public int accountID;
	public String accountType;
	public String name;
	public String email;
	public String secondaryEmail;
	public String contactInfo;
	public String selfDescription;
	public String location;
	public String docLink;
	public String employmentPreference;
	public long preferredStartDate;
	public int educationLevel;
	
	
	
	public ProfileSearcher(){
		
	}
	
	public ProfileSearcher( int accID,
							String accType,
							String searcherName,
							String searcherEmail,
							String secEmail,
							String cInfo,
							String sDescription,
							String documentLink,
							String empPref,
							String loc,
							long prefStartDate,
							int eduLvl){
		
		accountID = accID;
		accountType = accType;
		name = searcherName;
		email = searcherEmail;
		secondaryEmail = secEmail;
		contactInfo = cInfo;
		selfDescription =sDescription;
		docLink = documentLink;
		employmentPreference = empPref;
		location = loc;
		preferredStartDate = prefStartDate;
		educationLevel = eduLvl;
		
	}
	
	public String toXMLContent(){
		
		return "\t\t<auction" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" email=\"" + email + "\"" +
		" secondaryEmail=\"" + secondaryEmail + "\"" +
		" contactInfo=\"" + contactInfo + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" docLink=\"" + docLink + "\"" +
		" employmentPreference=\"" + employmentPreference + "\"" +
		" location=\"" + location + "\"" +
		" preferredStartDate=\"" + preferredStartDate + "\"" +
		" educationLevel=\"" + educationLevel + "\"" +
		"/>\n";
		
	}
 	
	
}






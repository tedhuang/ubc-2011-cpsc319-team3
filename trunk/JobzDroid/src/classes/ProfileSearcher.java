package classes;

public class ProfileSearcher {

	public int accountID;
	public String accountType;
	public String name;
	public String email;
	public String secondaryEmail;
	public String contactInfo;
	public String selfDescription;
	public String docLink;
	public String employmentPreference;
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
		educationLevel = eduLvl;
		
	}
	
	
	
}

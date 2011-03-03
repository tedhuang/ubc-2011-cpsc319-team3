package classes;

public class ProfileSearcher {

	public int accountID;
	public String accountType;
	public String name;
	public String email;
	public String secondaryEmail;
	public String phone;
	public String selfDescription;
	public String address;
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
							String phoneNum,
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
		phone = phoneNum;
		selfDescription =sDescription;
		docLink = documentLink;
		employmentPreference = empPref;
		address = loc;
		preferredStartDate = prefStartDate;
		educationLevel = eduLvl;
		
	}
	
	public String toXMLContent(){
		
		return "\t\t<profileSearcher" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" email=\"" + email + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" docLink=\"" + docLink + "\"" +
		" employmentPreference=\"" + employmentPreference + "\"" +
		" address=\"" + address + "\"" +
		" preferredStartDate=\"" + preferredStartDate + "\"" +
		" educationLevel=\"" + educationLevel + "\"" +
		"/>\n";
		
	}
 	
	
}






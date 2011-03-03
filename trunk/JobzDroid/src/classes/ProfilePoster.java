package classes;

public class ProfilePoster {

	public int accountID;
	public String accountType;
	public String name;
	public String email;
	public String secondaryEmail;
	public String contactInfo;
	public String selfDescription;
	public String affliation;

	public ProfilePoster(){
		
	}
	
	public ProfilePoster( int accID,
							String accType,
							String searcherName,
							String searcherEmail,
							String secEmail,
							String cInfo,
							String sDescription,
							String affl){
		
		accountID = accID;
		accountType = accType;
		name = searcherName;
		email = searcherEmail;
		secondaryEmail = secEmail;
		contactInfo = cInfo;
		selfDescription =sDescription;
		affliation = affl;

		
	}
	
}

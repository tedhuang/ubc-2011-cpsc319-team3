package classes;

public class ProfilePoster {

	public int accountID;
	public String accountType;
	public String name;
	public String email;
	public String secondaryEmail;
	public String phone;
	public String selfDescription;
	public String address;

	public ProfilePoster(){
		
	}
	
	public ProfilePoster( int accID,
							String accType,
							String searcherName,
							String searcherEmail,
							String phoneNum,
							String sDescription,
							String loc
							){
		
		accountID = accID;
		accountType = accType;
		name = searcherName;
		email = searcherEmail;
		phone = phoneNum;
		selfDescription =sDescription;
		address = loc;

		
	}
	
	public String toXMLContent(){
		
		return "\t\t<profilePoster" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" email=\"" + email + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" address=\"" + address + "\"" +
		"/>\n";
		
	}
	
}

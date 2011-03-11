package classes;

import java.util.ArrayList;

public class ProfileSearcher {

	public int accountID;
	public String accountType = "";
	public String name = "";
	public String phone = "";
	public String selfDescription = "";
	public String docLink = "";
	public String employmentPreference = "";
	public long preferredStartDate;
	public int educationLevel;
	
	public String email = "";
	public String secondaryEmail = "";
	
	public ArrayList<Location> addressList;

	
	public ProfileSearcher(){
		accountType = "searcher";
	}
	
	public String toXMLContent(){

		//Empty location just to avoid null pointers
		Location loc = new Location("");
		addressList.add(loc);
		
		String result = "\t\t<profile" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" docLink=\"" + docLink + "\"" +
		" employmentPreference=\"" + employmentPreference + "\"" +
		" preferredStartDate=\"" + preferredStartDate + "\"" +
		" educationLevel=\"" + educationLevel + "\"" +
		" email=\"" + email + "\"" +
		" secondaryEmail=\"" + secondaryEmail + "\"";
		
		result = result + ">\n";
		
		for( int i = 0 ; i < addressList.size() ; i++ ){
			result = result.concat("\t\t\t<location address=\"" 	 + addressList.get(i).address + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" ></location>\n" );
		}
		
		result = result.concat( "\t\t</profile>\n" );
		
		return result;
	}
 	
	
}






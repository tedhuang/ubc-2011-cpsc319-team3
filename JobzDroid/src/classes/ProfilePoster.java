package classes;

import java.util.ArrayList;

public class ProfilePoster {

	public int accountID;
	public String accountType = "";
	public String name = "";
	public String phone = "";
	public String selfDescription = "";
	public ArrayList<Location> addressList;

	public String email = "";
	public String secondaryEmail = "";
	
	
	public ProfilePoster(){
		accountType = "poster";
		addressList = new ArrayList<Location>();
	}
	
	
	public String toXMLContent(){
		
		//Empty location just to avoid null pointers
		if(addressList.isEmpty()){
			Location loc = new Location("Not Specified");
			addressList.add(loc);
		}
		
		if( secondaryEmail == null || secondaryEmail == ""){
			secondaryEmail = "N/A";
		}
		
		String result =  "\t\t<profile" +
			" accountID=\"" + accountID + "\"" +
			" accountType=\"" + accountType + "\"" +
			" name=\"" + name  + "\"" +
			" phone=\"" + phone + "\"" +
			" email=\"" + email + "\"" +
			" secondaryEmail=\"" + secondaryEmail + "\"" +
			" selfDescription=\"" + selfDescription + "\"";
			
		result = result + ">\n";
		
		for( int i = 0 ; i < addressList.size() ; i++ ){
			result = result.concat("\t\t\t<location address=\""  + addressList.get(i).address + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" ></location>\n" );
		}
		
		result = result.concat( "\t\t</profile>\n" );
		
		return result;
	}
	
}

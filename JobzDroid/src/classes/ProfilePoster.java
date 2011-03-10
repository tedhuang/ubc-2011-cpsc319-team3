package classes;

import java.util.ArrayList;

public class ProfilePoster {

	public int accountID;
	public String accountType;
	public String name;
	public String phone;
	public String selfDescription;
	public ArrayList<Location> addressList;

	public ProfilePoster(){
		
	}
	
	
	public String toXMLContent(){
		
		String result =  "\t\t<profilePoster" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\" >\n";
		
		for( int i = 0 ; i < addressList.size() ; i++ ){
			result.concat("\t\t\t<location address=\"" 	 + addressList.get(i).address + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" >\n" );
		}
		
		result = result.concat( "\t\t</profilePoster>\n" );
		
		return result;
	}
	
}

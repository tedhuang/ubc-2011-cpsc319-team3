package classes;

import java.util.ArrayList;

public class ProfilePoster {

	public int accountID;
	public String accountType = "";
	public String name = "";
	public String phone = "";
	public String selfDescription = "";
	public ArrayList<Location> addressList;

	public ProfilePoster(){
		accountType = "poster";
	}
	
	
	public String toXMLContent(){
		
		//Empty location just to avoid null pointers
		Location loc = new Location("");
		addressList.add(loc);
		
		String result =  "\t\t<profile" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\" >\n";
		
		for( int i = 0 ; i < addressList.size() ; i++ ){
			result = result.concat("\t\t\t<location address=\""  + addressList.get(i).address + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" ></location>\n" );
		}
		
		result = result.concat( "\t\t</profile>\n" );
		
		return result;
	}
	
}

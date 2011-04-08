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
			Location loc = new Location();
			addressList.add(loc);
		}
		
		if( secondaryEmail == null || secondaryEmail == ""){
			secondaryEmail = "";
		}
		
		if( phone == null || phone.equals("null")) {
			phone = "";
		}
		
		if( selfDescription == null || selfDescription.equals("null")) {
			selfDescription = "";
		}
		
		// escape chars for XML
		name = Utility.processXMLEscapeChars(name);
		selfDescription = Utility.processXMLEscapeChars(selfDescription);
		
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
			String addr = addressList.get(i).address;
			addr = Utility.processXMLEscapeChars(addr);
			result = result.concat("\t\t\t<location address=\""  + addr + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" ></location>\n" );
		}
		
		result = result.concat( "\t\t</profile>\n" );
		
		
		System.out.println(result);
		return result;
	}
	
}

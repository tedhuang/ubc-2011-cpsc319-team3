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
	public String startingDateFormatted = "";
	public String educationFormatted = "";
	
	public ArrayList<Location> addressList;

	
	public ProfileSearcher(){
		accountType = "searcher";
		addressList = new ArrayList<Location>();
		preferredStartDate = 0; //This is handled as not specified
	}
	
	public String toXMLContent(){

		//Empty location just to avoid null pointers
		if(addressList.isEmpty()){
			Location loc = new Location();
			addressList.add(loc);
		}
		
		if(preferredStartDate == 0)
			startingDateFormatted = "";
		else
			startingDateFormatted = Utility.dateConvertor(preferredStartDate);
	
		if( secondaryEmail == null || secondaryEmail == ""){
			secondaryEmail = "";
		}
		
		if( phone == null || phone.equals("null")) {
			phone = "";
		}
		
		if( selfDescription == null || selfDescription.equals("null")) {
			selfDescription = "";
		}
		
		// escape characters into valid XML
		name = Utility.processXMLEscapeChars(name);
		selfDescription = Utility.processXMLEscapeChars(selfDescription);
		
		educationFormatted = Utility.degreeConvertor( educationLevel );
		
		
		String result = "\t\t<profile" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" docLink=\"" + docLink + "\"" +
		" employmentPreference=\"" + employmentPreference + "\"" +
		" preferredStartDate=\"" + preferredStartDate + "\"" +
		" startingDateFormatted=\"" + startingDateFormatted + "\"" +
		" educationLevel=\"" + educationLevel + "\"" +
		" email=\"" + email + "\"" +
		" secondaryEmail=\"" + secondaryEmail + "\"" +
		" educationFormatted=\"" + educationFormatted + "\"";
		
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






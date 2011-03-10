package classes;

import java.util.ArrayList;

public class ProfileSearcher {

	public int accountID;
	public String accountType;
	public String name;
	public String phone;
	public String selfDescription;
	public String docLink;
	public String employmentPreference;
	public long preferredStartDate;
	public int educationLevel;
	
	public ArrayList<Location> addressList;

	
	public ProfileSearcher(){
		
	}
	
	public String toXMLContent(){
		
		String result = "\t\t<profileSearcher" +
		" accountID=\"" + accountID + "\"" +
		" accountType=\"" + accountType + "\"" +
		" name=\"" + name  + "\"" +
		" phone=\"" + phone + "\"" +
		" selfDescription=\"" + selfDescription + "\"" +
		" docLink=\"" + docLink + "\"" +
		" employmentPreference=\"" + employmentPreference + "\"" +
		" preferredStartDate=\"" + preferredStartDate + "\"" +
		" educationLevel=\"" + educationLevel + "\" >\n";
		
		for( int i = 0 ; i < addressList.size() ; i++ ){
			result.concat("\t\t\t<location address=\"" 	 + addressList.get(i).address + "\"" +
										 " latitude=\""  + addressList.get(i).latitude + "\"" +
										 " longitude=\"" + addressList.get(i).longitude + "\" >\n" );
		}
		
		result = result.concat( "\t\t</profileSearcher>\n" );
		
		return result;
	}
 	
	
}






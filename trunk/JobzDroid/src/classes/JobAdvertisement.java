package classes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JobAdvertisement {

	public int 		jobAdId;
	public int		ownerID;
	public int		educationReq;
	public int		numberOfViews;
	public String	jobAdTitle;
	public String	tags;
	public String	contactInfo;
	public long		expiryDate;
	public long		startingDate;
	public long		creationDate;
	public String	status;
	public String	jobAdDescription;
	public int		isApproved; // 0 = false, 1 = true
	public int	 	hasGradFunding; // 0 = false, 1 = true
	public String	jobAvailability;
	public String   location;
	public Location adLocation;
	
	public Map<String, Object> valueMap= new HashMap<String, Object>();
	public Map<String, String> adLocMap= new HashMap<String, String>();
	public ArrayList<Location> locationList;
	
	private ArrayList<String>fieldNames=new ArrayList<String>();
		
	public JobAdvertisement(){
		//Set Default values
		isApproved = 0;
		educationReq = 0;
		hasGradFunding = 0;
		status = "inactive";
		locationList = new ArrayList<Location>();
		location="See Detail";
		initValueMap();
	}
	
	
	
	public String toXMLContent(){
		
		String eduReqFormatted;
		String creationDateFormatted;
		String startingDateFormatted;
		String expiryDateFormatted;
		
		// escape characters into valid XML
		jobAdTitle = Utility.processXMLEscapeChars(jobAdTitle);
		tags = Utility.processXMLEscapeChars(tags);
		contactInfo = Utility.processXMLEscapeChars(contactInfo);
		jobAdDescription = Utility.processXMLEscapeChars(jobAdDescription);
		String gfStr=Utility.GFConvertor(hasGradFunding);
		if(locationList.isEmpty()){
//			Location loc = new Location("Not Specified");
//			locationList.add(loc);
		}
		
		if(status == null){
			status = "N/A";
		}
		
		if(jobAvailability == null){
			jobAvailability = "Not Specified";
		}
		
		eduReqFormatted 		= Utility.degreeConvertor(educationReq);
		creationDateFormatted	= Utility.dateConvertor(creationDate);
		expiryDateFormatted 	= Utility.dateConvertor(expiryDate);
		//String jobAvail	= Utility.jobTypeTranslator(false,jobAvailability);

		
		if(startingDate == 0)
			startingDateFormatted = "N/A";
		else
			startingDateFormatted = Utility.dateConvertor(startingDate);

		
		if(eduReqFormatted == null){
			eduReqFormatted = "N/A";
		}
		
		String result =  "\t<jobAd" +
		" jobAdId=\"" 				+ jobAdId + "\"" +
		" jobAdTitle=\"" 			+ jobAdTitle + "\"" +
		" tags=\"" 					+ tags + "\"" +
		" jobAvail=\"" 				+ jobAvailability + "\"" +
		" contactInfo=\"" 			+ contactInfo + "\"" +
		
		//TODO: remove the non-formatted values from XML if not required
		" expiryDate=\"" 			+ expiryDate + "\"" +
		" startingDate=\"" 			+ startingDate + "\"" +
		" creationDate=\"" 			+ creationDate + "\"" +
		" educationReq=\"" 			+ educationReq + "\"" +
		" hasGradFunding=\"" 		+ gfStr + "\"" +
		
		/**********FORMAT FOR THE OUTPUT TO THE CLIENT *********************/
		" creationDateFormatted=\"" + creationDateFormatted + "\"" +
		" startingDateFormatted=\"" + startingDateFormatted + "\"" +
		" expiryDateFormatted=\"" + expiryDateFormatted + "\"" +
		" eduReqFormatted=\"" + eduReqFormatted + "\"" +
		/******************************************************************/
		
		" status=\"" 				+ status + "\"" +
		" numberOfViews=\"" 		+ numberOfViews + "\"" + 
		" jobAdDescription=\"" 		+ jobAdDescription + "\""  +
		" isApproved=\"" 			+ isApproved + "\" >\n";
		
		/************ Add a list of location objects to XML ***********/
		for( int i = 0 ; i < locationList.size() ; i++ ){
			String addr = locationList.get(i).address;
			addr = Utility.processXMLEscapeChars(addr);
			result=result.concat("\t\t<location address=\""  + addr + "\"" +
										 " latitude=\""  + locationList.get(i).latitude + "\"" +
										 " longitude=\"" + locationList.get(i).longitude + "\" ></location>\n" );
		}
		
		result = result.concat("\t</jobAd>\n");
		
		System.out.println("JobAdvertisement Object XML:\n" + result);
		
		return result;
		
	}
	
	public String xmlParser(){
		
		StringBuffer xmlBuf=new StringBuffer();
		String SLASH = "\\";
		String QUO = "\"";
		String TAB = "\\t";
		String NL  ="\\n";
		String EQ  ="=";
		String SP  = " ";
		xmlBuf.append("\t<jobAd" +SP);
		
		for(Map.Entry<String, Object> entry : valueMap.entrySet()){
			String fld= entry.getKey();
			Object value = entry.getValue();
			if(value!=null){
				if(fld.equals("jobAdTitle")||fld.equals("tags")||fld.equals("contactInfo")||fld.equals("jobAdDescription")){
					xmlBuf.append(fld+ EQ + QUO + Utility.processXMLEscapeChars((String) value) +QUO+SP);
				}
				else if(fld.equals("educationReq")){
					int req = Integer.parseInt(value.toString());
					xmlBuf.append(fld + EQ + QUO + Utility.degreeConvertor(req) +QUO+SP);
				}
				else if(fld.matches("(?i).*date.*")){
					Long time = Long.valueOf(value.toString());
					if(time!=0){
						xmlBuf.append(fld+ EQ + QUO + Utility.dateConvertor(time) +QUO+SP);
					}
				}
				else{
					xmlBuf.append(fld+ EQ + QUO + value +QUO+SP);
				}
				
			}
			else{
				xmlBuf.append(fld + EQ + QUO + "N/A" +QUO+SP);
			}
			//String jobAvail	= Utility.jobTypeTranslator(false,jobAvailability);
		}
		xmlBuf.append(" >\n"); //eof putting ad info
		
//		StringBuffer[] locList=new StringBuffer[3];
//		int locIdx=0;
//		xmlBuf.append("\t\t<location" + SP); //sof location info
//		if(adLocMap.size()>0){
//			for(Map.Entry<String, String> entry : adLocMap.entrySet()){
//				String fld= entry.getKey();
//				Object value = entry.getValue();
//				if(fld.matches("(?i)addr.*")){
//					locList[locIdx]=new StringBuffer(new StringBuffer("\t\t<location >\n\t\t</location>\n").insert(12, fld+EQ+QUO+value+QUO+SP));
//					locIdx++;
//				}
//				else if(fld.matches("(?i)latlng.*")){
//					int idx=Integer.parseInt(fld.substring("latlng".length()));
//					if(locList[idx]!=null){
//						locList[idx].insert(12,fld+EQ+QUO+value+QUO+SP);
//					}
//					else{
//						locList[locIdx]=new StringBuffer(new StringBuffer("\t\t<location >\n\t\t</location>\n").insert(12, fld+EQ+QUO+value+QUO+SP));
//						locIdx++;
//					}
//				}
//			}
		StringBuffer locBuf[];
		if(adLocation!=null){ //for retrieving single ad
			locBuf=adLocation.locXMLParser();
			for(StringBuffer strBuf:locBuf){
				if(strBuf!=null){
					xmlBuf.append(strBuf);
				}
			}
		}
		
		
//		else{
//			xmlBuf.append("\t\t<location noLoc=\"Location Not Specified.\">\n\t\t</location>\n");
//		}
//		xmlBuf.append(">\n \t\t</location>\n");//eof location info
		xmlBuf.append("\t</jobAd>\n");//eof xml parsing
		
		System.out.println("JobAdvertisement Object XML:\n" + xmlBuf.toString());
		
		return xmlBuf.toString();
		
	}
	private void initValueMap(){
		try {
            Class cls = Class.forName("JobAdvertisement");
            Field fieldList[] = cls.getDeclaredFields();
            for (Field field:fieldList){
            	fieldNames.add(field.getName());
            }
          }
          catch (Throwable e) {
             System.err.println(e);
          }
       for(String str: fieldNames){
    	   valueMap.put(str, null);
       }
	}
	public void resetValueMap(){
		for(Map.Entry<String, Object> entry : valueMap.entrySet()){
			entry.setValue(null);
		}
	}
}

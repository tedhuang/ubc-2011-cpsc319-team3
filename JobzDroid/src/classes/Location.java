package classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Location {

	public String address;
	public String latlng;
	public Double latitude;
	public Double longitude;
	public Map<String, String>locationMap;
	public Map<String, String> latlngMap;
	public int masterJobId;
//	public Map<String, Object>locInfoMap=new HashMap<String, Object>(); 
	
	public Location(){}
	public Location(int jobId, String addr, String latlng){
		masterJobId=jobId;
		address=addr;
		this.latlng=latlng;
	}
	public Location(int jobId, Map <String, String> locationMap){
		masterJobId=jobId;
		this.locationMap=locationMap;
	}
	public static String locXMLParser(ArrayList<Location>locList){
		String QUO="\"";
		String EQ="=";
		String SP=" ";
//		StringBuffer locBuf= new StringBuffer();
		StringBuffer topTag= new StringBuffer("\t<locationList>\n\t<locationList>\n");//insert pos:16
		
			if(!locList.isEmpty()){
				for(Location loc : locList){
					StringBuffer temp = new StringBuffer("\t\t<location >\n\t\t</location>\n");//insert pos:12
					temp.insert(12, "addr"+EQ+QUO+loc.address+QUO+SP+"latlng"+EQ+QUO+loc.latlng+QUO+SP);
					topTag.insert(16,temp);
			}
		}
			return topTag.toString();
	}
	public  StringBuffer[] locXMLParser(){
		String QUO="\"";
		String EQ="=";
		String SP=" ";
//		StringBuffer locBuf= new StringBuffer();
//		StringBuffer topTag= new StringBuffer("\t<locationList>\n\t<locationList>\n");//insert pos:16
		StringBuffer[] locList=new StringBuffer[3];
		
		int locIdx=0;
		if(!locationMap.isEmpty()){
			for(Map.Entry<String, String> entry : locationMap.entrySet()){
				String fld= entry.getKey();
				Object value = entry.getValue();
				if(value!=null){
					if(fld.matches("(?i)addr.*")){
						int idx=Integer.parseInt(fld.substring("addr".length()));
						if(locList[idx]!=null){
							locList[idx].insert(12,fld+EQ+QUO+value+QUO+SP);
						}else{
							locList[locIdx]=new StringBuffer(new StringBuffer("\t\t<location >\n\t\t</location>\n").insert(12, fld+EQ+QUO+value+QUO+SP));
							locIdx++;
						}
							
					}
					else if(fld.matches("(?i)latlng.*")){
						int idx=Integer.parseInt(fld.substring("latlng".length()));
						if(locList[idx]!=null){
							locList[idx].insert(12,fld+EQ+QUO+value+QUO+SP);
						}
						else{
							locList[locIdx]=new StringBuffer(new StringBuffer("\t\t<location >\n\t\t</location>\n").insert(12, fld+EQ+QUO+value+QUO+SP));
							locIdx++;
						}
					}
				}//eof check null value
//				StringBuffer temp = new StringBuffer("\t\t<location >\n\t\t</location>\n");//insert pos:12
//				temp.insert(12, "addr"+EQ+QUO+loc.address+QUO+SP+"latlng"+EQ+QUO+loc.latlng+QUO+SP);
//				topTag.insert(16,temp);
			}
		}
			return locList;
	}
}

package classes;

import java.util.Map;


public class Location {

	public String address;
	public Double latitude;
	public Double longitude;
	public Map<String, String>locationMap;
	public int masterJobId;
//	public Map<String, Object>locInfoMap=new HashMap<String, Object>(); 
	
	public Location(){ //TODO FIX FOR PROFILE
		address="UBC";
		latitude=0.0;
		longitude=0.0;
	}
	
	public Location(int jobId, Map <String, String> locationMap){ //for jobAd location
		masterJobId=jobId;
		this.locationMap=locationMap;
	}
	public  StringBuffer[] locXMLParser(){
		String QUO="\"";
		String EQ="=";
		String SP=" ";
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
			}
		}
			return locList;
	}
}

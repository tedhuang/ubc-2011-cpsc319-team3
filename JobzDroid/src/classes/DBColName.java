package classes;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.*;
import java.util.*;

public class DBColName{
	
//	private static Map <String, String> DBcolNameDict=new HashMap<String, String>();
	
	private final String[]tbJobAdColArray={
				"idJobAd",
				"idAccount",
				"title",
				"description", 
				"expiryDate",
				"dateStarting",
				"status",
				"contactInfo",
				"educationRequired",
				"jobAvailability",
				"tags",
				"numberOfViews",
				"isApproved"
				
	};
	private Map<String, String>tbJobAdColMap=new HashMap<String, String>();
	
	
	
	private final String[]tbProfileColArray={
			"idAccount",
			"name",
			"phone",
			"selfDescription", 
			"educationlevel",
			"startingDate"
	};
	private Map<String, String>tbProfileColMap=new HashMap<String, String>();
		
		


/*
 * PROFILE SEARCH MAPPING
 */
	private final String[]searchAdFields={
			/************************************************************************************
			 * Those are input forms' name, MUST be matched
			 * Entries Starts With NO means CLIENTS CANNOT DO SUCH REQUEST (YET)
			 ************************************************************************************/
			"searchJobId",
			"searchTitle",
			"searchStartDate",
			"searchCompany",
			"searchEduReq",
			//////job avail////////////
			"searchFT", 
			"searchPT",
			"searchIS",
			/////// job avail///////////
			"searchTags",
			
			"searchStatus",	//admin privilege
			"searchAprv"//admin privilege
			
//			"NOsearchAcctId",
//			"NOsearchDescp",
//			"NOsearchED",
//			"NOsearchNumView",
			
	};
	private Map<String, String>searchAdFieldsMap=new HashMap<String, String>();
	
	
	
	private final String[]adEditFields={
			/************************************************************************************
			 * Those are input forms' name, MUST be matched
			 * Entries ""--NULL means CLIENTS CANNOT MODIFY SUCH Field (YET)
			 ************************************************************************************/
					"title-field",
			  		"desc-field",
			  		"expireTime-field",
			  		"startTime-field",
			  		"inact-field",
			  		"company-field",
			  		"edu-field",
			  		"ft-field",//pos:9
		  			"pt-field",
		  			"is-field",//pos:11
		  			"tag-field",
		  			"adId-field"
//		  			"loc-field"//pos:15
			
	};
	private Map<String, String>adEditFieldsMap=new HashMap<String, String>();
	
	
	
/*
 * PROFILE SEARCH MAPPING
 */
	private final String[]searchProfileFields={
			/************************************************************************************
			 * Those are input forms' name, MUST be matched
			 * Entries Starts With NO means CLIENTS CANNOT DO SUCH REQUEST (YET)
			 ************************************************************************************/
			"searchName",
			"searchEducation",
			
		//TODO:figure out how to include:
			//"searchEmploymentPref", 
			//"searchLocation",

	};
	private Map<String, String>searchProfileFieldsMap=new HashMap<String, String>();
	
	
	
	public DBColName(){
		initColMap();
		makeAdPostDict();
		makeSearchAdDict();
		makeSearchProfileDict();
	}
	
	private void initColMap(){
		for(int i=0; i<tbJobAdColArray.length; i++){
			tbJobAdColMap.put(tbJobAdColArray[i], tbJobAdColArray[i]);
		}
	}
	public Map<String, String> getColNameMap(){
		return tbJobAdColMap;
	}
	public Map <String, String> getDict(String action){
		//JobAd Functions:
		if(action.equals("searchJobAdvertisement")){
			return colDictSearchJobAd;
		}
		else if( action.equals("createJobAdvertisement")
				  ||
				 action.equals("saveJobAdDraft")
				  ||
				 action.equals("editJobAd")
				  ||
				  action.equals("updateDraft"))
		{
			return colDictEditJobAd;
		}
		
		//Profile Functions:
		else if(action.equals("searchProfile")){
			return colDictSearchProfile;
		}
		
		return null;
	}
	
	
/*
 * PROFILE SEARCH MAPPING
 */
	private static Map <String, String> colDictSearchProfile=new HashMap<String,String>();
	private void makeSearchProfileDict(){ 
		for(int i=0; i<searchProfileFields.length; i++){
			searchProfileFieldsMap.put(searchProfileFields[i], searchProfileFields[i]);
		}
		
		colDictSearchProfile.put(searchProfileFieldsMap.get("searchName"), 				tbProfileColMap.get("name"));
		colDictSearchProfile.put(searchProfileFieldsMap.get("searchEducation"), 		tbProfileColMap.get("educationlevel"));
	
	//TODO: Add these:
//		colDictSearchProfile.put(searchProfileFieldsMap.get("searchLocation"), 			tbProfileColMap.get("location"));
//		colDictSearchProfile.put(searchProfileFieldsMap.get("searchEmploymentPref"), 	tbProfileColMap.get("employmentPref"));

		
		colDictSearchProfile = Collections.unmodifiableMap(colDictSearchProfile);
	}
	
	
	
	private static Map <String, String> colDictEditJobAd=new HashMap<String, String>();
	private void makeAdPostDict(){
		for(int i=0; i<adEditFields.length; i++){
			adEditFieldsMap.put(adEditFields[i], adEditFields[i]);
		}
		
		colDictEditJobAd.put(adEditFieldsMap.get("title-field"), 		tbJobAdColMap.get("title"));
		colDictEditJobAd.put(adEditFieldsMap.get("desc-field"), 		tbJobAdColMap.get("description"));
		colDictEditJobAd.put(adEditFieldsMap.get("startTime-field"),	tbJobAdColMap.get("dateStarting"));
		colDictEditJobAd.put(adEditFieldsMap.get("company-field"), 		tbJobAdColMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictEditJobAd.put(adEditFieldsMap.get("edu-field"),			tbJobAdColMap.get("educationRequired"));
		colDictEditJobAd.put(adEditFieldsMap.get("ft-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEditJobAd.put(adEditFieldsMap.get("is-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEditJobAd.put(adEditFieldsMap.get("pt-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEditJobAd.put(adEditFieldsMap.get("tag-field"), 			tbJobAdColMap.get("tags"));
		colDictEditJobAd.put(adEditFieldsMap.get("expireTime-field"),	tbJobAdColMap.get("expiryDate"));
		colDictEditJobAd.put(adEditFieldsMap.get("adId-field"),			tbJobAdColMap.get("idJobAd"));
		//Location?
//		colDictEditJobAd.put(adEditFieldsMap.get("loc-field"), 		"address");
		
		colDictEditJobAd = Collections.unmodifiableMap(colDictEditJobAd);
	}
	
	private static Map <String, String> colDictSearchJobAd=new HashMap<String, String>();
	private void makeSearchAdDict(){//CREATE AND EDIT AD
		for(int i=0; i<searchAdFields.length; i++){
			searchAdFieldsMap.put(searchAdFields[i], searchAdFields[i]);
		}
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchTitle"), 		tbJobAdColMap.get("title"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchStartDate"),		tbJobAdColMap.get("dateStarting"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchCompany"), 		tbJobAdColMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchEduReq"),		tbJobAdColMap.get("educationRequired"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchFT"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchPT"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchIS"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchTags"), 			tbJobAdColMap.get("tags"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchStatus"), 		tbJobAdColMap.get("status"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchAprv"), 			tbJobAdColMap.get("isApproved"));
		colDictSearchJobAd.put("quickSearch", "quickSearch");
		
		colDictSearchJobAd = Collections.unmodifiableMap(colDictSearchJobAd);
	}
	
	
}
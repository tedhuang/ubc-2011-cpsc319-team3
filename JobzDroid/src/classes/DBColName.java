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
		  			"loc-field"//pos:15
			
	};
	private Map<String, String>adEditFieldsMap=new HashMap<String, String>();
	
	public DBColName(){
		initColMap();
		makeAdPostDict();
		makeSearchAdDict();
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
		if(action.equals("searchJobAdvertisement")){
			return colDictSearch;
		}
		else if(action.equals("createJobAdvertisement")||action.equals("saveJobAdDraft")){
			return colDictEdit;
		}
		
		return null;
	}
	
	private static Map <String, String> colDictEdit=new HashMap<String, String>();
	private void makeAdPostDict(){
		for(int i=0; i<adEditFields.length; i++){
			adEditFieldsMap.put(adEditFields[i], adEditFields[i]);
		}
		
		colDictEdit.put(adEditFieldsMap.get("title-field"), 		tbJobAdColMap.get("title"));
		colDictEdit.put(adEditFieldsMap.get("desc-field"), 			tbJobAdColMap.get("description"));
		colDictEdit.put(adEditFieldsMap.get("startTime-field"),		tbJobAdColMap.get("dateStarting"));
		colDictEdit.put(adEditFieldsMap.get("company-field"), 		tbJobAdColMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictEdit.put(adEditFieldsMap.get("edu-field"),			tbJobAdColMap.get("educationRequired"));
		colDictEdit.put(adEditFieldsMap.get("ft-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEdit.put(adEditFieldsMap.get("is-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEdit.put(adEditFieldsMap.get("pt-field"), 			tbJobAdColMap.get("jobAvailability"));
		colDictEdit.put(adEditFieldsMap.get("tag-field"), 			tbJobAdColMap.get("tags"));
		colDictEdit.put(adEditFieldsMap.get("expireTime-field"),	tbJobAdColMap.get("expiryDate"));
		//Location?
		colDictEdit.put(adEditFieldsMap.get("loc-field"), 		"address");
		
		colDictEdit = Collections.unmodifiableMap(colDictEdit);
	}
	
	private static Map <String, String> colDictSearch=new HashMap<String, String>();
	private void makeSearchAdDict(){//CREATE AND EDIT AD
		for(int i=0; i<searchAdFields.length; i++){
			searchAdFieldsMap.put(searchAdFields[i], searchAdFields[i]);
		}
		colDictSearch.put(searchAdFieldsMap.get("searchTitle"), 		tbJobAdColMap.get("title"));
		colDictSearch.put(searchAdFieldsMap.get("searchStartDate"),		tbJobAdColMap.get("dateStarting"));
		colDictSearch.put(searchAdFieldsMap.get("searchCompany"), 		tbJobAdColMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictSearch.put(searchAdFieldsMap.get("searchEduReq"),		tbJobAdColMap.get("educationRequired"));
		colDictSearch.put(searchAdFieldsMap.get("searchFT"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearch.put(searchAdFieldsMap.get("searchPT"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearch.put(searchAdFieldsMap.get("searchIS"), 			tbJobAdColMap.get("jobAvailability"));
		colDictSearch.put(searchAdFieldsMap.get("searchTags"), 			tbJobAdColMap.get("tags"));
		colDictSearch.put(searchAdFieldsMap.get("searchStatus"), 		tbJobAdColMap.get("status"));
		colDictSearch.put(searchAdFieldsMap.get("searchAprv"), 			tbJobAdColMap.get("isApproved"));
		colDictSearch.put("quickSearch", "quickSearch");
		
		colDictSearch = Collections.unmodifiableMap(colDictSearch);
	}
	
	
}
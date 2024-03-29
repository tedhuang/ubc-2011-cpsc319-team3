package classes;

import java.util.*;

public class DBColName{
	
/**********************************************************************************************
 * 			SEARCHING AND POSTING JOB AD MAPPING 
 **********************************************************************************************/
	private final String[]tbJobAdColArray={
				"idJobAd",
				"idAccount",
				"title",
				"location",
				"description", 
				"expiryDate",
				"dateStarting",
				"datePosted",
				"status",
				"contactInfo",
				"educationRequired",
				"jobAvailability",
				"tags",
				"numberOfViews",
				"isApproved",
				"hasGradFunding"
				
	};
	private Map<String, String>tbJobAdColMap=new HashMap<String, String>();
	
	private final String[]searchAdFields={
	/******************************************************************************
	 * - Input forms' names MUST be matched
	 * - PARAMETER NAME MUST BE MATCHED
	 * Entries Starts With NO means CLIENTS CANNOT DO SUCH REQUEST (YET)
	 *****************************************************************************/
			"searchJobId",
			"searchTitle",
			"searchStartDate",
			"searchCompany",
			"searchEduReq",
			
			/////SEARCH BY LOCATION/////
			"searchJobCity",
			"searchJobProvince",
			"searchJobCountry",
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
	/***************************************************************************
	 * Those are input forms' name, MUST be matched
	 * Entries ""--NULL means CLIENTS CANNOT MODIFY SUCH Field (YET)
	 ***************************************************************************/
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
		  			"adId-field",
		  			"gf-field",
		  			"loc-field"//pos:15
			
	};
	private Map<String, String>adEditFieldsMap=new HashMap<String, String>();
	
	
	
/**********************************************************************************************
 * 				PROFILE SEARCH MAPPING
 **********************************************************************************************/
	private final String[]tbProfileColArray={
			"idAccount",
			"name",
			"phone",
			"selfDescription", 
			"educationlevel",
			"startingDate"
	};
	private Map<String, String>tbProfileColMap=new HashMap<String, String>();
	private final String[]tbProfileSearcherColArray={
			"name",
			"educationLevel",
			"location",
			"startingDate",
			"educationFormatted"
		};
	private Map<String, String>tbProfileSearcherColMap = new HashMap<String, String>();
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
	
/***********************************************************************************
 * 			JOB AD LOCATION Input and Column Name Group
 **********************************************************************************/
	private final String[]tbAdLocColArray={
			"idJobAd",
			"addr0",
			"addr1",
			"addr2", 
			"latlng0",
			"latlng1",
			"latlng2",
	};
	private Map<String, String>tbAdLocColMap=new HashMap<String, String>();
	private final String[]tbAdLocInput={
			"idJobAd",
			"addr0",
			"addr1",
			"addr2", 
			"latlng0",
			"latlng1",
			"latlng2",
	};
	private Map<String, String>tbAdLocFieldMap=new HashMap<String, String>();
	
	private final String[]searchSearcherProfileFields={
			/************************************************************************************
			 * Those are input forms' name, MUST be matched
			 * Entries Starts With NO means CLIENTS CANNOT DO SUCH REQUEST (YET)
			 ************************************************************************************/
			"searchJSName",
			"searchJSEduReq",
			"searchJSLoc",
			"searchJSStartTime",
			"searchJSEduForm",
	};
	private Map<String, String>searchSearcherProfileFieldsMap=new HashMap<String, String>();
	
/***************************************************************************************************************************
 * 	Class Field-DBColName Translation
 ***************************************************************************************************************************/
	private final String[]adAdClsFields={
			  		"jobAdId",
			 		"ownerID",
			 		"educationReq",
			 		"numberOfViews",
			 		"jobAdTitle",
			 		"tags",
			 		"contactInfo",
			 		"expiryDate",
			 		"startingDate",
			 		"creationDate",
			 		"status",
			 		"jobAdDescription",
			 		"isApproved", 
			 	 	"hasGradFunding", 
			 		"jobAvailability",
			    	"location"
	};
	private Map<String, String>jobAdClsFldMap=new HashMap<String, String>();
/***********************************************************************************
 * Constructor INIT all Dictionary
 **********************************************************************************/
	public DBColName(){
		initColMap();
		makeAdPostDict();
		makeSearchAdDict();
		makeSearchProfileDict();
		makeSearchSearcherProfileDict();
		makeAdLocDict();
		
		makeJobAdClsDict();
		
		makeSuggDict();
	}
	
	private void initColMap(){
		for(int i=0; i<tbJobAdColArray.length; i++){
			tbJobAdColMap.put(tbJobAdColArray[i], tbJobAdColArray[i]);
		}
		for(int i=0; i<tbAdLocColArray.length; i++){//init tbAdLocColMap
			tbAdLocColMap.put(tbAdLocColArray[i], tbAdLocColArray[i]);
		}
		for(int i=0; i<tbProfileSearcherColArray.length; i++){
			tbProfileSearcherColMap.put(tbProfileSearcherColArray[i], tbProfileSearcherColArray[i]);
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
				 action.equals("updateOpenAd")
				  ||
				  action.equals("updateDraftAd"))
		{
			return colDictEditJobAd;
		}
		
		//Profile Functions:
		else if(action.equals("searchProfile")){
			return colDictSearchProfile;
		}
		else if(action.equals("jobAdLocation")){
			return colDictAdLoc;
		}
		
		else if(action.equals("searchSearcherProfile")){
			return colDictSearchSearcherProfile;
		}
		else if(action.equals("getSuggestions")){
			return searcherSuggMap;
		}
		
		return null;
	}
	
	public Map <String, String> getClsDict(String clsName){
		if(clsName.equals("jobAd")){
			return colDictJobAdCls;
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
	
	private static Map<String, String> colDictSearchSearcherProfile = new HashMap<String,String>();
	private void makeSearchSearcherProfileDict(){
		for(int i=0; i<searchSearcherProfileFields.length; i++){
			searchSearcherProfileFieldsMap.put(searchSearcherProfileFields[i], searchSearcherProfileFields[i]);
		}
		colDictSearchSearcherProfile.put(searchSearcherProfileFieldsMap.get("searchJSName"),	tbProfileSearcherColMap.get("name"));
		colDictSearchSearcherProfile.put(searchSearcherProfileFieldsMap.get("searchJSEduReq"), tbProfileSearcherColMap.get("educationLevel"));
		colDictSearchSearcherProfile.put(searchSearcherProfileFieldsMap.get("searchJSLoc"), tbProfileSearcherColMap.get("location"));
		colDictSearchSearcherProfile.put(searchSearcherProfileFieldsMap.get("searchJSStartTime"), tbProfileSearcherColMap.get("startingDate"));
		colDictSearchSearcherProfile.put(searchSearcherProfileFieldsMap.get("searchJSEduForm"), tbProfileSearcherColMap.get("educationFormatted"));
		
		colDictSearchSearcherProfile = Collections.unmodifiableMap(colDictSearchSearcherProfile);
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
		colDictEditJobAd.put(adEditFieldsMap.get("gf-field"),			tbJobAdColMap.get("hasGradFunding"));
		colDictEditJobAd.put(adEditFieldsMap.get("loc-field"),			tbJobAdColMap.get("location"));
		
		
		//Location?
//		colDictEditJobAd.put(adEditFieldsMap.get("loc-field"), 		"address");
		
		colDictEditJobAd = Collections.unmodifiableMap(colDictEditJobAd);
	}
	
	private static Map <String, String> colDictSearchJobAd=new HashMap<String, String>();
	private void makeSearchAdDict(){//CREATE AND EDIT AD
		for(int i=0; i<searchAdFields.length; i++){
			searchAdFieldsMap.put(searchAdFields[i], searchAdFields[i]);
		}
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchTitle"), 			tbJobAdColMap.get("title"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchStartDate"),		tbJobAdColMap.get("dateStarting"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchCompany"), 			tbJobAdColMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchEduReq"),			tbJobAdColMap.get("educationRequired"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchFT"), 				tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchPT"), 				tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchIS"), 				tbJobAdColMap.get("jobAvailability"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchTags"), 			tbJobAdColMap.get("tags"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchStatus"), 			tbJobAdColMap.get("status"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchAprv"), 			tbJobAdColMap.get("isApproved"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchJobCity"), 			tbJobAdColMap.get("location"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchJobProvince"), 		tbJobAdColMap.get("location"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchJobCountry"), 		tbJobAdColMap.get("location"));
		colDictSearchJobAd.put(searchAdFieldsMap.get("searchJobZip"), 			tbJobAdColMap.get("location"));
		
		colDictSearchJobAd.put("quickSearch", "quickSearch");
		
		colDictSearchJobAd = Collections.unmodifiableMap(colDictSearchJobAd);
	}
	
	
	private static Map <String, String> colDictAdLoc=new HashMap<String, String>();
	private void makeAdLocDict(){//CREATE AND EDIT AD
		for(int i=0; i<tbAdLocInput.length; i++){
			tbAdLocFieldMap.put(tbAdLocInput[i], tbAdLocInput[i]);
		}
		colDictAdLoc.put(tbAdLocFieldMap.get("idJobAd"), 			tbAdLocColMap.get("idJobAd"));
		colDictAdLoc.put(tbAdLocFieldMap.get("addr0"),				tbAdLocColMap.get("addr0"));
		colDictAdLoc.put(tbAdLocFieldMap.get("addr1"), 				tbAdLocColMap.get("addr1"));//TODO CHANGE DB COL
		colDictAdLoc.put(tbAdLocFieldMap.get("addr2"),				tbAdLocColMap.get("addr2"));
		colDictAdLoc.put(tbAdLocFieldMap.get("latlng0"), 			tbAdLocColMap.get("latlng0"));
		colDictAdLoc.put(tbAdLocFieldMap.get("latlng1"), 			tbAdLocColMap.get("latlng1"));
		colDictAdLoc.put(tbAdLocFieldMap.get("latlng2"), 			tbAdLocColMap.get("latlng2"));
		
		colDictAdLoc = Collections.unmodifiableMap(colDictAdLoc);
	}
	
	private static Map <String, String> colDictJobAdCls=new HashMap<String, String>();
	private void makeJobAdClsDict(){
		for(int i=0; i<adAdClsFields.length; i++){
			jobAdClsFldMap.put(adAdClsFields[i], adAdClsFields[i]);
		}
		
		colDictJobAdCls.put(tbJobAdColMap.get("idJobAd"),				jobAdClsFldMap.get("jobAdId"));
		colDictJobAdCls.put(tbJobAdColMap.get("idAccount"),				jobAdClsFldMap.get("ownerID"));
		colDictJobAdCls.put(tbJobAdColMap.get("location"),				jobAdClsFldMap.get("location"));
		colDictJobAdCls.put(tbJobAdColMap.get("title"),					jobAdClsFldMap.get("jobAdTitle") );
		colDictJobAdCls.put(tbJobAdColMap.get("description"),			jobAdClsFldMap.get("jobAdDescription") );
		colDictJobAdCls.put(tbJobAdColMap.get("dateStarting"),			jobAdClsFldMap.get("startingDate"));
		colDictJobAdCls.put(tbJobAdColMap.get("contactInfo"),			jobAdClsFldMap.get("contactInfo"));//TODO CHANGE DB COL
		colDictJobAdCls.put(tbJobAdColMap.get("educationRequired"),		jobAdClsFldMap.get("educationReq")	);
		colDictJobAdCls.put( tbJobAdColMap.get("jobAvailability"),		jobAdClsFldMap.get("jobAvailability"));
		colDictJobAdCls.put( tbJobAdColMap.get("tags"),					jobAdClsFldMap.get("tag-field"));
		colDictJobAdCls.put(tbJobAdColMap.get("expiryDate"),			jobAdClsFldMap.get("expiryDate"));
		colDictJobAdCls.put(tbJobAdColMap.get("hasGradFunding"),		jobAdClsFldMap.get("hasGradFunding"));
		colDictJobAdCls.put(tbJobAdColMap.get("status"),				jobAdClsFldMap.get("status"));
		colDictJobAdCls.put(tbJobAdColMap.get("tags"),					jobAdClsFldMap.get("tags"));
		colDictJobAdCls.put(tbJobAdColMap.get("isApproved"),			jobAdClsFldMap.get("isApproved"));
		colDictJobAdCls.put(tbJobAdColMap.get("datePosted"),			jobAdClsFldMap.get("creationDate"));
		
		colDictJobAdCls = Collections.unmodifiableMap(colDictJobAdCls);
	}
	
	private static Map <String, String> searcherSuggMap=new HashMap<String, String>();
	private void makeSuggDict(){
		
		searcherSuggMap.put(tbProfileSearcherColMap.get("educationLevel"),		tbJobAdColMap.get("educationRequired"));
		searcherSuggMap.put(tbProfileSearcherColMap.get("location"),			tbJobAdColMap.get("location"));
		searcherSuggMap.put(tbProfileSearcherColMap.get("startingDate"),		tbJobAdColMap.get("dateStarting") );
		
		searcherSuggMap = Collections.unmodifiableMap(searcherSuggMap);
	}
}
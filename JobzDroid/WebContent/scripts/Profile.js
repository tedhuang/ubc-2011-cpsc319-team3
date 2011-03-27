function ParseXMLResponse(responseXML)
{
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 	 
	 var response_text = "<h2>AJAX XML response from server: ";
	 response_text += message + "</h2>";

	 return response_text;
}

function loadProfileDetails(responseXML){
	
	var profile = responseXML.getElementsByTagName("profile").item(0);
	
	if( profile.getAttribute("accountType") == "searcher" ) {
		document.getElementById("name").innerHTML
			= profile.getAttribute("name");
		
		document.getElementById("email").innerHTML
			= profile.getAttribute("email");
		
		document.getElementById("secondaryEmail").innerHTML 
			= profile.getAttribute("secondaryEmail");
		
		document.getElementById("educationFormatted").innerHTML 
			= profile.getAttribute("educationFormatted");
		
		document.getElementById("empPref").innerHTML 
			= profile.getAttribute("empPref");
		
		document.getElementById("address").innerHTML 
			= profile.getAttribute("address");
		
		document.getElementById("startingDate").innerHTML 
			= profile.getAttribute("startingDate");
		
		document.getElementById("selfDescription").innerHTML 
			= profile.getAttribute("selfDescription");
		
	}
	else if ( profile.getAttribute("accountType") == "poster" ) {
		
		//TODO implement for poster
	}
	//TODO handle errors for invalid account type
}



function submitChangeProfile(accountType){
	
	//disables button to prevent multiple submit
	//document.getElementById("submitProfileButton").disabled=true;
	
	var sessionKey = $("#sessionKey").val();
		
	//Profile Changes
	var strName 		= document.getElementById("name").value;
	var strPhone 		= document.getElementById("phone").value;
	var strDescripton 	= document.getElementById("selfDescription").value;
	
	var strAddress 		= getAddress();
	var strLong			= getLongitude();
	var strLat			= getLatitude();
	
	if (accountType == "searcher"){
		var boolEmpPrefPT			= document.getElementById("partTimeCheck").checked;
		var boolEmpPrefFT			= document.getElementById("fullTimeCheck").checked;
		var boolEmpPrefIn			= document.getElementById("internCheck").checked;
		
		var strPreferredStartDate 	= document.getElementById("startingDate").value;
		var intEducationLevel 		= document.getElementById("educationLevel").value;
		
		
		var strDatePattern = /^\d{2}\/\d{2}\/\d{4}$/;
		if( strPreferredStartDate && strPreferredStartDate != "" ) {
			if(strDatePattern.test(strPreferredStartDate) == false){
				$("#startingDateError").text("Invalid date format");
				alert("Invalid Date Format");
				return;
			}
			else
				$("#startingDateError").text("");
		}
		else{
			$("#startingDateError").text("Please input a preferred starting date");
			alert("Please Input a preffered starting date");
		}
		
	}
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	
	request = new Request;
	request.addAction("editProfile");
	request.addSessionKey(sessionKey);
	
	request.addParam("name",strName);
	request.addParam("phone",strPhone);
	request.addParam("descripton",strDescripton);
	
	request.addParam("address", strAddress);
	request.addParam("longitude", strLong);
	request.addParam("latitude", strLat);
	
	if(accountType == "searcher"){
		request.addParam("empPrefPT", boolEmpPrefPT);
		request.addParam("empPrefFT", boolEmpPrefFT);
		request.addParam("empPrefIn", boolEmpPrefIn);
		request.addParam("educationLevel",intEducationLevel);
		request.addParam("preferredStartDate", strPreferredStartDate); 
	}
	

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());


	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //parse XML response from server
		  	//TODO: implement response
		  alert((xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue);
		  getSearcherProfileBySessionKey("profileTable", "profileHeading", "fileTable");
		  
	    }
	  };	
}




function getProfileById(idAccount, outputDiv, heading){
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getProfileById");
	request.addSessionKey(strSessionKey);
	request.addParam("idAccount", idAccount);

	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  xmlhttp = new XMLHttpRequest();
	else
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  
	xmlhttp.onreadystatechange = function(){
	  if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			//you can find this in uiBot.js under scripts folder
			buildProfileTb("profile", outputDiv, heading);
	    }
	  };	
	  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
}

function getProfileBySessionKey(outputDiv, heading){
  
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getProfileBySessionKey");
	request.addSessionKey(strSessionKey);

	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  xmlhttp = new XMLHttpRequest();
	else
	  xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange = function()
	  {
	  if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
	    {
		  //you can find this in uiBot.js under scripts folder
		  buildProfileEditTb("profile", outputDiv, heading);
	    }
	  };	
}

/*******************************************************************************************************************
 * 				searchSearcherProfile Function
 * outputDiv => The table container div
 * 
 *******************************************************************************************************************/
function searchSearcherProfile(outputDiv){
	
	request = new Request;
	request.addAction("searchSearcherProfile");
	
	var searchFields = $(":input", "#advSearchForm").serializeArray();
	var emptyCounts=0;
	jQuery.each(searchFields, function(i, field){
        if(field.value == ""){
        	emptyCounts++;
        }
        else{
        	request.addParam(field.name, field.value); //add parameter to the request according to how many search criteria filled
        }
	   });
	
	   if(emptyCounts != searchFields.length){//Check if All NULL
			if (window.XMLHttpRequest)
			  {// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlhttp=new XMLHttpRequest();
			  }
			else
			  {// code for IE6, IE5
				xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  }
			
			//send the parameters to the servlet with POST
			$("#feedback").html("<h2>Sending Request</h2>");
			xmlhttp.open("POST","../ServletProfile" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.send(request.toString());
			
			xmlhttp.onreadystatechange=function()
			  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
					//parse XML response from server
					buildJSListTb("profile", outputDiv);
		
			    }
			  };
	   }//ENDOF CHECK-ALL-NULL
	   else{
		   $("#feedback").html('<h2 class="info">Please enter Condition to search</h2>');
	   }
		  
}

/************************************************************************************************************
 * 				View Job Searcher Detail
 * - View Job Searcher Profile
 * 
 ************************************************************************************************************/
function getProfileSearcherById(mode, id, outputDiv)
{
//	$.fn.smartLightBox.openDivlb("edAdFrame", 'load','loading data...');
	request = new Request;
	request.addAction("getProfileSearcherById");
	request.addParam("accountId", id);
	var fb = $(".feedback", "#"+outputDiv);
	//change the text while sending the request
	fb.html("<h2>Sending getProfileSearcherById Request</h2>");
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		    //parse XML response from server
		  fb.html("<h2 class='good'> Successfully finished tasks</h2>");	
		  if(mode=="detail"){
		  	buildSearcherDetailTable("profile", outputDiv);
		  }
	    }
	  else if(xmlhttp.status!=200){
		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
	  }
	  };
}

/**************
 * 
 *DEPRECATED:
 *
 **************/
//
//function createProfile(){
//	//disables button to prevent multiple submit
//	document.getElementById("submitProfile").disabled=true;
//	
//	
//	var strSearcherPhone = document.getElementById("searcherPhone").value;
//	var strSearcherDescripton = document.getElementById("searcherDescripton").value;
//	var strEmpPref = document.getElementById("empPref").value;
//	var intEducationLevel = document.getElementById("educationLevel").value;
//	
//	
//	var strPosterName = document.getElementById("posterName").value;
//	var strPosterPhone = document.getElementById("posterPhone").value;
//	var strPosterDescripton = document.getElementById("posterDescripton").value;
//	//var strAffiliation = document.getElementById("affiliation").value;
//	
//	//Poster = 1, Searcher = 2
//	var intAccountType = document.getElementById("accountType").value; 
//	var intAccountID = document.getElementById("accountID").value;
//	
//	//Check for required fields:
//	if(name == null){
//		alert("Must Enter your name!");
//		document.getElementById("newJobAdButton").disabled=false;
//		return;
//	}
//
//	var xmlHttpReq;
//	if (window.XMLHttpRequest)
//	  {// code for IE7+, Firefox, Chrome, Opera, Safari
//	  xmlhttp=new XMLHttpRequest();
//	  }
//	else
//	  {// code for IE6, IE5
//	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
//	  }
//	  
//	xmlhttp.onreadystatechange=function()
//	  {
//	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
//	    {
//		    //parse XML response from server
//		    var responseText= ParseXMLResponse(xmlhttp.responseXML);
//	    	document.getElementById("feedback").innerHTML=responseText;
//
//	    }
//	  };
//	
//	
//	request = new Request;
//	request.addAction("createProfile");
//	request.addSessionKey("1234"); //TODO integrate session key
//	request.addParam("accountType", intAccountType); 	//Poster = 1, Searcher = 2
//	request.addParam("accountID", intAccountID);
//	
//	//alert("Account Type: " + intAccountType);
//	if(intAccountType == "2" ){
//		alert("account type = searcher");
//		request.addParam("searcherName",strSearcherName);
//		request.addParam("searcherPhone",strSearcherPhone);
//		request.addParam("searcherDescripton",strSearcherDescripton);
//		request.addParam("empPref",strEmpPref);
//		request.addParam("educationLevel",intEducationLevel);
//		
//	}else if(intAccountType == "1" ){
//		alert("account type = poster");
//		request.addParam("posterName",strPosterName);
//		request.addParam("posterPhone",strPosterPhone);
//		request.addParam("posterDescription",strPosterDescripton);
//		//request.addParam("affiliation",strAffiliation);
//	}
//	else{
//		alert("Please select an account type before submitting");
//		document.getElementById("submitProfile").disabled=false;
//		return;
//	}
//		
//
//	//send the parameters to the servlet with POST
//	xmlhttp.open("POST","../ServletProfile" ,true);
//	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
//	xmlhttp.send(request.toString());
//
//	//change the text while sending the request
//	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
//	
//}



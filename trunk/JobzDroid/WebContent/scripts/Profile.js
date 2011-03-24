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
	document.getElementById("submitProfileButton").disabled=true;
	
	var sessionKey = $("#sessionKey").val();
		
	//Profile Changes
	var strName 		= document.getElementById("name").value;
	var strSecEmail		= document.getElementById("secondaryEmail").value;
	var strPhone 		= document.getElementById("phone").value;
	var strDescripton 	= document.getElementById("selfDescription").value;
	
	var strAddress 		= getAddress();
	var strLong			= getLongitude();
	var strLat			= getLatitude();
	
	if (accountType == "searcher"){
		var boolEmpPrefPT			= document.getElementById("partTimeCheck").checked;
		var boolEmpPrefFT			= document.getElementById("fullTimeCheck").checked;
		var boolEmpPrefIn			= document.getElementById("internCheck").checked;
		
		var strPreferredStartDate 	= document.getElementById("startingDate");
		var intEducationLevel 		= document.getElementById("educationLevel").value;
		
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
	request.addParam("secEmail",strSecEmail);
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
		request.addParam("preferredStartDate", strPreferredStartDate); //This is taken as a long
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
		  
	    }
	  };	
}




function getProfileById(idAccount, outputDiv, heading){
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getProfileById");
	request.addSessionKey(strSessionKey);
	request.addParam("accountID", idAccount);

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
//	    	loadProfileDetails(xmlhttp.responseXML);
		  //you can find this in uiBot.js under scripts folder
		  buildProfileEditTb("profile", outputDiv, heading);
		  	
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



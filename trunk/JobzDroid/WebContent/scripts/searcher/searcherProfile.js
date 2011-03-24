//function ParseXMLResponse(responseXML)
//{
//	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
//	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
//	 	 
//	 var response_text = "<h2>AJAX XML response from server: ";
//	 response_text += message + "</h2>";
//
//	 return response_text;
//}
//
//function loadProfileDetails(responseXML){
//	
//	var profile = responseXML.getElementsByTagName("profile").item(0);
//	
//	if( profile.getAttribute("accountType") == "searcher" ) {
//		document.getElementById("name").innerHTML
//			= profile.getAttribute("name");
//		
//		document.getElementById("email").innerHTML
//			= profile.getAttribute("email");
//		
//		document.getElementById("secondaryEmail").innerHTML 
//			= profile.getAttribute("secondaryEmail");
//		
//		document.getElementById("educationFormatted").innerHTML 
//			= profile.getAttribute("educationFormatted");
//		
//		document.getElementById("empPref").innerHTML 
//			= profile.getAttribute("empPref");
//		
//		document.getElementById("address").innerHTML 
//			= profile.getAttribute("address");
//		
//		document.getElementById("startingDate").innerHTML 
//			= profile.getAttribute("startingDate");
//		
//		document.getElementById("selfDescription").innerHTML 
//			= profile.getAttribute("selfDescription");
//		
//	}
//
//	//TODO handle errors for invalid account type
//}


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


function getSearcherProfileBySessionKey(profileOutputDiv, profileHeading, fileOutputDiv ){
  
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
		  buildProfileSearcherEditTb("profile", profileOutputDiv, profileHeading);
		  listUserFiles(fileOutputDiv);
	    }
	  };	
}

function listUserFiles( outputDiv ) {
	var sessionKey = document.getElementById("sessionKey").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	var Params = "action=listUserDocuments" + "&sessionKey=" + sessionKey;
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletDocument" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params); 
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  buildSearcherFileTb( "file", outputDiv );
		    //Gets sessionKey and prints it to div
	    }
	  };
	
}





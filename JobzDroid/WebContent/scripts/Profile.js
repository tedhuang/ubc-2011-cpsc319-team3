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



function createProfile(){
	//disables button to prevent multiple submit
	document.getElementById("submitProfile").disabled=true;
	
	
	var strSearcherPhone = document.getElementById("searcherPhone").value;
	var strSearcherDescripton = document.getElementById("searcherDescripton").value;
	var strEmpPref = document.getElementById("empPref").value;
	var intEducationLevel = document.getElementById("educationLevel").value;
	
	
	var strPosterName = document.getElementById("posterName").value;
	var strPosterPhone = document.getElementById("posterPhone").value;
	var strPosterDescripton = document.getElementById("posterDescripton").value;
	//var strAffiliation = document.getElementById("affiliation").value;
	
	//Poster = 1, Searcher = 2
	var intAccountType = document.getElementById("accountType").value; 
	var intAccountID = document.getElementById("accountID").value;
	
	//Check for required fields:
	if(name == null){
		alert("Must Enter your name!");
		document.getElementById("newJobAdButton").disabled=false;
		return;
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
	  
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //parse XML response from server
		    var responseText= ParseXMLResponse(xmlhttp.responseXML);
	    	document.getElementById("feedback").innerHTML=responseText;

	    }
	  };
	
	
	request = new Request;
	request.addAction("createProfile");
	request.addSessionID("1234"); //TODO integrate session key
	request.addParam("accountType", intAccountType); 	//Poster = 1, Searcher = 2
	request.addParam("accountID", intAccountID);
	
	//alert("Account Type: " + intAccountType);
	if(intAccountType == "2" ){
		alert("account type = searcher");
		request.addParam("searcherName",strSearcherName);
		request.addParam("searcherPhone",strSearcherPhone);
		request.addParam("searcherDescripton",strSearcherDescripton);
		request.addParam("empPref",strEmpPref);
		request.addParam("educationLevel",intEducationLevel);
		
	}else if(intAccountType == "1" ){
		alert("account type = poster");
		request.addParam("posterName",strPosterName);
		request.addParam("posterPhone",strPosterPhone);
		request.addParam("posterDescription",strPosterDescripton);
		//request.addParam("affiliation",strAffiliation);
	}
	else{
		alert("Please select an account type before submitting");
		document.getElementById("submitProfile").disabled=false;
		return;
	}
		

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
}




function editProfile(){
	
	//disables button to prevent multiple submit
	document.getElementById("submitEdit").disabled=true;
	
	
	var strSearcherName =  document.getElementById("searcherName").value;
	var strSearcherPhone = document.getElementById("searcherPhone").value;
	var strSearcherDescripton = document.getElementById("searcherDescripton").value;
	var strEmpPref = document.getElementById("empPref").value;
	var strPreferredStartDate = document.getElementById("startDate");
	var intEducationLevel = document.getElementById("educationLevel").value;
	
	
	var strPosterName = document.getElementById("posterName").value;
	var strPosterPhone = document.getElementById("posterPhone").value;
	var strPosterDescripton = document.getElementById("posterDescripton").value;
	
	//Poster = 1, Searcher = 2
	var intAccountType = document.getElementById("accountType").value; 
	var intAccountID = document.getElementById("accountID").value;
	
	//Check for required fields:
	if(name == null){
		alert("Must Enter your name!");
		document.getElementById("newJobAdButton").disabled=false;
		return;
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
	  
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //parse XML response from server
		    var responseText= ParseXMLResponse(xmlhttp.responseXML);
	    	document.getElementById("feedback").innerHTML=responseText;

	    }
	  };
	
	
	request = new Request;
	request.addAction("editProfile");
	request.addSessionID("1234"); //TODO integrate session key
	request.addParam("accountType", intAccountType); 	//Poster = 1, Searcher = 2
	request.addParam("accountID", intAccountID);
	
	//alert("Account Type: " + intAccountType);
	if(intAccountType == "2" ){
		alert("account type = searcher");
		request.addParam("searcherName",strSearcherName);
		request.addParam("searcherPhone",strSearcherPhone);
		request.addParam("searcherDescripton",strSearcherDescripton);
		request.addParam("empPref",strEmpPref);
		request.addParam("educationLevel",intEducationLevel);
		
	}else if(intAccountType == "1" ){
		alert("account type = poster");
		request.addParam("posterName",strPosterName);
		request.addParam("posterPhone",strPosterPhone);
		request.addParam("posterDescription",strPosterDescripton);
	}
	else{
		alert("Please select an account type before submitting");
		document.getElementById("submitProfile").disabled=false;
		return;
	}
	

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
	
}

function getProfileById(){
	
	document.getElementById("getProfileButton").disabled=true;
	var intAccountID = document.getElementById("accountID").value;
	
	request = new Request;
	request.addAction("getProfileById");
	request.addSessionKey("1234"); //TODO: CHANGE THIS
	request.addParam("accountID", intAccountID);

	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //parse XML response from server
		    var responseText= ParseXMLResponse(xmlhttp.responseXML);
	    	document.getElementById("feedback").innerHTML=responseText;
	    	loadProfileDetails(xmlhttp.responseXML);
	    }
	  };	
	  
		//send the parameters to the servlet with POST
		xmlhttp.open("POST","../ServletProfile" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(request.toString());

		//change the text while sending the request
		document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
		
	  //TODO parse returned profile XML
}

function getProfileBySessionKey(outputDiv, heading){
  
	var sessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getProfileBySessionKey");
	request.addSessionKey(sessionKey);

	var xmlHttpReq;
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
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
//	    	loadProfileDetails(xmlhttp.responseXML);
		  buildProfileTb("profile", outputDiv, heading);
		  	
	    }
	  };	
}
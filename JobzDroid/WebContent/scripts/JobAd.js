function ParseXMLResponse(responseXML)
{
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 
	 var response_text = "<h2>AJAX XML response from server: ";
	 response_text += result + " " + message + "</h2>";

	 return response_text;
}

function loadJobAdDetails( responseXML ){
	
	
}



function getJobAdvertisement()
{
	
	
	
}


function createJobAdvertisement()
{
	var strTitle = document.getElementById("jobTitle").value;
	var strDescription = document.getElementById("jobDescription").value;
	var educationRequirement = document.getElementById("educationRequirement").value;
	//var strJobLocation = document.getElementById("jobLocation").value;
	var strContactInfo = document.getElementById("contactInfo").value;
	var strTags = document.getElementById("tags").value;
	
	var expiryYear = document.getElementById("expiryYear").value;
	var expiryMonth = document.getElementById("expiryMonth").value;
	var expiryDay = document.getElementById("expiryDay").value;
	
	var startingDay = document.getElementById("startingDay").value;
	var startingMonth = document.getElementById("startingMonth").value;
	var startingYear = document.getElementById("startingYear").value;
	
	
	//User Input Check:
	if(strTitle == null){
		alert("Must Enter Job Advertisement Title!");
		return;
	}
	document.getElementById("newJobAdButton").disabled=true;
	
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
	//TODO fix sessionID
	request.addAction("createJobAdvertisement");
	request.addSessionID("1234"); //TODO implement this
	request.addParam("strTitle", strTitle);
	request.addParam("strDescription", strDescription);
	request.addParam("educationRequirement", educationRequirement);
	//request.addParam("strJobLocation", strJobLocation);
	request.addParam("strContactInfo", strContactInfo);
	request.addParam("strTags", strTags);
	request.addParam("expiryYear", expiryYear);
	request.addParam("expiryMonth", expiryMonth);
	request.addParam("expiryDay", expiryDay);
	request.addParam("startingDay", startingDay);
	request.addParam("startingMonth", startingMonth);
	request.addParam("startingYear", startingYear);
	
		  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";

}




function searchJobAdvertisement(){
	
	var strSearchText = document.getElementById("searchText").value;
	var searchEducationReq = document.getElementById("searchEducationReq").value;
	var strSearchJobLoc = document.getElementById("searchJobLoc").value;
	var strTags = document.getElementById("tags").value;
	
	

	
	
	//User Input Check:
	if(strTitle == null){
		alert("Must Enter Job Advertisement Title!");
		return;
	}
	document.getElementById("newJobAdButton").disabled=true;
	
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
//		    var responseText= ParseXMLResponse(xmlhttp.responseXML);
//	    	document.getElementById("feedback").innerHTML=responseText;

	    }
	  };
	  
	  
	
	
	request = new Request;
	//TODO fix sessionID
	request.addAction("searchJobAdvertisement");
	request.addSessionID("1234"); //TODO implement this
	request.addParam("strTitle", strTitle);
	request.addParam("educationRequirement", searchEducationReq);
	request.addParam("strJobLocation", strSearchJobLoc);
	request.addParam("strTags", strTags);
	request.addParam("searchText", strSearchText);
	//request.addParam("strDescription", strDescription);
		  
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	  
}














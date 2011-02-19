
function ParseXMLResponse(responseXML)
{
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 
	 var xml_response_text = "<h2>AJAX XML response from server: ";
	 responseText += result + " " + message + "</h2>";

	 return xml_response_text;
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







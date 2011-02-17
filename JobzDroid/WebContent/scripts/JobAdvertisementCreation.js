function ParseXMLResponse(responseXML)
{
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 
	 var xml_response_text = "<h2>AJAX XML response from server: ";
	 responseText += result + " " + message + "</h2>";

	 return xml_response_text;
}


/***********************************************
* Drop Down Date select script- by JavaScriptKit.com
* This notice MUST stay intact for use
* Visit JavaScript Kit at http://www.javascriptkit.com/ for this script and more
***********************************************/
var monthtext=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec'];
function populatedropdown(dayfield, monthfield, yearfield){
	var today=new Date()
	var dayfield=document.getElementById(dayfield)
	var monthfield=document.getElementById(monthfield)
	var yearfield=document.getElementById(yearfield)
	for (var i=0; i<31; i++)
	dayfield.options[i]=new Option(i, i+1)
	dayfield.options[today.getDate()]=new Option(today.getDate(), today.getDate(), true, true) //select today's day
	for (var m=0; m<12; m++)
	monthfield.options[m]=new Option(monthtext[m], monthtext[m])
	monthfield.options[today.getMonth()]=new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true) //select today's month
	var thisyear=today.getFullYear()
	for (var y=0; y<20; y++){
	yearfield.options[y]=new Option(thisyear, thisyear)
	thisyear+=1
	}
	yearfield.options[0]=new Option(today.getFullYear(), today.getFullYear(), true, true) //select today's year
}



function createJobAdvertisement()
{
	var strTitle = document.getElementById("jobTitle").value;
	var strDescription = document.getElementById("jobDescription").value;
	var educationRequirement = document.getElementById("educationRequirement").value;
	var strJobLocation = document.getElementById("jobLocation").value;
	var strContactInfo = document.getElementById("contactInfo").value;
	//var startingDate = TODO: add this when it is implemented
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
//		    var responseText= auctionParseXMLResponse(xmlhttp.responseXML);
//	    	document.getElementById("feedback").innerHTML=responseText;

	    }
	  };
	
	  
//	var params = "manager=" + "JobAdvertismentManager" + "&function=" + "createJobAdvertisement"
//				+ "&strTitle=" + strTitle + "&strDescription=" + strDescription
//				+ "&educationRequirement=" + educationRequirement + "&strJobLocation=" + strJobLocation 
//				+ "&strContactInfo=" + strContactInfo +"&strTags=" + strTags
//				+ "&ExpiryWeek=" + ExpiryWeek + "&ExpiryDay=" + ExpiryDay;
		
	
	request = new Request;
	//TODO fix sessionID
	request.addAction("createJobAdvertisement");
	request.addSessionID("1234");
	request.addParam("strTitle", strTitle);
	request.addParam("strDescription", strDescription);
	request.addParam("educationRequirement", educationRequirement);
	request.addParam("strJobLocation", strJobLocation);
	request.addParam("strContactInfo", strContactInfo);
	request.addParam("strTags", strTags);
	request.addParam("expiryYear", expiryYear);
	request.addParam("expiryMonth", expiryMonth);
	request.addParam("expiryDay", expiryDay);
	request.addParam("startingDay", startingDay);
	request.addParam("startingMonth", startingMonth);
	request.addParam("startingYear", startingYear);
	
		  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAdvertisement" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";

}
















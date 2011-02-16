



function createJobAdvertisement()
{
	var strTitle = document.getElementById("jobTitle").value;
	var strDescription = document.getElementById("jobDescription").value;
	var educationRequirement = document.getElementById("educationRequirement").value;
	var strJobLocation = document.getElementById("jobLocation").value;
	var strContactInfo = document.getElementById("contactInfo").value;
	//var startingDate = TODO: add this when it is implemented
	var strTags = document.getElementById("tags").value;
	var ExpiryWeek = document.getElementById("expiryWeek").value;
	var ExpiryDay = document.getElementById("expiryDay").value;
	
	
	//User Input Check:
	if(strTitle == null){
		alert("Must Enter Job Advertisement Title!");
		return;
	}

	document.getElementById("newJobAdButton").disabled=true;
	
	
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
		    var responseText= auctionParseXMLResponse(xmlhttp.responseXML);
		   	
		    
	    	document.getElementById("feedback").innerHTML=responseText;

	    }
	  };
	
	  
	var params = "manager=" + "JobAdvertismentManager" + "function= " + "createJobAdvertisement"
				+ "strTitle=" + strTitle + "&strDescription=" + strDescription
				+ "&educationRequirement=" + educationRequirement + "&strJobLocation=" + strJobLocation 
				+ "&strContactInfo=" + strContactInfo +"&strTags=" + strTags
				+ "&ExpiryWeek=" + ExpiryWeek + "&ExpiryDay=" + ExpiryDay;
		
		
		
		  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletHTTPS" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(params);

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";

}
















function jobAdReqDispatcher(req, outputDiv)
{
	if(outputDiv!=null){
		switch (req) 
		{
			case loadAdList:
				
				loadAdList(outputDiv);
				break;
				
				
			default:
				break;
		}
	}
}

function loadAdList(outputDiv){
	//TODO Testing ONLY, RM after testing
	$("#ListJobAdButton").attr("disabled", true);
	
	request = new Request;
	request.addAction("loadAdList");
	request.addSessionID("1234"); //TODO: change this
	request.addParam("idJobAd", $("#jobAdId").val());
	

	//change the text while sending the request
	$("#feedback").html("<h2>Sending getJobAdById Request</h2>");
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6-
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
 
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function(){
		if (xmlhttp.readyState==4 && xmlhttp.status==200){
		    //parse XML response from server
			var tbody = $("tbody", outputDiv).html("");
	    	$("#feedback").html("<h2>Successfully finished tasks</h2>");
	    	$("jobAd",xmlhttp.responseXML).each(function() {//for All returned results
	    		  var jobAd = $(this);
	    		  var rowText = "<tr><td>"  + jobAd.attr("creationDate") + 
	    		  				"</td><td>" + jobAd.attr("jobAdTitle") 	 + 
	    		  				"</td><td>" + jobAd.attr("contactInfo")  + 
	    		  				"</td><td>" + jobAd.attr("educationReq") + "</td></tr>";
	    		  $(rowText).appendTo(tbody);
	    		   
	    		  
	    		});
	    	$("#ListJobAdButton").attr("disabled", false);

	    }
	  };
	  
}

function loadJobAdDetails( responseXML ){
	
	var jobAd = responseXML.getElementsByTagName("jobAd").item(0);
	
        var jobAdId			=	jobAd.getAttribute("jobAdId");
        var jobAdTitle		=	jobAd.getAttribute("jobAdTitle");
        var location		=	jobAd.getAttribute("location");
        var tags			=	jobAd.getAttribute("tags");
        var contactInfo		=	jobAd.getAttribute("contactInfo");
        var expiryDateMs	=	jobAd.getAttribute("expiryDate");
        var startingDateMs	=	jobAd.getAttribute("startingDate");
        var creationDateMs	=	jobAd.getAttribute("creationDate");
        var status			=	jobAd.getAttribute("status");
        var numberOfViews	=	jobAd.getAttribute("numberOfViews");
        var educationReq	=	jobAd.getAttribute("educationReq");
        var jobAdDescription=	jobAd.getAttribute("jobAdDescription");
        var isApproved		=	jobAd.getAttribute("isApproved");
        
        
        if(educationReq == 3){
        	educationReq = "Ph.D.";
        } 
        else if(educationReq = 2){
        	educationReq = "M.Sc.";
        } 
        else if(educationReq = 1){
        	educationReq = "B.Sc.";
        } else{
        	educationReq = "Not Specified";
        }
        
        
        var expiryDate = new Date(expiryDateMs);
        var startingDate = new Date(startingDateMs);
        var creationDate = new Date(creationDateMs);
        
        alert(expiryDate);
        
		
		$("#jobAdId").innerHTML=jobAdId;
		$("#jobTitle").val(jobAdTitle);
//		document.getElementById("location").innerHTML =location;
		$("#tags").val(tags);
		$("#contactInfo").val( contactInfo);
		$("#status").innerHTML = status;
		$("#numViews").innerHTML = numberOfViews; 
		$("#educationReq").innerHTML = educationReq;
		$("#jobDescription").val( jobAdDescription);
		$("#isApproved").innerHTML = isApproved;
		
		$("expiryDate").innerHTML = expiryDate;
		$("startingDate").innerHTML = startingDate;
		$("creationDate").innerHTML = creationDate;

		
}



function getJobAdById()
{
	//TODO Testing ONLY, RM after testing
	//$("#getJobAdButton").attr("disabled", true);
	var intJobAdId = document.getElementById("jobAdId").value;
	
	request = new Request;
	request.addAction("getJobAdById");
	request.addSessionID("1234"); //TODO: change this
	request.addParam("jobAdId", intJobAdId);
	

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending getJobAdById Request</h2>";
	
	
//	var xmlHttpReq;
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
		    //var responseText= ParseXMLResponse(xmlhttp.responseXML);
		    loadJobAdDetails(xmlhttp.responseXML);
	    	document.getElementById("feedback").innerHTML="Successfully finished tasks";
	    	$("#jobAdDetails").show();
	    	$("#getJobAdButton").attr("disabled", false);

	    }
	  };
	
	//send the parameters to the servlet with POST
		xmlhttp.open("POST","../ServletJobAd" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(request.toString());
}


function editJobAd(){
	//document.getElementById("submitEdit").disabled=true;
	
	document.getElementById("feedback").innerHTML="<h2>Sending Edit Request</h2>";

//	var xmlHttpReq;
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
	//var strEmpType = document.getElementById("empType").value;
	//var searchEducationReq = document.getElementById("searchEducationReq").value;
	//var strSearchJobLoc = document.getElementById("searchJobLoc").value;
	//var strTags = document.getElementById("tags").value;
	
	
	
	//User Input Check:
	if(strTitle == null){
		alert("Must Enter Job Advertisement Title!");
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
	//TODO fix sessionID
	request.addAction("searchJobAdvertisement");
	request.addSessionID("1234"); //TODO implement this
	request.addParam("searchText", strSearchText);
	//request.addParam("educationRequirement", searchEducationReq);
	//request.addParam("jobLocation", strSearchJobLoc);
	//request.addParam("strDescription", strDescription);
		  
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	  
}














function jobAdReqDispatcher(req, outputDiv)
{
	if(outputDiv!=null){
		switch (req) 
		{
			case loadAdList:
				
				loadAdList(outputDiv);
				break;
			
			case search:
				searchAd(outputDiv);
				break;
				
			default:
				break;
		}
	}
}

function searchAd(outputDiv){
	//TODO Testing ONLY, RM after testing
	$("#ListJobAdButton").attr("disabled", true);
	
	request = new Request;
	request.addAction("loadAdList");
	request.addSessionID("1234"); //TODO: change this
	//TODO AUTO DETECT NUMBER OF INPUTS
	request.addParam("idJobAd", $("#jobAdId").val());
	

	//change the text while sending the request
	$("#feedback").html("<h2>Sending SEARCH Request</h2>");
	
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
	//When request is finished and results passed back
	xmlhttp.onreadystatechange=function(){
		if (xmlhttp.readyState==4 && xmlhttp.status==200){
			$("#feedback").html("<h2>Successfully finished tasks</h2>");
			//parse XML response from server
			buildTable("jobAd", outputDiv);
	    	$("#ListJobAdButton").attr("disabled", false);

	    }
	  };
	  
}

function loadAdList(outputDiv){
	//TODO Testing ONLY, RM after testing
	$("#ListJobAdButton").attr("disabled", true);
	
	request = new Request;
	request.addAction("loadAdList");
	request.addSessionID("1234"); //TODO: change this
	request.addParam("searchJobAdId", $("#jobAdId").val());
	

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
	//When request is finished and results passed back
	xmlhttp.onreadystatechange=function(){
		if (xmlhttp.readyState==4 && xmlhttp.status==200){
			$("#feedback").html("<h2>Successfully finished tasks</h2>");
			//parse XML response from server
			buildTable("jobAd", outputDiv);
	    	$("#ListJobAdButton").attr("disabled", false);

	    }
	  };
	  
}
/************************************************************************************************

**
 * 					BUILD TABLE FUNCTION
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
 

*************************************************************************************************

*/
function buildTable(xmlObj, outputDiv){
	var tbody = $("tbody", outputDiv).html("");
	$(xmlObj,xmlhttp.responseXML).each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var rowText = "<tr><td>"  + jobAd.attr("creationDate") + 
		  				"</td><td>" + jobAd.attr("jobAdTitle") 	 + 
		  				"</td><td>" + jobAd.attr("contactInfo")  + 
		  				"</td><td>" + jobAd.attr("educationReq") + 

"</td></tr>";
		  $(rowText).appendTo(tbody);
		  
		});
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
        
        alert(creationDate);
        
		
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

function searchJobAdvertisement(outputDiv){
	
	request = new Request;
	request.addAction("searchJobAdvertisement");
	request.addSessionID("1234"); //TODO: change this
	
//	var titleSearch = $("#titleSearch").val();
//	var eduReqsearch = $("#eduReqsearch").val();
//	var locSearch = $("#locSearch").val();
//	var tagsSearch = $("#tagsSearch").val();
	
	
	var searchFields = $(":input", "#searchBox").serializeArray();
	jQuery.each(searchFields, function(i, field){
        if(field.value == ""){//DO NOTHING
        }
        else{
        	request.addParam(field.name, field.value); //add parameter to the request 

according to how many search criteria filled
        }
	   });
	//User Input Check:
	
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
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  $("#feedback").html("<h2>Successfully finished tasks</h2>");
			//parse XML response from server
			buildTable("jobAd", outputDiv);

	    }
	  };
	  
}
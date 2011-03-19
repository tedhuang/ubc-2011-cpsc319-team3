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
			buildAdListTb("jobAd", outputDiv);
	    	$("#ListJobAdButton").attr("disabled", false);

	    }
	  };
	  
}


function loadJobAdDetails( responseXML ){
	
	var jobAd = responseXML.getElementsByTagName("jobAd").item(0);
	
        var jobAdId			=	jobAd.getAttribute("jobAdId");
        var jobAdTitle		=	jobAd.getAttribute("jobAdTitle");
        var tags			=	jobAd.getAttribute("tags");
        var contactInfo		=	jobAd.getAttribute("contactInfo");
        var expiryDate		=	jobAd.getAttribute("expiryDateFormatted");
        var startingDate	=	jobAd.getAttribute("startingDateFormatted");
        var creationDate	=	jobAd.getAttribute("creationDateFormatted");
        var status			=	jobAd.getAttribute("status");
        var numberOfViews	=	jobAd.getAttribute("numberOfViews");
        var educationReq	=	jobAd.getAttribute("educationReq");
        var jobAdDescription=	jobAd.getAttribute("jobAdDescription");
        var isApproved		=	jobAd.getAttribute("isApproved");
        
        var locList			=	jobAd.getAttribute("locationList");
        
		if(isApproved){
			isApproved = "Yes";
		}
		else
			isApproved = "No";
		
		$("#jobTitle").text(jobAdTitle);
		$("#tags").text(tags);
		$("#contactInfo").text( contactInfo);
		$("#jobDescription").text( jobAdDescription);
		
		
		document.getElementById("isApproved").innerHTML = isApproved;
		document.getElementById("jobAdId").innerHTML = jobAdId;
		document.getElementById("status").innerHTML = status;
		document.getElementById("educationReq").value = educationReq;
		
		document.getElementById("expiryDate").innerHTML = expiryDate;
		document.getElementById("startingDate").innerHTML = startingDate;
		document.getElementById("creationDate").innerHTML = creationDate;
		
		document.getElementById("numViews").innerHTML = numberOfViews;
		
		
		//showMap() //TODO: integrate showMap from Google Maps
}








//TODO: hook up with UI
function changeJobAdStatus(){
	
	//TODO: use these ID for UI
	var intJobAdId = document.getElementById("jobAdId").value;
	var strNewStatus = document.getElementById("newStatus").value; 

	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");//USE console.log(); instead
	}
	
	request = new Request;
	request.addAction("changeJobAdStatus");
	request.addParam("jobAdId", intJobAdId);
	request.addParam("status", strNewStatus);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
	
}

function adminDeleteJobAd(){
	
	var sessionKey = document.getElementById("sessionKey").value;
	var intJobAdId = document.getElementById("jobAdId").value;
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminDeleteJobAd");
	request.addParam("jobAdId", intJobAdId);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Delete Request</h2>";
}

function adminApprove(){
	
	var sessionKey = document.getElementById("sessionKey").value;
	var intJobAdId = document.getElementById("jobAdId").value;
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminApprove");
	request.addParam("jobAdId", intJobAdId);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
}


function adminDeny(){
	
	var sessionKey = document.getElementById("sessionKey").value;
	var intJobAdId = document.getElementById("jobAdId").value;
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminDeny");
	request.addParam("jobAdId", intJobAdId);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
}


//TODO: hook up with UI
function extendJobAdExpiry(){
	
	//TODO: use these ID for UI
	var longNewExpiry = document.getElementById("newExpiry").value;
	var intJobAdId = document.getElementById("jobAdId").value;

	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("extendJobAdExpiry");
	request.addParam("jobAdId", intJobAdId);
	request.addParam("expiryDate", longNewExpiry);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
}


//TODO: hook up with UI
function submitJobAdForApproval(){
	
	//TODO: use these ID for UI
	var intJobAdId = document.getElementById("jobAdId").value;

	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("submitJobAdForApproval");
	request.addParam("jobAdId", intJobAdId);
	
	//Response Handling:
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
		   var message = xmlhttp.responseXML.getElementById("message");
		   var result = xmlhttp.responseXML.getElementById("result");
		   var responseText= result + ": " + message;
		   document.getElementById("feedback").innerHTML=responseText;
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
}

/*******************************************************************************************************************************
 * 						POSTJOBAD
 * 
 * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
 *******************************************************************************************************************************/
function postJobAd(mode, formDiv, heading){
	var noNullData=false;
	var theForm =$("#"+formDiv);
	var theheading =$("#"+heading);
	var sessionKey = $("#sessionKey").val();
	request = new Request;
	if(mode=="submit" ){
		request.addAction("createJobAdvertisement");
		noNullData = checkMandatory(theForm);
	}
	else if(mode=="draft"){
		request.addAction("saveJobAdDraft");
		noNullData = true;
	}
	else if(mode=="edit"){
		request.addAction("editJobAd");
		noNullData = checkMandatory(theForm);
	}
	
	if(noNullData){
		request.addSessionKey( sessionKey );
		var searchFields = $(":input", theForm).serializeArray();
		
		jQuery.each(searchFields, function(i, field){
	          if(field.name=="expireTime-field" || field.name=="startTime-field"){
	        	request.addParam(field.name, field.value);
	          }
	          else{
				request.addParam(field.name, field.value); //add parameter to the request
	          }
		   });
		
		if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}
		else{// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		if(mode=="submit"){
			theheading.html("<h2 class='info'>Sending Request...</h2>");
		}
		else if(mode=="draft"){
			theheading.html("<h2 class='info'>Saving Draft...</h2>");
		}
		else if(mode=="edit"){
			theheading.html("<h2 class='info'>Updating Your Ad...</h2>");
		}
		xmlhttp.open("POST","../ServletJobAd" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(request.toString());
		
		xmlhttp.onreadystatechange=function(){
		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  
//			  var message = xmlhttp.responseXML.getElementById("message");xmlhttp.responseXML.getElementById("message") +
		    theheading.html("<h2 class='good'>Action Performed Successfully!</h2>");
		    }
		  else{
			  theheading.html("<h2 class='error'>opps something is wrong!</h2>");
		  }
		};
		
		
	}//IF MANDATORIES FILLED
}

function checkMandatory(formContainer){
	var count=0;
	var error = "<h2 class='error'>Hmm...You seem to miss something</h2>";
	var chkList = $('.mustNotNull', formContainer).get();
	
	$(chkList).each(function(){
		if($(this).val()== ""){
			
			$(error).insertAfter($(this).siblings('label'));
		}
		else{
			count++;
		}
	});
	if(count==chkList.length){
		return true;
	}
	else{
		return false;
	}
}



	  
	  
	  




/*******************************************************************************************************************
 * 				searchJobAdvertisement Function
 * outputDiv => The table container div
 * 
 *******************************************************************************************************************/
function searchJobAdvertisement(outputDiv){
	
	request = new Request;
	request.addAction("searchJobAdvertisement");
	
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
			xmlhttp.open("POST","../ServletJobAd" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.send(request.toString());
			
			xmlhttp.onreadystatechange=function()
			  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
					//parse XML response from server
					buildAdListTb("jobAd", outputDiv);
		
			    }
			  };
	   }//ENDOF CHECK-ALL-NULL
	   else{
		   $("#feedback").html('<h2 class="info">Please enter Condition to search</h2>');
	   }
		  
}

function quickSearchJobAd(outputDiv){
	
	request = new Request;
	request.addAction("searchJobAdvertisement");
	
//	var searchFields = $("#quickSearchBox", ).serializeArray();
	if($("#quickSearchBox", "#qkSearchForm" ).val() != ""){ //Check if All NULL
		
			request.addParam($("#quickSearchBox", "#qkSearchForm" ).attr("name"), $("#quickSearchBox", "#qkSearchForm" ).val()); //add parameter to the request according to how many search criteria filled

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
			xmlhttp.open("POST","../ServletJobAd" ,true); //PATH TO SERVLET DIFFERS FROM TESTPAGES
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.send(request.toString());
			
			xmlhttp.onreadystatechange=function()
			  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
				  $("#feedback").html("<h2>Successfully finished tasks</h2>");
					//parse XML response from server
					buildAdListTb("jobAd", outputDiv);
		
			    }
			  };
	   }//ENDOF CHECK-ALL-NULL
}
/************************************************************************************************************
 * 				View Job Ad Detail
 * - View Detail
 * - Edit Detail
 ************************************************************************************************************/
function getJobAdById(mode, id, outputDiv)
{
	request = new Request;
	request.addAction("getJobAdById");
	request.addParam("jobAdId", id);
	var fb = $(".feedback", "#"+outputDiv);
	//change the text while sending the request
	fb.html("<h2>Sending getJobAdById Request</h2>");
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		    //parse XML response from server
		  fb.html("<h2 class='good'> Successfully finished tasks</h2>");	
		  if(mode=="detail"){
		  	buildDetailTable("jobAd", outputDiv);
		  }
		  else if(mode=="edit"){
			  $.fn.DynaSmartTab.loadEdData("jobAd", outputDiv, mode);
		  }
	    }
	  else{
		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
	  }
	  };
}


/************************************************************************************************************
 * 				Get Owner's Ad
 * @param outputDiv
 ************************************************************************************************************/
function getJobAdByOwner(outputDiv){
	
	//var intOwnerId = document.getElementById("ownerId").value;
	
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getJobAdByOwner");
//	request.addSessionKey(document.getElementById("sessionKey").value ); 
	request.addParam("sessionKey", strSessionKey);
	
//	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  buildOwnerAdTb("jobAd", outputDiv);//uibot
	    }
	  else{
		  //TODO Lightbox error
	  }
	  };	  
}

function delJobAd(tbRow, jobAdId){
	
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("deleteJobAd");
	request.addParam("sessionKey", strSessionKey);
	request.addParam("jobAdId", jobAdId);
	
//	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  tbRow.remove();
	    }
	  else{
		  //TODO Lightbox error
		  console.log("delete was unsuccessful");
	  }
	  };	  
}
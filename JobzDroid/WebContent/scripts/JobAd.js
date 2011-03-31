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


/**
 * DEPRECATED - DELETE DURING REFACTOR
 */
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

/**
 * DEPRECATED - DELETE DURING REFACTOR
 */
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
function changeJobAdStatus(intJobAdId, strNewStatus){
	
	//TODO: use these ID for UI
	//var intJobAdId = document.getElementById("jobAdId").value;
	//var strNewStatus = document.getElementById("newStatus").value; 

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

/**
 * Admin Function: handles delete job ad
 */
function adminDeleteJobAd(intJobAdId){
	
	var sessionKey = document.getElementById("sessionKey").value;
	//var intJobAdId = document.getElementById("jobAdId").value;
	
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
		   var responseText = (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
		   var result = (xmlhttp.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
		   document.getElementById("feedback").innerHTML=responseText;
		   if (result){
			   //TODO: add UI transition handling
			   alert("Job Ad Deleted Successfully (TODO: add UI tranisition)");
		   }
		   else{
			   alert("Failed to Delete Job Ad");
		   }
	    }
	  };
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Delete Request</h2>";
}


/**
 * Admin Function: handles approving job ads
 */
function adminApprove(intJobAdId){
	
	var sessionKey = document.getElementById("sessionKey").value;
	//var intJobAdId = document.getElementById("jobAdId").value;
	
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
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		   var responseText = (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
		   var result = (xmlhttp.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
		   document.getElementById("feedback").innerHTML=responseText;
		   if (result){
			   //TODO: add UI transition handling
			   alert("Job Ad Approved Successfully (TODO: add UI tranisition)");
		   }
		   else{
			   alert("Failed to Approve Job Ad");
		   }
	    }
	  };
}


function adminDeny(intJobAdId){
	
	var sessionKey = document.getElementById("sessionKey").value;
	//var intJobAdId = document.getElementById("jobAdId").value;
	
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
	  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		   var responseText = (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
		   var result = (xmlhttp.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
		   document.getElementById("feedback").innerHTML=responseText;
		   if (result){
			   //TODO: add UI transition handling
			   alert("Job Ad Denied Successfully (TODO: add UI tranisition)");
		   }
		   else{
			   alert("Failed to Deny Job Ad");
		   }
	    }
	  };
}


//TODO: hook up with UI
function extendJobAdExpiry(intJobAdId, longNewExpiry){
	
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
	  
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
	
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
}


//TODO: hook up with UI
function submitJobAdForApproval(intJobAdId){
	
	//TODO: use these ID for UI
	//var intJobAdId = document.getElementById("jobAdId").value;

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
	  
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send( request.toString() );

	//change the text while sending the request
	document.getElementById("feedback").innerHTML="<h2>Sending Request</h2>";
	
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
}

/*******************************************************************************************************************************
 * 						POSTJOBAD
 * 
 * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
 *******************************************************************************************************************************/
function postJobAd(mode, formDiv, heading){
	var noNullData=false;
	var theForm =$("#"+formDiv);
	var theheading =$("."+heading);
	var sessionKey = $("#sessionKey").val();
	var infoText;
	request = new Request;
	if(mode=="submit" ){
		request.addAction("createJobAdvertisement");
		noNullData = checkMandatory(theForm);
		infoText="Submitting Your Request...";
	}
	else if(mode=="draft"){
		request.addAction("saveJobAdDraft");
		noNullData = true;
		infoText="Saving Draft...";
	}
	else if(mode=="edit"){
		request.addAction("editJobAd");
		noNullData = checkMandatory(theForm);
		infoText="Updating...";
	}
	else if(mode=="updateDraft"){
		request.addAction("updateDraft");
		noNullData = checkMandatory(theForm);
		infoText="Updating...";
	}
	if(noNullData){
		$.fn.smartLightBox.openDivlb(formDiv,'load',infoText);
		request.addSessionKey( sessionKey );
		var searchFields = $(":input:not('.map')", theForm).serializeArray();
		
		jQuery.each(searchFields, function(i, field){
	          if(field.name=="expireTime-field" || field.name=="startTime-field"){
	        	request.addParam(field.name, field.value);//TODO ???
	          }
	          else{
				request.addParam(field.name, field.value); //add parameter to the request
	          }
		   });
		//get location info
		var locList = $("li", $("#locList", theForm)).get();
		var i = 1;
		$.each(locList, function(){ // get location from the list
			request.addParam("addr"+i, $(this).data("addr"));
			request.addParam("latlng"+i, $(this).data("latlng"));
			i++;
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
		  	$("#lbImg", theForm).removeClass("load").addClass("good");
			$("#lbMsg",theForm).html("Action Successful!");
			$.fn.smartLightBox.closeLightBox(2000, formDiv);  
			setTimeout ( $.fn.DynaSmartTab.close, 2000 );
				
//		    theheading.html("<h2 class='good'>Action Performed Successfully!</h2>");
		    }
		  else if(xmlhttp.status!=200){
			  	$("#lbImg", theForm).removeClass("load").addClass("alert");
				$("#lbMsg",theForm).html("Action Not Successful, please try again");
				$.fn.smartLightBox.closeLightBox(2000, formDiv);
//			  theheading.html("<h2 class='error'>opps something is wrong!</h2>");
		  }
		};
		
	}//IF MANDATORIES FILLED
}

function checkMandatory(formContainer){
	var count=0;
	var error = "<h2 class='error'>Hmm...You seem to miss something</h2>";
	var chkList = $('.mustNotNull', formContainer).get();
	
	$(chkList).each(function(){
		if(!$(this).val().length){
			
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
//	$.fn.smartLightBox.openDivlb("edAdFrame", 'load','loading data...');
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
	$.fn.smartLightBox.openlb('small','Retrieving Information...','load');
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
		  $.fn.smartLightBox.closeLightBox(500);
	    }
	  else if(xmlhttp.status!=200){
		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
	  }
	  };
}

/************************************************************************************************************
 * 				Get All Job Ads
 * @param outputDiv
 ************************************************************************************************************/
function getAllJobAd(outputDiv){
	
	$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
	var strSessionKey = $("#sessionKey").val();
	request = new Request;
	
	request.addAction("getAllJobAd");
	request.addParam("sessionKey", strSessionKey);
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  
		  buildBrowseJobAdTb("jobAd", outputDiv);//uibot
	    }
	  else{
		  //TODO: implement error handling
	  }
	  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	};	  
}



/************************************************************************************************************
 * 				Get Job Ad for Guest
 * @param outputDiv
 ************************************************************************************************************/
function guestViewJobAd(outputDiv, mode){
	
	//$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');

	var index = $("#browseIndex").val();
	
	if(mode == "first"){
		//first index is 0, do nothing
	}
	if(mode == "next")
		index = parseInt(index)+10; //update index
	
	 if(mode == "prev")
		index = parseInt(index)-10;
	  
	
	request = new Request;
	request.addAction("getSomeJobAd");
	request.addParam("startingIndex", index);
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  
		  buildGuestJobAdTb("jobAd", outputDiv);//uibot
		  
			var xmlObj = $("jobAd",xmlhttp.responseXML);

			if(xmlObj.length < 10){//if this is the last page of results
				$("#prevButton").attr("disabled", false);
				$("#nextButton").attr("disabled", true);
			}
			else if(index <= 0){
				$("#prevButton").attr("disabled", true);
				$("#nextButton").attr("disabled", false);
			}
			else{
				$("#prevButton").attr("disabled", false);
				$("#nextButton").attr("disabled", false);
			}
			
		  $("#browseIndex").val(index ); //increase index by 10
		  $("#feedback").text("Browse Index: " + $("#browseIndex").val() );
	    }
	  else{
		  //TODO: implement error handling
	  }
	  //$.fn.smartLightBox.closeLightBox(1000,"home-frame");
	};	  
}


/************************************************************************************************************
 * 				Get Owner's Ad
 * @param outputDiv
 ************************************************************************************************************/
function getJobAdByOwner(outputDiv){
	
	$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
	var strSessionKey = $("#sessionKey").val();
	request = new Request;
	request.addAction("getJobAdByOwner");
	request.addParam("sessionKey", strSessionKey);
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
		  
		  buildOwnerAdTb("jobAd", outputDiv);//uibot
	    }
	  else{
		  
	  }
	  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
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
		  $("#lbImg", "#lightBox").removeClass("load").addClass("good");
		  $("#lbMsg","#lightBox").html("Your Ad Was Deleted!");
		  $.fn.smartLightBox.closeLightBox(1500);
		  tbRow.remove();
	    }
	  else if ( xmlhttp.status!=200){
			  console.log("delete was unsuccessful");
		  }
		  //TODO Lightbox error
	  };	  
}
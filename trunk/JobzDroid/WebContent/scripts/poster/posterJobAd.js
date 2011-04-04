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



///*******************************************************************************************************************************
// * 						POSTJOBAD
// * 
// * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
// *******************************************************************************************************************************/
//function postJobAd(mode, formDiv, heading){
//	var noNullData=false;
//	var theForm =$("#"+formDiv);
//	var theheading =$("."+heading);
//	var sessionKey = $("#sessionKey").val();
//	var infoText;
//	request = new Request;
//	if(mode=="submit" ){
//		request.addAction("createJobAdvertisement");
//		noNullData = checkMandatory(theForm);
//		infoText="Submitting Your Request...";
//	}
//	else if(mode=="draft"){
//		request.addAction("saveJobAdDraft");
//		noNullData = true;
//		infoText="Saving Draft...";
//	}
//	else if(mode=="edit"){
//		request.addAction("editJobAd");
//		noNullData = checkMandatory(theForm);
//		infoText="Updating...";
//	}
//	else if(mode=="updateDraft"){
//		request.addAction("updateDraft");
//		noNullData = checkMandatory(theForm);
//		infoText="Updating...";
//	}
//	if(noNullData){
//		$.fn.smartLightBox.openDivlb(formDiv,'load',infoText);
//		request.addSessionKey( sessionKey );
//		var searchFields = $(":input:not('.map')", theForm).serializeArray();
//		
//		jQuery.each(searchFields, function(i, field){
//	          if(field.name=="expireTime-field" || field.name=="startTime-field"){
//	        	request.addParam(field.name, field.value);//TODO ???
//	          }
//	          else{
//				request.addParam(field.name, field.value); //add parameter to the request
//	          }
//		   });
//		//get location info
//		var locList = $("li", $("#locList", theForm)).get();
//		var i = 1;
//		$.each(locList, function(){ // get location from the list
//			request.addParam("addr"+i, $(this).data("addr"));
//			request.addParam("latlng"+i, $(this).data("latlng"));
//			i++;
//		});
//		
//		if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
//			xmlhttp=new XMLHttpRequest();
//		}
//		else{// code for IE6, IE5
//			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
//		}
//		
//		if(mode=="submit"){
//			theheading.html("<h2 class='info'>Sending Request...</h2>");
//		}
//		else if(mode=="draft"){
//			theheading.html("<h2 class='info'>Saving Draft...</h2>");
//		}
//		else if(mode=="edit"){
//			theheading.html("<h2 class='info'>Updating Your Ad...</h2>");
//		}
//		xmlhttp.open("POST","../ServletJobAd" ,true);
//		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
//		xmlhttp.send(request.toString());
//		
//		xmlhttp.onreadystatechange=function(){
//		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
//			  
////			  var message = xmlhttp.responseXML.getElementById("message");xmlhttp.responseXML.getElementById("message") +
//		  	$("#lbImg", theForm).removeClass("load").addClass("good");
//			$("#lbMsg",theForm).html("Action Successful!");
//			$.fn.smartLightBox.closeLightBox(2000, formDiv);  
//			setTimeout ( $.fn.DynaSmartTab.close, 2000 );
//				
////		    theheading.html("<h2 class='good'>Action Performed Successfully!</h2>");
//		    }
//		  else if(xmlhttp.status!=200){
//			  	$("#lbImg", theForm).removeClass("load").addClass("alert");
//				$("#lbMsg",theForm).html("Action Not Successful, please try again");
//				$.fn.smartLightBox.closeLightBox(2000, formDiv);
////			  theheading.html("<h2 class='error'>opps something is wrong!</h2>");
//		  }
//		};
//		
//	}//IF MANDATORIES FILLED
//}

/*******************************************************************************************************************************
 * 						POSTJOBAD
 * 
 * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
 *******************************************************************************************************************************/
function postJobAd(mode, formDiv){
	var noNullData=false;
	var theForm =$("#"+formDiv);
	var theheading =$("."+heading);
	var sessionKey = $("#sessionKey").val();
	var infoText;
	request = new Request;
	if(mode=="create" ){
		request.addAction("createJobAdvertisement");
		noNullData = checkMandatory(theForm);
		infoText="Submitting Your Request...";
	}
	else if(mode=="draft"){
		request.addAction("saveJobAdDraft");
		noNullData = true;
		infoText="Saving Draft...";
	}
	
	if(noNullData){
		$.fn.smartLightBox.openDivlb(formDiv,'load',infoText);
		request.addSessionKey( sessionKey );
		var inputFields = $(":input:not('.map')", theForm).serializeArray();
		
		jQuery.each(inputFields, function(i, field){
				request.addParam(field.name, field.value); //add parameter to the request
		   });
		//get location info
		var locList = $("li", $("#locList", theForm)).get();
		if(locList.length!=0){
			var location="";
			$.each(locList, function(index){ // get location from the list
				request.addParam("addr"+index, $(this).data("addr"));
				request.addParam("latlng"+index, $(this).data("latlng"));
				location += $(this).data("city")+","+$(this).data("province")+","+$(this).data("zip")+"-";
			});
			location=location.substring(0,location.length-1); //remove last "-"
			request.addParam("loc-field", location);
		}
		
		if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}
		else{// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		if(mode=="create"){
			theheading.html("<h2 class='info'>Sending Request...</h2>");
		}
		else if(mode=="draft"){
			theheading.html("<h2 class='info'>Saving Draft...</h2>");
		}
		xmlhttp.open("POST","../ServletJobAd" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(request.toString());
		
		console.log(request.toString());
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

/*******************************************************************************************************************************
 * 						Update Draft or Open JobAd
 * 
 * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
 *******************************************************************************************************************************/
function updateJobAd(mode, formDiv){
	var theForm =domObjById(formDiv);
	var theheading =$(theForm.find(".heading"));
	var sessionKey = $("#sessionKey").val();
	var infoText;
	request = new Request;
	if(mode=="openAd" ){
		request.addAction("updateOpenAd");
	}
	else if(mode=="draftAd"){
		request.addAction("updateDraftAd");
	}
	
	if(checkMandatory(theForm)){
		
		request.addSessionKey( sessionKey );
		var inputFields = $(":input", theForm).not('.map, #oldAdValues').serializeArray();
		var compareResult =compareChange("oldAdValues", inputFields, formDiv);
		
		if(compareResult.numChanged){ //only update changed data
			var changedData = compareResult.changedData;
			$.each( changedData.data(),function(name, value) {
				request.addParam(name, value);
			});
		changedData.remove();//remove the cache(and the dom)
		console.log(request.toString());
		//get location info
//		var locList = $("li", $("#locList", theForm)).get();
//		if(locList.length){
//			var location="";
//			$.each(locList, function(index){ // get location from the list
//				request.addParam("addr"+index, $(this).data("addr"));
//				request.addParam("latlng"+index, $(this).data("latlng"));
//				location += $(this).data("city")+","+$(this).data("province")+","+$(this).data("zip")+"-";
//			});
//			location=location.substring(0,location.length-1); //remove last "-"
//			request.addParam("loc-field", location);
//		}
		
		var xhr=createXHR();
		if(xhr){
			try{
				xhr.open("POST","../ServletJobAd" ,true);
				xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				xhr.onreadystatechange = processResult;
				xhr.send(request.toString());
				$.fn.smartLightBox.openDivlb(formDiv,'load',"Updating...");
			}catch(e){
				
			}
		}//eof if xhr not null
	  }//eof check if change
		else{
			$.fn.smartLightBox.closeLightBox(0);
			$.fn.smartLightBox.diaBox("You Didn't Change Anything", "alert", "notification");
		}
	}//eof check mandatory
	function processResult(){
		if (xhr.readyState == 4) {
			try {
				  if (xhr.status == 200) {
					  $("#lbImg", theForm).removeClass("load").addClass("good");
						$("#lbMsg",theForm).html("Action Successful!");
						$.fn.smartLightBox.closeLightBox(2000, formDiv);  
						setTimeout ( $.fn.DynaSmartTab.close, 2000 );
				  }
                else { 
                	$("#lbImg", theForm).removeClass("load").addClass("alert");
    				$("#lbMsg",theForm).html("Action Not Successful, please try again");
    				$.fn.smartLightBox.closeLightBox(2000, formDiv);
                }
			} 
			catch (e){
			}
	    }
//		  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	}//eof processResult
} //eof updateJobAd

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

function compareChange(oldVal, newVal, formDiv){
	
	var changedData =$('<input>')
					.attr({'type':'HIDDEN', 'id':'changedData'})
					.appendTo(domObjById(formDiv));
	var numChange=0;
	oldVal = domObjById(oldVal);
	$.each(newVal, function(i, field){
		var fld=field.name;
		if(fld=="adId-field"){
			changedData.data(fld, field.value);
		}
		else if(field.value!=oldVal.data(fld)){
			changedData.data(fld, field.value);
			numChange++;
		}
	});
//	changedData.data("numChanged", numChange);
	var compareResult={numChanged: numChange, changedData:changedData};
//	return changedData;
	return compareResult;
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
			  $.fn.DynaSmartTab.loadEdData("jobAd", outputDiv);
		  }
	    }
	  else if(xmlhttp.status!=200){
		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
	  }
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
		  
		  buildOwnerAdTb("jobAd", outputDiv);//posterUiBot
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
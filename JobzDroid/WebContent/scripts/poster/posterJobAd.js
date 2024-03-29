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


/*******************************************************************************************************************************
 * 						POSTJOBAD
 * 
 * - IN CHARGE OF SUBMITTING AD OR SAVING DRAFT 
 *******************************************************************************************************************************/
function postJobAd(mode, formDiv){
	var noNullData=false;
	var theForm =$("#"+formDiv);
//	var theheading =$("."+heading);
	var sessionKey = $("#sessionKey").val();
	var infoText;
	request = new Request;
	var inputFields = $("#typeInForm .field, #desc-div .field", theForm).get();//.serializeArray(); //get type-in-form and description
	var selFields=$("#chooseForm .selField", theForm).get();//.serializeArray(); //get choose fields
	var data=({inputFld:inputFields, selFld:selFields});
	
	if(mode=="create" ){
		request.addAction("createJobAdvertisement");
		infoText="Submitting Your Request...";
	}
	else if(mode=="draft"){
		request.addAction("saveJobAdDraft");
		infoText="Saving Draft...";
	}
	noNullData=checkMandatory(data,theForm);
	if(noNullData){
//		$.fn.smartLightBox.openDivlb(formDiv,'load',infoText);
		request.addSessionKey( sessionKey );
		var dataFields = $(":input:not('.map')", theForm).serializeArray();
		
		jQuery.each(dataFields, function(i, field){
				request.addParam(field.name, field.value); //add parameter to the request
		   });
		//get location info
		var locList = $("li", $("#locList", theForm)).get();
		if(locList.length!=0){
			var location="";
			$.each(locList, function(index){ // get location from the list
				location="";
				$.each($(this).data(), function(key, value){
					if(key!="lat" || key!="lng"){
						if(key=="addr"|| key=="latlng")
							if(value!=null){
								request.addParam(key+index, value);
//								request.addParam("addr"+index, $(this).data("addr"));
//								request.addParam("latlng"+index, $(this).data("latlng"));
							}
						}
//						else if(value!=null){
//							
//						}
					if(key.match(/city|province|country|zip/gi)){
						if(typeof value != undefined  && value!= ""){
							location += value+",";
						}
						
					}
//					 $(this).data("city")+","+
//								$(this).data("province")+","+
//								$(this).data("country")+","+
//								$(this).data("zip")+"-";
				}); //eof extracting each location
			});
			location=location.substring(0,location.length-1); //remove last "-"
			request.addParam("loc-field", location);
		}
		
		function processResult(){
			if (xhr.readyState == 4) {
				try {
					  if (xhr.status == 200) {
//						  var message = xmlhttp.responseXML.getElementById("message");xmlhttp.responseXML.getElementById("message") +
						  	$("#lbImg", theForm).removeClass("load").addClass("good");
							$("#lbMsg",theForm).html("Action Successful!");
							$.fn.smartLightBox.closeLightBox(2000, formDiv);  
							setTimeout ( $.fn.DynaSmartTab.close, 2000 );
					  }
	                else { 
	                	$("#lbImg", theForm).removeClass("load").addClass("alert");
	    				$("#lbMsg",theForm).html("Action Not Successful, please try again");
	    				$.fn.smartLightBox.closeLightBox(2000, formDiv);
//	    			  theheading.html("<h2 class='error'>opps something is wrong!</h2>");
	                }
				} 
				catch (e){
				}
		    }
//			  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
		}//eof processResult
		
		var xhr=createXHR();
		if(xhr){
			try{
				xhr.open("POST","../ServletJobAd" ,true);
				xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				xhr.onreadystatechange = processResult;
				xhr.send(request.toString());
				$.fn.smartLightBox.openDivlb(formDiv,'load',"Publishing...");
			}catch(e){
				
			}
		}
		
		console.log(request.toString());
		
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
	else if(mode.match(/draftAd|publishInactive/gi)){
		request.addAction("updateDraftAd");
	}
	var inputFields = $("#typeInForm .field, #desc-div .field", theForm).get(); //.serializeArray(); //get type-in-form and description
	var selFields=$("#chooseForm .selField", theForm).get();//.serializeArray(); //get choose fields
	var data=({inputFld:inputFields, selFld:selFields});
	
	if(checkMandatory(data,theForm)){
		
		request.addSessionKey( sessionKey );
		var dataFields = $(":input:not('.map')", theForm).serializeArray();
		var compareResult =compareChange("oldAdValues", dataFields, formDiv);
		if(compareResult.DataChanged){ //only update changed data
			var changedData = compareResult.changedData;
			$.each( changedData.data(),function(name, value) {
				request.addParam(name, value);
			});
			changedData.remove();//remove the cache(and the dom)
			}
		else{
			$.fn.smartLightBox.closeLightBox(0);
			$.fn.smartLightBox.diaBox("You Didn't Change Anything", "alert", "notification");
		}
		
		if(mode=="publishInactive"){
			request.addParam("updateDead", mode);
		}
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
				console.log(e);
			}
		}//eof if xhr not null
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

function checkMandatory(dataObj, formDiv){
	var noNull=true;
	$.each(dataObj, function(key){
		switch(key){
		case "inputFld":
			$.each(this, function(){
				var input = $(this).find(':input');
				if(input.hasClass("mustNotNull")){
					if(!input.val().length){
						$(this).find('label').css('background','red');
						noNull=false;
					}
				}
				
			});
			break;
			
		case "selFld":
			$.each(this, function(){
				var inputs = $(this).find(':input');
				if(inputs.hasClass("mustNotNull")){
//					if(!inputs.find(':checked')){
					if(!$(this).find(':checked').length){
						$(this).find('label.topLabel').css('background','red');
						noNull=false;
					}
				}
				
			});
		break;
		}
		
	});
	noNull? null:$('<h2></h2>').addClass('error').html('Hmm...You seem to missed something').appendTo($('h2.welcome', formDiv));
	return noNull;
}
//	var chkInput = $('.mustNotNull', formContainer).not(":radio");
//	var chkSel =$(':radio.mustNotNull :checked', formContainer);
//	$(chkInput).each(function(){
//		if(!$(this).val().length){
//			$(error).insertAfter($(this).siblings('label'));
//		}
//		else{
//			count++;
//		}
//	});
////	$(chkSel).each(function(){
//	var temp=chkSel.find(":checked");
//		if(temp.length){
//			count++;
//		}
//		else{
//			$(error).insertAfter($(this).siblings('label'));
//		}
////	});
//	
//	if(count==chkList.length){
//		return true;
//	}
//	else{
//		return false;
//	}
//}

function compareChange(oldVal, newVal, formDiv){
	
	var changedData =$('<input>')
					.attr({'type':'HIDDEN', 'id':'changedData'})
					.appendTo(domObjById(formDiv));
	var DataChanged=false;
	oldVal = domObjById(oldVal);
	$.each(newVal, function(i, field){
		var fld=field.name;
		if(fld=="adId-field"){
			changedData.data(fld, field.value);
		}
		else if(field.value!=oldVal.data(fld)){
			changedData.data(fld, field.value);
			DataChanged=true;
		}
	});
//	changedData.data("numChanged", numChange);
	var compareResult={DataChanged: DataChanged, changedData:changedData};
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
	
	function processResult(){
		if (xhr.readyState == 4) {
			try {
				  if (xhr.status == 200) {
					  fb.html("<h2 class='good'> Successfully finished tasks</h2>");	
					  if(mode=="detail"){
					  	buildDetailTable("jobAd", outputDiv, xhr.responseXML);
					  }
					  else if(mode=="edit"){
						  $.fn.DynaSmartTab.loadEdData("jobAd", outputDiv, xhr.responseXML);
					  }
				  }
                else { 
                	 fb.html("<h2 class='error'> Successfully finished tasks</h2>");
                }
			} 
			catch (e){
				console.log(e);
			}
	    }
//		  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	}//eof processResult
	
	var xhr=createXHR();
	if(xhr){
		try{
			xhr.open("POST","../ServletJobAd" ,true);
			xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xhr.onreadystatechange = processResult;
			xhr.send(request.toString());
		}catch(e){
			
		}
	}
	
	console.log(request.toString());
	
}


/************************************************************************************************************
 * 				Get Owner's Ad
 * @param outputDiv
 ************************************************************************************************************/
function getJobAdByOwner(outputDiv){
	
	var strSessionKey = $("#sessionKey").val();
	request = new Request;
	request.addAction("getJobAdByOwner");
	request.addParam("sessionKey", strSessionKey);
	var xmlhttp=createXHR();
	if(xmlhttp){
	  try{
		xmlhttp.open("POST","../ServletJobAd" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.onreadystatechange = processResponse;
		xmlhttp.send(request.toString());
		$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
	  }catch(e){
	  }
	}
	
	function processResponse(){
	  if (xmlhttp.readyState==4){ 
	    try {
		  if(xmlhttp.status==200){
			  buildOwnerAdTb("jobAd", outputDiv, xmlhttp.responseXML);//posterUiBot
	    }
		else{
		}
	   }catch(e){
		   //error-handling
	   }
	 }
	  $.fn.smartLightBox.closeLightBox(0,"home-frame");
	}
}



function changeJobAdStatus(intJobAdId, strNewStatus){
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");//USE console.log(); instead
	}
	
	var strSessionKey = $("#sessionKey").val();

	request = new Request;
	request.addAction("changeJobAdStatus");
	request.addParam("sessionKey", strSessionKey);
	request.addParam("jobAdId", intJobAdId);
	request.addParam("status", strNewStatus);

	
	//Concurrent Ajax handling
	var xmlhttp=createXHR();
	if(xmlhttp){
		try{
			//send the parameters to the servlet with POST
			xmlhttp.open("POST","../ServletJobAd" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.onreadystatechange = processResponse;
			xmlhttp.send( request.toString() );
		}catch(e){
			
		}
	}
	$("#feedback").text("Sending Request");

	function processResponse(){
		  if (xmlhttp.readyState==4){ 
		    try {
			  if(xmlhttp.status==200){
				  //$.fn.smartLightBox.diaBox
					$.fn.smartLightBox.closeLightBox(1000);
		    }
			else{
				  console.log("Change Job Ad Status Failed");
				  	$("#lbImg", "#home-frame").removeClass("load").addClass("alert");
					$("#lbMsg","#home-frame").html("Action Not Successful, please try again");
					$.fn.smartLightBox.closeLightBox(1000);
			}
		   }catch(e){
			   //error-handling
		   }
		 }
	}
}


function delJobAd(tbRow, jobAdId){
	
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
//	request.addAction("changeJobAdStatus");
//	request.addParam("status", "inactive");
	request.addAction("deleteJobAd");
	request.addParam("sessionKey", strSessionKey);
	request.addParam("jobAdId", jobAdId);
	
	var xmlhttp=createXHR();
	if(xmlhttp){
	  try{
		xmlhttp.open("POST","../ServletJobAd" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.onreadystatechange = processResponse;
		xmlhttp.send(request.toString());
	  }catch(e){
	  }
	}
	
	function processResponse(){
	  if (xmlhttp.readyState==4){ 
	    try {
		  if(xmlhttp.status==200){
			  $("#lbImg", "#lightBox").removeClass("load").addClass("good");
			  $("#lbMsg","#lightBox").html("Your Ad Was Deleted!");
			  $.fn.smartLightBox.closeLightBox(1500);
			  tbRow.remove();
	    }
		else{
			 console.log("set job ad to inactive was unsuccessful");
		}
	   }catch(e){
		   //error-handling
	   }
	 }//eof readystate==4
	}
}
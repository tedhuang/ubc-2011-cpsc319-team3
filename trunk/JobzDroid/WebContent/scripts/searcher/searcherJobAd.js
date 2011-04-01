
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
			
	//		$.fn.smartLightBox.openlb('small','Retrieving Information...','load');
			xmlhttp.onreadystatechange=function()
			  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
					//parse XML response from server
					buildAdListTb("jobAd", outputDiv);
	//				$.fn.smartLightBox.closeLightBox(0);
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
			
		//	$.fn.smartLightBox.openlb('small','Retrieving Information...','load');
			xmlhttp.onreadystatechange=function()
			  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200)
			    {
				  $("#feedback").html("<h2>Successfully finished tasks</h2>");
					//parse XML response from server
					buildAdListTb("jobAd", outputDiv);
		//			$.fn.smartLightBox.closeLightBox(0);
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
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
//	$.fn.smartLightBox.openlb('small','Retrieving Information...','load');
	
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
	  else if(xmlhttp.status!=200){
		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
	  }
//	  $.fn.smartLightBox.closeLightBox(0);
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
	if(index < 0)
		index = 0;
	
	if(mode == "first")
		index = 0;
	else if(mode == "next")
		index = parseInt(index)+20; //update index
	else if(mode == "prev")
		index = parseInt(index)-20;	  
	
	request = new Request;
	request.addAction("getSomeJobAd");
	request.addParam("startingIndex", index);
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function(){
		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  buildGuestJobAdTb("jobAd", outputDiv);//uibot
			  
				var xmlObj = $("jobAd",xmlhttp.responseXML);

				if(xmlObj.length < 20 && index >= 20){//if this is the last page of results
					$("#prevButton").attr("disabled", false);
					$("#nextButton").attr("disabled", true);
				}
				else if(xmlObj.length < 20 && index < 20){ // if result size < 20 on first page
					$("#prevButton").attr("disabled", true);
					$("#nextButton").attr("disabled", true);
				}
				else if(index < 20){ // if first page
					$("#prevButton").attr("disabled", true);
					$("#nextButton").attr("disabled", false);
				}
				else{ // middle pages
					$("#prevButton").attr("disabled", false);
					$("#nextButton").attr("disabled", false);
				}
				
			  $("#browseIndex").val(index ); //increase index by 20
		    }
		  //$.fn.smartLightBox.closeLightBox(1000,"home-frame");
		};	  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	

}


/************************************************************************************************************
 * 				Get suggestion for searcher
 * @param outputDiv
 ************************************************************************************************************/
function getSuggestionForSearcher(outputDiv){
	
//	$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
	var strSessionKey = $("#sessionKey").val();
	request = new Request;
	request.addAction("getSuggestions");
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
		  
//		  buildOwnerAdTb("jobAd", outputDiv);//uibot
	    }
	  else{
		  
	  }
//	  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	};	  
}
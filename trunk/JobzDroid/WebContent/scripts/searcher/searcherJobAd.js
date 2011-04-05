
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
	 if(emptyCounts != searchFields.length){
		var xhr=createXHR();
		if(xhr){
			try{
				xhr.open("POST","../ServletJobAd" ,true);
				xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				xhr.onreadystatechange = processResult;
				xhr.send(request.toString());
				$("#feedback").html("<h2>Sending Request</h2>");
			}catch(e){
				
			}
		}
	 }//eof check empty fields
	 function processResult(){
		 if (xhr.readyState == 4) {
				try {
					  if (xhr.status == 200) {
						  buildAdListTb("jobAd", outputDiv,xhr.responseXML);
					  }
	                else { 
	                	$("#feedback").html("<h2 class='error'>Well this is embrassing, you request cannot be preformed</h2>");
	                }
				} 
				catch (e){
					console.log(e);
				}
		 }
	 }
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
function getJobAdById(id, outputDiv)
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
		  	buildDetailTable("jobAd", outputDiv);
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
function getSuggestionForSearcher(outputDiv, initSugg){
	
//	$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
	var strSessionKey = $("#sessionKey").val();
	request = new Request;
	request.addAction("getSuggestions");
	request.addParam("sessionKey", strSessionKey);
	request.addParam("mode", initSugg);
	
	var xhr = createXHR(); //Now created xhr used a function
	function processResult(){
		if (xhr.readyState == 4) {
			try {
				  if (xhr.status == 200) {
					  buildSuggList("jobAd", outputDiv, xhr.responseXML);
				  }
                else{ 
                	 console.log("Status error");
                }
			} 
			catch (e){
			}
	    }
//		  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	}//eof processResult
	
	if(xhr){
		try {
			  xhr.open("POST", "../ServletJobAd", true);
			  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			  xhr.onreadystatechange = processResult; //bind to callBack to have threaded ajax calls
			  xhr.send(request.toString());
		} catch (e) {
			             //Handle error
		  }
	}	  
}//eof getSugg

function buildSuggList(xmlTag, outputDiv, xhrResponse){
	var list = $("#"+outputDiv);
	var xmlObj = $(xmlTag,xhrResponse);
	if(xmlObj.length==0){//if no results
//		$("#feedback").html("<h2 class='error'>No Results Found. Profile.</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj\
			$('<li></li>')
			.append('<span class="title jsBtn">'+$(this).attr("jobAdTitle")+'</span>')
			.append('<span class="qkView">'+
					$(this).attr("creationDate") + " | " +
					$(this).attr("educationReq") + " | " +
					$(this).attr("location") 	 + " | " +
					$(this).attr("contactInfo") +
					'</span>')
			.delegate("span.title", "click", function(){
				$.fn.DynaSmartTab.viewDetail($(this).attr("jobAdId"));
			})
			.appendTo(list);
		 });
	}
}
/************************************************************************************************************
 * 				saveFavouriteJobAd
 * @param outputDiv
 ************************************************************************************************************/	

function saveFavouriteJobAd(sessKey, jobId){
	
	request = new Request;
	request.addAction("saveFavouriteJobAd");
	request.addParam("sessionKey", sessKey);
	request.addParam("jobAdId", jobId);
	
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

	
}

/************************************************************************************************************
 * 				Prepare & Setup Favourite Job Ad List
 * @param outputDiv
 ************************************************************************************************************/
function listFavouriteJobAd(outputDiv){
	outputDiv="#"+outputDiv;
	request = new Request;
	request.addAction("listFavouriteJobAd");
	
	var strSessionKey = $("#sessionKey").val();
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
		  buildFavouriteListTb("jobAd", outputDiv);	  
	    }
	  else{
		  //TODO: implement error handling
	  }
	};
}

/************************************************************************************************************
 * 				Build Favourite Job Ad List 
 * @param 
 ************************************************************************************************************/	
function buildFavouriteListTb(targetXMLTag, outputDiv){
	
	var strSessionKey = $("#sessionKey").val();
	
	var tbody  = $("tbody", outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
		$("#feedbackFavourites").html("<h2 class='error'>You have not saved any favourite Job Advertisements </h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAdXML = $(this);
		  var tr =$('<tr></tr>');
		  $('<td></td>').attr("id", id='td-pDate').text(jobAdXML.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').addClass('jsBtn').text(jobAdXML.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-status').text(jobAdXML.attr("contactInfo")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-eduReq').text(jobAdXML.attr("eduReqFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-jobAvail').text(jobAdXML.attr("jobAvail")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-loc').html(jobAdXML.children("location").attr("address")).appendTo(tr);
		  
		  $('<div></div>')
		  .addClass('jsBtn rmFavJob')
		  .click(function(){
			  deleteFavouriteJobAd(strSessionKey, jobAdXML.attr('jobAdId'));
		  })
		  .appendTo(tr);
		  
		 // $.fn.DynaSmartTab.searchJSTool(tr, profileSearcherXML.attr("accountID"));
		  tr.appendTo(tbody);
		});
		 
		 $("tr:odd", tbody).addClass("oddRow");
		 //$("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}	

/************************************************************************************************************
 * 				deleteFavouriteJobAd
 * @param outputDiv
 ************************************************************************************************************/	

function deleteFavouriteJobAd(sessKey, jobId){
	
	request = new Request;
	request.addAction("deleteFavouriteJobAd");
	request.addParam("sessionKey", sessKey);
	request.addParam("jobAdId", jobId);
	
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

	
}
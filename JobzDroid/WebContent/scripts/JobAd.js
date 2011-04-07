
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
				  //AJAX: TODO:remove this job ad row
				  $("#row-"+intJobAdId).remove();
		    }
			else{
				  console.log("Change Job Ad Status Failed");
				  	$("#lbImg", theForm).removeClass("load").addClass("alert");
					$("#lbMsg",theForm).html("Action Not Successful, please try again");
					$.fn.smartLightBox.closeLightBox(1000, formDiv);
			}
		   }catch(e){
			   //error-handling
		   }
		 }
	}
	
	
}

/**
 * Admin Function: handles delete job ad
 */
function adminDeleteJobAd(intJobAdId){
	
	var formDiv = 'home-frame';
	var theForm =$("#"+formDiv);
	var sessionKey = $("#sessionKey").val();
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminDeleteJobAd");
	request.addParam("jobAdId", intJobAdId);
	request.addParam("sessionKey", sessionKey);
	
	$("#feedback").text("Sending Delete Request");

	//Concurrent Ajax handling
	var xmlhttp=createXHR();
	if(xmlhttp){
		try{
			//send the parameters to the servlet with POST
			xmlhttp.open("POST","../ServletAdmin" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.onreadystatechange = processResponse;
			xmlhttp.send( request.toString() );
		}catch(e){
			
		}
	}
	
	function processResponse(){
		  if (xmlhttp.readyState==4){ 
		    try {
			  if(xmlhttp.status==200){
				  //AJAX: TODO:remove this job ad row
				  $("#row-"+intJobAdId).remove();
		    }
			else{
				  console.log("Deny Job Ad not successsful");
				  	$("#lbImg", theForm).removeClass("load").addClass("alert");
					$("#lbMsg",theForm).html("Deleting Not Successful, please try again");
					$.fn.smartLightBox.closeLightBox(1000, formDiv);
			}
		   }catch(e){
			   //error-handling
		   }
		 }
	}  
	
}


/**
 * Admin Function: handles approving job ads
 */
function adminApprove(intJobAdId){
		
	var formDiv = 'home-frame';
	var theForm =$("#"+formDiv);
	var tbody  = $("tbody", "#allJobAdtable");
//	$.fn.smartLightBox.openDivlb(formDiv,'load','Loading..');
	
	var sessionKey = document.getElementById("sessionKey").value;
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminApprove");
	request.addParam("jobAdId", intJobAdId);
	request.addParam("sessionKey", sessionKey);
	
	$("#feedback").text("Sending Request");
	
	//Concurrent Ajax handling
	var xmlhttp=createXHR();
	if(xmlhttp){
		try{
			//send the parameters to the servlet with POST
			xmlhttp.open("POST","../ServletAdmin" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.onreadystatechange = processResponse;
			xmlhttp.send( request.toString() );
		}catch(e){
			
		}
	}
	
	function processResponse(){
		  if (xmlhttp.readyState==4){ 
		    try {
			  if(xmlhttp.status==200){
				  $("#td-status-"+intJobAdId).text("open");
				  $("#td-approval-"+intJobAdId).text("Approved");
				  $("input[name='"+intJobAdId+"']")[0].checked = true;
				  $("#row-"+intJobAdId).removeClass("oddRow change");
				  $("tr:odd", tbody).addClass("oddRow");
		    }
			else{
				  console.log("Approve Job Ad not successful");		
				  	$("#lbImg", theForm).removeClass("load").addClass("alert");
					$("#lbMsg",theForm).html("Approving Not Successful, please try again");
					$.fn.smartLightBox.closeLightBox(1000, formDiv);
			}
		   }catch(e){
			   //error-handling
		   }
		 }
	}
}


function adminDeny(intJobAdId){

	var tbody  = $("tbody", "#allJobAdtable");
	var formDiv = 'home-frame';
	var theForm =$("#"+formDiv);
	
	var sessionKey =  $("#sessionKey").val();
	
	if( intJobAdId == null ){
		alert("Job Ad ID is not provided");
	}
	
	request = new Request;
	request.addAction("adminDeny");
	request.addParam("jobAdId", intJobAdId);
	request.addParam("sessionKey", sessionKey);
	
	
	//Concurrent Ajax handling
	var xmlhttp=createXHR();
	if(xmlhttp){
		try{
			//send the parameters to the servlet with POST
			xmlhttp.open("POST","../ServletAdmin" ,true);
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlhttp.onreadystatechange = processResponse;
			xmlhttp.send( request.toString() );
		}catch(e){
			
		}
	}
	
	function processResponse(){
		  if (xmlhttp.readyState==4){ 
		    try {
			  if(xmlhttp.status==200){
				  $("#td-status-"+intJobAdId).text("draft");
				  $("#td-approval-"+intJobAdId).text("Not Approved");
				  $("input[name='"+intJobAdId+"']")[1].checked = true;
				  $("#row-"+intJobAdId).removeClass("oddRow change");
				  $("tr:odd", tbody).addClass("oddRow");
		    }
			else{
				  console.log("Deny Job Ad not successsful");
				  	$("#lbImg", theForm).removeClass("load").addClass("alert");
					$("#lbMsg",theForm).html("Denying Not Successful, please try again");
					$.fn.smartLightBox.closeLightBox(1000, formDiv);
			}
		   }catch(e){
			   //error-handling
		   }
		 }
	}  
	  
}


function adminBatchChangeJobAd(){    
	//$.fn.smartLightBox.openDivlb("home-frame",'load','Reloading Data, please wait...');
	
	var xmlObj = $("jobAd",xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
	  	$("#lbImg", $("#home-frame")).removeClass("load").addClass("alert");
		$("#lbMsg",$("#home-frame")).html("Denying Not Successful, please try again");
		//$.fn.smartLightBox.closeLightBox(2000, "home-frame");
	}
	else{
		xmlObj.each( function() {
		    
			//for All returned xml obj
			var jobAd = $(this);
			var jobAdId = jobAd.attr("jobAdId");
			var isApprovedOld=jobAd.attr("isApproved");
			var radioButton= $('input:radio[name='+jobAdId+']:checked').val();
			var isCallFinished=false;
			  //Job Ad is Approved
			  if( isApprovedOld == 0 && radioButton == "approve"){
				   adminApprove(jobAdId); 
			  }
			  //Job Ad is Denied
			  else if( isApprovedOld == 1 && radioButton == "deny" ){
				   adminDeny(jobAdId);
			  }
			  //Job Ad is Deleted
			  else if( radioButton == "delete" ){
				  adminDeleteJobAd(jobAdId);
			  }else{
				  //Nothing Changed for this Job Ad
			  }
		});//END OF FOR EACH LOOP
	}
    //$.fn.smartLightBox.closeLightBox(2000, "home-frame");
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
//function getJobAdById(mode, id, outputDiv)
//{
////	$.fn.smartLightBox.openDivlb("edAdFrame", 'load','loading data...');
//	request = new Request;
//	request.addAction("getJobAdById");
//	request.addParam("jobAdId", id);
//	var fb = $(".feedback", "#"+outputDiv);
//	//change the text while sending the request
//	fb.html("<h2>Sending getJobAdById Request</h2>");
//	
//	if (window.XMLHttpRequest)
//	  {// code for IE7+, Firefox, Chrome, Opera, Safari
//	  xmlhttp=new XMLHttpRequest();
//	  }
//	else
//	  {// code for IE6, IE5
//	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
//	  }
//	$.fn.smartLightBox.openlb('small','Retrieving Information...','load');
//	//send the parameters to the servlet with POST
//	xmlhttp.open("POST","../ServletJobAd" ,true);
//	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
//	xmlhttp.send(request.toString());
//	
//	xmlhttp.onreadystatechange=function()
//	  {
//	  if (xmlhttp.readyState==4 && xmlhttp.status==200){
//		    //parse XML response from server
//		  fb.html("<h2 class='good'> Successfully finished tasks</h2>");	
//		  if(mode=="detail"){
//		  	buildDetailTable("jobAd", outputDiv);
//		  }
//		  else if(mode=="edit"){
//			  $.fn.DynaSmartTab.loadEdData("jobAd", outputDiv, mode);
//		  }
//		  $.fn.smartLightBox.closeLightBox(0);
//	    }
//	  else if(xmlhttp.status!=200){
//		  fb.html("<h2 class='error'> Successfully finished tasks</h2>");
//	  }
//	  };
//}
function getJobAdById(id, outputDiv){
	
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
	$.fn.smartLightBox.openDivlb("adDetailFrame", 'load','loading data...');
	
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
	  $.fn.smartLightBox.closeLightBox(0, "adDetailFrame");
	  };
}
/************************************************************************************************************
 * 				Function used by admin to get job ads of all status
 * @param outputDiv
 ************************************************************************************************************/
function adminGetJobAd(outputDiv, mode){
	
	$.fn.smartLightBox.openDivlb("home-frame",'load','loading...');

	var strSessionKey = $("#sessionKey").val();
	
	var filter = $("#filter").text();
	
	var index = $("#browseIndex").val();
	if(index < 0)
		index = 0;
	
	if(mode == "first")
		index = 0;
	else if(mode == "next")
		index = parseInt(index)+15; //update index
	else if(mode == "prev")
		index = parseInt(index)-15;	  
	
	request = new Request;
	request.addAction("adminGetJobAd");
	request.addParam("startingIndex", index);
	request.addParam("sessionKey", strSessionKey);
	request.addParam("filter", filter);

	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange=function(){
		  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  
			  buildAdminJobAdTb("jobAd", outputDiv);//uibot
			  
				var xmlObj = $("jobAd",xmlhttp.responseXML);

				if(xmlObj.length < 15 && index >= 15){//if this is the last page of results
					$("#prevButton").attr("disabled", false);
					$("#nextButton").attr("disabled", true);
				}
				else if(xmlObj.length < 15 && index < 15){ // if result size < 15 on first page
					$("#prevButton").attr("disabled", true);
					$("#nextButton").attr("disabled", true);
				}
				else if(index < 15){ // if first page
					$("#prevButton").attr("disabled", true);
					$("#nextButton").attr("disabled", false);
				}
				else{ // middle pages
					$("#prevButton").attr("disabled", false);
					$("#nextButton").attr("disabled", false);
				}
			  $("#browseIndex").val(index ); //increase index by 15
		    }
		};	  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAdmin" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
}



/************************************************************************************************************
 * 				Get Job Ad for Guest
 * @param outputDiv
 ************************************************************************************************************/
function guestViewJobAd(outputDiv, mode){
	
	$.fn.smartLightBox.openlb('small','Loading...','load');

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
		    $.fn.smartLightBox.closeLightBox(500);
		};	  
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

}

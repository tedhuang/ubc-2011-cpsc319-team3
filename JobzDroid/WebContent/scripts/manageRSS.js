/**
 * Javascript file for managing RSS (admin)
 */

$("document").ready(function() {
	// client side error checking
	$("input").bind("change", validateInput);
	// allow content to have maximum 2048 characters
	$("#contentInput").bind("keyup", function(){
		limitChars('contentInput', 10000, 'contentInfo');
	});
	// send request to admin servlet on submit
	$("#submitButton").bind("click", postRSS);
	$('#tabs').DynaSmartTab({});
	$('#sideMenu').sideNavMenu({});
});

// client side error checking, can add additional checks on other fields if needed
function validateInput(evt){
	// case: rss title input changed
	if( $(this).attr('id') == "titleInput" ){
		var title = $(this).val();
		if(trim(title) == "")
			$("#titleError").text("Title must not be empty.");
		else
			$("#titleError").text("");
	}
}

// send post news request
function postRSS(evt){
	$("#submitButton").attr("disabled", true);
	$("#statusText").removeClass();	
	var strRSSTitle = trim($("#titleInput").val());
	var strRSSContent = $("#contentInput").val();
	var strSessionKey = $("#sessionKey").val();
	var strFeedType = $("#feedType").val(); 
	
	var xmlHttpReq;
	if (window.XMLHttpRequest){
		// Firefox, Chrome, Opera, Safari
		xmlHttpReq = new XMLHttpRequest();
	}
	else{
		// IE
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					//parse XML response from server
					var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;	
					 if(boolResult == "true"){
						 $("#statusText").addClass("successTag");
						 $("#submitButton").removeAttr("disabled");
						 $("#titleInput").val("");
						 $("#contentInput").val("");
						 loadPageWithSession('manageRSS.jsp');
					 }
					 else
						 $("#statusText").addClass("errorTag");		
					 
					$("#submitButton").removeAttr("disabled");
			    	$("#statusText").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("postRSS");
	request.addSessionKey(strSessionKey);
	request.addParam("title", strRSSTitle);
	request.addParam("content", strRSSContent);
	request.addParam("type", strFeedType);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}



function sendDeleteRSSRequest(index){
	var strSessionKey = $("#sessionKey").val();
    var b = confirm("Are you sure to delete News ID: " + idNews + "?\nIts associated RSS entry will also be removed.");
    if (b == false)
        return false;
    
	$(".linkImg").attr("disabled", true);	
	
	var xmlHttpReq;
	if (window.XMLHttpRequest){
		// Firefox, Chrome, Opera, Safari
		xmlHttpReq = new XMLHttpRequest();
	}
	else{
		// IE
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					//parse XML response from server
					 var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					 var strMsg = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;				
					 $(".linkImg").removeAttr("disabled");
					 alert(strMsg);
					 if(boolResult == "true")
						 loadPageWithSession('manageRSS.jsp');
				}
			}};
	}
	request = new Request;
	request.addAction("deleteRSSEntry");
	request.addSessionKey(strSessionKey);
	request.addParam("entryIndex", entryIndex);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
}
/**
 * Javascript file for forget password page
 */

$("document").ready(function() {
	$("#sendButton").bind("click",sendPasswordChangeRequest);
});

function sendPasswordChangeRequest(evt){
	$("#sendButton").attr("disabled", true);
	$("#statusText").removeClass("errorTag");	
	$("#statusText").removeClass("successTag");
	var strEmail = $("#emailInput").val();
	
	var xmlHttpReq;
	if (window.XMLHttpRequest){
		// Firefox, Chrome, Opera, Safari
		xmlHttpReq = new XMLHttpRequest();
	}
	else{
		// IE
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	  
	xmlHttpReq.onreadystatechange = function(){
		if (xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			$("#sendButton").removeAttr("disabled");
			//parse XML response from server
			var responseText = parseRegResponse(xmlHttpReq.responseXML);
	    	$("#statusText").text(responseText);
		}};

	request = new Request;
	request.addAction("requestForgetPassword");
	request.addParam("email", strEmail);

	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAccount", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}

// parses response from server
function parseRegResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 if(boolResult == "true"){
		 $("#statusText").addClass("successTag");
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}
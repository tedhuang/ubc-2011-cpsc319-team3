/**
 * Javascript for Unban User Page (admin)
 */

$("document").ready(function() {
	$("#filter").bind("keyup", function(){
		applyFilter("userTable", "filter");
	});
	$("#submitButton").bind("click", sendUnbanRequest);
	$('#tabs').DynaSmartTab({});
});

function copyEmailToInput(email){
	$("#userNameInput").val(email);
	return false;
}

function viewProfile(idAccount){
	getProfileById(idAccount, 'profileTable', 'profileHeading');
	return false;
}

// send ban request to admin servlet
function sendUnbanRequest(evt){
	var strUserName = trim($("#userNameInput").val());
	var strSessionKey = $("#sessionKey").val();
	// ask user to confirm first
    var b = confirm("Are you sure to unban user " + strUserName + "?");
    if (b == false)
        return false;

	$("#submitButton").attr("disabled", true);
	$("#statusText").removeClass("errorTag");	
	$("#statusText").removeClass("successTag");
	
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
					var responseText = parseUnbanResponse(xmlHttpReq.responseXML);
					$("#submitButton").removeAttr("disabled");
			    	$("#statusText").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("unban");
	request.addSessionKey(strSessionKey);
	request.addParam("email", strUserName);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}

// parses response from server
function parseUnbanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if unban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#statusText").addClass("successTag");
		 loadPageWithSession('unban.jsp');
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}
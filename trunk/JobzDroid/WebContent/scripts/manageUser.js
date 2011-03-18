/**
 * Javascript for Ban User Page (admin)
 */
var submittedUser = "";

$("document").ready(function() {
	$("#filterBan").bind("keyup", function(){
		applyFilter("tableBanUser", "filterBan");
	});
	$("#filterUnban").bind("keyup", function(){
		applyFilter("tableUnbanUser", "filterUnban");
	});
	$("#submitBan").bind("click", function(){
		sendBanRequest($("#userNameInputFirstFrame").val());
	});
	$("#submitUnban").bind("click", function(){
		sendUnbanRequest($("#userNameInputSecondFrame").val());
	});
	$("#submitDeleteFirstFrame").bind("click", function(){
		sendDeleteAccountRequest($("#userNameInputFirstFrame").val(), "manageUser.jsp", "statusTextFirstFrame");
	});
	$("#submitDeleteSecondFrame").bind("click", function(){
		sendDeleteAccountRequest($("#userNameInputSecondFrame").val(), "managerUser.jsp", "statusTextSecondFrame");
	});
	$('#tabs').DynaSmartTab({});
	$('#sideMenu').sideNavMenu({});
});

function copyEmailToInput(email, inputID){
	$("#"+inputID).val(email);
	return false;
}

function viewProfile(idAccount){
	getProfileById(idAccount, 'profileTable', 'profileHeading');
	return false;
}

// send ban request to admin servlet
function sendBanRequest(strUserName){
	var strSessionKey = $("#sessionKey").val();
	// ask user to confirm first
    var b = confirm("Are you sure to ban user " + strUserName + "?");
    if (b == false)
        return false;

	$("button").attr("disabled", true);
	$(".linkImg").attr("disabled", true);
	$("#statusTextFirstFrame").removeClass("errorTag");	
	$("#statusTextFirstFrame").removeClass("successTag");
	
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
					var responseText = parseBanResponse(xmlHttpReq.responseXML);
					$("button").removeAttr("disabled");
					$(".linkImg").removeAttr("disabled");
			    	$("#statusTextFirstFrame").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("ban");
	request.addSessionKey(strSessionKey);
	request.addParam("email", strUserName);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
		
	//update status text
	submittedUser = strUserName;	
	$("#statusTextFirstFrame").text("Processing...This may take a moment.");
}

// parses response from server
function parseBanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if ban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#statusTextFirstFrame").addClass("successTag");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else
		 $("#statusTextFirstFrame").addClass("errorTag");
	 return strMsg;
}

//send unban request to admin servlet
function sendUnbanRequest(strUserName){
	var strSessionKey = $("#sessionKey").val();
	// ask user to confirm first
    var b = confirm("Are you sure to unban user " + strUserName + "?");
    if (b == false)
        return false;

	$("button").attr("disabled", true);
	$(".linkImg").attr("disabled", true);
	$("#statusTextSecondFrame").removeClass("errorTag");	
	$("#statusTextSecondFrame").removeClass("successTag");
	
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
					$("#button").removeAttr("disabled");
					$(".linkImg").removeAttr("disabled");
			    	$("#statusTextSecondFrame").text(responseText);
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
	$("#statusTextSecondFrame").text("Processing...This may take a moment.");
}

// parses response from server
function parseUnbanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if unban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#statusTextSecondFrame").addClass("successTag");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else
		 $("#statusTextSecondFrame").addClass("errorTag");
	 return strMsg;
}
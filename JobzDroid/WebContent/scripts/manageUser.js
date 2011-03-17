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
	$("#submitBan").bind("click", sendBanRequest);
	$("#submitUnban").bind("click", sendUnbanRequest);
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
function sendBanRequest(evt){
	var strUserName = trim($("#banUserNameInput").val());
	var strSessionKey = $("#sessionKey").val();
	// ask user to confirm first
    var b = confirm("Are you sure to ban user " + strUserName + "?");
    if (b == false)
        return false;

	$("#submitBan").attr("disabled", true);
	$("#statusTextBan").removeClass("errorTag");	
	$("#statusTextBan").removeClass("successTag");
	
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
					$("#submitBan").removeAttr("disabled");
			    	$("#statusTextBan").text(responseText);
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
	$("#statusTextBan").text("Processing...This may take a moment.");
}

// parses response from server
function parseBanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if ban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#statusTextBan").addClass("successTag");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else
		 $("#statusTextBan").addClass("errorTag");
	 return strMsg;
}

//send unban request to admin servlet
function sendUnbanRequest(evt){
	var strUserName = trim($("#unbanUserNameInput").val());
	var strSessionKey = $("#sessionKey").val();
	// ask user to confirm first
    var b = confirm("Are you sure to unban user " + strUserName + "?");
    if (b == false)
        return false;

	$("#submitUnban").attr("disabled", true);
	$("#statusTextUnban").removeClass("errorTag");	
	$("#statusTextUnban").removeClass("successTag");
	
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
					$("#submitUnban").removeAttr("disabled");
			    	$("#statusTextUnban").text(responseText);
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
	$("#statusTextUnban").text("Processing...This may take a moment.");
}

// parses response from server
function parseUnbanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if unban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#statusTextUnban").addClass("successTag");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else
		 $("#statusTextUnban").addClass("errorTag");
	 return strMsg;
}

//send delete account request to admin servlet
function sendDeleteAccountRequest(accountName, pageSource){
    var b = confirm("Are you sure to PERMANENTLY delete account " + accountName + "?");
    if (b == false)
        return false;
    var strSessionKey = $("#sessionKey").val();
	$(".linkImg").attr("disabled", true);
	$("#statusTextDelete").removeClass("errorTag");	
	$("#statusTextDelete").removeClass("successTag");
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
		xmlHttpReq = new XMLHttpRequest();
	else
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					//parse XML response from server
					
					 var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					 var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
					 // if registration sucessful, then update button text and function
					 if(boolResult == "true"){
						 $("#statusTextDelete").addClass("successTag");
						 loadPageWithSession(pageSource);
					 }
					 else
						 $("#statusTextDelete").addClass("errorTag");
										
					$(".linkImg").removeAttr("disabled");
			    	$("#statusTextDelete").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("deleteAccount");
	request.addSessionKey(strSessionKey);
	request.addParam("accountName", accountName);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
		
	//update status text
	$("#statusTextDelete").text("Processing...This may take a moment.");
}
/**
 * Javascript file for adding/deleting admin accounts (super admin)
 */
$("document").ready(function() {
	$("#filter").bind("keyup", function(){
		applyFilter("userTable", "filter");
	});
	$("#createAdminFrame input").bind("change", validateInputs);
	$("#createAdminButton").bind("click", sendCreateAdminRequest);
	$('#tabs').DynaSmartTab({});
});

function deleteAdmin(accountName){
    var b = confirm("Are you sure to permanently delete " + accountName + "?");
    if (b == false)
        return false;
}

//client side error checking
function validateInputs(evt){
	// case: account name
	if( $(this).attr('id') == "accountName" ){
			var strAccountNamePattern = /^([A-Za-z0-9_\-\.]){5,50}$/;
			var strAccountName = $(this).val();
			if(strAccountNamePattern.test(strAccountName) == false) 
				$("#userNameError").text("Account name must be 5+ non-special characters.");
			else
				$("#userNameError").text("");
	}
	// case: password changed (must be 5-15 non-white-space characters)
	else if( $(this).attr('id') == "password1" ){
		var strPasswordPattern = /^([A-Za-z0-9_\-\.]){5,15}$/;
		var password = $(this).val();
		if(strPasswordPattern.test(password) == false)
			$("#password1Error").text("Password must be 5 - 15 non-special characters.");
		else
			$("#password1Error").text("");
	}
	// case: pw re-type changed (must be same as pw1)
	else if( $(this).attr('id') == "password2" ){
		var password1 = $("#password1").val();
		var password2 = $(this).val();
		if(password1 != password2)
			$("#password2Error").text("Passwords do not match.");
		else
			$("#password2Error").text("");
	}
}

// send create admin request to admin servlet
function sendCreateAdminRequest(evt){
	var strAccountName = $("#accountName").val();
	var strPassword = $("#password1").val();
	var strPasswordRepeat = $("#password2").val();
	var strSessionKey = $("#sessionKey").val();

	$("#createAdminButton").attr("disabled", true);
	$("#statusTextCreate").removeClass("errorTag");	
	$("#statusTextCreate").removeClass("successTag");
	
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
					var responseText = parseResponseCreate(xmlHttpReq.responseXML);
					$("#createAdminButton").removeAttr("disabled");
			    	$("#statusTextCreate").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("createAdmin");
	request.addSessionKey(strSessionKey);
	request.addParam("accountName", strAccountName);
	request.addParam("password", strPassword);
	request.addParam("passwordRepeat", strPasswordRepeat);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
		
	//update status text and submitted email
	$("#statusTextCreate").text("Processing...This may take a moment.");
}

// parses response from server for admin creation
function parseResponseCreate(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if registration sucessful, then update button text and function
	 if(boolResult == "true"){
		 $("#statusTextCreate").addClass("successTag");
		 loadPageWithSession('manageAdmin.jsp');
	 }
	 else
		 $("#statusTextCreate").addClass("errorTag");
	 return strMsg;
}
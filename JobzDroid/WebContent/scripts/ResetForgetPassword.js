/**
 * Javascript file for reset forgotten password page
 */
$("document").ready(function() {
	// validate fields on change
	$("input").bind("change", validatePasswords);
	// reset password on submit
	$("#submitButton").bind("click",sendResetPassword);
});

function validatePasswords(evt){
	// case: new password changed (must be 5-15 non-white-space characters)
	if( $(this).attr('id') == "password1" ){
		var strPasswordPattern = /^([A-Za-z0-9_\-\.]){5,15}$/;
		var password = $(this).val();
		if(strPasswordPattern.test(password) == false) {
			$("#password1Error").text("Password must be 5 to 15 characters long, and cannot contain special characters.");
		}
		else{
			$("#password1Error").text("");
		}
	}
	// case: pw re-type changed (must be same as pw1)
	else if( $(this).attr('id') == "password2" ){
		var password1 = $("#password1").val();
		var password2 = $(this).val();
		if(password1 != password2) {
			$("#password2Error").text("Passwords do not match.");
		}
		else{
			$("#password2Error").text("");
		}
	}
}

function sendResetPassword(evt){
	$("#submitButton").attr("disabled", true);
	$("#statusText").removeClass("errorTag");	
	$("#statusText").removeClass("successTag");
	var strPassword = $("#password1").val();
	var strPasswordRepeat = $("#password2").val();
	var strIdPasswordReset = $("#idPasswordReset").val();
	
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
			$("#submitButton").removeAttr("disabled");
			//parse XML response from server
			var responseText = parseResponse(xmlHttpReq.responseXML);		   
	    	$("#statusText").text(responseText);
		}};


	request = new Request;
	request.addAction("resetForgetPassword");
	request.addParam("password", strPassword);
	request.addParam("passwordRepeat", strPasswordRepeat);
	request.addParam("idPasswordReset", strIdPasswordReset);

	//send the request to servlet
	xmlHttpReq.open("POST","http://localhost:8080/JobzDroid/ServletAccount", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}

// parses response from server
function parseResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 if(boolResult == "true"){
		 $("#statusText").addClass("successTag");
		 $("input").attr("disabled", true);
		 $("#submitButton").text("Return to Home Page");
		 $("#submitButton").unbind("click", sendResetPassword);
		 $("#submitButton").bind("click", function(){
			 window.location = "./index.html";
		 });
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}
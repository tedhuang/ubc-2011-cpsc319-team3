/**
 * Javascript file for user registration page
 */

$("document").ready(function() {
	// update name label based on account type chosen
	$("input:[name=accountType]").bind("change",function(){
		if( $("input:[name=accountType]:checked").val() == "searcher" ){
			$("#nameLabel").text("Name: ");
		}
		else{
			$("#nameLabel").text("Company/organization name: ");
		}
	});
	// client side error checking
	$("input").bind("change", validateForm);
	// send request to registeration servlet on submit
	$("#submitButton").bind("click",sendRegRequest);
});

// client side error checking
function validateForm(evt){
	// case: account type changed (must have at least one selected)
	if( $(this).is("[name=accountType]") ){
		if( !$(this).val() ){
			alert("Account type not selected!");
		}
		$("#nameError").text("");
	}
	// case: email changed
	else if( $(this).attr('id') == "emailAddress" ){
			var strEmailPattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,})$/;
			var strEmailAddress = $(this).val();
			if(strEmailPattern.test(strEmailAddress) == false) {
				$("#emailError").text("Invalid Email Address");
			}
			else{
				$("#emailError").text("");
			}				
	}
	// case: password changed (must be 5-15 non-white-space characters)
	else if( $(this).attr('id') == "password1" ){
		var strPasswordPattern = /^([A-Za-z0-9_\-\.]){5,15}$/;
		var password = $(this).val();
		if(strPasswordPattern.test(password) == false) {
			$("#password1Error").text("Must be 5 - 15 non-special characters.");
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
	// case: name field changed (must not be empty)
	else if( $(this).attr('id') == "name" ){
		var strNamePattern = /^([A-Za-z0-9_\-\.])+$/;
		var strName = $("#name").val();
		var accountType = $("input[name=accountType]:checked").val();
		if( !strName || strName == "" ) {
			if(accountType == "searcher"){
				$("#nameError").text("Name must not be empty.");
			}
			else if(accountType == "poster"){
				$("#nameError").text("Company/organization name must not be empty.");
			}
		}
		else if(strNamePattern.test(strName) == false)	{
			$("#nameError").text("Special characters are not allowed.");
		}
		else{
			$("#nameError").text("");
		}
	}
}

// sends account reg request to the corresponding servlet
function sendRegRequest(evt){
	$("#submitButton").attr("disabled", true);
	var strEmail = $("#emailAddress").val();
	var strPassword = $("#password1").val();
	var strPasswordRepeat = $("#password2").val();
	var strAccountType = $("input[name=accountType]:checked").val();
	var strName = $("#name").val();
	
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
			//parse XML response from server
			var responseText = parseRegResponse(xmlHttpReq.responseXML);	
			$("#submitButton").removeAttr("disabled");
	    	$("#statusText").text(responseText);
		}};


	request = new Request;
	request.addAction("register");
	request.addParam("email", strEmail);
	request.addParam("password", strPassword);
	request.addParam("passwordRepeat", strPasswordRepeat);
	request.addParam("accountType", strAccountType);
	request.addParam("name", strName);

	//send the request to servlet
	xmlHttpReq.open("POST","./ServletAccount", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing... Please wait.");
}

// parses response from server
function parseRegResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 if(boolResult == "true"){
		 $("input").attr("disabled", true);
		 $("#submitButton").text("Return to Home Page");
		 $("#submitButton").unbind("click", sendRegRequest);
		 $("#submitButton").bind("click", function(){
			 window.location = "./index.html";
		 });
	 }
	 return strMsg;
}
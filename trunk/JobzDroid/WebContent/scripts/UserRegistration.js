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
	// real-time client side error checking
	$("input").bind("change", validateForm);
	// send request to registeration servlet on submit
	$("#submitButton").bind("click",sendRegRequest);
});

// real-time client side error checking
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
		var strPasswordPattern = /^\S{5,15}$/;
		var password = $(this).val();
		if(strPasswordPattern.test(password) == false) {
			$("#password1Error").text("Password must be 5 to 15 characters long, and cannot contain white spaces.");
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
		else{
			$("#nameError").text("");
		}
	}
}

// sends account reg request to the corresponding servlet
function sendRegRequest(evt){
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
	    	$("#statusText").text(responseText);
		}};
	
	var params = "Email=" + encodeURIComponent(strEmail) + "&Password=" + encodeURIComponent(strPassword) + 
				"&PasswordRepeat=" + encodeURIComponent(strPasswordRepeat) + "&AccountType=" + encodeURIComponent(strAccountType)
				+ "&Name=" + encodeURIComponent(strName);

	//send the parameters to servlet
	xmlHttpReq.open("POST","../ServletUserRegistration", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(params);
	
	//update status text
	$("#statusText").text("Sending Request...");
}


function parseRegResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 /*
	 if(result == "true"){
		 window.location="./userRegistration.html";
	 }*/
	 return strMsg;
}
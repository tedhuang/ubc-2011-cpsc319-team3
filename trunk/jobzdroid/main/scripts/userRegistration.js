/**
 * Javascript file for user registration page
 */

$("document").ready(function() {
	// update name label based on account type chosen
	$("input:[name=accType]").bind("change",function(){
		if( $("input:[name=accType]:checked").val() == "searcher" ){
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
	if( $(this).is("[name=accType]") ){
		if( !$(this).val() ){
			alert("Account type not selected!");
		}
		$("#nameErr").text("");
	}
	// case: email changed
	else if( $(this).attr('id') == "emailAddr" ){
			var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,})$/;
			var addr = $(this).val();
			if(reg.test(addr) == false) {
				$("#emailErr").text("Invalid Email Address");
			}
			else{
				$("#emailErr").text("");
			}				
	}
	// case: password changed (must be 5-15 non-white-space characters)
	else if( $(this).attr('id') == "pw1" ){
		var reg = /^\S{5,15}$/;
		var pw = $(this).val();
		if(reg.test(pw) == false) {
			$("#pw1Err").text("Password must be 5 to 15 characters long, and cannot contain white spaces.");
		}
		else{
			$("#pw1Err").text("");
		}
	}
	// case: pw re-type changed (must be same as pw1)
	else if( $(this).attr('id') == "pw2" ){
		var pw1 = $("#pw1").val();
		var pw2 = $(this).val();
		if(pw1 != pw2) {
			$("#pw2Err").text("Passwords do not match.");
		}
		else{
			$("#pw2Err").text("");
		}
	}
	// case: name field changed (must not be empty)
	else if( $(this).attr('id') == "name" ){
		var name = $("#name").val();
		var accType = $("input[name=accType]:checked").val();
		if( !name || name == "" ) {
			if(accType == "searcher"){
				$("#nameErr").text("Name must not be empty.");
			}
			else if(accType == "poster"){
				$("#nameErr").text("Company/organization name must not be empty.");
			}
		}
		else{
			$("#nameErr").text("");
		}
	}
}

// sends account reg request to the corresponding servlet
function sendRegRequest(evt){
	var email = $("#emailAddr").val();
	var pw = $("#pw1").val();
	var pwRepeat = $("#pw2").val();
	var accType = $("input[name=accType]:checked").val();
	var name = $("#name").val();
	if (window.XMLHttpRequest){
		// Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	}
	else{
		// IE
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	  
	xmlhttp.onreadystatechange = function(){
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
			//parse XML response from server
			var responseText = parseRegResponse(xmlhttp.responseXML);		   
	    	$("#statusText").text(responseText);
		}
	};
	
	var params = "Email=" + encodeURIComponent(email) + "&Password=" + encodeURIComponent(pw) + "&PasswordRepeat=" + encodeURIComponent(pwRepeat)
				+ "&AccountType=" + encodeURIComponent(accType) + "&Name=" + encodeURIComponent(name);

	//send the parameters to servlet
	xmlhttp.open("POST","../UserRegistrationServlet" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(params);
	
	//update status text
	$("#statusText").text("Sending Request...");
}


function parseRegResponse(responseXML){	
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;	 
	 
	 /*var responseText = "";
	 
	 if(success == "false"){
		 responseText = "This email address has already been used. Please use another one.";
	 }
	 else{
		 responseText = "User Created!";
	 }*/
	 
	 return result;
}
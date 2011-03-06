/**
 * Javascript file for user registration page
 */

$("document").ready(function() {
	// generate input fields based on account type chosen
	$("input:[name=accountType]").bind("change",function(){
		if( $("input:[name=accountType]:checked").val() == "searcher" ){
			$(".accountTypeSpecific").remove();
			$("#regTable").append(
					"<tr class='accountTypeSpecific'>" +
						"<td class='label force-no-break'><div> ** Education level: </div></td>" +
						"<td>" +
				        	"<select id='eduLevel'>" +
				        		"<option value='0'>None</option>" +
				        		"<option value='1'>B.Sc</option>" +
				        		"<option value='2'>M.Sc</option>" +
				        		"<option value='3'>Ph.D</option>" +
				        	"</select>" +
				        "</td>" +
				    "</tr>" +
				    "<tr class='accountTypeSpecific'>" +
				    	"<td class='label force-no-break'><div> Starting date (yyyy-mm-dd): </div></td>" + 
				    	"<td>" +
				        	"<input type='text' id='startingDate' class='textinput' maxlength='50' tabindex='14'>" +
				        	"<span id='startingDateError' class='errorTag'></span>" +
				        "</td>" +
				    "</tr>" +
				    "<tr class='accountTypeSpecific'>" +
					    "<td class='label force-no-break'>" +
					        "<div> Employment preference: </div>" +
					    "</td>" + 
					    "<td>" +
							"<input type='checkbox' name='empPref' value='full' /> Full time" + 
							"<input type='checkbox' name='empPref' value='part' /> Part time " +
							"<input type='checkbox' name='empPref' value='intern' /> Internship" + 
					    "</td>" +
					"</tr>"
				);
		}
		else{
			$(".accountTypeSpecific").remove();
		}
	});
	// client side error checking
	$("input").bind("change", validateForm);
	// allow description to have maximum 250 characters
	$("#description").bind("keyup", function(){
		limitChars('description', 250, 'descInfo');
	});
	// send request to registeration servlet on submit
	$("#submitButton").bind("click",sendRegRequest);
});

// client side error checking
function validateForm(evt){
	// case: account type changed (must have at least one selected)
	if( $(this).is("[name=accountType]") ){
		if( !$(this).val() )
			alert("Account type not selected!");
		$("#nameError").text("");
	}
	// case: primary email changed
	else if( $(this).attr('id') == "emailAddress" ){
			var strEmailPattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,})$/;
			var strEmailAddress = $(this).val();
			if(strEmailPattern.test(strEmailAddress) == false) 
				$("#emailError").text("Invalid Email Address");
			else
				$("#emailError").text("");
	}
	// case: secondary email changed
	else if( $(this).attr('id') == "secondaryEmailAddress" ){
		var strEmailPattern = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,})$/;
		var strEmailAddress = $(this).val();
		if( strEmailAddress && strEmailAddress != "" ){
			if(strEmailPattern.test(strEmailAddress) == false) 
				$("#secondaryEmailError").text("Invalid Email Address");
			else
				$("#secondaryEmailError").text("");
		}
		else
			$("#secondaryEmailError").text("");
	}
	// case: password changed (must be 5-15 non-white-space characters)
	else if( $(this).attr('id') == "password1" ){
		var strPasswordPattern = /^([A-Za-z0-9_\-\.]){5,15}$/;
		var password = $(this).val();
		if(strPasswordPattern.test(password) == false)
			$("#password1Error").text("Must be 5 - 15 non-special characters.");
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
	// case: name field changed (must not be empty)
	else if( $(this).attr('id') == "name" ){
		var strName = $("#name").val();
		var accountType = $("input[name=accountType]:checked").val();
		if( !strName || strName == "" ) 
			$("#nameError").text("Name must not be empty.");
		else
			$("#nameError").text("");
	}
	// case: phone changed
	else if( $(this).attr('id') == "phone" ){
		var strPhonePattern = /^\(?(\d{3})\)?[- ]?(\d{3})[- ]?(\d{4})$/;
		var strPhone = $(this).val();
		if( strPhone && strPhone != "" ){
			if(strPhonePattern.test(strPhone) == false) 
				$("#phoneError").text("Invalid Phone Number.");
			else
				$("#phoneError").text("");
		}
		else
			$("#phoneError").text("");
	}
	// case: starting date changed
	else if( $(this).attr('id') == "startingDate" ){
		var strStartingDate = $("#startingDate").val();
		var strDatePattern = /^\d{4}\/\d{2}\/\d{2}$/;
		if( strStartingDate && strStartingDate != "" ) {
			if(strDatePattern.test(strStartingDate) == false)
				$("#startingDateError").text("Invalid date format.");
		}
		else
			$("#startingDateError").text("");
	}
}

// sends account reg request to the corresponding servlet
function sendRegRequest(evt){
	$("#submitButton").attr("disabled", true);
	$("#statusText").removeClass("errorTag");	
	$("#statusText").removeClass("successTag");
	var strEmail = $("#emailAddress").val();
	var strSecondaryEmail = $("#secondaryEmailAddress").val();
	var strPassword = $("#password1").val();
	var strPasswordRepeat = $("#password2").val();
	var strAccountType = $("input[name=accountType]:checked").val();
	var strName = $("#name").val();
	var strAddress = $("#address").val();
	var strPhone = $("#phone").val();
	var strDescription = $("#description").val();
	
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
					var responseText = parseRegResponse(xmlHttpReq.responseXML);
					$("#submitButton").removeAttr("disabled");
			    	$("#statusText").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("register");
	// common parameters
	request.addParam("email", strEmail);
	if( strSecondaryEmail && strSecondaryEmail != "" )
		request.addParam("secondaryEmail", strSecondaryEmail);
	request.addParam("password", strPassword);
	request.addParam("passwordRepeat", strPasswordRepeat);
	request.addParam("accountType", strAccountType);
	request.addParam("name", strName);
	if( strAddress && strAddress != "" )
		request.addParam("address", strAddress);
	if( strPhone && strPhone != "" )
		request.addParam("phone", strPhone);
	if( strDescription && strDescription != "" )
		request.addParam("description", strDescription);
	
	// account type specific parameters
	if( strAccountType == "searcher" ){
		var eduLevel = $("#eduLevel").val();
		var strStartingDate = $("#startingDate").val();
		var strEmpPref = "";
		$('input[name=empPref]:checked').each(function() {
			strEmpPref += $(this).val() + "_";
			});
		request.addParam("eduLevel", eduLevel);
		if( strStartingDate && strStartingDate != "")
			request.addParam("startingDate", strStartingDate);
		if( strEmpPref && strEmpPref != "")
			request.addParam("empPref", strEmpPref);
	}
	else{
		// no poster specific fields currently
	}
	//send the request to servlet
	xmlHttpReq.open("POST","./ServletAccount", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}

// parses response from server
function parseRegResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if registration sucessful, then update button text and function
	 if(boolResult == "true"){
		 $("input").attr("disabled", true);
		 $("select").attr("disabled", true);
		 $("textarea").attr("disabled", true);
		 $("#statusText").addClass("successTag");
		 $("#submitButton").text("Return to Home Page");
		 $("#submitButton").unbind("click", sendRegRequest);
		 $("#submitButton").bind("click", function(){
			 window.location = "./index.html";
		 });
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}
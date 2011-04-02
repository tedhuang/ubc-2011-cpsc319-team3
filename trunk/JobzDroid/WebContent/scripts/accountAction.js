/*****************************************************************************************************
 * 					Helper Function - Handles Changes for Primary Email
 ****************************************************************************************************/
function changePrimaryEmail(sessionKey, newEmail){
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	request = new Request;
	request.addAction("requestEmailChange");
	request.addSessionKey(sessionKey);
	
	request.addParam("newEmail",newEmail);
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());


	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
			document.getElementById("submitAccountButton").disabled=false;
		  //TODO: parse response
			alert( (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue );
	    }
	  };
}

/*****************************************************************************************************
 * 					Helper function - Handles Changes for Secondary Email
 ****************************************************************************************************/
function changeSecondaryEmail(sessionKey, secEmail){
	request = new Request;
	request.addAction("requestSecondaryEmailChange");
	request.addSessionKey(sessionKey);
	
	request.addParam("secEmail",secEmail);
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
			document.getElementById("submitAccountButton").disabled=false;
		  //TODO: parse response
			alert( (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue);
			
			
	    }
	  };
}

/*****************************************************************************************************
 * 					Helper function - Handles Changes for Password
 ****************************************************************************************************/
function changePassword(sessionKey, oldPassword, newPassword, newPasswordRepeat){
	request = new Request;
	request.addAction("requestPasswordChange");
	request.addSessionKey(sessionKey);	
	request.addParam("oldPassword", oldPassword);
	request.addParam("newPassword", newPassword);
	request.addParam("newPasswordRepeat", newPasswordRepeat);
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
			document.getElementById("submitAccountButton").disabled=false;
		  //TODO: parse response
			alert( (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue);
			
			
	    }
	  };	
}


/*****************************************************************************************************
 * 					Change Account Fields - Primary E-mail, Secondary E-mail, Password
 ****************************************************************************************************/
function submitChangeAccount(){
	
	//disables button to prevent multiple submit
	document.getElementById("submitAccountButton").disabled=true;
	
	var sessionKey = $("#sessionKey").val();
	
	//Account Changes
	var strNewEmail 	= document.getElementById("emailNew").value;
	var strSecEmail		= document.getElementById("secondaryEmail").value;
	var strOldPW 		= document.getElementById("passwordOld").value;
	var strNewPW	 	= document.getElementById("passwordNew").value;
	var strRepeatPW		= document.getElementById("passwordRepeat").value;

	
	//Check if secondary e-mail has been given a value
	if(strSecEmail && strSecEmail != ""){
		//TODO: implement real-time AJAX response for format check
		changeSecondaryEmail(sessionKey, strSecEmail);
	}

	//Check if primary e-mail has been given a value
	if(strNewEmail && strNewEmail != ""){
		//TODO: implement real-time AJAX response for format check
		changePrimaryEmail(sessionKey, strNewEmail);
	}

	//Check if password fields have been filled
	if(strNewPW && strNewPW != ""){
		if( ( strOldPW && strOldPW != ""  ) && (strRepeatPW && strRepeatPW != "") ){
			
			if(strNewPW != strRepeatPW){
				//TODO: implement real-time AJAX response
				alert("Error: new password and repeat password does not match");
			}
			else{
				//Initiate password change functions
				changePassword(sessionKey, strOldPW, strNewPW, strRepeatPW);
			}
				
			
		}else{
			alert("Error: some password fields are empty");
			document.getElementById("submitAccountButton").disabled=false;
			return;
		}
	}
	
	
}


/*****************************************************************************************************
 * 					Send Password Change - Send Change Passowrd Request
 ****************************************************************************************************/
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
			var responseText = parseForgetPasswordResponse(xmlHttpReq.responseXML);
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
function parseForgetPasswordResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 if(boolResult == "true"){
		 $("#statusText").addClass("successTag");
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}




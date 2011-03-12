// read session key from URL on load
$("document").ready(function(){
	loadSessionKeyFromURL();
	});

/*****************************************************************************************************
 * 					LOG IN FUNCTION
 ****************************************************************************************************/
function userLoginRequest()//TODO Recover lightbox element
{
//	$("#loginBox").hide();
//	openbox("sign-inLoading",'',1);
	$("#submitButton").attr("disabled", true);
	$("#submitButton").text("Processing...");
	$("#loginError").text("");
	var email = document.getElementById("email").value;
	var password = document.getElementById("password").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //Gets sessionKey and prints it to div
			var sessionKey = (xmlhttp.responseXML.getElementsByTagName("sessionKey")[0]).childNodes[0].nodeValue;
			//var userID = (xmlhttp.responseXML.getElementsByTagName("userID")[0]).childNodes[0].nodeValue;
			
		    if( sessionKey != "null" ) 
			    { 	
					var action = (xmlhttp.responseXML.getElementsByTagName("action")[0]).childNodes[0].nodeValue;
		    		document.sid.action = action;
					document.getElementById("sessionKey").value = sessionKey;
					document.sid.submit();
		    	}
		    else
		    	{
			    	//TODO: implement error handling
		    		$("#submitButton").removeAttr("disabled");
		    		$("#submitButton").text("Log in");
					$("#loginError").text("Incorrect usersname(your Email address) and password combination.");
//		    		closePopup("sign-inLoading");
//			    	$("#loginBox").show();
		    	}
	    }
	  };
	  
	var Params = "action=requestForLogin" + "&email=" + email + "&password=" + password;

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","./ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);
}

/*****************************************************************************************************
 * 					ADMIN LOG IN FUNCTION
 ****************************************************************************************************/
function adminLoginRequest()
{
	$("#submitButton").attr("disabled", true);
	$("#submitButton").text("Processing...");
	$("#loginError").text("");
	var strEmail = document.getElementById("email").value;
	var strPassword = document.getElementById("password").value;
	
	if (window.XMLHttpRequest)
		xmlHttpReq = new XMLHttpRequest();
	else
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	  
	xmlHttpReq.onreadystatechange=function(){
	  if (xmlHttpReq.readyState==4 && xmlHttpReq.status==200)
	    {
		    // get sessionKey
			var sessionKey = (xmlHttpReq.responseXML.getElementsByTagName("sessionKey")[0]).childNodes[0].nodeValue;			
		    if( sessionKey != "null" ) { 
		    	var action = (xmlHttpReq.responseXML.getElementsByTagName("action")[0]).childNodes[0].nodeValue;
		    	document.sid.action = action;
		    	document.getElementById("sessionKey").value = sessionKey;
		    	document.sid.submit();
		    }
		    else {
	    		$("#submitButton").removeAttr("disabled");
	    		$("#submitButton").text("Log in");
				$("#loginError").text("Failed admin login attempt.");
	    	}
	    }
	  };
	request = new Request;
	request.addAction("requestForAdminLogin");
	request.addParam("email", strEmail);
	request.addParam("password", strPassword);

	//send the parameters to the servlet with POST
	xmlHttpReq.open("POST","../ServletAdmin" ,true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
}


/*****************************************************************************************************
 * 					LOG OUT FUNCTION
 ****************************************************************************************************/
function userLogoutRequest()
{
	var sessionKey = document.getElementById("sessionKey").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  //TODO change this
			document.getElementById("header").innerHTML = "You have LOGGED OUT!";   //if logout successfully, redirect to the main page 
	    }
	  else //logout failed display error messege
		 {
		  	
		 }
	  };
	  
	  var Params = "action=requestForLogout" + "&sessionKey=" + sessionKey;

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","./ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);
}

/*****************************************************************************************************
 * 					Get Session Key Function
 ****************************************************************************************************/
function loadSessionKeyFromURL()
{
	name = "sessionKey";
	var start=location.search.indexOf("?"+name+"=");
	
	if ( start<0 ){
		start=location.search.indexOf("&"+name+"=");
	}
	if (start<0){
		return '';
	}
	
	start += name.length+2;
	
	var end=location.search.indexOf("&",start)-1;
	if (end<0) {
		end=location.search.length;
	}
	
	var result='';
	for(var i = start;i<=end;i++) {
		var c = location.search.charAt(i);
//		next line replaces '+' with ' '
//		result = result + (c=='+'?' ':c);
		result = result + (c);
	}
	$("#sessionKey").val(result);
	//TODO wipe the sessionKey from address
}

/*****************************************************************************************************
 * 					Load the URL and Pass the session key via form
 ****************************************************************************************************/
function loadPageWithSession( pageURL )
{		
	var sessionKey = document.getElementById("sessionKey").value;
    if( sessionKey != "null" ) {
    	document.sid.action = pageURL;
    	document.sid.submit();
    }
    return false;
}

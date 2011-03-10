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
	var email = $("#email").val();
	var password = $("#password").val();
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	var Params = "action=requestForLogin" + "&email=" + email + "&password=" + password;

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","./ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);

	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //Gets sessionKey and prints it to div
			var sessionKey = (xmlhttp.responseXML.getElementsByTagName("sessionKey")[0]).childNodes[0].nodeValue;
			//var userID = (xmlhttp.responseXML.getElementsByTagName("userID")[0]).childNodes[0].nodeValue;
			
		    if( sessionKey != "null" ) 
			    { 	
		    		document.sid.action = action;
					document.$("#sessionKey").val( sessionKey );
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
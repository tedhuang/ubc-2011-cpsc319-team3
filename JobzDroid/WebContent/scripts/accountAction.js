/*****************************************************************************************************
 * 					LOG IN FUNCTION
 ****************************************************************************************************/
function userLoginRequest()//TODO Recover lightbox element
{
//	$("#loginBox").hide();
//	openbox("sign-inLoading",'',1);
	var email = $("#email").value;
	var password = $("#password").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	var Params = "action=requestforlogin" + "&email=" + email + "&password=" + password;
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
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
					var responseText = "<h2>User " + email + " logged in with SESSION KEY: ";
										responseText += sessionKey  + "</h2>";		
					
					$("#myDiv").html(responseText);
					$("#header").html("LOGGED IN!");
					$("#sessKey").val( sessionKey );
					//$("#submitUserID").value = userID;
					//$("#close").submit();
					//$("#loginBox").remove();
		    	}
		    else
		    	{
			    	//TODO: implement error handling
			    	alert("Login Failed");
//		    		closePopup("sign-inLoading");
			    	$("#myDiv").html("<h2>Login Failed!</h2>");
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
	var sessKey = $("#sessKey").val();
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	var Params = "action=requestforlogout" + "&sessKey=" + sessKey;
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params); 
	
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
			$("#header").innerHTML = "You have LOGGED OUT!";   //if logout successfully, redirect to the main page 
	    }
	  else //logout failed display error messege
		 {
		  	
		 }
	  };
	  
}
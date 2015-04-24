# Introduction #

Additional details and examples on handling parameter on the client side.


# Using Sessions #

Many request calls requires the use to be signed in and given a privilege level, we verify this privilege level via session. A session key will be stored on the client side, which will be passed along with the request for a certain calls. The servlet will use this sessionkey to determine the session, and the privilege level, of the user, before executing the action.

## Requesting a Session ##

Upon successful login, a session will be generated, and a sessionKey will be included in the response to the login request.  Once the response is received, the client should store the sessionKey inside a hiddenn form.

  * Hidden form
```
<body>
<h1> insert other html junk here </h1>
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
```

When we get the response back, we need to store the sessionKey insdie the hiden form

  * code that logs in, and stores sessionKey Inside Hidden form after getting a response
```

function userLoginRequest()
{

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
	  
  // This is the key function that executes after getting a response
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //Gets sessionKey and stores it in hidden form
			var sessionKey = (xmlhttp.responseXML.getElementsByTagName("sessionKey")[0]).childNodes[0].nodeValue;
			
		    if( sessionKey != "null" ) 
			    { 	
document.getElementById("sessionKey").value = sessionKey;
					document.sid.submit();
		    	}
	    }
	  };
	  
	var Params = "action=requestForLogin" + "&email=" + email + "&password=" + password;

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);
}
```


> Since there is multiple web page for our client, we need a way to pass the sessionKey between the pages.  We will use the "get" method from our hidden form to submit the sessionKey to the next page.  I will post two examples, one for link (which you can apply for button as well), and one example for auto-redirect.

  * example for link. Include the following code with previous
```
<script>
function sendSessionKey()
{
  document.sid.action = "Test1.html";
  document.sid.submit();
}
</script>

<a href="javascript:sendSessionKey();">Go to Test1.html</a>
```

  * example for auto-redirect, modify the previous code with following
```
// previous code...
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //Gets sessionKey and stores it in hidden form
			var sessionKey = (xmlhttp.responseXML.getElementsByTagName("sessionKey")[0]).childNodes[0].nodeValue;
			
		    if( sessionKey != "null" ) 
			    {
                                document.getElementById("sessionKey").value = sessionKey;
		    		document.sid.action = "Test1.html";
					document.sid.submit();
		    	}
	    }
	  };
```

  * For implmentation details of above code, see /WebContent/index.html which includes /WebContent/scripts/authentication.js

Once you entered the new page, you will notice that the URL will now contain a new parameter "sessionKey=...".  That is the session key, and the new page must implement code to read this from the URL and stores it inside a new hidden form.  It may also be wise to remove the parameter, which we will talk about later.

  * code that reads the sessionKey from URL.  loadSessionKeyFromURL() is already coded, simply import authentication.js
```

<script type="text/javascript" src='../scripts/testAuthentication.js'></script>

<script type="text/javascript">


$("document").ready(function() {
	loadSessionKeyFromURL();
});
</script>
```

  * for real implementation, see /WebContent/home.html

## send session key for server call that requires one ##

simply include the session key inside the parameter of a servlet call, like this

```
	  var Params = "action=doSomething" + "&sessionKey=" + document.getElementById("sessionKey").value;

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletSomething" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);
```

Now you can get session key from server, pass session key between pages, and send session key with a server call!!  See BasicRequestResponse and ResponseDetails for more information on how the request/response flow for JobzDroid works.


## Removing SessionKey from URL ##

TO BE MADE
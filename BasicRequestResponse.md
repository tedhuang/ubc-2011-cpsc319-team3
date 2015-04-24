# Introduction #

Every action performed by JobzDroid, with the exception of system automated functions, will be initiated by the JobzDroid client.  The client requests and action on the server, and the server will attempt to execute the request.  The request is sent in a specific format.  The server may reply to the client, and this is sent in a specific format as well.  We will attempt to explain the process in the following article.  You can look at more detailed page of RequestDetails and ResponseDetails.


# Client side #

Requests will be sent from the client side.  For more specific info, see RequestDetails

## Initiating Requests ##
Most browsers supports sending of HTTPXML requests, here's the code that initiates one, for cross browser compatibiliy.
```
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
```


## Specifying a handling function for Response ##
Requests are sent from the client.  Most requests, you will expect a response. To perform an action upon receiving response, do this...

```
xmlhttp.onreadystatechange=function(){
  if (xmlhttp.readyState==4 && xmlhttp.status==200) {
    //get the XML value of tag myInfo
    var sessionKey = (xmlhttp.responseXML.getElementsByTagName("myInfo")[0]).childNodes[0].nodeValue;
    //another good method is to use xmlhttp.responseXML.getAttribute...
  }
  else {
    //do something when response fails ie status!=200
  };
```


## Specifying an action for Response ##

When sending a request, you must specify the servlet you are intending to call.  You will most likely need to add additional parameters to the request.  Since most of our serlvet will handle multiple actions, one of these parameter will likely be "action".  Below is an example of login, we are making a request to ServletAccount, with the action being "requestForLogin", and additional parameters of "email" and "password".

```

	var Params = "action=requestForLogin" + "&email=" + email + "&password=" + password;

	//send the parameters to the ServletAccount via POST method
	xmlhttp.open("POST","../ServletAccount" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);
```

  * For real implementation samples, see /WebContent/scripts/authentication.js


## Sessions ##

Each client may contain a hidden form for storing session key, the client must submit this session key for certain "action".  See RequestDetails for this feature.

# Server Side #

Requests will be sent to JobzDroid server, and will be handled through one of the servlets. Once the request is handled, a response will be sent.  For more detail, see ResponseDetail.


## The Servlet ##

Requests will be sent to a servlet, these servlet will examine the "action" field of the request and figure how how to handle the request.  For each servlet, there is a list of available "action"s it will be programmed to handle.  Here is an example

```
    public ServletSomething() {
        super();
    }
    
    // Enumerates the action parameter
	private enum EnumAction	{
		action0,
		action1,
		action2,
		action3,
		action4
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("action");
		// forward to error page if request is invalid
		try{
			EnumAction.valueOf(action);
		}
		catch(Exception e){
		//	forwardErrorPage(request, response);
			throw new ServletException("Invalid account servlet action.");
		}
		
		switch( EnumAction.valueOf(action) ){

			case action0:
				doAction1(request, response);
				break;

			case action2:
				doAction2(request, response);
				break;

			case action3:
				doAction3(request, response);
				break;				

			case action4:
				doNothing(request, response);
				break;				
				
		}
	}
```

## Sending Response ##

Response should be sent in XML format, and should always contain a "message" and a "result" field.

```
		// Write XML containing message and result to response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + result + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
```

  * for real implementation sample, see /src/servlets/ServletAccount->userLoginRequest()
# Introduction #

How servlet handles request and how to use session to check


## Checking Session ##

Inside our DBManager, we have a function getSessionByKey( String sessionKey ) which fetches a Session object from DB that matches the given sessionKey.
Session object contains several useful information, including the user's idAccount, user's accountType, as well as the expiryTime of the session.  Usually, we will want to check the idAccount and accountType to verify user's privilege and his ID, in order to perform the tasks at hand.

  * Sample call
```
  Session currentSession = dbManager.getSessionByKey( stringSomeKey );
  if ( currentSession.getAccountType().equals("admin")) {
    System.out.println("user account type is admin, we can do admin stuf!");
  }
;
```

To implement user checking for certain "action", there are two ways to implment this.

After calling the function appropriate to handling the "action", we check the user type.  We break if the user type does not match.

  * use break labels to "early exit" after account check (current solution)
```
	private void doAdminOnlyAction(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		//initialize return statements
		String message = "Admin action failed";
		boolean isSuccessful = false;
		
		//check user's sessionKey
		Session userSession = dbManager.getSessionByKey(request.getParameter("sessionKey"));
		
		earlyExit: {
			
		if ( userSession == null ) {
			System.out.println("session key invalid");
			message = "Failed to authenticate user session";
			break earlyExit;
		}
		else {
			//TODO implmement this
			if( 		userSession.getAccountType().equals("admin")) {
				System.out.print("User has the correct priviliege");
			}
			else {
				message = "User does not have the right privilege";
				break earlyExit;
			}

		}
                // add code here to do admin only stuff

		}//earlyExit:

                // sending of response
		StringBuffer XMLResponse = new StringBuffer();	
		XMLResponse.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		XMLResponse.append("<response>\n");
		XMLResponse.append("\t<result>" + isSuccessful + "</result>\n");
		XMLResponse.append("\t<message>" + message + "</message>\n");
		XMLResponse.append("</response>\n");
		response.setContentType("application/xml");
		response.getWriter().println(XMLResponse);
	}
```

Or we can determine the privlege after switching on "action", before entering the handling function.

  * Handle session checking inside the "action" switch statement (for future consideration)
```
                switch( EnumAction.valueOf(action) ){

                        case adminAction:
                                if( userSession.getAccountType().equals("admin") )
                                doAdminAction(request, response);
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
                // We will haveto handle the response externally at this point.
```
Currently, we pass the response inside the handling function.  We may have to handle the response outside the handling function in order for this structure to work.

## More Information ##

There are more information to be added.  Also, check out BasicRequestResponse and RequestDetails to get a wholistic picture how of this entire process works!
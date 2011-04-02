<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, managers.AccountManager, classes.Session
    , java.io.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<title>Post News</title>
</head>
<body>
	<%
	int idAccSession, idAccDoc;
	int idDoc = -1; 
	// check session key and document id to determine if the user has permission
	DBManager dbManager = DBManager.getInstance();
	AccountManager acctManager = new AccountManager();
	String strIdOwner = request.getParameter("idOwner");
	String filename = request.getParameter("filename");
	
	String sessionKey = request.getParameter("sessionKey");
	Session s = acctManager.getSessionByKey(sessionKey);
	
	// if invalid document owner id, then redirect to error page
	int idOwner = -1;
	try{
		idOwner = Integer.parseInt(strIdOwner);
	}
	catch(NumberFormatException e){
		response.sendRedirect("error.html");
	}
	// if invalid session, then redirect to login page
	if (s == null){
		response.sendRedirect("index.html");
	}
	// if session belongs to a searcher that does not own the document, then redirect to error page
	else if ( s.getAccountType().equals("searcher") && s.getIdAccount() != idOwner ){
		response.sendRedirect("error.html");
	}
	else{
		// if all checks pass, then allow user to download file
		String filepath = "/JobzDroid/Documents/" + idOwner + "/";
		
		BufferedInputStream inputStream = null;
		try{
			File file = new File( filepath + filename );
			// clear web page headers from from output stream
			out.clear();
			//set response headers
			response.setContentType("text/plain");			
			response.addHeader("Content-Disposition","attachment; filename=" + filename );
			response.setContentLength( (int) file.length() );
			FileInputStream input = new FileInputStream(file);
			inputStream = new BufferedInputStream(input);
			int readBytes = 0;

			//read from the file; write to the ServletOutputStream
			while((readBytes = inputStream.read()) != -1)
				out.write(readBytes);
		} 
		catch(IOException e) {
			System.out.println("An error has occured while user " + s.getIdAccount()
					+ " is downloading file " + filepath + filename + " : " + e.getMessage());
			response.sendRedirect("error.html");	        
		} 
		finally {
		    //close input stream
		    if (inputStream != null)
		    	inputStream.close();		         
		}
	}
	%>	
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
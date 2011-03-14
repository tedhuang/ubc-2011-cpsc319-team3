<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, managers.DocumentManager, classes.Session, classes.Document
    , java.io.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="../css/mainStyle.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<title>Post News</title>
</head>
<body>
	<%	// Example URL to access this page: http://localhost:8080/JobsDroid/downloadDoc?idDoc=1&sessionKey=12345
	int idAccSession, idAccDoc;
	int idDoc = -1; 
	// check session key and document id to determine if the user has permission
	DBManager dbManager = DBManager.getInstance();
	String sessionKey = request.getParameter("sessionKey");
	Session s = dbManager.getSessionByKey(sessionKey);

	DocumentManager docManager = DocumentManager.getInstance();
	String strIdDoc = request.getParameter("idDoc");
	
	// if invalid docId, then forward to error page 
	try{
		idDoc = Integer.parseInt(strIdDoc);
	}
	catch(NumberFormatException e){
	%>
		<jsp:forward page="error.html" />
	<%
	}
	Document document = docManager.getDocumentById(idDoc);
	
	// if document doesn't exist, then forward to error page
	if ( document == null ){
		%>
		<jsp:forward page="error.html" />
		<%
	}	
	// if invalid session, then forward to error page
	else if (s == null){
	%>
		<jsp:forward page="error.html" />
	<%
	}
	// if session belongs to a searcher that does not own the document, then forward to error page
	else if ( s.getAccountType().equals("searcher") ){
		idAccDoc = document.getIdAccount();
		idAccSession = s.getIdAccount();
		if( idAccSession != idAccDoc ){
		%>
			<jsp:forward page="error.html" />
		<%
		}
	}
	else{
		// if all checks pass, then allow user to download file
		String filename = document.getDocName();
		String filepath = document.getDocPath();
		
		BufferedInputStream inputStream = null;
		%>
			<p>Download starting...</p>
		<%
		try{
			File file = new File( filepath + filename);
			
			//set response headers
			response.setContentType("text/plain");			
			response.addHeader("Content-Disposition","attachment; filename="+ filename );
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
			throw new ServletException("An error has occured while user " + s.getIdAccount()
					+ " is downloading file " + filepath + filename + " : " + e.getMessage());		        
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
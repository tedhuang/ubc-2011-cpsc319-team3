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
	<%
		
		BufferedInputStream inputStream = null;
		ServletOutputStream outStream = null;
		String filepath = "c:/JobzDroid/Documents/test.wmv";
		try{
			outStream = response.getOutputStream();
			File file = new File( filepath );
			
			//set response headers
			response.setContentType("text/plain");			
			response.addHeader("Content-Disposition","attachment; filename="+ "lol.abc" );
			response.setContentLength( (int) file.length() );
			
			FileInputStream input = new FileInputStream(file);
			inputStream = new BufferedInputStream(input);
			int readBytes = 0;

			//read from the file; write to the ServletOutputStream
			while((readBytes = inputStream.read()) != -1)
				outStream.write(readBytes);
		} 
		catch(IOException e) {
			System.out.println("An error has occured is downloading file " + filepath + " : " + e.getMessage());
			throw new ServletException(e.getMessage());		        
		} 
		finally {
		    //close input/output streams
		    if (outStream != null)		    	
		    	outStream.close();
		    if (inputStream != null)
		    	inputStream.close();		         
		}
	%>	
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, managers.DocumentManager, classes.Session, classes.Document
    , java.io.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<%
		
		BufferedInputStream inputStream = null;
		String filepath = "/JobzDroid/Documents/test.wmv";
		try{
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
				out.write(readBytes);
		} 
		catch(IOException e) {
			System.out.println("An error has occured is downloading file " + filepath + " : " + e.getMessage());
			throw new ServletException(e.getMessage());		        
		} 
		finally {
		    //close input/output streams
		    if (inputStream != null)
		    	inputStream.close();		         
		}
	%>	
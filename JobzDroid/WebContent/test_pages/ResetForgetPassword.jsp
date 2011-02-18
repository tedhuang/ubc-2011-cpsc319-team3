<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="http://localhost:8080/JobzDroid/css/UserRegistration.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js"></script>		
	<script type="text/javascript" src='http://localhost:8080/JobzDroid/scripts/Utility.js'></script>
	<script type="text/javascript" src="http://localhost:8080/JobzDroid/scripts/ResetForgetPassword.js"></script>
	<title>Reset Forget Password Test</title>
</head>
<body>
	<%	
	DBManager dbManager = new DBManager();
	String idPasswordReset = request.getParameter("id");
	int idAccount = dbManager.getIdAccountFromIdPasswordReset(idPasswordReset);
	// if password reset id is invalid or expired, then forward to error page.
	if (idAccount == -1){
	%>
	<jsp:forward page="../error.html" />
	<%
	}
	else{
	%>	
		<input type="hidden" id="idPasswordReset"  value=<%= idPasswordReset %> />

		<label for="password1"></label>Your New Password: <input id="password1" type="password" name="password" maxlength="15" />
		<span id="password1Error" class="errorTag"></span><br />
		
		<label for="password2"></label>Re-type password: <input id="password2" type="password" name="password" maxlength="15" />
		<span id="password2Error" class="errorTag"></span><br />
		
		<button id="submitButton" type="button">Submit</button><br />
		<p id="statusText"></p>		
	<%
	}
	%>
</body>
</html>
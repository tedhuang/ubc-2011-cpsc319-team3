<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="css/mainStyle.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>		
	<script type="text/javascript" src='scripts/Utility.js'></script>
	<script type="text/javascript" src="scripts/ResetForgetPassword.js"></script>
	<title>Reset Forgotten Password</title>
</head>
<body>
	<%	
	DBManager dbManager = DBManager.getInstance();
	String idPasswordReset = request.getParameter("id");
	int idAccount = dbManager.getIdAccountFromIdPasswordReset(idPasswordReset);
	// if password reset id is invalid or expired, then forward to error page.
	if (idAccount == -1){
		response.sendRedirect("error.html");	
	}
	else{
	%>	
	<div class="main">
	  <div class="header">
		<a id="logo" title="home" href="index.html">
	        <img src="images/logo-small.png"/>
		</a>
		<ul id="topnav" class="topnav">
		    <li><a href="news.jsp" target="_blank" class="btn">News</a></li>
		    <li><a href="rss.html" target="_blank" class="btn">RSS</a></li>
		    <li><a href="#" class="btn">View Job Ads</a></li>
		</ul>
	  </div>
	  
	  <br/>	
	  <h1><b><font size='4'>Reset Your Password:</font></b></h1>
	  <p class="pagefont">Please enter and confirm your new password below.</p>
		<table class="pagefont">
		<tbody>
			<tr>
				<td><b>Enter new password: </b></td>
				<td><input id="password1" type="password" name="password" maxlength="15" /></td>
				<td><span id="password1Error" class="errorTag"></span></td>
			</tr>
			<tr>
				<td><b>Confirm new password: </b></td>
				<td><input id="password2" type="password" name="password" maxlength="15" /></td>
				<td><span id="password2Error" class="errorTag"></span></td>
			</tr>
			<tr>
			<td><br /><button id="submitButton" type="button">Submit</button></td>
			</tr>
		</tbody>
		</table>
		<b><span id="statusText" class="pagefont"></span></b>
		<br/>
		<hr/>
	</div>

	<ul class="footer_wrapper2">
		<li>
			©2011 JobzDroid
		</li>
	</ul>	
	<%
	}
	%>	
	<input type="hidden" id="idPasswordReset"  value=<%= idPasswordReset %> />
</body>
</html>
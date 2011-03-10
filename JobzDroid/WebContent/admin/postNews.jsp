<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Post News</title>
</head>
<body>
	<%	
	DBManager dbManager = DBManager.getInstance();
	String sessionKey = request.getParameter("sessionKey");
	Session s = dbManager.getSessionByKey(sessionKey);
	// if invalid or non-admin session, then forward to error page.
	if (s == null){
	%>
		<jsp:forward page="../error.html" />
	<%
	}
	else if ( !s.getAccountType().equals("admin") && !s.getAccountType().equals("superAdmin")){
	%>
		<jsp:forward page="../error.html" />
	<%
	}
	else{ //TODO ADD POST NEWS UI
	%>	
		<div class="main">
		  <div class="header">
			<a id="logo" title="home" href="http://localhost:8080/JobzDroid/index.html">
		        <img src="http://localhost:8080/JobzDroid/images/logo-small.png"/>
			</a>
			<ul id="topnav" class="topnav">
			    <li><a href="#" class="btn">News</a></li>
			    <li><a href="#" class="btn">RSS</a></li>
			    <li><a href="#" class="btn">View Job Ads</a></li>
			</ul>
		  </div>
		  
		  <br/>	
		  <h1><b><font size='4'>Post News</font></b></h1>
		</div>
	
		<ul class="footer_wrapper2">
			<li>
				©2011 JobzDroid
			</li>
		</ul>	
	<%
	}
	%>	
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
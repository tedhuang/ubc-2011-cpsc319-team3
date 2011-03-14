<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />	
	<link href="http://localhost:8080/JobzDroid/css/mainStyle.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>		
	<script type="text/javascript" src='http://localhost:8080/JobzDroid/scripts/Utility.js'></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
<title>JobzDroid - Admin Home</title>
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
	else{ //TODO ADD ADMIN HOME UI
	%>	
		<div class="main">
		  <div class="header">
			<a id="logo" title="Home" href="#" onclick="loadPageWithSession('home.jsp')">
		        <img src="../images/logo-small.png"/>
			</a>
			<ul id="topnav" class="topnav">
			    <li><a href="../news.jsp" target="_blank" class="btn">News</a></li>
			    <li><a href="../rss/rss.html" target="_blank" class="btn">RSS</a></li>
			    <li><a href="#" class="btn">View Job Ads</a></li>
			</ul>
		  </div>
		  
		  <br/>	
		  <h1><b><font size='4'>Admin Home</font></b></h1>
		  <a href="#" onclick="loadPageWithSession('postNews.jsp')">Post News</a>
		  <a href="#" onclick="loadPageWithSession('manageJobAd.jsp')">Manage Jobs Advertisements</a>
		  <a href="#" onclick="loadPageWithSession('ban.jsp')">Ban User</a>
		  <a href="#" onclick="loadPageWithSession('unban.jsp')">Unban User</a>
		  
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
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
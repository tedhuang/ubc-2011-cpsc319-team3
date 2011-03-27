<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />	
	<link href="http://localhost:8080/JobzDroid/css/mainStyle.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>		
	<script type="text/javascript" src='../scripts/Utility.js'></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
<title>JobzDroid - Admin Home</title>
</head>
<body>
	<%	
	DBManager dbManager = DBManager.getInstance();
	String sessionKey = request.getParameter("sessionKey");
	Session s = dbManager.getSessionByKey(sessionKey);
	// if invalid or non-admin session, then redirect to index page.
	if (s == null){
		response.sendRedirect("../index.html");	
	}
	else if ( !s.getAccountType().equals("admin") && !s.getAccountType().equals("superAdmin")){
		response.sendRedirect("../index.html");	
	}
	else{
	%>	
		<div class="main">
		  <div class="header">
			<a id="logo" title="Home" href="#" onclick="loadPageWithSession('home.jsp')">
		        <img src="../images/logo-small.png"/>
			</a>
			<ul id="topnav" class="topnav">
			    <li><a href="../news.jsp" target="_blank" class="btn">News</a></li>
			    <li><a href="../rss.html" target="_blank" class="btn">RSS</a></li>
			    <li><a href="../guest/viewJobAdHome.jsp" class="btn">View Job Ads</a></li>
			</ul>
		  </div>
		  
		  <br/>	
		  <h2 class="welcome"><b><font size='4'>Administrator Home</font></b></h2>
		  <a href="#" onclick="loadPageWithSession('manageJobAd.jsp')">Manage Jobs Advertisements</a><br/>
		  <a href="#" onclick="loadPageWithSession('manageUser.jsp')">Manage Users</a><br/>
		  <a href="#" onclick="loadPageWithSession('manageNews.jsp')">Manage Site News</a><br/>
		  <a href="#" onclick="loadPageWithSession('manageRSS.jsp')">Manage RSS</a><br/>
		  <%
		  if(s.getAccountType().equals("superAdmin")){
			  %>
			  <a href="#" onclick="loadPageWithSession('manageAdmin.jsp')">Manage Admin Accounts</a><br/>
			  <%
		  }
		  %>
		  <a href="#" onclick="userLogoutRequest()">Log Out</a>
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
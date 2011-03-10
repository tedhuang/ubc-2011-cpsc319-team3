<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="css/mainStyle.css" rel="stylesheet" type="text/css" />
	<title>JobzDroid - Site News</title>
</head>
<body>
	<%	
	DBManager dbManager = DBManager.getInstance();
	// if password reset id is invalid or expired, then forward to error page.
	%>
	<div class="main">
	  <div class="header">
		<a id="logo" title="Home" href="index.html">
	        <img src="images/logo-small.png"/>
		</a>
		<ul id="topnav" class="topnav">
		    <li><a href="#" class="btn">News</a></li>
		    <li><a href="rss/rss.html" target="_blank" class="btn">RSS</a></li>
		    <li><a href="#" target="_blank" class="btn">View Job Ads</a></li>
		</ul>
	  </div>
	  
	  <br/>	
	  <h1><b><font size='4'>Site News</font></b></h1>
	  <%
	  	
	  %>
	  <br/>
	  <hr/>
	</div>

	<ul class="footer_wrapper2">
		<li>
			©2011 JobzDroid
		</li>
	</ul>	
</body>
</html>
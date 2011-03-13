<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session"%>
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
	<%	// check session key
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
	else{
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
		  <div class="regbox">
			<h3 class="heading-text">Ban User</h3>
				<table id="userTable">
				</table>
				<table>
					<tbody>
					  <tr>
					    <td class="clean"></td>
					  </tr>
					  <tr>
		    			<td class="label">
		          		    User name (Email address): 
		    			</td>
					    <td style="width: 100px">
					        <div>
					            <input type="text" class="textinput" id="newsTitle" size="50" maxlength="100" tabindex="11"/>
					      		<span id="titleError" class="errorTag"></span>
					        </div>
					    </td>
					    <td>
		          		    <button id="submitButton" type="button">Ban Hammer</button>
		    			</td>
					  </tr>
					  <tr>
					    <td class="clean"></td>
					  </tr>
				</tbody>
			</table>
			<p id="statusText" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>	
		  </div>	
		  
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
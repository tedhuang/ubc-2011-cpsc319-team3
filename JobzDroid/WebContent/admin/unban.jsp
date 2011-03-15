<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session, classes.Account, classes.Utility, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="../css/mainStyle.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/unban.js"></script>
	<title>Unban User</title>
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
		// get all searcher and poster accounts
		ArrayList<Account> users = dbManager.getSearcherPosterAccounts();
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
			<h3 class="heading-text">Unban User</h3>
			<h1><b><font size='4'>Banned Users</font></b></h1>
			<span class="label">
		          Filter: 
		    </span>
			<input type="text" class="textinput" id="filter" size="30"/>
			
			<table id="userTable">
				<thead>
					<tr>
						<th>Email</th>
						<th>Secondary Email</th>
						<th>User Type</th>
						<th>Account Creation Date</th>
					</tr>
				</thead>
				<%	// display all banned searcher poster accounts
					String email, secondaryEmail, type, strDateTimeCreated, emailID;
					long dateTimeCreated;
					for(int i = 0; i < users.size(); i++){
						Account acc = users.get(i);
						if( acc.getStatus().equals("banned") ){
							email = acc.getEmail();
							secondaryEmail = acc.getSecondaryEmail();
							if( secondaryEmail == null)
								secondaryEmail = "";
							type = acc.getType();
							dateTimeCreated = acc.getDateTimeCreated();
							strDateTimeCreated = Utility.longToDateString(dateTimeCreated, "PST");
							%>
								<tr title="<%= email %>">
									<td ><a href="#userNameInput" onclick="copyEmailToInput('<%= email %>')"><%= email %></a></td>
									<td><%= secondaryEmail %></td>
									<td><%= type %></td>
									<td><%= strDateTimeCreated %></td>
									<td><button onclick="viewProfile()">View Profile</button></td>
								</tr>
							<%
						}
					}
				%>
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
				            <input type="text" class="textinput" id="userNameInput" size="50" maxlength="100" tabindex="11"/>
				        </div>
				    </td>
				    <td>
	          		    <button id="submitButton" type="button">Unban</button>
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
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session, classes.Account, classes.Utility, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="../css/mainStyle.css" rel="stylesheet" type="text/css" />
	<link href="../css/DynaSmartTab.css" rel="stylesheet" type="text/css"/>
	<link href="../css/sideNavMenu.css" rel="stylesheet" type="text/css"/>
	
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/Profile.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src="../scripts/manageUser.js"></script>
	
	<title>Ban User</title>
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
		<!--Start tabs-->
	
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
		  <!-- STARTOF SIDEMENU -->
 	<ul id="sideMenu" class="sideNavMenu">
		<li>
			<a id="newadbtn" class="jsBtn" onclick="loadPageWithSession('manageJobAd.jsp')">
				<img src="../images/icon/ad_icon.jpg"/>
				<h2>Manage Job Ads</h2>
			</a>
		</li>
		
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageUser.jsp')">
				<img src="../images/icon/user_icon.jpg"/>
				<h2>Manage Users</h2>
			</a>
		</li>
	
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageNews.jsp')">
				<img src="../images/icon/news_icon.jpg"/>
				<h2>Manage Site News</h2>
			</a>
		</li>
		
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageRSS.jsp')">
				<img src="../images/icon/rss_icon.png"/>
				<h2>Manage RSS</h2>
			</a>
		</li>
		
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageAdmin.jsp')">
				<img src="../images/icon/admin_icon.jpg"/>
				<h2>Manage Admins</h2>
			</a>
		</li>
  	</ul><!--ENDOF SideMenu-->
  	
	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="banTab">
  				<a href="#banFrame"><h2>Ban User</h2></a>
			</li>
			<li id="profileTab">
  				<a href="#profileFrame"><h2>Profile</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	
	
  <div id="tabFrame">		
		<div id="banFrame" class="subFrame unremovable">
			<h1><b><font size='4'>List of Active Users</font></b></h1>
			<span class="label">
		          Email Filter: 
		    </span>
			<input type="text" class="textinput" id="filter" size="30"/>
			
			 <table id="userTable">
				<thead>
					<tr>
						<th>Account ID</th>
						<th>Email</th>
						<th>Secondary Email</th>
						<th>User Type</th>
						<th>Account Creation Date</th>
						<th>Profile</th>
					</tr>
				</thead>
				<tbody>
				<%	// display all active searcher poster accounts
					String email, secondaryEmail, type, strDateTimeCreated;
					int idAccount;
					long dateTimeCreated;
					for(int i = 0; i < users.size(); i++){
						Account acc = users.get(i);
						if( acc.getStatus().equals("active") ){
							idAccount = acc.getIdAccount();
							email = acc.getEmail();
							secondaryEmail = acc.getSecondaryEmail();
							if( secondaryEmail == null)
								secondaryEmail = "";
							type = acc.getType();
							dateTimeCreated = acc.getDateTimeCreated();
							strDateTimeCreated = Utility.longToDateString(dateTimeCreated, "PST");
							%>
								<tr title="<%= email %>">
									<td><%= idAccount %></td>
									<td><a href="#userNameInput"  onclick="copyEmailToInput('<%= email %>')"><%= email %></a></td>
									<td><%= secondaryEmail %></td>
									<td><%= type %></td>
									<td><%= strDateTimeCreated %></td>
									<td>
										<a title="View Profile" onclick="viewProfile('<%= idAccount %>')" class="linkImg">
		       						 		<img src="../images/icon/view_profile.png"/>
										</a>									
									</td>
								</tr>
							<%
						}
					}
				%>
				</tbody>
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
		</div><!--end of BANFRAME-->
		
		<div id="profileFrame" class="subFrame">
			 <div id="profileTable" class="resultTableDiv noBorder">
			 	<h2 id="profileHeading" class="welcome"></h2><span id="profileFB"></span>
				<table>
					<tbody>
					</tbody>
				</table>
			 </div>		
		</div><!--end of TABPROFILEFRAME-->
		
	  </div><!--ENDOF TABFRAME-->
	</div>   <!--end of tabs DIV-->		  
		  
		

	<%
	}
	%>	
	</div><!-- ENDOF MAIN -->
	<ul class="footer_wrapper2">
		<li>
			©2011 JobzDroid
		</li>
	</ul>	
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
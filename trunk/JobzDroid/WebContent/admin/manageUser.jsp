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
	<link href="../css/smartLightbox.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/smartLightbox.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/Profile.js"></script>
	<script type="text/javascript" src="../scripts/uiBot.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src="../scripts/deleteAccount.js"></script>
	<script type="text/javascript" src="../scripts/manageUser.js"></script>
	
	<title>Manage Users</title>
</head>
<body>
	<%	// check session key
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
		// get all searcher and poster accounts
		ArrayList<Account> users = dbManager.getSearcherPosterAccounts();
	%>	
	<!--Start tabs-->
	
	<div class="main">
		  <div class="header">
			<a id="logo" title="Home" href="../index.html" target="_blank">
		        <img src="../images/logo-small.png"/>
			</a>
			<ul id="topnav" class="topnav">
			    <li><a href="../news.jsp" target="_blank" class="btn">News</a></li>
			    <li><a href="../rss.html" target="_blank" class="btn">RSS</a></li>
			    <li><a href="../guest/viewJobAdHome.jsp" target="_blank" class="btn">View Job Ads</a></li>
			</ul>
		  </div>		  
		  <br/>	
	<!-- STARTOF SIDEMENU -->
 	<ul id="sideMenu" class="sideNavMenu">
		<li>
			<a id="newadbtn" class="jsBtn" onclick="loadPageWithSession('manageJobAd.jsp')">
				<img src="../images/icon/ad_icon.png"/>
				<h2>Manage Job Ads</h2>
			</a>
		</li>
		
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageUser.jsp')">
				<img src="../images/icon/user_icon.png"/>
				<h2>Manage Users</h2>
			</a>
		</li>
	
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageNews.jsp')">
				<img src="../images/icon/news_icon.png"/>
				<h2>Manage News</h2>
			</a>
		</li>
		
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageRSS.jsp')">
				<img src="../images/icon/rss.png"/>
				<h2>Manage RSS</h2>
			</a>
		</li>
	 <%
	 if(s.getAccountType().equals("superAdmin")){
	 %>
		<li>
			<a class="jsBtn" onclick="loadPageWithSession('manageAdmin.jsp')">
				<img src="../images/icon/admin_icon.png"/>
				<h2>Manage Admin</h2>
			</a>
		</li>
	 <%
	  }
	 %>
	 	<li>
			<a class="jsBtn" onclick="userLogoutRequest()">
				<img src="../images/icon/logout_icon.png"/>
				<h2>Log Out</h2>
			</a>
		</li>
  	</ul><!--ENDOF SideMenu-->
  	
	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="banTab">
  				<a href="#banFrame"><h2>Ban User</h2></a>
			</li>
			<li id="unbanTab">
  				<a href="#unbanFrame"><h2>Unban User</h2></a>
			</li>
			<li id="profileTab">
  				<a href="#profileFrame"><h2>Profile</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	
	
  <div id="tabFrame">		
		<div id="banFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>List of Active Users</font></b></h2>
			<span class="label">
		          Email Filter: 
		    </span>
			<input type="text" class="textinput" id="filterBan" size="30"/>
			
			 <table id="tableBanUser">
				<thead>
					<tr>
						<th>Account ID</th>
						<th>Email</th>
						<th>Secondary Email</th>
						<th>User Type</th>
						<th>Account Creation Date</th>
					</tr>
				</thead>
				<tbody>
				<%	// display all active searcher poster accounts
					for(int i = 0; i < users.size(); i++){
						Account acc = users.get(i);
						if( acc.getStatus().equals("active") ){
							int idAccount = acc.getIdAccount();
							String email = acc.getEmail();
							String secondaryEmail = acc.getSecondaryEmail();
							if( secondaryEmail == null)
								secondaryEmail = "";
							String type = acc.getType();
							long dateTimeCreated = acc.getDateTimeCreated();
							String strDateTimeCreated = Utility.longToDateString(dateTimeCreated, "PST");
							%>
								<tr title="<%= email %>">
									<td><%= idAccount %></td>
									<td><a href="#userNameInputFirstFrame"  onclick="viewProfile('<%= idAccount %>', 0)"><%= email %></a></td>
									<td><%= secondaryEmail %></td>
									<td><%= type %></td>
									<td><%= strDateTimeCreated %></td>
									<td>
										<a title="Ban" onclick="sendBanRequest('<%= email %>')" class="linkImg">
		       						 		<img src="../images/icon/ban_icon.png"/>
										</a>									
									</td>
									<td>
										<a title="Delete" onclick="sendDeleteAccountRequest('<%= email %>', 'manageUser.jsp')" class="linkImg">
		       						 		<img src="../images/icon/delete_icon.png"/>
										</a>									
									</td>
								</tr>
							<%
						}
					}
				%>
				</tbody>
			</table>
		    <br/>		
		</div><!--end of BANFRAME-->
		
		<div id="unbanFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>List of Banned Users</font></b></h2>
			<span class="label">
		          Email Filter: 
		    </span>
			<input type="text" class="textinput" id="filterUnban" size="30"/>
			
			 <table id="tableUnbanUser">
				<thead>
					<tr>
						<th>Account ID</th>
						<th>Email</th>
						<th>Secondary Email</th>
						<th>User Type</th>
						<th>Account Creation Date</th>
						<th>Profile</th>
						<th>Unban</th>
						<th>Delete</th>
					</tr>
				</thead>
				<tbody>
				<%	// display all active searcher poster accounts
					for(int i = 0; i < users.size(); i++){
						Account acc = users.get(i);
						if( acc.getStatus().equals("banned") ){
							int idAccount = acc.getIdAccount();
							String email = acc.getEmail();
							String secondaryEmail = acc.getSecondaryEmail();
							if( secondaryEmail == null)
								secondaryEmail = "";
							String type = acc.getType();
							long dateTimeCreated = acc.getDateTimeCreated();
							String strDateTimeCreated = Utility.longToDateString(dateTimeCreated, "PST");
							%>
								<tr title="<%= email %>">
									<td><%= idAccount %></td>
									<td><a href="#userNameInputSecondFrame"  onclick="viewProfile('<%= idAccount %>', 1)"><%= email %></a></td>
									<td><%= secondaryEmail %></td>
									<td><%= type %></td>
									<td><%= strDateTimeCreated %></td>
									<td>
										<a title="Unban" onclick="sendUnbanRequest('<%= email %>')" class="linkImg">
		       						 		<img src="../images/icon/unban_icon.png"/>
										</a>									
									</td>
									<td>
									<a title="Delete" onclick="sendDeleteAccountRequest('<%= email %>', 'manageUser.jsp')" class="linkImg">
	       						 		<img src="../images/icon/delete_icon.png"/>
									</a>									
									</td>
								</tr>
							<%
						}
					}
				%>
				</tbody>
			</table>
			<p id="statusTextSecondFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>		
		</div><!--end of UNBANFRAME-->
		
		<div id="profileFrame" class="subFrame">
			 <div id="profileTable" class="resultTableDiv noBorder">
			 	<h2 id="profileHeading" class="welcome">Please select a user.</h2>
			 	<span id="profileFB" class="errorTag"></span>
				<table>
					<tbody>
					</tbody>
				</table>
			 </div>		
			 <div id="fileDiv" class="resultTableDiv">
				<table>
					<thead>
						<tr>
							<th id="col-fileName" style="text-align:center">
								File Name
							</th>
							<th id="col-fileSize" style="text-align:center">
								Size (in MB)
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table><!--ENDOF TABLE-->
			</div><!--ENDOF File TABLE-->
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
	<div id="lightBox"></div>
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
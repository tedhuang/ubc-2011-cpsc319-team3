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
	<script type="text/javascript" src="../scripts/deleteAccount.js"></script>
	<script type="text/javascript" src="../scripts/manageAdmin.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	
	<title>Manage Admin Accounts</title>
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
		ArrayList<Account> admins = dbManager.getAdminAccounts();
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
			    <li><a href="../guest/viewJobAdHome.jsp" class="btn">View Job Ads</a></li>
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
				<h2>Manage Admins</h2>
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
  	</ul>
  	<!--ENDOF SideMenu-->
  	
	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="viewTab">
  				<a href="#viewFrame"><h2>View Admins</h2></a>
			</li>
			<li id="createAdminTab">
  				<a href="#createAdminFrame"><h2>Create Admin</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	
	
  <div id="tabFrame">		
		<div id="viewFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>List of Admins</font></b></h2>
			<span class="label">
		          Account Name Filter: 
		    </span>
			<input type="text" class="textinput" id="filterAdmin" size="30"/>
			
			 <table id="tableAdmin">
				<thead>
					<tr>
						<th>Account ID</th>
						<th>Account Name</th>
						<th>Account Creation Date</th>
					</tr>
				</thead>
				<tbody>
				<%	// display all active searcher poster accounts
					String accountName, type, strDateTimeCreated;
					int idAccount;
					long dateTimeCreated;
					for(int i = 0; i < admins.size(); i++){
						Account acc = admins.get(i);
						idAccount = acc.getIdAccount();
						accountName = acc.getEmail();
						type = acc.getType();
						dateTimeCreated = acc.getDateTimeCreated();
						strDateTimeCreated = Utility.longToDateString(dateTimeCreated, "PST");
						%>
							<tr title="<%= accountName %>">
								<td><%= idAccount %></td>
								<td><%= accountName %></td>
								<td><%= strDateTimeCreated %></td>
								<td>
									<a title="Delete" onclick="sendDeleteAccountRequest('<%= accountName %>', 'manageAdmin.jsp', 'statusTextFirstFrame')" class="linkImg">
	       						 		<img src="../images/icon/delete_icon.png"/>
									</a>									
								</td>
							</tr>
						<%
					}
				%>
				</tbody>
			</table>
			<p id="statusTextFirstFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>		
		</div><!--end of VIEWFRAME-->
		
		<div id="createAdminFrame" class="subFrame unremovable">
		<h2 class="welcome"><b><font size='4'>Create New Admin Account</font></b></h2>
			 <table>
				<tbody>
				  <tr>
				    <td class="clean"></td>
				  </tr>
				  <tr>
	    			<td class="label">
	          		    Account name: 
	    			</td>
				    <td style="width: 100px">
				         <input type="text" class="textinput" id="accountName" size="30" maxlength="100" tabindex="11"/>
				    </td>
				    <td>
				         <span id="userNameError" class="errorTag"></span>
				    </td>
				  </tr>
				  <tr>
				    <td class="label">
				            Password:
				    </td> 
				    <td>
				        <input id="password1" class="textinput" type="password" size="20" name="password" maxlength="15" tabindex="14"/>
				    </td>
				    <td>
				        <span id="password1Error" class="errorTag"></span>
				    </td>
				  </tr>
				  <tr>
				    <td class="label">
				            Confirm password:
				    </td> 
				    <td>
				        <input id="password2" class="textinput" type="password" size="20" name="password" maxlength="15" tabindex="14"/>
				    </td>
				    <td>
				        <span id="password2Error" class="errorTag"></span>
				    </td>
				  </tr>
				  <tr>
				     <td>
	          		    <button id="createAdminButton" type="button">Create Account</button>
	    			</td>
				  </tr>
				  <tr>
				    <td class="clean"></td>
				  </tr>
				</tbody>
			</table>
			<p id="statusTextSecondFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>		
		</div><!--end of CREATEADMINFRAME-->
		
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
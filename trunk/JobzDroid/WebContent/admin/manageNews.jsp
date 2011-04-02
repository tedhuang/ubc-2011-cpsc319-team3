<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.AccountManager, managers.NewsManager, classes.Session, classes.NewsEntry, classes.Utility, java.util.ArrayList"%>
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
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src="../scripts/manageNews.js"></script>
	<title>Manage News</title>
</head>
<body>
	<%	// check session key
	AccountManager accManager = new AccountManager();
	String sessionKey = request.getParameter("sessionKey");
	Session s = accManager.getSessionByKey(sessionKey);
	// if invalid or non-admin session, then redirect to index page.
	if (s == null){
		response.sendRedirect("../index.html");	
	}
	else if ( !s.getAccountType().equals("admin") && !s.getAccountType().equals("superAdmin")){
		response.sendRedirect("../index.html");	
	}
	else{
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
			<li id="manageNewsTab">
  				<a href="#manageNewsFrame"><h2>Manage News</h2></a>
			</li>
			<li id="postNewsTab">
  				<a href="#postNewsFrame"><h2>Post News</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	  
	  <div id="tabFrame">
		  <div id="manageNewsFrame" class="subFrame unremovable">
		  <h2 class="welcome"><b><font size='4'>News List</font></b></h2>
		  	  <table>
			  <%
			  // read news entries
			  	NewsManager newsManager = NewsManager.getInstance();
				ArrayList<NewsEntry> entries = newsManager.loadNewsEntries();
			  	for(int i = 0; i < entries.size(); i++){
			  		NewsEntry entry = entries.get(i);
			  		int idNews = entry.getIdNews();
			  		String title = entry.getTitle();
			  		String content = entry.getContent();
			  		long dateTimePublished = entry.getDateTimePublished();
			  		// display publish date in PST
			  		String formattedDate = Utility.longToDateString(dateTimePublished, "PST");
			  %>
			  		<tr style="font-weight:bold">
			  			<td >
			  				News ID: <%= idNews %>	  			
		  					<a title="Delete" onclick="sendDeleteNewsRequest('<%= idNews %>')" class="linkImg" style="float:right">
	       						 		<img src="../images/icon/delete_icon.png"/>
							</a>
			  			</td>
			  		</tr>
		  			<tr style="font-weight:bold">
		  				<td>Title: <%= title %> 
		  				</td>
		  			</tr>
		  			<tr style="font-style: italic">
		  				<td><%= formattedDate %></td>
		  			</tr>
		  			<tr>
		  				<td><%= content %></td>
		  			</tr>	  			
				  	<tr>
				  	  <td class="clean"></td>
				 	</tr>
				 	<tr>
				  	  <td class="clean"></td>
				 	</tr>
			  <%		
			  	}
			  %>
			  </table>
		  </div><!--end of VIEWNEWSFRAME-->
		  
		  <div id="postNewsFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>Post Site News</font></b></h2>
				<table>
					<tbody>
					  <tr>
					    <td class="clean"></td>
					  </tr>
					  <tr>
		    			<td style="width: 155px" class="label">
		          		    Title: 
		    			</td>
					    <td style="width: 272px">
					        <div>
					            <input type="text" class="textinput" id="newsTitle" size="134" maxlength="100" tabindex="11"/>
					      		<span id="titleError" class="errorTag"></span>
					        </div>
					    </td>
					  </tr>
					  <tr>
					    <td class="clean"></td>
					  </tr>
					<tr>
					    <td class="label">			        
					        Content:			        
					    </td> 
					    <td>
					        <textarea id="newsContent" class="textinput" rows="17" cols="131" tabindex="14"></textarea><br/>
					        <span>Note: News RSS will be updated automatically when news entries are posted here.</span><br/>
					        <span>Note: You can add HTML tags for styling.</span><br/>
					        <span id="contentInfo"></span>
					    </td>
					</tr>
				</tbody>
			</table>
				
			<p align="center"><button id="submitButton" type="button">Submit</button></p>
			<!--TODO STYLE THE BUTTON-->
		    <br/>	
		  </div> <!--end of TABPOSTNEWSFRAME-->		  
		</div> <!--ENDOF TABFRAME-->
	   </div> <!--end of tabs DIV-->	
	  </div><!-- ENDOF MAIN -->
	  
	<ul class="footer_wrapper2">
		<li>
			©2011 JobzDroid
		</li>
	</ul>	
	<%
	}
	%>	
	<div id="lightBox"></div>
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
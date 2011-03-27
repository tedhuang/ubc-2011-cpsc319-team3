<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, managers.RSSManager, managers.SystemManager, classes.Session,
     classes.Utility, java.util.List, com.sun.syndication.feed.synd.*"%>
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
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src="../scripts/manageRSS.js"></script>
	<title>Manage RSS</title>
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
		// read feed information
		SyndFeed newsFeed, jobAdFeed;
		try{
			newsFeed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + "news.xml");
			jobAdFeed = RSSManager.readFeedFromURL(SystemManager.serverBaseURL + "jobAd.xml");
		}
		catch(Exception e){
			throw new ServletException("Error reading feed information: " + e.getMessage());
		}
		List<SyndEntry> newsFeedEntries = newsFeed.getEntries();
		List<SyndEntry> jobAdFeedEntries = jobAdFeed.getEntries();
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
			<a class="jsBtn" onclick="loadPageWithSession('manageJobAd.jsp')">
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
  	</ul><!--ENDOF SideMenu-->
	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="newsRSSTab">
  				<a href="#newsRSSFrame"><h2>News RSS</h2></a>
			</li>
			<li id="jobAdRSSTab">
  				<a href="#jobAdRSSFrame"><h2>Job Ad RSS</h2></a>
			</li>
			<li id="postRSSTab">
  				<a href="#postRSSFrame"><h2>Post RSS</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	  
	  <div id="tabFrame">
		  <div id="newsRSSFrame" class="subFrame unremovable">		  
			<h2 class="welcome"><b><font size='4'> Manage News RSS</font></b></h2>
		  	  <table>
		  	  <%
		  	  	for(int i = 0; i < newsFeedEntries.size(); i++){
		  	  		SyndEntry entry = newsFeedEntries.get(i);
		  	  		String title = entry.getTitle();
		  	  		String content = entry.getDescription().getValue();
		  	  		long pubDate = entry.getPublishedDate().getTime();
		  	  		String formattedDate = Utility.longToDateString(pubDate, "PST");
		  	  %>
	  			<tr style="font-weight:bold">
	  				<td style="width: 955px">
	  					Title: <%= title %> 	  						
	  					<a title="Delete" onclick="deleteNewsRssEntry('<%= i %>')" class="linkImg" style="float:right">
       						 		<img src="../images/icon/delete_icon.png"/>
						</a>
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
		  </div> <!--end of NEWS_RSS_FRAME-->		
		  
		  <div id="jobAdRSSFrame" class="subFrame unremovable">		  
			<h2 class="welcome"><b><font size='4'> Manage Job Ads RSS</font></b></h2>
		  	  <table>
		  	  <%
		  	  	for(int i = 0; i < jobAdFeedEntries.size(); i++){
		  	  		SyndEntry entry = jobAdFeedEntries.get(i);
		  	  		String title = entry.getTitle();
		  	  		String content = entry.getDescription().getValue();
		  	  		long pubDate = entry.getPublishedDate().getTime();
		  	  		String formattedDate = Utility.longToDateString(pubDate, "PST");
		  	  		String link = entry.getLink();
		  	  		
		  	  		List<SyndCategory> categoryList = entry.getCategories();
		  	  		String categories = "";
		  	  		for(int j = 0; j < categoryList.size(); j++){
		  	  			categories.concat(categoryList.get(j).getName());		  	  			
		  	  			categories = categories.trim();
		  	  		}
		  	  		if(categories.equals(""))
		  	  			categories = "None";
		  	  %>
	  			<tr style="font-weight:bold">
	  				<td style="width: 955px">Title: <%= title %> 	  						
	  					<a title="Delete" onclick="deleteJobAdRssEntry('<%= i %>')" class="linkImg" style="float:right">
       						 		<img src="../images/icon/delete_icon.png"/>
						</a>
	  				</td>
	  			</tr>
	  			<tr style="font-style: italic">
	  				<td><%= formattedDate %></td>
	  			</tr>
	  			<tr style="font-style: italic">
	  				<td>Categories: <%= categories %></td>
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
		  </div><!--end of JOB_AD_RSS_FRAME-->
		    
		  <div id="postRSSFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'> Post New RSS Entry</font></b></h2>
				<table>
					<tbody>
					  <tr>
					    <td class="clean"></td>
					  </tr>
					  <tr>
					  	 <td class="label">
			          		 Choose a Feed: 
			   			 </td> 
					  	 <td>
					        <select id="feedType">
							  <option value="news">News RSS</option>
							  <option value="jobAd">Job Ad RSS</option>
							</select>
			  			 </td>
			    	  </tr>
					  <tr>
		    			<td class="label">
		          		    Title: 
		    			</td>
					    <td style="width: 272px">
				            <input type="text" class="textinput" id="titleInput" size="134" maxlength="100" tabindex="11"/>
				      		<span id="titleError" class="errorTag"></span>
					    </td>
					  </tr>
					  <tr>
					  	<td class="label">
		          		    Link: 
		    			</td>
					    <td>
					        <input type="text" class="textinput" id="linkInput" size="134" maxlength="100" tabindex="11"/><br/>
					        <span>For example: Entering "index.html" will make the link "<%= SystemManager.serverBaseURL %>index.html".</span>
					    </td>
					  </tr>
					  <tr>
					  	<td class="label">
		          		    Categories: 
		    			</td>
					    <td>
					        <input type="text" class="textinput" id="caterogiesInput" size="134" maxlength="100" tabindex="11"/><br/>
					        <span>Enter categories each separated by a comma. <br/>Each feed entry may belong to multiple categories for readers to filter. </span>
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
					        <textarea id="contentInput" class="textinput" rows="17" cols="131" tabindex="14"></textarea>
					        <br/>
					        <span>Note: You can add HTML tags for styling.</span><br/>
					        <span id="contentInfo"></span>
					    </td>
					  </tr>
				   </tbody>
		       </table>				
			<p align="center"><button id="submitButton" type="button">Submit</button></p>
			<!--TODO STYLE THE BUTTON-->
			<p id="statusText" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>	
		  </div><!--end of POST_RSS_FRAME-->
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
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
</body>
</html>
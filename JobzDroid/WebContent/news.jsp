<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.NewsManager, classes.NewsEntry, classes.Utility, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="css/mainStyle.css" rel="stylesheet" type="text/css" />
	<title>JobzDroid - Site News</title>
</head>
<body>
	<%	// get news from DB
	NewsManager newsManager = NewsManager.getInstance();
	ArrayList<NewsEntry> entries = newsManager.loadNewsEntries();
	%>
	<div class="main">
	  <div class="header">
		<a id="logo" title="Home" href="index.html">
	        <img src="images/logo-small.png"/>
		</a>
		<ul id="topnav" class="topnav">
		    <li><a href="#" class="btn">News</a></li>
		    <li><a href="rss.html" class="btn">RSS</a></li>
		    <li><a href="#" class="btn">View Job Ads</a></li>
		</ul>
	  </div>
	  
	  <br/>	
	  <h1><b><font size='4'>Site News</font></b></h1>
	  <table>
	  <%
	  // read news entries
	  	for(int i = 0; i < entries.size(); i++){
	  		NewsEntry entry = entries.get(i);
	  		String title = entry.getTitle();
	  		String content = entry.getContent();
	  		long dateTimePublished = entry.getDateTimePublished();
	  		// display publish date in PST
	  		String formattedDate = Utility.longToDateString(dateTimePublished, "PST");
	  %>
  			<tr style="font-weight:bold">
  				<td><%= title %></td>
  			</tr>
  			<tr>
  				<td><%= formattedDate %></td>
  			</tr>
  			<tr>
  				<td><%= content %></td>
  			</tr>	  			
		  	<tr>
		  	  <td class="clean"></td>
		 	</tr>
	  <%		
	  	}
	  %>
	  </table>
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
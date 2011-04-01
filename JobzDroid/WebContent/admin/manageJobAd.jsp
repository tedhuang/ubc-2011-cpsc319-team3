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
	<link rel="stylesheet" href="../css/jq-ui/jquery.ui.all.css"/>
	<link rel="stylesheet" href="../css/smartLightbox.css"/>
	
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/smartLightbox.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/Profile.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src='../scripts/JobAd.js'></script>
	<script type="text/javascript" src='../scripts/uiBot.js'></script>
	
	
<script type="text/javascript">
   
    $(document).ready(function(){
    	// Smart Tab 
		//ADD/RM "sliding:false" to parameter to toggle hiding effect
  		$('#lightBox').smartLightBox({});
		$('#sideMenu').sideNavMenu({});
    	$('#tabs').DynaSmartTab({});

    	getAllJobAd("allJobAdtable");
		
    	bindClearError();
	});
</script>
	
	<title>Manage Job Ad</title>
</head>
<body>
	<div id="feedback">Debug: Feedback Area</div>

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
		// get all Job Advertisements
		//ArrayList<JobAdvertisement> JobAd = dbManager.getSearcherPosterAccounts();
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
		  
		  
<!-- ==================================================================== -->		
<!--===================== SIDE MENU  =====================================-->
<!-- ==================================================================== -->	
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
  	
  	
<!-- ==================================================================== -->		
<!--===================== NAVBAR =========================================-->
<!-- ==================================================================== -->

	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="jobAdTab">
  				<a href="#home-frame"><h2>All Job Ads</h2></a>
  			</li>
			<li id="jobAdDetailsTab">
  				<a href="#jobAdDetailsFrame"><h2>Job Ad Details</h2></a>
			</li>
		</ul>
	  </div><!--ENDOF NAVBAR-->
	
	
<!-- ==================================================================== -->		
<!--===================== ALL JOB AD FRAME===============================-->
<!-- ==================================================================== -->	

  <div id="tabFrame">		
		<div id="home-frame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>List of Job Advertisements</font></b></h2>

		 <div id="headToolBar">
          	<ul id="filter">
          		<li><a class="jsBtn cAll" onclick="filterTable('', allJobAdtable)">View All<span id="numActive"></span></a></li>
	          	<li><a class="jsBtn cOpen" onclick="filterTable('open', allJobAdtable)">Open<span id="numActive"></span></a></li>
				<li><a class="jsBtn cDraft" onclick="filterTable('draft', allJobAdtable)">Draft<span id="numDraft"></span></a></li>
				<li><a class="jsBtn cPending" onclick="filterTable('pending', allJobAdtable)">Pending<span id="numPending"></span></a></li>
				<li><a class="jsBtn cInact" onclick="filterTable('inactive', allJobAdtable)">Inactive<span id="numInactive"></span></a></li>
          		
          	</ul>
          </div>
	       <div id="allJobAdtable" class="resultTableDiv">
	          <table>
				<thead>
					<tr>
						<th id="col-viewCreationDate">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Date Posted</div>
							</div>
						</th>
						<th id="col-viewStartingDate">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Job Start Date</div>
							</div>
						</th>
						
						<th id="col-viewTitle">
							<div  class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Job Title</div>
							</div>
						</th>			
						<th id="col-viewLoc">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Job Location</div>
							</div>
						</th>
						<th id="col-Tools">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Status</div>
							</div>
						</th>
						<th id="col-approvalStatus">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Approval Status</div>
							</div>
						</th>
						<th id="col-ButtonApprove">
							<div class="columnButton" >
								<div class="headText">Approve</div>
							</div>
						</th>
						<th id="col-ButtonDeny">
							<div class="columnButton">
								<div class="headText">Deny</div>
							</div>
						</th>
						<th id="col-ButtonDelete">
							<div class="columnButton" >
								<div class="headText">Delete</div>
							</div>
						</th>
						
					</tr>
				
				</thead>
					<tbody></tbody>
				</table><!--ENDOF TABLE-->
				
			</div>
			
			<button type="button" onclick="getAllJobAd('allJobAdtable')">Load all Job Ads</button>
			
		
			<p id="statusTextFirstFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>
		</div><!--end of ALL JOB AD FRAME-->
		

<!-- ==================================================================== -->		
<!--===================== JOB AD DETAILS FRAME  ==========================-->
<!-- ==================================================================== -->	

		<div id="jobAdDetailsFrame" class="subFrame">
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
	<div id="lightBox"></div>	
	<form name="sid" method="get" action="">
		<input type="hidden" id="sessionKey" name="sessionKey"/>
	</form>
	
	
	
</body>
</html>
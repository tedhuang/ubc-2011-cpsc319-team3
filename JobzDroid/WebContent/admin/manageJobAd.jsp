<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.AccountManager, classes.Session, classes.Account, classes.Utility, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="../css/mainStyle.css" rel="stylesheet" type="text/css" />
	<link href="../css/DynaSmartTab.css" rel="stylesheet" type="text/css"/>
	<link href="../css/sideNavMenu.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" href="../css/jq-ui/jquery.ui.all.css"/>
	<link rel="stylesheet" href="../css/smartLightbox.css"/>
 	<link href="../css/smartMap.css" rel="stylesheet" type="text/css"/>
	
	
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/smartLightbox.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/Profile.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/admin/adminTab.js"></script>
	<script type="text/javascript" src='../scripts/JobAd.js'></script>
	<script type="text/javascript" src='../scripts/uiBot.js'></script>
	<script type="text/javascript" src='../scripts/SmartMap.js'></script>
	
	
<script type="text/javascript">
   
    $(document).ready(function(){
    	// Smart Tab 
		//ADD/RM "sliding:false" to parameter to toggle hiding effect
  		$('#lightBox').smartLightBox({});
		$('#sideMenu').sideNavMenu({});
    	$('#tabs').DynaSmartTab({});

    	changeFilterStatus("all");
    	adminGetJobAd('allJobAdtable', 'first');
        $.fn.smartLightBox.closeLightBox(3000);
    	
    	bindClearError();
	});
    
    //Changes the filter input on the page
    function changeFilterStatus(newFilter){
    	if(newFilter == "all")
    		$("#filter").text("all");
    	
    	else if(newFilter == "pending")
    		$("#filter").text("pending");
    	
    	else if(newFilter == "inactive")
    		$("#filter").text("inactive");
    	
    	else if(newFilter == "draft")
    		$("#filter").text("draft");
    	
    	else
    		alert("Error, invalid filter status");
    }

    function submitChanges(){
   		var check = confirm("Are you sure you want to submit the changes");
		if( check == true){
			$.fn.smartLightBox.openDivlb("home-frame",'load','Making the changes..');
	    	adminBatchChangeJobAd();
	    	//adminGetJobAd('allJobAdtable', 'first');
	        $.fn.smartLightBox.closeLightBox(5000, "home-frame");
	    	
		}
    }
    
</script>
	
	<title>Manage Job Ad</title>
</head>
<body>
	<div id="feedback"></div>

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
			    <li><a href="../guest/viewJobAdHome.jsp" class="btn" target="_blank">Browse Job Ads</a></li>
			    <li><a href="../news.jsp" class="btn" target="_blank">News</a></li>
			    <li><a href="../rss.html" class="btn" target="_blank">RSS</a></li>
			    <li><a href="../faq.html" class="btn" target="_blank">FAQ</a></li>
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
			<li id="adDetailTab" class="hideOnly iniHidden">
				<span class="close">x</span>
  				<a href="#adDetailFrame"><span>View Detail</span></a>
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
		     <span><b>Filter By Status:</b></span>
          	<ul id="filterArea">
          		<li><a class="jsBtn cAll" onclick='changeFilterStatus("all"); 	  adminGetJobAd("allJobAdtable", "first")'>View All</a></li>
          		<li><a class="jsBtn cAll" onclick='changeFilterStatus("pending"); adminGetJobAd("allJobAdtable", "first")'>Pending</a></li>
          		<li><a class="jsBtn cAll" onclick='changeFilterStatus("draft");	  adminGetJobAd("allJobAdtable", "first")'>Draft</a></li>
          		<li><a class="jsBtn cAll" onclick='changeFilterStatus("inactive");adminGetJobAd("allJobAdtable", "first")'>Inactive</a></li>
          		
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
				<div id="buttonDiv" align="center">
					<button type="button" id='prevButton' onclick="adminGetJobAd('allJobAdtable', 'prev')">Previous 15</button>
			  		<button type="button" id='nextButton' onclick="adminGetJobAd('allJobAdtable', 'next')">Next 15</button>	
			  		<!-- <button type="button" id='nextButton' onclick="">Approve All</button> -->
			  		<button type="button" id='nextButton' onclick='submitChanges()'>Submit All Changes</button>	
			  		<button type="button" id='nextButton' onclick="adminGetJobAd('allJobAdtable', 'first')">Reset/Refresh</button>	
		  		</div>
		  		<br>
			</div>
				
				
			<p id="statusTextFirstFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>
		</div><!--end of ALL JOB AD FRAME-->
		

<!-- ==================================================================== -->		
<!--===================== JOB AD DETAILS FRAME  ==========================-->
<!-- ==================================================================== -->	

	<div id="adDetailFrame" class="subFrame unremovable">
		<div id="adDetailTable" class="resultTableDiv noBorder fullTab">
			<h2 id="adDetailHeading" class="welcome heading"></h2>
			<table><tbody></tbody></table>
		</div>
	</div><!--end of EDADFRAME-->
		
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
	
	<input id="browseIndex"  value="0"/>
	<input id="filter" type="hidden" value="all"/>
	
</body>
</html>
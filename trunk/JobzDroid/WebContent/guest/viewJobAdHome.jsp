<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session, classes.Account, classes.Utility, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link href="../css/mainStyle.css" rel="stylesheet" type="text/css" />
	<link href="../css/lightboxCenter.css" rel="stylesheet" type="text/css" />
	<link href="../css/DynaSmartTab.css" rel="stylesheet" type="text/css"/>
	<link href="../css/smartMap.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" href="../css/jq-ui/jquery.ui.all.css"/>
	
	<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script type="text/javascript" src="../scripts/smartLightbox.js"></script>
	<script type="text/javascript" src="../scripts/Utility.js"></script>
	<script type="text/javascript" src="../scripts/authentication.js"></script>
	<script type="text/javascript" src="../scripts/sideNavMenu.js"></script>
	<script type="text/javascript" src="../scripts/DynaSmartTab.js"></script>
	<script type="text/javascript" src='../scripts/JobAd.js'></script>
	<script type="text/javascript" src='../scripts/uiBot.js'></script>
	<script type="text/javascript" src='../scripts/guest/guestTab.js'></script>
	<script type="text/javascript" src='../scripts/SmartMap.js'></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>

<script type="text/javascript">
   
    $(document).ready(function(){
    	// Smart Tab 
		//ADD/RM "sliding:false" to parameter to toggle hiding effect
  		$('#lightBox').smartLightBox({});
		$('#sideMenu').sideNavMenu({});
    	$('#tabs').DynaSmartTab({});

    	bindClearError();
	});
</script>

<title>Guest View Job Ads</title>
</head>

<body>
	<div id="feedback">Debug: Feedback Area</div>


   <div class="main">
	  <div class="header">
		<a id="logo" title="Home" href="../index.html">
	        <img src="../images/logo-small.png"/>
		</a>
		<ul id="topnav" class="topnav">
		    <li><a href="../news.jsp" target="_blank" class="btn">News</a></li>
		    <li><a href="../rss.html" target="_blank" class="btn">RSS</a></li>
		    <li><a href="#" target="_blank" class="btn">View Job Ads</a></li>
		</ul>
	  </div><!--ENDOF DIV header-->		  
	<br/>	

<!-- ==================================================================== -->		
<!--===================== NAVBAR =========================================-->
<!-- ==================================================================== -->

	<div id="tabs" class="tabPane">
  	  <div id="navBar" class="navBar">
		<ul>
			<li id="jobAdTab">
  				<a href="#allJobAdFrame"><span>View Job Ads</span></a>
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
		<div id="allJobAdFrame" class="subFrame unremovable">
			<h2 class="welcome"><b><font size='4'>List of Current Job Advertisements</font></b></h2>

		 <div id="headToolBar">
          </div>
	       <div id="allJobAdtable" class="resultTableDiv noBorder fullTab">
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
						<th id="col-viewEdu">
							<div class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Education Requirement</div>
							</div>
						</th>
						<th id="col-ButtonViewDetail">
							<div class="columnButton" >
								<div class="headText"></div>
							</div>
						</th>						
					</tr>
				
				</thead>
					<tbody></tbody>
				</table><!--ENDOF TABLE-->			
			</div>						
			<p style='text-align:center'>
				<button type="button" id='prevButton'onclick="guestViewJobAd('allJobAdtable', 'prev')">Previous 20</button>
				<button type="button" id='nextButton' onclick="guestViewJobAd('allJobAdtable', 'next')">Next 20</button>
			</p>
			<p id="statusTextFirstFrame" class="pagefont" align="center" style="font-weight:bold" ></p>
		    <br/>
		</div><!--end of ALL JOB AD FRAME-->

<!-- ==================================================================== -->		
<!--===================== JOB AD DETAILS FRAME ==========================-->
<!-- ==================================================================== -->	

		<div id="adDetailFrame" class="subFrame unremovable">
			<div id="adDetailTable" class="resultTableDiv noBorder fullTab">
				<h2 id="adDetailHeading" class="welcome heading"></h2>
				<table><tbody></tbody></table>
		</div>
	</div><!--end of TADFRAME-->

	</div><!--ENDOF TABFRAME-->
</div><!--ENDOF TABS-->
</div><!--ENDOF MAIN-->
	

	<ul class="footer_wrapper"><li>©2011 JobzDroid</li></ul>
	<div id="lightBox"></div>
	<form name="sid" method="get" action=""><input type="hidden" id="sessionKey" name="sessionKey"/></form>

<input id="browseIndex" type="hidden" value="0"/>

</body>
</html>
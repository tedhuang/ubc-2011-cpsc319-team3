<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.sql.*, managers.DBManager, classes.Session"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<title>Admin Job Advertisement Management</title>

<script type="text/javascript" src='http://code.jquery.com/jquery-latest.min.js'></script>
<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAd.js'></script>


</head>

<script language="JavaScript">

//window.onload=initializePage();

  function initializePage(){
	  $("#jobAdDetails").hide();
	  //document.getElementById("jobTitle").disabled=true;
  }
  
</script>

<body onload="initializePage()">

	<%	
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
	else{ //TODO ADD ADMIN HOME UI
	%>	
		Select a Job Ad: <br/>
		Job Ad ID: <input id="jobAdId" type="text" name="jobAdId" size="15"/><br/>
		<button id="getJobAdButton" type="button" onclick="getJobAdById()">Find Job Ad</button> <br/>
		<br/>
		
		Delete Job Ad: <br/>
		<button id="adminDeleteButton" type="button" onclick="adminDeleteJobAd()">Delete (Permanently)</button> <br/>
		<br/>
		
		Approve/Deny Job Ad: <br/>
		<button id="adminApproveButton" type="button" onclick="adminApprove()">Approve</button> <br/>
		<button id="adminDenyButton" type="button" onclick="adminDeny()">Deny</button>
		
		<h4>Feedback:</h4>
		<div id=feedback>Feedback Area</div>
		
		
		
		<br/>
		<br/>
		<br/>
		<h2>Job Ad Details:</h2>
		
		<div id="jobAdDetails">
			
			<h4>JobAd ID:</h4> <span id="jobAdId">Unknown</span> <br/>
			
			<h4>Current Status:</h4> <span id="status">Unknown</span><br/>
			
			<h4>Approval Status:</h4> <span id="isApproved">Unknown</span> <br/>
			
			<h4>Title:</h4> <span id="jobTitle"></span><br/>
			
			<h4>Description:</h4><br/>
			<span id="jobDescription" ></span>
			<br/><br/>
			
			<h4>Education Requirement:</h4>
			<select id="educationReq" name="educationReq" disabled="disabled">
					<option value="0">None</option>
					<option value="1">B.Sc.</option>
					<option value="2">M.Sc.</option>
					<option value="3">Ph.D.</option>
				</select>		
			
			<br/>
			
			<h4>Job Location(s) (separated by comma):</h4> <br/>
			~Need to integrate with Google Map~ <br/>
			<span id="address"></span>
			<div id="mapCanvas"></div> 
			
			<br/>
			
			<h4>Contact Info:</h4>
			<input id="contactInfo" type="text" name="contactInfo" size="20"/><br/>
			<br/>
			
			<h4>Tags:</h4>
			 <input id="tags" type="text" name="tags" size="20"/><br/>
			<br/>
			
			<h4>Starting Date:  </h4>
			<span id="startingDate"></span>
			<br/>
			
			<h4>Expiring on:</h4>
			<span id="expiryDate"></span>
			<br/>
			
			<h4>Created on:</h4>
			<span id="creationDate"></span>
			<br/>
			
			<h4>Number of views:</h4>
			<span id="numViews"></span>
			<br/>
		
		</div>
		
		
			<form name="sid" method="get" action="">
				<input id="sessionKey" name="sessionKey"/>
			</form>
	<%
	}
	%>	








</body>
</html>
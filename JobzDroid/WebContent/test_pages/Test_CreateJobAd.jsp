<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Job Ad Creation Test</title>

<script type="text/javascript" src='http://code.jquery.com/jquery-latest.min.js'></script>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"> </script> 
<script type="text/javascript" src='../scripts/GoogleMaps.js'></script>
<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAd.js'></script>

<script type="text/javascript" src='../scripts/testAuthentication.js'></script>
<script type="text/javascript" src='https://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js'></script>

<link href="../css/GoogleMapsTest.css" rel="stylesheet" type="text/css" />


<script type="text/javascript">


$("document").ready(function() {
	loadSessionKeyFromURL();
});
</script>

</head>

<body>

<br>

<h2>Create New Job Advertisement</h2>
Title: <input id="jobTitle" type="text" name="jobTitle" size="20"><br>

Description:  <br>
<textarea id="jobDescription" rows="4" cols="20">
</textarea> <br>

Education Requirement: 
<select id="educationRequirement" name="educationRequirement">
			<option value="0">None</option>
			<option value="1">B.Sc.</option>
			<option value="2">M.Sc.</option>
			<option value="3">Ph.D.</option>
		</select>			
		
<br>
Job Location(s) (separated by comma): <br>
~TODO: Change "Save button" to radio selection~
<input id="address" type="text" name="address" size="20"><br>
<button type="button" onclick="calculateLocation()">Find Location</button><br>

<!-- Location Feedback: -->
<span id=latitude></span> <br>
<span id=longitude></span><br>

<span id=resultTableTitle></span>
<table id="lookUpTable"></table><br/>
===============================================================<br>


<br>


Job Availability <br>
<select id="jobAvailability" name="jobAvailability">
			<option value="none">not specified</option>
			<option value="fullTime">Full Time</option>
			<option value="partTime">Part Time</option>
			<option value="internship">Internship</option>
		</select>
<br />


Contact Info: <input id="contactInfo" type="text" name="contactInfo" size="20"><br>
<br>

Tags: <input id="tags" type="text" name="tags" size="20"><br>


Starting Date:  
	<form action="" name="formStartingDate">
	<select id="startingDay">
	</select> 
	<select id="startingMonth">
	</select> 
	<select id="startingYear">
	</select> 
	</form>
	<script type="text/javascript">
		//populatedropdown(id_of_day_select, id_of_month_select, id_of_year_select)
		$("document").ready(function(){
			populatedropdown("startingDay", "startingMonth", "startingYear");
		});
	</script> 
	<br>

Expire In... : 
	<form action="" name="formExpiryDate">
	<select id="expiryDay">
	</select> 
	<select id="expiryMonth">
	</select> 
	<select id="expiryYear">
	</select> 
	</form>
	<script type="text/javascript">
		//populatedropdown(id_of_day_select, id_of_month_select, id_of_year_select)
		populatedropdown("expiryDay", "expiryMonth", "expiryYear");
	</script>
	<br>


<div id="feedback"><h3>Feedback Area</h3></div>
<button id="newJobAdButton" type="button" onclick="createJobAdvertisement()">Create Job Advertisement</button>


	<form name="sid" method="get" action="">
		<input id="sessionKey" name="sessionKey"/>
	</form>
	
</body>
</html>



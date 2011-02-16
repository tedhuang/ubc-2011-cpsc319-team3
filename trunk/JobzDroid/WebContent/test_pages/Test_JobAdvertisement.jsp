<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test: This is where title goes?</title>

<script type="text/javascript" src='../scripts/JobAdvertisementCreation.js'></script>
<script type="text/javascript" src="../scripts/basiccalendar.js">

/***********************************************
* Basic Calendar-By Brian Gosselin at http://scriptasylum.com/bgaudiodr/
* Script featured on Dynamic Drive (http://www.dynamicdrive.com)
* This notice must stay intact for use
* Visit http://www.dynamicdrive.com/ for full source code
***********************************************/
</script> 


<!-- TODO: move to css section when we use it -->
<!-- Calendar Code -->
<style type="text/css">

.main {
width:200px;
border:1px solid black;
}

.month {
background-color:black;
font:bold 12px verdana;
color:white;
}

.daysofweek {
background-color:gray;
font:bold 12px verdana;
color:white;
}

.days {
font-size: 12px;
font-family:verdana;
color:black;
background-color: lightyellow;
padding: 2px;
}

.days #today{
font-weight: bold;
color: red;
}
</style>

</head>



<script type="text/javascript">


function ParseXMLResponse(responseXML)
{
	 var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 
	 var xml_response_text = "<h2>AJAX XML response from server: ";
	 responseText += result + " " + message + "</h2>";

	 return xml_response_text;
}


</script>



<body>



<!-- Fields of Input -->
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
Job Location(s) (separated by comma): <input id="jobLocation" type="text" name="jobLocation" size="20"><br>
<br>

Contact Info: <input id="contactInfo" type="text" name="contactInfo" size="20"><br>
<br>

Starting Date: (TODO: add a calendar implementation) <br>


<!-- TODO: include for calendar function
<script type="text/javascript">

var todaydate = new Date();
var curmonth = todaydate.getMonth()+1; //get current month (1-12)
var curyear = todaydate.getFullYear(); //get current year

document.write(buildCal(curmonth ,curyear, "main", "month", "daysofweek", "days", 1));
</script>
 -->


Tags: <input id="tags" type="text" name="tags" size="20"><br>

<!-- The value returned by the selection corresponds to the number of days till expiry -->
Expire In... :
			Weeks:<select id="expiryWeek" name="expiryWeek">
			<option value="0"></option>
			<option value="1">One</option>
			<option value="2">Two</option>
			<option value="3">Three</option>
			<option value="4">Four</option>
			<option value="5">Five</option>
			<option value="6">Six</option>
			<option value="7">Seven</option>
			<option value="8">Eight</option>
												
			</select>
			Days:<select id="expiryDay" name="expiryDay">
			<option value="0"></option>
			<option value="1">One</option>
			<option value="2">Two</option>
			<option value="3">Three</option>
			<option value="4">Four</option>
			<option value="5">Five</option>
			<option value="6">Six</option>
			</select>
 <br>


<div id="feedback"><h3>Feedback Area</h3></div>
<button id="newJobAdButton" type="button" onclick="createJobAdvertisement()">Create Job Advertisement</button>

</body>
</html>



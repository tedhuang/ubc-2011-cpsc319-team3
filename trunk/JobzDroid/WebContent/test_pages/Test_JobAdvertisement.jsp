<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Testing Title</title>
	
	<script type="text/javascript" src='../scripts/Utility.js'></script>
	<script type="text/javascript" src='../scripts/JobAdvertisementCreation.js'></script>

	<script type="text/javascript">
	
	/***********************************************
	* Drop Down Date select script- by JavaScriptKit.com
	* This notice MUST stay intact for use
	* Visit JavaScript Kit at http://www.javascriptkit.com/ for this script and more
	***********************************************/
	
	var monthtext=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec'];
	
	function populatedropdown(dayfield, monthfield, yearfield){
		var today=new Date()
		var dayfield=document.getElementById(dayfield)
		var monthfield=document.getElementById(monthfield)
		var yearfield=document.getElementById(yearfield)
		for (var i=0; i<31; i++)
		dayfield.options[i]=new Option(i, i+1)
		dayfield.options[today.getDate()]=new Option(today.getDate(), today.getDate(), true, true) //select today's day
		for (var m=0; m<12; m++)
		monthfield.options[m]=new Option(monthtext[m], monthtext[m])
		monthfield.options[today.getMonth()]=new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true) //select today's month
		var thisyear=today.getFullYear()
		for (var y=0; y<20; y++){
		yearfield.options[y]=new Option(thisyear, thisyear)
		thisyear+=1
		}
		yearfield.options[0]=new Option(today.getFullYear(), today.getFullYear(), true, true) //select today's year
	}
	
	</script>

 
 
</head>


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
		window.onload=function(){
		populatedropdown("startingDay", "startingMonth", "startingYear");
		};
	</script> 
	<br>


Tags: <input id="tags" type="text" name="tags" size="20"><br>


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
		window.onload=function(){
		populatedropdown("expireDay", "expireMonth", "expireYear");
		};
	</script>
	<br>


<div id="feedback"><h3>Feedback Area</h3></div>
<button id="newJobAdButton" type="button" onclick="createJobAdvertisement()">Create Job Advertisement</button>

</body>
</html>



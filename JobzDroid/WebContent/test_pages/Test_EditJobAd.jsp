<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit Job Ad</title>
<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAd.js'></script>

<script language="JavaScript">

//window.onload=initializePage();

  function showJobAdDetails(){
     document.getElementById("jobAdDetails").style.display = "block";
  }
  
  function initializePage(){
	  alert("test");
	  document.getElementById("jobAdDetails").style.display="none";
	  //document.getElementById("jobTitle").disabled=true;
  }
  
</script>


</head>


<body onload="intializePage()">

<br>
AccountID:
<input id="jobAdId" type="text"  size="20"><br>
<button id="getJobAdButton" type="button" onclick="getJobAdById()">Submit</button>



<div id="jobAdDetails">
	Title: <input id="jobTitle" type="text" name="jobTitle" size="20" ><br>
	
	Current Status:
	<div id=status>Unknown</div> <br>
	
	Approval Status:
	<div id=isApproved>Unknown</div> <br>
	
	Description:  <br>
	<textarea id="jobDescription" type="text" rows="4" cols="20" >
	</textarea> <br>
	
	Education Requirement: 
	<div id="educationReq">Unknown</div>
	
	<br>
	Job Location(s) (separated by comma): <br>
	~Need to integrate with Google Map~ <br>
	<!-- Need to integrate with Google Map
	<input id="jobLocation" type="text" name="jobLocation" size="20"><br> -->
	<br>
	
	Contact Info: <input id="contactInfo" type="text" name="contactInfo" size="20"><br>
	<br>
	
	Tags: <input id="tags" type="text" name="tags" size="20"><br>
	<br>
	
	Starting Date:  <br>
	~TODO: finish this~<!-- 
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
			window.onload=function(){
			populatedropdown("startingDay", "startingMonth", "startingYear");
			};
		</script> 
		<br>
	 -->
	 
	Expire In... :  <br>
	~TODO: finish this~ <!-- 
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
	 -->
	 	
	Created On:<br>
	~TODO: complete this~<br>
	<br>
	Number of Views:<br>
	<div id=numViews>Unknown</div>
		
		
</div>

<br>
<button id="editJobAdButton" type="button" onclick="editJobAd()">Submit</button>
<div id="feedback"><h3>Feedback Area</h3></div>

</body>

</html>






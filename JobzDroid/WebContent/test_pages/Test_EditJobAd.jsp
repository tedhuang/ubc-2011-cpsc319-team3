<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Edit Job Ad</title>

<script src='https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js' type='text/javascript'></script>
<link href="../css/mainStyle.css" rel="stylesheet" type="text/css">
<link href="../css/resultTable.css" rel="stylesheet" type="text/css">
<link href="../css/GoogleMapsTest.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAd.js'></script>

<script language="JavaScript">

//window.onload=initializePage();

  function showJobAdDetails(){
     document.getElementById("jobAdDetails").style.display = "block";
  }

  function enableEdit(){
	  
  }
  
  function initializePage(){
	  $("#jobAdDetails").hide();
	  //document.getElementById("jobTitle").disabled=true;
  }
  
</script>


</head>
<body onload="initializePage()">

<div id="feedback"><h3>Feedback Area</h3></div>
<br>
JobAdID:
<input id="jobAdId" type="text"  size="20"><br>
<button id="getJobAdButton" type="button" onclick="getJobAdById()">Submit</button>
<button id="ListJobAdButton" type="button" onclick="jobAdReqDispatcher(loadAdList, resultTable)">Load List</button>


<div id="resultTable" class="resultTableDiv">
	<table>
		<thead>
			<tr>
				<th id="col-viewDate">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Date</div>
					</div>
				</th>
				<th id="col-viewTitle">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Job Title</div>
					</div>
				</th>
				<th id="col-viewCompany">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Company</div>
					</div>
				</th>
				<th id="col-viewLoc">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Location</div>
					</div>
				</th>
				<th id="col-viewAvail">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Availability</div>
					</div>
				</th>
				<th id="col-viewMap">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Map</div>
					</div>
				</th>
				<th id="col-Tools">
					<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
						<div class="headText">Tools</div>
					</div>
				</th>
			</tr>
		</thead>
		<tbody>
		
		</tbody>
	</table><!--ENDOF TABLE-->
  </div>

<div id="jobAdDetails">

	<button id="editJobAdButton" type="button" onclick="editJobAd()">Submit Edit</button>
	<br><br>
	
	JobAd ID: <span id="jobAdId">Unknown</span> <br/>
	
	Current Status: <span id="status">Unknown</span><br/>
	
	Approval Status: <span id="isApproved">Unknown</span> <br/>
	
	Title: <input id="jobTitle" type="text" name="jobTitle" size="20" ><br>
	
	Description:  <br>
	<textarea id="jobDescription" type="text" rows="4" cols="20" >
	</textarea> <br/><br/>
	
	Education Requirement: 
	<select id="educationReq" name="educationReq">
			<option value="0">None</option>
			<option value="1">B.Sc.</option>
			<option value="2">M.Sc.</option>
			<option value="3">Ph.D.</option>
		</select>		
	
	<br/>
	<br/>
	
	Job Location(s) (separated by comma): <br>
	~Need to integrate with Google Map~ <br>
	<span id="address"></span>
	<div id="mapCanvas"></div> 
	
	<br>
	
	Contact Info: <input id="contactInfo" type="text" name="contactInfo" size="20"><br>
	<br>
	
	Tags: <input id="tags" type="text" name="tags" size="20"><br>
	<br>
	
	Starting Date:  
	<span id="startingDate"></span>
	<br/>
	
	Expiring on:  
	<span id="expiryDate"></span>
	<br/>
	
	Created on:
	<span id="creationDate"></span>
	<br/>
	
	Number of views:
	<span id="numViews"></span>
	<br/>
	
	<form name="sid" method="get" action="">
		<input id="sessionKey" name="sessionKey"/>
	</form>


	
</div>

</body>
</html>






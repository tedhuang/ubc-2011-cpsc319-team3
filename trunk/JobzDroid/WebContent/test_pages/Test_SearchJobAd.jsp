<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Job Ad Test Page</title>
<script src='https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js' type='text/javascript'></script>
<link href="../css/mainStyle.css" rel="stylesheet" type="text/css">
<link href="../css/resultTable.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAd.js'></script>



</head>

<body>
<form id="searchBox">
		<h2>Job Advertisement Search</h2> <br>
		
		<h3>Search input: </h3>
		<input id="searchKeyword" type="text" name="searchKeyword" size="40"><br>
		<h3>Search title: </h3>
		<input id="titleSearch" type="text" name="searchTitle" size="40"><br>
		
		<h3>Criteria:</h3>
		
		Education Requirement: 
		<select id="eduReqsearch" name="searchEduReq">
					<option value="">Choose a degree</option>
					<option value="1">B.Sc.</option>
					<option value="2">M.Sc.</option>
					<option value="3">Ph.D.</option>
		</select>
		
		Job Location(s) (separated by comma): ~ to be added ~
		<input id="locSearch" type="text" name="searchJobLoc" size="20"><br>
		
		Employment Type: ~ to be added ~
		<div id="empType"></div>
		<!-- 
		Tags: <input id="searchTags" type="text" name="tags" size="20"><br>
		 -->
		<button id="newJobAdButton" type="button" onclick="searchJobAdvertisement(resultTable)">Search</button>
		<br>
</form>

<div id="feedback"><h3>Feedback Area</h3></div>
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

</body>
</html>














<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Job Ad Test Page</title>

<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAdvertisementSearch.js'></script>



</head>

<body>

<h2>Job Advertisement Search</h2> <br>

<h3>Search input: </h3>
<input id="searchText" type="text" name="searchText" size="40"><br>
<br>


<h3>Criteria:</h3>

Education Requirement: 
<select id="searchEducationReq" name="searchEducationReq">
			<option value="0">None</option>
			<option value="1">B.Sc.</option>
			<option value="2">M.Sc.</option>
			<option value="3">Ph.D.</option>
		</select>

Job Location(s) (separated by comma): ~ to be added ~
<input id="searchJobLoc" type="text" name="searchJobLoc" size="20"><br>

Employment Type: ~ to be added ~
<div id="empType"></div>


<!-- 
Tags: <input id="searchTags" type="text" name="tags" size="20"><br>
 -->

<button id="newJobAdButton" type="button" onclick="searchJobAdvertisement()">Search</button>
<br>

<div id="feedback"><h3>Feedback Area</h3></div>



</body>
</html>














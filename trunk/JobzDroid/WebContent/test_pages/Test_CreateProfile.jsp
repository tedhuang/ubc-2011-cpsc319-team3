<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Profile Test Page</title>
<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/Profile.js'></script>


<script language="JavaScript">

  function showPosterForm(){
     document.getElementById("formPoster").style.display = "block";
     document.getElementById("formSearcher").style.display= "none";
  }

  function showSearcherform(){
      document.getElementById("formSearcher").style.display = "block";
      document.getElementById("formPoster").style.display = "none";
  }
  
</script>



</head>



<body onload="document.getElementById('formPoster').style.display='none'; document.getElementById('formSearcher').style.display='none'">

<h3>Select Account Type:</h3>
<form>
<input type="radio" id="accountType" name="accountType" value="1" onclick="showPosterForm()"/> Job Poster<br />
<input type="radio" id="accountType" name="accountType" value="2" onclick="showSearcherform()"/> Job Searcher
</form>
<br>

AccountID: (need to make this be handled in the back)
<input id="accountID" type="text" size="20"><br>

<div id="formSearcher">
<form>
	Name: 
	<input id="searcherName" type="text" size="20"><br>
	
	Secondary E-mail:
	<input id="searcherSecEmail" type="text" size="20"><br>
	
	Contact Info:
	<input id="searcherContactInfo" type="text"  size="20"><br>
	
	Self Description:<br>
	<textarea id="searcherDescripton" rows="4" cols="20"></textarea> 
	<br>
	
	Location: <br>
	~To be integrated with Google Map~
	
	Level of Education:<br>
	<select id="educationLevel" name="educationLevel">
			<option value="0">None</option>
			<option value="1">B.Sc.</option>
			<option value="2">M.Sc.</option>
			<option value="3">Ph.D.</option>
		</select>
			
	<br>
	
	Document:<br>
	~Pending Implementation~<br>
	
	Employment Preference:
	<select id="empPref" name="empPref">
				<option value="none">None</option>
				<option value="full">Full-Time</option>
				<option value="part">Part-Time</option>
				<option value="intern">Internship</option>
			</select>
</form>
</div>

<br>

<div id="formPoster" >
<form>
	Name: 
	<input id="posterName" type="text" size="20"><br>
	
	Secondary E-mail:
	<input id="posterSecEmail" type="text" size="20"><br>
	
	Contact Info:
	<input id="posterContactInfo" type="text"  size="20"><br>
	
	Self Description:<br>
	<textarea id="posterDescripton" rows="4" cols="20"></textarea> 
	<br>
	
	Affliation
	<input id="affiliation" type="text"  size="20"><br>
</form>
</div>

<br>
<br>
<button id="submitProfile" type="button" onclick="createProfile()">Submit</button>
<div id="feedback"><h3>Feedback Area</h3></div>


</body>
</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<script type="text/javascript">
function loadPage()
{
	var w = document.pageTogo.mydropdown.selectedIndex;
	var page = document.pageTogo.mydropdown.options[w].value;
	var path="http://localhost:8080/JobzDroid/test_pages/";
	document.getElementById("testFrames").src = path+page;
}
</script>
<body>
Test Page To GO: <div id="ToolBar" style="border: 3px coral solid; width: 100%">

<form name="pageTogo">
<select name="mydropdown">
<option value="UserRegistration.html">Registration</option>
<option value="ForgetPassword.html">ForgetPassword</option>
<option value="ResetForgetPassword.jsp">Password Reset</option>
<option value="accessTest.jsp">LogIn/Out</option>
</select>
</form>

<button onclick="loadPage()">GO!</button>

</div>
<div id="frameContainer">
<iframe id="testFrames" width="100%" height="300" scrolling="yes">
</iframe>
</div>
</body>
</html>
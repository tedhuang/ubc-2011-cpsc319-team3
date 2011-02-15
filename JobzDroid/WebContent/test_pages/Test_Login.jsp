<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
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

<!-- 
User Name: <input id="userName" type="text" name="userName" size="20"><br>
Password: <input id="password" type="text" name="password" size="20"><br>
        	  
<div id="feedback"><h2>Feedback Area</h2></div>


<button type="button" onclick="createLoginRequest()">Login</button>

 -->


</body>
</html>
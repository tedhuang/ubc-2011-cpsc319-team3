<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<script type="text/javascript">

function ParseXMLResponse(responseXML)
{
	 //var result = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 //var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 
	 //var responseText = "<h2>AJAX XML response from server: ";
	 //responseText += result + " " + message + "</h2>";

	 //return responseText;
}

function create_test_request()
{
	
	var text_field = document.getElementById("text_field").value;
	
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  
	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		    //parse XML response from server
		    //var responseText= ParseXMLResponse(xmlhttp.responseXML);
		   
	    }
	  }

	var Params = "httpsURL=" + text_field;

	//send the parameters to the servlet with POST
	xmlhttp.open("GET","../Test_HTTPS_Servlet" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params);

	
}
</script>

</head>
<body>


text_field: <input id="text_field" type="text" name="text_field" size="20"><br>
        	  
<div id="myDiv"><h2>Feedback Area</h2></div>


<button type="button" onclick="create_test_request()">test</button>
    

</body>
</html>
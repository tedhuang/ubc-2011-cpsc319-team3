<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="../scripts/accountAction.js" type="text/javascript"></script>
<title>Access Test</title>
</head>
<body>

<H2>Login</H2>
<br />
User:
<input id="username" name="username" class="form-login" title="Username" value="" size="30" maxlength="2048" />
Password:
<input id="password" name="password" type="password" class="form-login" title="Password" value="" size="30" maxlength="2048" />
<br />
<span class="login-box-options"><input type="checkbox" name="1" value="1"> Remember Me 
 <a href="#" style="margin-left:30px;">Forgot password?</a></span>
<br />
<br />
<button	onclick="userLoginRequest()">Login</button>
<button	onclick="userLogoutRequest()">Logout</button>

<div id="myDiv"><h2>Debug Message for page</h2></div>
</body>
</html>
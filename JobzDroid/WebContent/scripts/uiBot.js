
function initUI(){
	if($("sessionKey").val()!=null){
		request = new request;
		request.addAction("initUI");
		request.addParam("sessKey", $("sessionKey").val());
		
		if (window.XMLHttpRequest)
		  {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		  }
		else
		  {// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		  }
		
		//send the parameters to the servlet with POST
		$("#feedback").html("<h2>Sending Request</h2>");
		xmlhttp.open("POST","./ServletAccount" ,true);
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlhttp.send(request.toString());
		
		
	}
	
	else{//TODO REDIRECT TO ERROR PAGE
		
	}
} 
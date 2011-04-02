
function loadTagList(outputDiv){
	//TODO Testing ONLY, RM after testing
	$("#ListtagButton").attr("disabled", true);
	
	request = new Request;
	request.addAction("loadAdList");
	request.addSessionID("1234"); //TODO: change this
	request.addParam("searchtagId", $("#tagId").val());
	

	//change the text while sending the request
	$("#feedback").html("<h2>Sending gettagById Request</h2>");
	
	if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	}
	else{// code for IE6-
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
 
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	//When request is finished and results passed back
	xmlhttp.onreadystatechange=function(){
		if (xmlhttp.readyState==4 && xmlhttp.status==200){
			$("#feedback").html("<h2>Successfully finished tasks</h2>");
			//parse XML response from server
			buildAdListTb("tag", outputDiv);
	    	$("#ListtagButton").attr("disabled", false);

	    }
	  };
	  
}

/************************************************************************************************************
 * 				saveFavouritetag
 * @param outputDiv
 ************************************************************************************************************/	

function saveTagsToSystem(sessKey, jobId){
	
	request = new Request;
	request.addAction("saveFavouritetag");
	request.addParam("sessionKey", sessKey);
	request.addParam("tagId", jobId);
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletJobAd" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());

	
}
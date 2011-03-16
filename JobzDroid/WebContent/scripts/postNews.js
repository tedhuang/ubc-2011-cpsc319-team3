/**
 * Javascript for post news (admin) page.
 */

$("document").ready(function() {
	// client side error checking
	$("#newsTitle").bind("change", validateInput);
	// allow content to have maximum 2048 characters
	$("#newsContent").bind("keyup", function(){
		limitChars('newsContent', 2048, 'contentInfo');
	});
	// send request to admin servlet on submit
	$("#submitButton").bind("click", postNews);
});

// client side error checking
function validateInput(evt){
	// case: News title changed
	if( $(this).attr('id') == "newsTitle" ){
		var title = $(this).val();
		if(trim(title) == "")
			$("#titleError").text("Title must not be empty.");
		else
			$("#titleError").text("");
	}
}

// send post news request
function postNews(evt){
	$("#submitButton").attr("disabled", true);
	$("#statusText").removeClass();	
	var strNewsTitle = trim($("#newsTitle").val());
	var strNewsContent = $("#newsContent").val();
	var strSessionKey = $("#sessionKey").val();
	
	var xmlHttpReq;
	if (window.XMLHttpRequest){
		// Firefox, Chrome, Opera, Safari
		xmlHttpReq = new XMLHttpRequest();
	}
	else{
		// IE
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					//parse XML response from server
					var responseText = parsePostNewsResponse(xmlHttpReq.responseXML);
					$("#submitButton").removeAttr("disabled");
			    	$("#statusText").text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("postNews");
	request.addSessionKey(strSessionKey);
	request.addParam("title", strNewsTitle);
	request.addParam("content", strNewsContent);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
	
	//update status text
	$("#statusText").text("Processing...This may take a moment.");
}

// parses response from server
function parsePostNewsResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if registration sucessful, then update button text and function
	 if(boolResult == "true"){
		 $("#statusText").addClass("successTag");
		 $("#submitButton").removeAttr("disabled");
		 $("#newsTitle").val("");
		 $("#newsContent").val("");
	 }
	 else
		 $("#statusText").addClass("errorTag");
	 return strMsg;
}
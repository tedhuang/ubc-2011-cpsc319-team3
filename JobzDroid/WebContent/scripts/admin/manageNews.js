/**
 * Javascript for post news (admin) page.
 */

$("document").ready(function() {
	// client side error checking
	$("#newsTitle").bind("change", validateInput);
	// allow content to have maximum 2048 characters
	$("#newsContent").bind("keyup", function(){
		limitChars('newsContent', 10000, 'contentInfo');
	});
	// send request to admin servlet on submit
	$("#submitButton").bind("click", postNews);
	$('#tabs').DynaSmartTab({});
	$('#sideMenu').sideNavMenu({});
	$('#lightBox').smartLightBox({});
});

// client side error checking, can add additional checks on other fields if needed
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
	var strNewsTitle = trim($("#newsTitle").val());
	var strNewsContent = $("#newsContent").val();
	var strSessionKey = $("#sessionKey").val();
	$.fn.smartLightBox.openlb('small','Posting News Entry...','load');
	
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
					$("#submitButton").removeAttr("disabled");
					//parse XML response from server
					var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;	
					 $("#lbMsg","#lightBox").html(responseText);
					 if(boolResult == "true"){
						 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
						 $.fn.smartLightBox.closeLightBox(2000);
						 loadPageWithSession('manageNews.jsp');
					 }
					 else{
						 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
						 $.fn.smartLightBox.closeLightBox(2000);
					 }
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
}

function sendDeleteNewsRequest(idNews){
	var strSessionKey = $("#sessionKey").val();
    var b = confirm("Are you sure to delete News ID: " + idNews + "?\nIts associated RSS entry will also be removed.");
    if (b == false)
        return false;
    
    $.fn.smartLightBox.openlb('small','Deleting News Entry...','load');
	
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
					 var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					 var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;	
					 $("#lbMsg","#lightBox").html(responseText);
					 if(boolResult == "true"){
						 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
						 $.fn.smartLightBox.closeLightBox(2000);
						 loadPageWithSession('manageNews.jsp');
					 }
					 else{
						 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
					 }
				}
			}};
	}
	request = new Request;
	request.addAction("deleteNews");
	request.addSessionKey(strSessionKey);
	request.addParam("idNews", idNews);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
}
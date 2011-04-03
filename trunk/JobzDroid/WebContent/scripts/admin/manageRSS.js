/**
 * Javascript file for managing RSS (admin) 
 */

$("document").ready(function() {
	// client side error checking
	$("input").bind("change", validateInput);
	// allow content to have maximum 2048 characters
	$("#contentInput").bind("keyup", function(){
		limitChars('contentInput', 10000, 'contentInfo');
	});
	// send request to admin servlet on submit
	$("#submitButton").bind("click", postRSS);
	$('#tabs').DynaSmartTab({});
	$('#sideMenu').sideNavMenu({});
	$('#lightBox').smartLightBox({});
});

// client side error checking, can add additional checks on other fields if needed
function validateInput(evt){
	// case: rss title input changed
	if( $(this).attr('id') == "titleInput" ){
		var title = $(this).val();
		if(trim(title) == "")
			$("#titleError").text("Title must not be empty.");
		else
			$("#titleError").text("");
	}
}

// send post news request
function postRSS(evt){
	$("#submitButton").attr("disabled", true);
	var strRSSTitle = trim($("#titleInput").val());
	var strLink = trim($("#linkInput").val());
	var strRSSContent = $("#contentInput").val();
	var strSessionKey = $("#sessionKey").val();
	var strFeedType = $("#feedType").val(); 
	var strCategories = trim($("#caterogiesInput").val());
	$.fn.smartLightBox.openlb('small','Posting RSS Entry...','load');
	
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
					 if(boolResult == "true"){
						 $("#lbMsg","#lightBox").html(responseText + "It may take a while for the entry to be updated in the browser.");
						 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
						 $.fn.smartLightBox.closeLightBox(2500);
						 setTimeout("loadPageWithSession('manageRSS.jsp')",2500);
					 }
					 else{
						 $("#lbMsg","#lightBox").html(responseText);
						 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
						 $.fn.smartLightBox.closeLightBox(2000);
					 }
				}
			}};
	}
	request = new Request;
	request.addAction("postRSSEntry");
	request.addSessionKey(strSessionKey);
	request.addParam("title", strRSSTitle);
	request.addParam("link", strLink);
	request.addParam("content", strRSSContent);
	request.addParam("type", strFeedType);
	request.addParam("categories", strCategories);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());	
}

function deleteNewsRssEntry(index){
	sendDeleteRSSRequest("news", index);
}

function deleteJobAdRssEntry(index){
	sendDeleteRSSRequest("jobAd", index);
}

function sendDeleteRSSRequest(type, index){
	var strSessionKey = $("#sessionKey").val();
    var b = confirm("Are you sure to delete the selected feed entry?");
    if (b == false)
        return false;
    $.fn.smartLightBox.openlb('small','Deleting RSS Entry...','load');
	
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
					 if(boolResult == "true"){
						 $("#lbMsg","#lightBox").html(responseText + "<br/>It may take a while for the entry to be updated in the browser.");
						 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
						 $.fn.smartLightBox.closeLightBox(2500);
						 setTimeout("loadPageWithSession('manageRSS.jsp')",2500);
					 }
					 else{
						 $("#lbMsg","#lightBox").html(responseText);
						 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
						 $.fn.smartLightBox.closeLightBox(2000);
					 }
				}
			}};
	}
	request = new Request;
	request.addAction("removeRSSEntry");
	request.addSessionKey(strSessionKey);
	request.addParam("entryIndex", index);
	request.addParam("feedType", type);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
}
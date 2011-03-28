/**
 * Javascript for Ban User Page (admin)
 */

$("document").ready(function() {
	$("#filterBan").bind("keyup", function(){
		applyFilter("tableBanUser", "filterBan");
	});
	$("#filterUnban").bind("keyup", function(){
		applyFilter("tableUnbanUser", "filterUnban");
	});
	$('#tabs').DynaSmartTab({});
	$('#sideMenu').sideNavMenu({});
	$('#lightBox').smartLightBox({});
});

function viewProfile(idAccount, currTabIndex){
	hideFrame(currTabIndex);
	showProfileTab();
	// inside Profile.js
	getProfileById(idAccount, 'profileTable', 'profileHeading', 'fileDiv');
	return false;
}

// send ban request to admin servlet
function sendBanRequest(strUserName){
	var strSessionKey = $("#sessionKey").val();
	// ask for reason to ban
    var strReason = prompt("Please enter a reason to ban " + strUserName + " below.\n" +
    		" An email message will be sent to the user with the information you enter.");
    if( strReason == null )
    	return false;
    else if ( strReason == "" ){
    	alert("Please enter the reason of banning the user.");
        return false;
    }
	$.fn.smartLightBox.openlb('small','Processing Ban Request...','load');
	
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
					var responseText = parseBanResponse(xmlHttpReq.responseXML);
					 $("#lbMsg","#lightBox").html(responseText);
					 $.fn.smartLightBox.closeLightBox(2000);
				}
			}};
	}
	request = new Request;
	request.addAction("ban");
	request.addSessionKey(strSessionKey);
	request.addParam("email", strUserName);
	request.addParam("reason", strReason);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
}

// parses response from server
function parseBanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if ban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else{
		 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
	 }
	 return strMsg;
}

//send unban request to admin servlet
function sendUnbanRequest(strUserName){
	var strSessionKey = $("#sessionKey").val();
	// ask for reason to unban
    var strReason = prompt("Please enter a reason to unban " + strUserName + " below.\n" +
    		" An email message will be sent to the user with the information you enter.");
    if( strReason == null )
    	return false;
    else if ( strReason == "" ){
    	alert("Please enter the reason of unbanning the user.");
        return false;
    }    	
    $.fn.smartLightBox.openlb('small','Processing Unban Request...','load');
    
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
					var responseText = parseUnbanResponse(xmlHttpReq.responseXML);
					$("#lbMsg","#lightBox").html(responseText);
					$.fn.smartLightBox.closeLightBox(2000);
				}
			}};
	}
	request = new Request;
	request.addAction("unban");
	request.addSessionKey(strSessionKey);
	request.addParam("email", strUserName);
	request.addParam("reason", strReason);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());	
}

// parses response from server
function parseUnbanResponse(responseXML){	
	 var boolResult = (responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
	 var strMsg = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
	 // if unban sucessful, then refresh page to reflect changes
	 if(boolResult == "true"){
		 $("#lbImg", "#lightBox").removeClass("load").addClass("good");
		 loadPageWithSession('manageUser.jsp');
	 }
	 else
		 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
	 return strMsg;
}

function hideFrame(tabIndex){
	var tabs = $($("ul > li > a ","#navBar"), obj);
	var curTab = tabs.eq(tabIndex);
	$($(curTab, obj).attr("href"), obj).hide();
}

function showProfileTab(){
	var tabs = $($("ul > li > a ","#navBar"), obj);
	// profile tab is at index 2
	curTabIdx = 2;
	var profileTab = tabs.eq(2); 
    $(tabs, obj).removeClass("curTab");
    profileTab.addClass("curTab");
    profileTab.parent().show();
    $($(profileTab, obj).attr("href"), obj).show();
    return true;
}
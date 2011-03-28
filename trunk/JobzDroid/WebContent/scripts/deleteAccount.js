/**
 * Javascript file for admin pages that contain delete account functions
 */
//send delete account request to admin servlet
function sendDeleteAccountRequest(accountName, pageSource){
	// ask for reason to delete if deleting a user (not admin)
	if(pageSource == "manageUser.jsp"){
	    var strReason = prompt("Please enter a reason to delete " + accountName + " below.\n" +
	    		" An email message will be sent to the user with the information you enter.");
	    if( strReason == null )
	    	return false;
	    else if ( strReason == "" ){
	    	alert("Please enter the reason of deleting the user.");
	        return false;
	    }
	}
    var b = confirm("Are you sure to PERMANENTLY delete account " + accountName + "?");
    if (b == false)
        return false;
    var strSessionKey = $("#sessionKey").val();
    
	$("button").attr("disabled", true);
	$.fn.smartLightBox.openlb('small','Deleting Account...','load');
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
		xmlHttpReq = new XMLHttpRequest();
	else
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					$("button").removeAttr("disabled");
					
					//parse XML response from server					
					 var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					 var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
					 $("#lbMsg","#lightBox").html(responseText);
					 if(boolResult == "true"){
						 $("#lbImg", "#lightBox").removeClass("load").addClass("info");
						 $.fn.smartLightBox.closeLightBox(2000);
						 loadPageWithSession(pageSource);
					 }
					 else{
						 $("#lbImg", "#lightBox").removeClass("load").addClass("alert");
						 $.fn.smartLightBox.closeLightBox(2000);
					 }
				}
			}};
	}
	request = new Request;
	request.addAction("deleteAccount");
	request.addSessionKey(strSessionKey);
	request.addParam("accountName", accountName);
	request.addParam("reason", strReason);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());		
}
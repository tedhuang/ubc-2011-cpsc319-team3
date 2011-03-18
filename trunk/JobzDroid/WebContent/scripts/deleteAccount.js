/**
 * Javascript file for admin pages that contain delete account functions
 */
//send delete account request to admin servlet
function sendDeleteAccountRequest(accountName, pageSource, statusTextID){
    var b = confirm("Are you sure to PERMANENTLY delete account " + accountName + "?");
    if (b == false)
        return false;
    var strSessionKey = $("#sessionKey").val();
	$(".linkImg").attr("disabled", true);
	$("button").attr("disabled", true);
	$("#"+statusTextID).removeClass("errorTag");	
	$("#"+statusTextID).removeClass("successTag");
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
		xmlHttpReq = new XMLHttpRequest();
	else
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	
	if(xmlHttpReq){
		xmlHttpReq.onreadystatechange = function(){
			if (xmlHttpReq.readyState == 4){
				if(xmlHttpReq.status == 200){
					//parse XML response from server
					
					 var boolResult = (xmlHttpReq.responseXML.getElementsByTagName("result")[0]).childNodes[0].nodeValue;
					 var responseText = (xmlHttpReq.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
					 // if registration sucessful, then update button text and function
					 if(boolResult == "true"){
						 $("#"+statusTextID).addClass("successTag");
						 loadPageWithSession(pageSource);
					 }
					 else
						 $("#"+statusTextID).addClass("errorTag");
										
					$(".linkImg").removeAttr("disabled");
					$("button").removeAttr("disabled");
			    	$("#"+statusTextID).text(responseText);
				}
			}};
	}
	request = new Request;
	request.addAction("deleteAccount");
	request.addSessionKey(strSessionKey);
	request.addParam("accountName", accountName);
	
	//send the request to servlet
	xmlHttpReq.open("POST","../ServletAdmin", true);
	xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlHttpReq.send(request.toString());
		
	//update status text
	$("#"+statusTextID).text("Processing...This may take a moment.");
}
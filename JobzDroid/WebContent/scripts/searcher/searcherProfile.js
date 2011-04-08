
function submitChangeProfile(accountType){
	$.fn.smartLightBox.openDivlb('profileFrame','load','Loading..');

	//disables button to prevent multiple submit
	document.getElementById("submitProfileButton").disabled=true;
	
	var sessionKey = $("#sessionKey").val();
		
	//Profile Changes
	var strName 		= document.getElementById("name").value;
	var strSecEmail		= document.getElementById("secondaryEmail").value;
	var strPhone 		= document.getElementById("phone").value;
	var strDescripton 	= document.getElementById("selfDescription").value;
	
	var strAddress 		= getAddress();
	var strLong			= getLongitude();
	var strLat			= getLatitude();
	
	if (accountType == "searcher"){
		var boolEmpPrefPT			= document.getElementById("partTimeCheck").checked;
		var boolEmpPrefFT			= document.getElementById("fullTimeCheck").checked;
		var boolEmpPrefIn			= document.getElementById("internCheck").checked;
		
		var strPreferredStartDate 	= document.getElementById("startingDate");
		var intEducationLevel 		= document.getElementById("educationLevel").value;
		
	}
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	
	request = new Request;
	request.addAction("editProfile");
	request.addSessionKey(sessionKey);
	
	request.addParam("name",strName);
	request.addParam("secEmail",strSecEmail);
	request.addParam("phone",strPhone);
	request.addParam("descripton",strDescripton);
	
	request.addParam("address", strAddress);
	request.addParam("longitude", strLong);
	request.addParam("latitude", strLat);
	
	if(accountType == "searcher"){
		request.addParam("empPrefPT", boolEmpPrefPT);
		request.addParam("empPrefFT", boolEmpPrefFT);
		request.addParam("empPrefIn", boolEmpPrefIn);
		request.addParam("educationLevel",intEducationLevel);
		request.addParam("preferredStartDate", strPreferredStartDate); //This is taken as a long
	}
	

	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());


	xmlhttp.onreadystatechange=function()
	  {
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  //alert((xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue);
			getSearcherProfileBySessionKey("profileTable", "profileHeading", "fileTable");
			$.fn.smartLightBox.closeLightBox(1000, "profileFrame");
	    }else{
		  	$("#lbImg", "#profileFrame").removeClass("load").addClass("alert");
			$("#lbMsg","#profileFrame").html("Approving Not Successful, please try again");
			$.fn.smartLightBox.closeLightBox(1000, "profileFrame");
	    }
	  };	
}




//function getProfileAndFileList(profileOutputDiv, profileHeading, fileOutputDiv){
//	getSearcherProfileBySessionKey(profileOutputDiv, profileHeading, fileOutputDiv );
//	listUserFiles(fileOutputDiv);
//}




function getSearcherProfileBySessionKey(profileOutputDiv, profileHeading, fileOutputDiv ){
  
	$.fn.smartLightBox.openDivlb("profileFrame", 'load','Loading Profile Data...');
	
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("getProfileBySessionKey");
	request.addSessionKey(strSessionKey);
	
//	//Concurrent Ajax handling
//	var xmlhttp=createXHR();
//	if(xmlhttp){
//		try{
//			//send the parameters to the servlet with POST
//			xmlhttp.open("POST","../ServletProfile" ,true);
//			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
//			xmlhttp.onreadystatechange = processResponse;
//			xmlhttp.send( request.toString() );
//		}catch(e){
//			
//		}
//	}
//	
//	function processResponse(){
//		  if (xmlhttp.readyState==4){ 
//		    try {
//			  if(xmlhttp.status==200){
//				  buildProfileEditTb("profile", profileOutputDiv, profileHeading);
//				  buildSearcherFileTb("file", fileOutputDiv);
//				  $.fn.smartLightBox.closeLightBox(500, "profileFrame");
//		    }
//			else{
//				  console.log("Get Seracher Profile failed");
//				  	$("#lbImg", "#profileFrame").removeClass("load").addClass("alert");
//					$("#lbMsg","#profileFrame").html("Action not successful, please try again");
//					$.fn.smartLightBox.closeLightBox(500, "profileFrame");
//			}
//		   }catch(e){
//			   //error-handling
//		   }
//		 }
//	}  
	
	var xmlHttpReq;
	if (window.XMLHttpRequest)
	  xmlhttp = new XMLHttpRequest();
	else
	  xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletProfile" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString());
	
	xmlhttp.onreadystatechange = function()
	  {
	  if ( xmlhttp.readyState == 4   && xmlhttp.status == 200)
	    {
		  //you can find this in uiBot.js under scripts folder
		  buildProfileEditTb("profile", profileOutputDiv, profileHeading);
		  buildSearcherFileTb("file", fileOutputDiv);
		  $.fn.smartLightBox.closeLightBox(500, "profileFrame");
		  
	    }else{
	    	
	    	//TODO: fix bug: Get profile seems to hit here 3 times	    	
//        	$("#lbImg", "#profileFrame").removeClass("load").addClass("alert");
//			$("#lbMsg","#profileFrame").html("Action Not Successful, please try again");
			 $.fn.smartLightBox.closeLightBox(500, "profileFrame");
	    }
	    	
	  };	
}

function listUserFiles( outputDiv ) {
	var sessionKey = document.getElementById("sessionKey").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	var Params = "action=listUserDocuments" + "&sessionKey=" + sessionKey;
	//send the parameters to the servlet with POST
	xmlhttp.open("POST", "../ServletDocument",true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(Params); 
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  buildSearcherFileTb( "file", outputDiv );
		    //Gets sessionKey and prints it to div
	    }
	  };
	
}

function uploadSearcherFile() {
	var sessionKey = document.getElementById("sessionKey").value;
	document.fileUploadForm.sessionKey.value = sessionKey;
	document.fileUploadForm.action = "../ServletDocument";
	var Params = "sessionKey=" + sessionKey;
	
	var url = "../ServletDocument";
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
    	xmlhttp = new XMLHttpRequest();
    	xmlhttp.onreadystatechange = fileUploadProgress;
    	
    	try {
    		xmlhttp.open("GET", "../ServletDocument", true);
    	}
    	catch(e) {
    		alert(e);
    	}
    	xmlhttp.send(Params);
    }
	else if  (window.ActiveXObject ){// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		
		if( xmlhttp ) {
			xmlhttp.onreadystatechange = fileUploadProgress;
			xmlhttp.open("GET", "../ServletDocument", true);
			xmlhttp.send(Params);
		}
	}

	
}

function fileUploadProgress() {

	/**
	 *	State	Description
	 *	0		The request is not initialized
	 *	1		The request has been set up
	 *	2		The request has been sent
	 *	3		The request is in process
	 *	4		The request is complete
	 */
	if (xmlhttp.readyState == 4)
	{
		if (xmlhttp.status == 200) // OK response
		{
			var xml = xmlhttp.responseXML;
			
			if ( xml == null ) {
				window.setTimeout("fileUploadProgress();", 500);
			}
			
			// No need to iterate since there will only be one set of lines
			var isNotFinished = xml.getElementsByTagName("finished")[0];
			var myBytesRead = xml.getElementsByTagName("bytes_read")[0];
			var myContentLength = xml.getElementsByTagName("content_length")[0];
			var myPercent = xml.getElementsByTagName("percent_complete")[0];

			// Check to see if it's even started yet
			if ((isNotFinished == null) && (myPercent == null))
			{
				$("#initializing").hide();

				// Sleep then call the function again
				window.setTimeout("fileUploadProgress();", 100);
			}
			else 
			{
				$("#initializing").hide();
				$("#progressBarTable").hide();
				$("#percentCompleteTable").hide();
				$("#bytesRead").hide();

				myBytesRead = myBytesRead.firstChild.data;
				myContentLength = myContentLength.firstChild.data;

				if (myPercent != null) // It's started, get the status of the upload
				{
					myPercent = myPercent.firstChild.data;
		
					$("#progressBar").css("width", myPercent + "%");
					$("#bytesRead").html(myBytesRead + " of " +	myContentLength + " bytes read");
					$("#percentComplete").html(myPercent + "%");
	
					// Sleep then call the function again
					window.setTimeout("fileUploadProgress();", 100);
				}
				else
				{
					$("bytesRead").hide();
					$("progressBar").css("width", "100%");
					$("percentComplete").html("Done!");
					

					var msg =  (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
					$("#fileFeedback").text(msg);
					setTimeout( "refreshFiles()", 2000 );
				}
			}
		}
		else
		{
			alert(xmlhttp.statusText);
		}
	}
//	if (xmlhttp.readyState==4 && xmlhttp.status==200) {
//		var message = (responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;;
//		document.getElementById("fileFeedback").innerHTML = message;
//    }
}

function refreshFiles() {
	$("#refreshProfileButton").click();
}

function deleteSearcherFile(filename) {
    var b = confirm("Are you sure to PERMANENTLY delete the selected file?");
    if (b == false)
        return false;
	var strSessionKey = document.getElementById("sessionKey").value;
	
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	request = new Request;
	request.addAction("deleteDocument");
	request.addSessionKey(strSessionKey);
	request.addParam("fileName", filename);
	
	//send the parameters to the servlet with POST
	xmlhttp.open("POST","../ServletDocument" ,true);
	xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xmlhttp.send(request.toString()); 
	
	xmlhttp.onreadystatechange=function(){
	  if (xmlhttp.readyState==4 && xmlhttp.status==200)
	    {
		  var msg =  (xmlhttp.responseXML.getElementsByTagName("message")[0]).childNodes[0].nodeValue;
		  $("#fileFeedback").text(msg);
		  $("#refreshProfileButton").click();
	    }
	  };
}

function profileSummary(outputDiv){
	var strSessionKey = $("#sessionKey").val();
	
	request = new Request;
	request.addAction("smrSearcherProfile");
	request.addSessionKey(strSessionKey);
	
	var xhr = createXHR(); //Now created xhr used a function
	function processResult(){
		if (xhr.readyState == 4) {
			try {
				  if (xhr.status == 200) {
					  var result = $("profileSmr",xhr.responseXML);
					  console.log(result.text());
					  $("#"+outputDiv).html('<h2 class="welcome">'+ result.text() +'</h2>');
				  }
                else{ 
                	 console.log("Status error");
                	 console.log("Server down...");
                }
			} 	
			catch (e){
			}
	    }
//		  $.fn.smartLightBox.closeLightBox(1000,"home-frame");
	}//eof processResult
	if(xhr){
		try {
			  xhr.open("POST", "../ServletProfile", true);
			  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			  xhr.onreadystatechange = processResult; //bind to callBack to have threaded ajax calls
			  xhr.send(request.toString());
		} catch (e) {
			             //Handle error
		  }
	}	  
}


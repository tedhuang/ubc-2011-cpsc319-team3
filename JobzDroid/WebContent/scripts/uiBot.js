/********************************************************************************************************************
 * 						Build a table for ad detail
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildDetailTable(targetXMLTag, outputDiv, xmlResponse){
	var fb =$(".feedback", "#"+outputDiv);
	var heading=$('.heading', "#"+outputDiv);
	
	var jobAd = $(targetXMLTag,xmlResponse);
	
	if(jobAd.length==0){//if no results
		fb.html("<h2 class='error'>Oops, you are looking at something that does not exist</h2>");
	}
	else{
		var tbody  = $( "tbody", "#"+outputDiv).html("");
		heading.text(jobAd.attr("jobAdTitle"));//TODO FIX the HEADING
		var tr = $('<tr></tr>').addClass("verticalTb");
		$('<tr></tr>')
		.append('<td class="verticalTh">Date Posted</td><td class="verticalTb">'+ jobAd.attr("creationDate")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Company</td><td class="verticalTb">'+ jobAd.attr("contactInfo")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Degree Required</td><td class="verticalTb">'+ jobAd.attr("eduReq")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Position Type</td><td class="verticalTb">'+ jobAd.attr("jobAvailability")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Starting Date</td><td class="verticalTb">'+ jobAd.attr("startingDate")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Expiry Date</td><td class="verticalTb">'+ jobAd.attr("expiryDate")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Grad Funding Availability</td><td class="verticalTb">'+ jobAd.attr("hasGradFunding")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Tags</td><td class="verticalTb">'+ jobAd.attr("tags")+ '</td>')
		.appendTo(tbody);
		$('<tr></tr>')
		.append('<td class="verticalTh">Job Description</td><td class="verticalTb">'+ jobAd.attr("jobAdDescription")+ '</td>')
		.appendTo(tbody);
		var locRow =$('<tr></tr>')
		.append('<td class="verticalTh">Location</td>')
		.appendTo(tbody);
		var locTd = $('<td class="verticalTb loc-td"></td>').appendTo(locRow);
		locTd.smartMap({displayMap:true});
		var locations=jobAd.find("location");
		if(locations.length){ //if there is some location
		var locObjArray = [];
		$.each(locations, function(){
			var locObj={addr:null, latlng:null};
			locObj.addr=$(this).attr("addr");
			locObj.latlng=$(this).attr("latlng");
			locObjArray.push(locObj);
		});
		$.fn.smartMap.adDetailMapDisplay(locTd, locObjArray);
		}
		else{
		locTd.text("Location not specified.");
		}
		
	 	fb.hide();
	}
}



/************************************************************************************************
 * 					BUILD TABLE CONTAINING LIMITED NUMBER JOB ADS - used by Guests and Searchers
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildGuestJobAdTb(targetXMLTag, outputDiv){
	
	//TODO: finish implementing
	var tbody  = $("tbody", "#"+outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
//		$(".feedback").html("<h2 class='info'>You Have Not Yet Posted Anything</h2>");
//		$("#"+outputDiv).html("<h2 class='info'>Unable to find any Job Ads</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var tr = $('<tr></tr>');
		  $('<td></td>').addClass('td-date').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-date').text(jobAd.attr("startingDateFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-title').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').addClass('td-loc').text(jobAd.attr("location")).appendTo(tr);
		  $('<td></td>').addClass('td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  //$('<td></td>').attr("id", id='td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr); 
		  $.fn.DynaSmartTab.guestTool(tr, jobAd.attr("jobAdId"));
		  tr.appendTo(tbody);
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}






/************************************************************************************************
 * 					BUILD TABLE CONTAINING ALL JOB ADS - used by Admin(manageJobAd.jsp)
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildAdminJobAdTb(targetXMLTag, outputDiv){
	
	var tbody  = $("tbody", "#"+outputDiv).html("");

	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
//		$(".feedback").html("<h2 class='info'>You Have Not Yet Posted Anything</h2>");
//		$("#"+outputDiv).html("<h2 class='info'>Unable to find any Job Ads</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var jobAdId = jobAd.attr("jobAdId");
		  var isApproved=jobAd.attr("isApproved") ;

		  var tr = $('<tr></tr>').attr("id", id='row-'+jobAdId);//.addClass("oddRow hover");
		  
		  $('<td></td>').addClass('td-cdate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-sdate').text(jobAd.attr("startingDateFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-title').addClass('jsBtn').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-status-'+jobAdId).text(jobAd.attr("status")).appendTo(tr);
		  
		  var isApprovedFormatted;
		  if(jobAd.attr("isApproved") == 0)
				isApprovedFormatted = "Not Approved";
		  else
				isApprovedFormatted = "Approved";
		  $('<td></td>').attr("id", id='td-approval-'+jobAdId).text(isApprovedFormatted).appendTo(tr);
		  

		  //ADD CHECK BOXES
		  var approveInput = $('<td></td>').addClass('tool');
		  var denyInput = $('<td></td>').addClass('tool');
		  var deleteInput = $('<td></td>').addClass('tool');
		  
		  //ADD RADIO INPUTS
		  $('<input type="radio" value="approve">').attr("name", jobAdId).attr("id", "approve-"+jobAdId).appendTo(approveInput).click(function(){
			  if( isApproved == 0 &&  $("input[name='"+jobAdId+"']")[0].checked ){
				  tr.removeClass("oddRow");
				  tr.addClass("oddRow change");
			  }else{
				  tr.removeClass("oddRow change");
				  $("tr:odd", tbody).addClass("oddRow");
			  }
		  });
		  $('<input type="radio" value="deny">').attr("name", jobAdId).attr("id", "deny-"+jobAdId).appendTo(denyInput).click(function(){
			  if( isApproved == 1 &&  $("input[name='"+jobAdId+"']")[1].checked ){
				  tr.removeClass("oddRow");
				  tr.addClass("oddRow change");
			  }else{
				  tr.removeClass("oddRow change");
				  $("tr:odd", tbody).addClass("oddRow");
			  }
		  });
		  $('<input type="radio" value="delete">').attr("name", jobAdId).attr("id", "delete-"+jobAdId).appendTo(deleteInput).click(function(){
			  if( $("input[name='"+jobAdId+"']")[2].checked ){
				  tr.removeClass("oddRow");
				  tr.addClass("oddRow change");
			  }else{
				  tr.removeClass("oddRow change");
				  $("tr:odd", tbody).addClass("oddRow");
			  }
		  });		  
		  
		  approveInput.appendTo(tr);
		  denyInput.appendTo(tr);
		  deleteInput.appendTo(tr);
		  $.fn.DynaSmartTab.adminTool(tr, jobAdId);
		  
		  tr.appendTo(tbody);
		  
		  //Update Radio button to current settings
			  if( isApproved == 0 ){
				  $("input[name='"+jobAdId+"']")[1].checked = true;
			  }else{
				  $("input[name='"+jobAdId+"']")[0].checked = true;
			  }
		  
		    
		});//END OF FOR EACH FUNCTION
		
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
		 
	}
	  $.fn.smartLightBox.closeLightBox(200,"home-frame");
}





function filterTable(filter, tbContainerId){
	
	var tbody  = $("tbody", "#"+tbContainerId);
	var rows   = $('tr', tbody).get();
	filter==""?
			$(rows).show():
			$(rows).each(function(){
				$('td.td-status', $(this)).html()==filter?
						$(this).show():$(this).hide();
		});
	}

function resetFields(formContainer){
	
	var temp = $(':input', "#"+formContainer);
	if(temp.length >0){
		
		temp
		 .not(':button, :submit, :reset, :hidden')
		 .val('')
		 .removeAttr('checked')
		 .removeAttr('selected');
	}
	clearError(formContainer);
}
function clearError(formContainerId){
	$($('h2.error', "#"+formContainerId).get()).each(function(){
		$(this).remove();
	});
}
function bindClearError(){
	$('.mustNotNull').change(function(){
		var errorTag = $(this).parent('div').find('h2.error');
		if(errorTag){
			errorTag.remove();
		}
	});
}


/************************************************************************************************
 * 					BUILD TABLE FOR AD LIST
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildAdListTb(targetXMLTag, outputDiv){
	outputDiv="#"+outputDiv;
	var tbody  = $("tbody", outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
		$("#feedback").html("<h2 class='error'>No Results Found</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var tr =$('<tr></tr>');
		  $('<td></td>').attr("id", id='td-pDate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').addClass('jsBtn').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-status').text(jobAd.attr("contactInfo")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-loc').html(jobAd.children("location").attr("address")).appendTo(tr);
		  var strSessionKey = $("#sessionKey").val();
//		  var favouriteAnchor =  "<a class='button' href='#' " +
//		  		"onclick='saveFavouriteJobAd("+ strSessionKey +", "+ jobAd.attr('jobAdId')+">" +
//		  		"<span>Save</span></a>";
		  $('<a></a>')
		  .addClass('jsBtn')
		  .click(function(){
			  saveFavouriteJobAd(strSessionKey, jobAd.attr('jobAdId'));
		  })
		  .text("Save")
		  .appendTo(tr);
//		  tr.append(favouriteAnchor);
		  
		  tr.appendTo(tbody);
		  $("#td-title", tr).click(function(){
			  getJobAdById(jobAd.attr("jobAdId"),'adDetailTable');
			 });
		});
		 
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}

function saveFavouriteJobAd(sessKey, jobId){
	
	request = new Request;
	request.addAction("saveFavouriteJobAd");
	request.addParam("sessionKey", sessKey);
	request.addParam("jobAdId", jobId);

	
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


/******************************************************
 * 			build file list for POSTER
 ******************************************************/
function buildSearcherFileViewingTb(targetXMLTag, outputDiv){
	var tbody  = $("tbody", "#"+outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	var sKey = $("#sessionKey").val();
	if(xmlObj.length==0){//if no results
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
			  var file = $(this);
			  var filename = file.attr("fileName");
			  var idOwner = file.attr("idOwner");
			  var tr = $('<tr></tr>');
			  var filenameAnchor =  $('<a></a>').attr('href', '../downloadDoc.jsp?sessionKey=' + sKey + '&filename=' + filename + '&idOwner=' + idOwner).text(filename);
			  var filenameCell = $('<td></td>');
			  filenameCell.css("text-align","center");
			  filenameAnchor.appendTo(filenameCell);
			  filenameCell.appendTo(tr);
			  
			  var filesizeCell = $('<td></td>');
			  filesizeCell.text(file.attr("size"));
			  filesizeCell.css("text-align","center");
			  filesizeCell.appendTo(tr);
			  
			  tr.appendTo(tbody);	  
		});
		 $("tr:odd", tbody).addClass("oddRow");
	}
}

/********************************************************************************************************************
 * 						Build a table for profile editing
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildProfileEditTb(targetXMLTag, outputDiv, heading, xmlResp){
	var tbody  = $( "tbody", "#"+outputDiv).html("");
	var profile = $(targetXMLTag, xmlResp);
	
	if(profile.length==0){//if no results
		$("#profileFB").html("<h2 class='error'>Oops, you are looking at something that does not exist!</h2>");
	}
	else{
		var accountType = profile.attr("accountType");	

		var educationLevelSelection = 	"<select id=\"educationLevel\" name=\"educationLevel\" style = \"DISPLAY: none;\"> " +
											"<option value=\"0\">None</option>"  +
											"<option value=\"1\">B.Sc.</option>" +
											"<option value=\"2\">M.Sc.</option>" +
											"<option value=\"3\">Ph.D.</option>" +
										"</select>";
		
		var employmentPrefSelection = "<div id=empPrefSelectionDiv style='DISPLAY: none'>" +
										  "<input type='checkbox' name='partTimeCheck' id='partTimeCheck' value='parttime'  > Part-Time " +
										  "<input type='checkbox' name='fullTimeCheck' id='fullTimeCheck' value='fulltime'  > Full-Time" +
										  "<input type='checkbox' name='internCheck'   id='internCheck'   value='internship'> Internship" +
									  "</div>";
		
		var addressButton = "<button type='button' id=\"addressButton\" style = 'DISPLAY: none;' onclick='calculateLocation()'>Find Location</button>";
		var addressResult = "<span type='text' id='locFeedback'></span><br><br>" +
							"<span id='resultTableTitle'></span> <table id='lookUpTable'></table>";
		
		var accountText = 
				"<tr id='oldEmailRow' ><td>Your Account E-mail</td><td>"	+ profile.attr("email")						+ "</td><td></td></tr>" +
				"<tr id='newEmailRow' 	 style='DISPLAY:none;'><td>New E-mail</td><td>"				+ " " + "</td><td><input id='emailNew' /><span id='emailError'></td></tr>" +
				"<tr id='oldSecEmailRow'><td>Your Backup E-mail</td><td>"	+ profile.attr("secondaryEmail") 			+ "</td><td></td></tr>" +
				"<tr id='secEmailRow' 	 style='DISPLAY:none;'><td>New Secondary E-mail</td><td>"	+ " " + "</td><td><input id='secondaryEmail'/></td></tr>" +
				"<tr id='oldPWRow' 		 style='DISPLAY:none;'><td>Old Password</td><td>"			+ " " + "</td><td><input id='passwordOld'   /></td></tr>" +
				"<tr id='newPWRow' 		 style='DISPLAY:none;'><td>New Password</td><td>"			+ " " + "</td><td><input id='passwordNew'   /><span id='pwError'></td></tr>" +
				"<tr id='repeatPWRow' 	 style='DISPLAY:none;'><td>Repeat Password</td><td>"		+ "	" + "</td><td><input id='passwordRepeat'/></td></tr>";

		var profileText =
			"<tr id='nameRow'><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td><td><input id='name' style='DISPLAY: none;'/><span id='nameError'></td></tr>" +
			"<tr id='phoneRow'><td>Your Phone Number</td><td>"	+ profile.attr("phone")				+ "</td><td><input id='phone'style='DISPLAY: none;'/><span id='phoneError'></td></tr>" +
			"<tr id='descRow' ><td>Your Self Description</td><td>"+ profile.attr("selfDescription")	+ "</td><td><textarea id='selfDescription' rows='10' cols='50' style = 'DISPLAY: none;'/><br/><span id='descInfo'></span></td></tr>";

		//Add Searcher Fields
		if( accountType == "searcher"){	
			profileText +=
				"<tr id='eduRow'><td>Your Education Level</td><td>"			+ profile.attr("educationFormatted")	+ "</td><td>" + educationLevelSelection + "</td></tr>" +
				"<tr id='epRow'><td>Your Employment Preference</td><td>"	+ profile.attr("employmentPreference")	+ "</td><td>" + employmentPrefSelection + "</td></tr>" +
				"<tr id='sdRow'><td>You're Available From</td><td>"		+ profile.attr("startingDateFormatted")	+ "</td><td><input id='startingDate' style = 'DISPLAY: none;'/> <span id='startingDateError'></span> </td></tr>";
		}
		
		//Add Address Input Field
		var address = profile.find("location").attr("address");		
		profileText += 
			"<tr id='addrRow'><td>Your Address</td>  <td>"+address +"</td>  <td><input id='loc-filed' style='DISPLAY:none;'/></td></tr>" +
			"<tr id='addrBRow'><td></td><td></td><td>" + addressButton + "</td></tr>" +
			"<tr id='addrRRow'><td></td><td></td><td>" + addressResult + "</td></tr>";
			
			
		//Add buttons 
		buttonHTML = 
			"<tr>" +
				"<td><button id=\"enableAccountEditButton\" type=\"button\" onclick=\'enableAccountEdit(\""+accountType+"\")'>Change E-mail and Password</button></td>" +
				"<td></td><td><button id='submitAccountButton' style = 'DISPLAY: none;' onclick=\'submitChangeAccount()'>Submit</button></td>" +
			"</tr>"+
			"<tr>" +
				"<td><button id=\"enableProfileEditButton\" type=\"button\" onclick=\'enableProfileEdit(\""+accountType+"\")'>Change Profile Fields</button></td>" +
				"<td></td><td><button id='submitProfileButton' style = 'DISPLAY: none;' onclick=\'submitChangeProfile(\""+accountType+"\")'>Submit</button></td>" +
			"</tr>"+
			
		//Display the old values		
		 $(tbody).append(accountText);
		 $(tbody).append(profileText);
		 $(tbody).append(buttonHTML);
		 $(tbody).find('tr').find('td:first').addClass("nameCol");
		 $(tbody).find('tr').find('td:last').addClass("dataCol");
		 $("#detailFB").hide();
		 
		 //document.getElementById("emailNew").innerHtml=profile.attr("email");
		 
		$("#emailNew").val(profile.attr("email")); 
		$("#secondaryEmail").val(profile.attr("secondaryEmail"));

		$("#name").val(profile.attr("name"));
		$("#phone").val(profile.attr("phone"));
		
		var processedDesc = profile.attr("selfDescription").replace(/&nbsp;/gi, ' ');
		processedDesc = processedDesc.replace(/<br \/>/gi, '\n');
		
		$("#selfDescription").val(processedDesc);
		$("#selfDescription").bind("keyup", function(){
			limitChars('selfDescription', 5000, 'descInfo');
		});
		$("#loc-filed").val(profile.attr("address"));
		
		if(accountType == "searcher"){
			$("#startingDate").val(profile.attr("startingDateFormatted"));
			//$("#empPrefSelectionDiv").show(); //TODO: make the check boxes check the correct submitted employment preference
			var empPref = profile.attr("employmentPreference");
			//$("input[id=partTimeCheck]").attr('checked');
			$("#educationLevel").val(profile.attr("educationLevel"));
		}
	}
}

/********************************************************************************************************************
 * 						Enable Account Edit
 *********************************************************************************************************************/
function enableAccountEdit(accountType){
	$("#emailNew").show();
	$("#secondaryEmail").show();
	$("#passwordOld").show();
	$("#passwordNew").show();
	$("#passwordRepeat").show();
	
	$("#oldEmailRow").show();
	$("#newEmailRow").show();
	$("#oldSecEmailRow").show();
	$("#secEmailRow").show();
	$("#oldPWRow").show();
	$("#newPWRow").show();
	$("#repeatPWRow").show();
	$("#submitAccountButton").show();
	
	//Hide Edit Profile Fields
	$("#nameRow").hide();
	$("#phoneRow").hide();
	$("#descRow").hide();
	$("#addrRow").hide();
	$("#addrBRow").hide();
	$("#addrRRow").hide();
	
	$("#addressButton").hide();
	$("#name").hide();
	$("#phone").hide();
	$("#selfDescription").hide();
	$("#loc-filed").hide();
	$("#addressButton").hide();
	
	if(accountType == "searcher"){
		$("#startingDate").hide();
		$("#empPrefSelectionDiv").hide(); 
		$("#educationLevel").hide();
		$("#eduRow").hide();
		$("#epRow").hide();
		$("#sdRow").hide();
	}
	$("#submitProfileButton").hide();
	
}



/********************************************************************************************************************
 * 						Enable Profile Edit
 *********************************************************************************************************************/
function enableProfileEdit(accountType)
{
	$("#nameRow").show();
	$("#phoneRow").show();
	$("#descRow").show();
	$("#addrRow").show();
	$("#addrBRow").show();
	$("#addrRRow").show();
	
	$("#addressButton").show();
	$("#name").show();
	$("#phone").show();
	$("#selfDescription").show();
	$("#loc-filed").show();
	$("#addressButton").show();
	
	if(accountType == "searcher"){
		$("#startingDate").show();
		$("#empPrefSelectionDiv").show(); 
		$("#educationLevel").show();
		$("#eduRow").show();
		$("#epRow").show();
		$("#sdRow").show();
	}
	$("#submitProfileButton").show();
	
	//Hide Account Fields
	$("#oldEmailRow").hide();
	$("#newEmailRow").hide();
	$("#oldSecEmailRow").hide();
	$("#secEmailRow").hide();
	$("#oldPWRow").hide();
	$("#newPWRow").hide();
	$("#repeatPWRow").hide();
	$("#submitAccountButton").hide();
}


/********************************************************************************************************************
 * 						Reverts back to Profile Display
 *********************************************************************************************************************/
function redisplayProfile(accountType)
{
	$("#nameRow").show();
	$("#phoneRow").show();
	$("#descRow").show();
	$("#addrRow").show();
	$("#addrBRow").hide();
	$("#addrRRow").hide();
	
	$("#addressButton").hide();
	$("#name").hide();
	$("#phone").hide();
	$("#selfDescription").hide();
	$("#loc-filed").hide();
	$("#addressButton").hide();
	
	if(accountType == "searcher"){
		$("#startingDate").hide();
		$("#empPrefSelectionDiv").hide(); 
		$("#educationLevel").hide();
		$("#eduRow").show();
		$("#epRow").show();
		$("#sdRow").show();
	}
	$("#submitProfileButton").hide();
	
	//Hide Account Fields
	$("#oldEmailRow").show();
	$("#newEmailRow").hide();
	$("#oldSecEmailRow").show();
	$("#secEmailRow").hide();
	$("#oldPWRow").hide();
	$("#newPWRow").hide();
	$("#repeatPWRow").hide();
	$("#submitAccountButton").hide();
}




/*******************************************************************************************************************************
 * 		Customize Default DOM ELEMENTS
 * *****************************************************************************************************************************/
	function customizeInput(inputClass, styleNotSel, styleSel){
		var element = $("."+inputClass);
		element.css("display", "none");//hide the default style
		element.addClass(styleNotSel); //add not selected style to it
		var inputType = element.attr("type");
		
		switch(inputType){
		
		case "checkbox":
			element.change(function(){ //toggle styled effects
		        if($(this).is(":checked")){
		            $(this).next("label").addClass(styleSel);
		        }else{
		            $(this).next("label").removeClass(styleSel);
		        }
		    });
			break;
		case "radio":
			element.change(function(){
		        if($(this).is(":checked")){
		            $(element + ":not(:checked)").removeClass(styleSel);
		            $(this).next("label").addClass(styleSel);
		        }
		    }); 
			break;
		}
		
	}
	
	function setupLabel() {
	    if ($('.label-cb input').length) {
	        $('.label-cb').each(function(){ 
	            $(this).removeClass('cby');
	        });
	        $('.label-cb input:checked').each(function(){ 
	            $(this).parent('label').addClass('cby');
	        });                
	    };
	    if ($('.label-rb input').length) {
	        $('.label-rb').each(function(){ 
	            $(this).removeClass('rby');
	        });
	        $('.label-rb input:checked').each(function(){ 
	            $(this).parent('label').addClass('rby');
	        });
	    };
	};



/******************************************************
 * 			build jobSearcher Search List
 ******************************************************/
	function buildJSListTb(targetXMLTag, outputDiv){
		outputDiv="#"+outputDiv;
		var tbody  = $("tbody", outputDiv).html("");
		var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
		if(xmlObj.length==0){//if no results
			$("#feedback").html("<h2 class='error'>No Results Found.</h2>");
		}
		else{
			xmlObj.each(function() {//for All returned xml obj
			  var profileSearcherXML = $(this);
			  var tr =$('<tr></tr>');
			  var degree = {"str": degreeConvertor(profileSearcherXML.attr("educationLevel")), 
					  		"degreeNum":profileSearcherXML.attr("educationLevel")};
			  $('<td></td>').attr("id", id='td-name').addClass('jsDetailBtn').html(profileSearcherXML.attr("name")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-eduReq').text(degree.str).appendTo(tr);
			  $('<td></td>').attr("id", id='td-loc').html(profileSearcherXML.children("location").attr("address")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-startDate').text(profileSearcherXML.attr("startingDateFormatted")).appendTo(tr);
			  
			  
			  $.fn.DynaSmartTab.searchJSTool(tr, profileSearcherXML.attr("accountID"));
			  tr.appendTo(tbody);
			});
			 
			 $("tr:odd", tbody).addClass("oddRow");
			 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
		}
	}	
	function degreeConvertor(input){
		switch(input){
		case "1": return "B.Sc"; break;
		case "2": return "M.Sc"; break;
		case "3": return "Ph.D"; break;
		default: return "Unavailable"; break;
		}
	}
/***************************************************************
 * buildSearcherDetailTable(targetXMLTag, outputDiv)
 ****************************************************************/
	function buildSearcherDetailTable(targetXMLTag, outputDiv){
		var tbody  = $("tbody", "#"+outputDiv).html("");
		var fb =$(".detailFB", "#"+outputDiv);
		var heading=$('.jsDetailHeading', "#"+outputDiv);
		var profile = $(targetXMLTag,xmlhttp.responseXML);
		if(profile.length==0){//if no results
			fb.html("<h2 class='error'>Oops, you are looking at something not does not exist</h2>");
		}
		else{				
			heading.text(profile.attr("name")+"\'s Profile");//TODO FIX the HEADING
			var rowText = "<tr><td>Email</td><td>" 						+ profile.attr("email") 						+ "</td></tr>" +
		  				"<tr><td>Secondary Email</td><td>"				+ profile.attr("secondaryEmail")				+ "</td></tr>" +
		  				"<tr><td>Phone</td><td>"						+ profile.attr("phone")							+ "</td></tr>" +
		  				"<tr><td>Education Level</td><td>"				+ profile.attr("educationFormatted") 			+ "</td></tr>" +
		  				"<tr><td>Preferred Start Date</td><td>"			+ profile.attr("startingDateFormatted")			+ "</td></tr>" +
		  				"<tr><td>Employment Preference</td><td>"		+ profile.attr("employmentPreference")			+ "</td></tr>" +
		  				"<tr><td>Location</td><td>"						+ profile.children("location").attr("address")	+ "</td></tr>" +
		  				"<tr><td>Self Description</td><td>"				+ profile.attr("selfDescription")				+ "</td></tr>" +
		  				"<tr class='clean'></tr>";
		  
		    $(tbody).append(rowText);
		 	$(tbody).find('tr').find('td:first').addClass("nameCol");
		 	$(tbody).find('tr').find('td:last').addClass("dataCol");
		 	fb.hide();
		}
	}

	
	

/********************************************************************************************************************
 * 						Build a table for profile
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildProfileTb(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", "#" + outputDiv).html("");
	var profile = $(targetXMLTag, xmlhttp.responseXML);
	if(profile.length==0){//if no results
		$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
	}
	else{
		
		switch(profile.attr("accountType")){
		
		case("searcher"):
			
		  $("#"+heading).html( profile.attr("name") + "'s Profile");
		  var rowText = "<tr><td>Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
		  				"<tr><td>Account Type</td><td>"		+ "Job Searcher"					+ "</td></tr>" +
		  				"<tr><td>Account Name</td><td>"		+ profile.attr("email")				+ "</td></tr>" +
		  				"<tr><td>Secondary Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
		  				"<tr><td>Phone</td><td>"			+ profile.attr("phone")				+ "</td></tr>" +
		  				"<tr><td>Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td></tr>" +
		 //				"<tr><td>Employer Preference</td><td>"  + profile.attr("employmentPreference") 	+ "</td></tr>" +
		 //				"<tr><td>Location</td><td>"		        + profile.attr("address")				+ "</td></tr>" +
		  				"<tr><td>Starting Date</td><td>"        + profile.attr("startingDateFormatted")	+ "</td></tr>" +
		  				"<tr><td>Self Description</td><td>"		+ profile.attr("selfDescription")		+ "</td></tr>";
		  break;
		  
		case "poster":
			
			$("#"+heading).html( profile.attr("name") + "'s Profile");
			  var rowText = "<tr><td>Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
			  				"<tr><td>Account Type</td><td>"		+ "Job Poster"						+ "</td></tr>" +
			  				"<tr><td>Account Name</td><td>"		+ profile.attr("email")				+ "</td></tr>" +
			  				"<tr><td>Secondary Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
			  				"<tr><td>Phone</td><td>"			+ profile.attr("phone")				+ "</td></tr>" +
		//	  				"<tr><td>Location</td><td>"		    + profile.attr("address")			+ "</td></tr>" +
			  				"<tr><td>Self Description</td><td>"	+ profile.attr("selfDescription")	+ "</td></tr>";
			
		  break;
		  
		default:
			$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
			break;
		
		}
		 $(tbody).append(rowText);
		 $(tbody).find('tr').find('td:first').addClass("nameCol");	
		 $(tbody).find('tr').find('td:last').addClass("dataCol");
	}
}
	



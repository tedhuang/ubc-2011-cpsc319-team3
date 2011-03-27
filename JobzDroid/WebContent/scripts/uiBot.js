/********************************************************************************************************************
 * 						Build a table for ad detail
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildDetailTable(targetXMLTag, outputDiv){
	var fb =$(".feedback", "#"+outputDiv);
	var heading=$('.heading', "#"+outputDiv);
	
	var jobAd = $(targetXMLTag,xmlhttp.responseXML);
	if(jobAd.length==0){//if no results
		fb.html("<h2 class='error'>Oops, you are looking at something not does not exist</h2>");
	}
	else{
		var gradFunding = jobAd.attr("hasGradFunding");
		if( gradFunding == 1 )
			gradFunding = "Yes";
		else
			gradFunding = "No";

		var tbody  = $( "tbody", "#"+outputDiv).html("");
		heading.text(jobAd.attr("jobAdTitle"));//TODO FIX the HEADING
		var rowText = "<tr><td>Date Posted</td><td>" 				+ jobAd.attr("creationDateFormatted") 			+ "</td></tr>" +
	  				"<tr><td>Location</td><td>"						+ jobAd.find("location").attr("address")		+ "</td></tr>" +
	  				"<tr><td>Minimal Degree Requirement</td><td>"	+ jobAd.attr("eduReqFormatted")					+ "</td></tr>" +
	  				"<tr><td>Available Positions</td><td>"			+ jobAd.attr("jobAvail") 						+ "</td></tr>" +
	  				"<tr><td>Has Graduate Funding</td><td>"			+ gradFunding									+ "</td></tr>" +
	  				"<tr><td>Starting Date</td><td>"				+ jobAd.attr("startingDateFormatted")			+ "</td></tr>" +
	  				"<tr><td>Contact Info</td><td>"					+ jobAd.attr("contactInfo")						+ "</td></tr>" +
	  				"<tr><td>Job Description</td><td>"				+ jobAd.attr("jobAdDescription")				+ "</td></tr>" +
	  				"<tr class='clean'></tr>" +	
	  				"<tr><td>Tags</td><td>"							+ jobAd.attr("tags")							+ "</td></tr>" ;
	  
	    $(tbody).append(rowText);
	 	$(tbody).find('tr').find('td:first').addClass("nameCol");
	 	$(tbody).find('tr').find('td:last').addClass("dataCol");
	 	$.fn.smartLightBox.closeLightBox(0, $("#"+outputDiv).parent(".subFrame").attr('id'));
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
		  $('<td></td>').attr("id", id='td-pDate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').text(jobAd.attr("startingDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-loc').text(jobAd.children("location").attr("address")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  //$('<td></td>').attr("id", id='td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr); 
		  

		  var viewButton = $('<td></td>');
		  viewButton.appendTo(tr);
		  
		  $("<button>View</button>").attr("id", id='td-viewButton').click(
			function() {
 				openTab('adDetailTab'); 
    			open_adDetail(jobAd.attr("jobAdId"));
				getJobAdById("detail", jobAd.attr("jobAdId"), "adDetailTable");
			     //TODO: implement transition loading screen - UI
			  }
		  ).appendTo(viewButton);
		  
		// $.fn.DynaSmartTab.floatingTool(tr, jobAd.attr("jobAdId"));
		  tr.appendTo(tbody);
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}






/************************************************************************************************
 * 					BUILD TABLE CONTAINING ALL JOB ADS - used by Admin(manageJobAd.jsp) and Searcher
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildBrowseJobAdTb(targetXMLTag, outputDiv){
	
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
		  $('<td></td>').attr("id", id='td-pDate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').text(jobAd.attr("startingDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  //$('<td></td>').attr("id", id='td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  //$('<td></td>').attr("id", id='td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr); 
		  $('<td></td>').attr("id", id='td-loc').text(jobAd.children("location").attr("address")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-status').text(jobAd.attr("status")).appendTo(tr);
		  
		  var isApprovedFormatted;
		  if(jobAd.attr("isApproved") == 0)
				isApprovedFormatted = "Not Approved";
		  else
				isApprovedFormatted = "Approved";
		  $('<td></td>').attr("id", id='td-approval').text(isApprovedFormatted).appendTo(tr);
		  
		  
		  //ADD ADMIN FUNCTIONS:
		  var approveButton = $('<td></td>');
		  var denyButton = $('<td></td>');
		  var deleteButton = $('<td></td>');
		  
		  approveButton.appendTo(tr);
		  denyButton.appendTo(tr);
		  deleteButton.appendTo(tr);
		  
		  $("<button>Approve</button>").attr("id", id='td-approveButton').click(
			function() {
			  if(jobAd.attr("isApproved") == 1)
				  alert("This Job Ad has already been approved");
			  else{			  
			      alert("Approving Job Ad - TODO: Add transition loading screen..");
				  adminApprove(jobAd.attr("jobAdId"));
			      //TODO: implement transition loading screen - UI
			  }
		  }).appendTo(approveButton);
		  
		  $("<button>Deny</button>").attr("id", id='td-denyButton').click(
					function() {
					  if(jobAd.attr("isApproved") == 0)
						  alert("This Job Ad has not yet been approved");
					  else{			  
					      alert("Denying Job Ad - TODO: Add transition loading screen..");
					      adminDeny(jobAd.attr("jobAdId"));
					      //TODO: implement transition loading screen - UI
					  }
		  }).appendTo(denyButton);
		  
		  $("<button>Delete</button>").attr("id", id='td-deleteButton').click(
					function() {
						  alert("This Job Ad will be permanently deleted! (TODO: Add Checks)");
						  adminDeleteJobAd(jobAd.attr("jobAdId"));
					      //TODO: implement transition loading screen - UI
					  }
		  ).appendTo(deleteButton);
		  
		  
		  
		  //TODO: get the tabs working with approve/deny/delete
		  //$.fn.DynaSmartTab.floatingTool(tr, jobAd.attr("jobAdId"));
		  tr.appendTo(tbody);
		  
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}




/************************************************************************************************
 * 					BUILD TABLE FOR OWNER's AD LIST
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildOwnerAdTb(targetXMLTag, outputDiv){
	var tbody  = $("tbody", "#"+outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
		$("#"+outputDiv).hide();
		$("<h2 class='info'></h2>").html("You Have Not Yet Posted Anything").insertBefore("#"+outputDiv);
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var tr = $('<tr></tr>');
		  $('<td></td>').addClass('td-pDate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-title').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').addClass('td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  $('<td></td>').addClass('td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr);
		  $('<td></td>').addClass('td-loc').text(jobAd.children("location").attr("address")).appendTo(tr);
		  $('<td></td>').addClass('td-status').text(jobAd.attr("status")).appendTo(tr);
		  
		  $.fn.DynaSmartTab.floatingTool(tr, jobAd.attr("jobAdId"));
		  tr.appendTo(tbody);
		  
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
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
		  $('<td></td>').attr("id", id='td-loc').text(jobAd.children("location").attr("address")).appendTo(tr);
		  
		  tr.appendTo(tbody);
		  $("#td-title", tr).click(function(){
			  getJobAdById("detail", jobAd.attr("jobAdId"),'adDetailTable');
			 });
		});
		 
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}


/********************************************************************************************************************
 * 						Build a table for searcher profile editing
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildProfileSearcherEditTb(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", "#"+outputDiv).html("");
	var profile = $(targetXMLTag, xmlhttp.responseXML);
	
	if(profile.length==0){//if no results
		$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
	}
	else{
		var accountType = profile.attr("accountType");	
		var tbCell = $('<td></td>');
		var inputForm =$('<input/>');
		var enableEdBtn=$('<button></button>').attr({ type: 'button', 
													  onclick: 'enableProfileEdit('+accountType+')'
													  
													  });
		var editEmailButton 	= "<button id='editEmailButton' style='DISPLAY: none;' onclick='buildEditEmail(\"tbody\")' >Change Email</button>";
		var editPasswordButton	= "<button id=\"editPasswordButton\" style = \"DISPLAY: none;\" onclick=\"buildEditPassword()\">Change Password</button>";
		
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
		var addressResult = "<span type='text' id='locFeedback'></span>" +
							"<span id='resultTableTitle'></span> <table id='lookUpTable'></table>";
		

		var accountText = 
				"<tr><td>Your Account E-mail</td><td>"	+ profile.attr("email")						+ "</td><td></td></tr>" +
				"<tr id=newEmailRow 	 style='DISPLAY:none;'><td>New E-mail</td><td>"				+ " " + "</td><td><input id='emailNew' /></td></tr>" +
				"<tr><td>Your Backup E-mail</td><td>"	+ profile.attr("secondaryEmail") 			+ "</td><td></td></tr>" +
				"<tr id='secEmailRow' 	 style='DISPLAY:none;'><td>New Secondary E-mail</td><td>"	+ " " + "</td><td><input id='secondaryEmail'/></td></tr>" +
				"<tr id='oldPWRow' 		 style='DISPLAY:none;'><td>Old Password</td><td>"			+ " " + "</td><td><input id='passwordOld'   /></td></tr>" +
				"<tr id='newPWRow' 		 style='DISPLAY:none;'><td>New Password</td><td>"			+ " " + "</td><td><input id='passwordNew'   /></td></tr>" +
				"<tr id='repeatPWRow' 	 style='DISPLAY:none;'><td>Repeat Password</td><td>"		+ "	" + "</td><td><input id='passwordRepeat'/></td></tr>";

			
		var profileText =
			"<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td><td><input id='name'		    style = 'DISPLAY: none;' /></td></tr>" +
			"<tr><td>Your Phone Number</td><td>"	+ profile.attr("phone")				+ "</td><td><input id='phone' 			style = 'DISPLAY: none;' /></td></tr>" +
			
			"<tr><td>Your Self Description</td><td>"+ profile.attr("selfDescription")	+ "</td><td><textarea id='selfDescription' rows='10' cols='50' style = 'DISPLAY: none;'/><br/><span id='descInfo'></span></td></tr>";
		
		profileText +=
			"<tr><td>Your Education Level</td><td>"			+ profile.attr("educationFormatted")	+ "</td><td>" + educationLevelSelection + "</td></tr>" +
			"<tr><td>Your Employment Preference</td><td>"	+ profile.attr("employmentPreference")	+ "</td><td>" + employmentPrefSelection + "</td></tr>" +
			"<tr><td>You're Available From</td><td>"		+ profile.attr("startingDateFormatted")	+ "</td><td><input id='startingDate' style = 'DISPLAY: none;'/> TODO: yyyy/mm/dd (add date picker?) </td></tr>";

		
		//Add Address Input Field
		var address = profile.find("location").attr("address");		
			
		profileText += 
			"<tr><td>Your Address</td>  <td>"+address +"</td>  <td><input id='loc-filed' style='DISPLAY:none;'/></td></tr>" +
			"<tr><td></td><td></td><td>" + addressButton + "</td></tr>" +
			"<tr><td></td><td></td><td>" + addressResult + "</td></tr>";
			
			
		//Add buttons 
		buttonHTML = 
			"<tr>" +
				"<td><button id=\"enableAccountEditButton\" type=\"button\" onclick=\'enableAccountEdit(\""+accountType+"\")'>Change E-mail and Password</button></td>" +
				"<td></td><td><button id='submitAccountButton' style = 'DISPLAY: none;' onclick=\'submitChangeAccount()'>Submit</button></td>" +
			"</tr>"+
			"<tr>" +
				"<td><button id=\"enableProfileEditButton\" type=\"button\" onclick=\'enableProfileEdit(\""+accountType+"\")'>Change Profile Fields</button></td>" +
				"<td></td><td><button id='submitProfileButton' style = 'DISPLAY: none;' onclick=\'submitChangeProfile(\""+accountType+"\")'>Submit</button></td>" +
			"</tr>";
			
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
		$("#selfDescription").val(profile.attr("selfDescription"));
		$("#selfDescription").bind("keyup", function(){
			limitChars('selfDescription', 5000, 'descInfo');
		});
		$("#loc-filed").val(profile.attr("address"));
		
		
		$("#startingDate").val(profile.attr("startingDateFormatted"));
		//$("#empPrefSelectionDiv").show(); //TODO: make the check boxes check the correct submitted employment preference
		var empPref = profile.attr("employmentPreference");
		//$("input[id=partTimeCheck]").attr('checked');
		$("#educationLevel").val(profile.attr("educationLevel"));
	}
}

/******************************************************
 * 			build file list
 ******************************************************/
function buildSearcherFileTb(targetXMLTag, outputDiv){
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
		  var deleteAnchor =  "<a title='Delete' onclick='deleteSearcherFile(\"" + filename +"\")'" +
		  		" class='linkImg' style='float:right'><img src='../images/icon/delete_icon.png'/></a>";
		  var filenameCell = $('<td></td>');
		  filenameAnchor.appendTo(filenameCell);
		  filenameCell.appendTo(tr);
		  $('<td></td>').text(file.attr("size")).appendTo(tr);
		  tr.append(deleteAnchor);
		  
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
function buildProfileEditTb(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", "#"+outputDiv).html("");
	var profile = $(targetXMLTag, xmlhttp.responseXML);
	
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
		$("#selfDescription").val(profile.attr("selfDescription"));
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
			$("#feedback").html("<h2 class='error'>No Results Found. Profile.</h2>");
		}
		else{
			xmlObj.each(function() {//for All returned xml obj
			  var profileSearcherXML = $(this);
			  var tr =$('<tr></tr>');
			  $('<td></td>').attr("id", id='td-name').addClass('jsDetailBtn').text(profileSearcherXML.attr("name")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-eduReq').text(profileSearcherXML.attr("educationFormatted")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-loc').text(profileSearcherXML.children("location").attr("address")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-startDate').text(profileSearcherXML.attr("preferredStartDate")).appendTo(tr);
			  
			  tr.appendTo(tbody);
			  console.log(profileSearcherXML.attr("accountID"));
			  tr.delegate("#td-name", 'click', function(){
				  getProfileSearcherById("detail", profileSearcherXML.attr("accountID"), 'jsDetailTable', 'fileDiv');
			  });
//			  $("#td-name", tr).click(function(){
//				  getProfileSearcherById("detail", profileSearcherXML.attr("accountID"), 'jsDetailTable');
//				 });
			});
			 
			 $("tr:odd", tbody).addClass("oddRow");
			 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
		}
	}	
	
/***************************************************************
 * buildSearcherDetailTable(targetXMLTag, outputDiv)
 ****************************************************************/
	function buildSearcherDetailTable(targetXMLTag, outputDiv){
		outputDiv='jsDetailTable';
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
		  				"<tr><td>Education Level</td><td>"				+ profile.attr("educationFormatted") 				+ "</td></tr>" +
		  				"<tr><td>Preferred Start Date</td><td>"			+ profile.attr("preferredStartDate")			+ "</td></tr>" +
		  				"<tr><td>Location</td><td>"						+ profile.children("location").attr("address")	+ "</td></tr>" +
		  				"<tr><td>Self Description</td><td>"				+ profile.attr("selfDescription")				+ "</td></tr>" +
		  				"<tr class='clean'></tr>";
		  
		    $(tbody).append(rowText);
		 	$(tbody).find('tr').find('td:first').addClass("nameCol");
		 	$(tbody).find('tr').find('td:last').addClass("dataCol");
		 	$.fn.smartLightBox.closeLightBox(0, $("#"+outputDiv).parent(".subFrame").attr('id'));
		 	fb.hide();
		}
	}

	
	
	
	
	
/**************************
 * DEPRECATED FUNCTIONS:
 **************************/
//	/********************************************************************************************************************
//	 * 						Build a table for profile
//	 * @param targetXMLTag
//	 * @param outputDiv
//	 * @param heading
//	 *********************************************************************************************************************/
//	function buildProfileTb(targetXMLTag, outputDiv, heading){
//		var tbody  = $( "tbody", "#" + outputDiv).html("");
//		var profile = $(targetXMLTag, xmlhttp.responseXML);
//		if(profile.length==0){//if no results
//			$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
//		}
//		else{
//			
//			switch(profile.attr("accountType")){
//			
//			case("searcher"):
//				
//			  $("#"+heading).html( profile.attr("name") + "'s Profile");
//			  var rowText = "<tr><td>Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
//			  				"<tr><td>Secondary Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
//			  				"<tr><td>Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td></tr>" +
//			  				"<tr><td>Employer Preference</td><td>"  + profile.attr("empPref") 			+ "</td></tr>" +
//			  				"<tr><td>Location</td><td>"		        + profile.attr("address")			+ "</td></tr>" +
//			  				"<tr><td>Starting Date</td><td>"        + profile.attr("startingDate")		+ "</td></tr>" +
//			  				"<tr><td>Self Description</td><td>"		+ profile.attr("selfDescription")	+ "</td></tr>";
//			  break;
//			  
//			case "poster":
//				
//				$("#"+heading).html( profile.attr("name") + "'s Profile");
//				  var rowText = "<tr><td>Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
//				  				"<tr><td>Secondary Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
//				  				"<tr><td>Location</td><td>"		    + profile.attr("address")			+ "</td></tr>" +
//				  				"<tr><td>Self Description</td><td>"	+ profile.attr("selfDescription")	+ "</td></tr>";
//				
//			  break;
//			  
//			default:
//				$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
//				break;
//			
//			}
//			 $(tbody).append(rowText);
//			 $(tbody).find('tr').find('td:first').addClass("nameCol");
//			 $(tbody).find('tr').find('td:last').addClass("dataCol");
//			 $("#detailFB").hide();
//		}
//	}
//	



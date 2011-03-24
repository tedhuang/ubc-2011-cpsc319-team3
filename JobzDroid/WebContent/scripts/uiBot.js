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
//		switch (mode){
//			case "detail":
				
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
 * 						Build a table for profile
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildProfileTb(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", outputDiv).html("");
	var profile = $(targetXMLTag, xmlhttp.responseXML);
	if(profile.length==0){//if no results
		$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
	}
	else{
		
		switch(profile.attr("accountType")){
		
		case("searcher"):
			
		  $(heading).html( profile.attr("name") + "'s Profile");
		  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
		  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
		  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td></tr>" +
		  				"<tr><td>Your Job Type</td><td>"		+ profile.attr("empPref") 			+ "</td></tr>" +
		  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td></tr>" +
		  				"<tr><td>You're Available From</td><td>"+ profile.attr("startingDate")		+ "</td></tr>" +
		  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td></tr>";
		  break;
		  
		case "poster":
			
			$(heading).html( profile.attr("name") + "'s Profile");
			  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td></tr>" +
			  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td></tr>" +
			  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td></tr>" +
			  				"<tr><td>Your Job Type</td><td>"		+ profile.attr("empPref") 			+ "</td></tr>" +
			  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td></tr>" +
			  				"<tr><td>You're Available From</td><td>"+ profile.attr("startingDate")		+ "</td></tr>" +
			  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td></tr>";
			
		  break;
		  
		default:
			$("#profileFB").html("<h2 class='error'>Oops, you are looking at something does not exist</h2>");
			break;
		
		}
		 $(tbody).append(rowText);
		 $(tbody).find('tr').find('td:first').addClass("nameCol");
		 $(tbody).find('tr').find('td:last').addClass("dataCol");
		 $("#detailFB").hide();
	}
}


/********************************************************************************************************************
 * 						Build a table for profile editing
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildProfileEditTb(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", outputDiv).html("");
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
			
			"<tr><td>Your Self Description</td><td>"+ profile.attr("selfDescription")	+ "</td><td><textarea id='selfDescription' rows='4' cols='20' style = 'DISPLAY: none;'/></td></tr>";
		

	
		//Add Searcher Fields
		if( accountType == "searcher"){	
			profileText +=
				"<tr><td>Your Education Level</td><td>"			+ profile.attr("educationFormatted")	+ "</td><td>" + educationLevelSelection + "</td></tr>" +
				"<tr><td>Your Employment Preference</td><td>"	+ profile.attr("employmentPreference")	+ "</td><td>" + employmentPrefSelection + "</td></tr>" +
				"<tr><td>You're Available From</td><td>"		+ profile.attr("startingDateFormatted")	+ "</td><td><input id='startingDate' style = 'DISPLAY: none;'/> TODO: yyyy/mm/dd (add date picker?) </td></tr>";
		}
		
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
	$("#newEmailRow").show();
	$("#secEmailRow").show();
	$("#oldPWRow").show();
	$("#newPWRow").show();
	$("#repeatPWRow").show();
	$("#submitAccountButton").show();
	
	//Hide Edit Profile Fields
	$("#name").hide();
	$("#phone").hide();
	$("#educationLevel").hide();
	$("#selfDescription").hide();
	$("#loc-filed").hide();
	$("#addressButton").hide();
	
	if(accountType == "searcher"){
		$("#startingDate").hide();
		$("#empPrefSelectionDiv").hide(); 
	}
	$("#submitProfileButton").hide();
	
}



/********************************************************************************************************************
 * 						Enable Profile Edit
 *********************************************************************************************************************/
function enableProfileEdit(accountType)
{
	$("#name").show();
	$("#phone").show();
	$("#selfDescription").show();
	$("#loc-filed").show();
	$("#addressButton").show();
	
	if(accountType == "searcher"){
		$("#startingDate").show();
		$("#empPrefSelectionDiv").show(); 
		$("#educationLevel").show();
	}
	$("#submitProfileButton").show();
	
	//Hide Account Fields
	$("#secEmailRow").hide();
	$("#newEmailRow").hide();
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
			  $('<td></td>').attr("id", id='td-eduReq').text(profileSearcherXML.attr("educationLevel")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-loc').text(profileSearcherXML.children("location").attr("address")).appendTo(tr);
			  $('<td></td>').attr("id", id='td-startDate').text(profileSearcherXML.attr("preferredStartDate")).appendTo(tr);
			  
			  tr.appendTo(tbody);
			  $("#td-name", tr).click(function(){
				  buildProfileTb(targetXMLTag, outputDiv, heading);
				 });
			});
			 
			 $("tr:odd", tbody).addClass("oddRow");
			 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
		}
	}	
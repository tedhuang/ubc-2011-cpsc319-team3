/********************************************************************************************************************
 * 						Build a table for ad detail
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildDetailTable(mode, targetXMLTag, outputDiv){
	
	var jobAd = $(targetXMLTag,xmlhttp.responseXML);
	if(jobAd.length==0){//if no results
		$("#detailFB").html("<h2 class='error'>Oops, you are looking at something not does not exist</h2>");
	}
	else{
		switch (mode){
			case "detail":
				
				var tbody  = $( "tbody", outputDiv).html("");
				$('.heading', "#"+outputDiv).text(jobAd.attr("jobAdTitle"));//TODO FIX the HEADING
				var rowText = "<tr><td>Date Posted</td><td>" 					+ jobAd.attr("creationDateFormatted") 			+ "</td></tr>" +
			  				"<tr><td>Location</td><td>"						+ jobAd.children("location").attr("address")	+ "</td></tr>" +
			  				"<tr><td>Minimal Degree Requirement</td><td>"	+ jobAd.attr("eduReqFormatted")					+ "</td></tr>" +
			  				"<tr><td>Job Type</td><td>"						+ jobAd.attr("jobAvail") 						+ "</td></tr>" +
			  				"<tr><td>Starting Date</td><td>"				+ jobAd.attr("startingDateFormatted")			+ "</td></tr>" +
			  				"<tr><td>Contact Info</td><td>"					+ jobAd.attr("contactInfo")						+ "</td></tr>" +
			  				"<tr><td>Job Description</td><td>"				+ jobAd.attr("jobAdDescription")				+ "</td></tr>" +
			  				"<tr class='clean'></tr>" +
			  				"<tr><td>Tags</td><td>"							+ jobAd.attr("tags")							+ "</td></tr>" ;
			  
			    $(tbody).append(rowText);
			 	$(tbody).find('tr').find('td:first').addClass("nameCol");
			 	$(tbody).find('tr').find('td:last').addClass("dataCol");
			 	$("#detailFB").hide();
			 	break;
		 	
			case "edit":
				$("input[name='title-field']", "#"+outputDiv).val(jobAd.attr("jobAdTitle"));
				$("input[name='company-field']", "#"+outputDiv).val(jobAd.attr("contactInfo"));
				$("input[name='tag-field']", "#"+outputDiv).val(jobAd.attr("tags"));
				$("input[name='desc-field']", "#"+outputDiv).val(jobAd.attr("jobAdDescription"));
//				$("input[name='edu-field']", "#"+outputDiv).val(jobAd.attr("eduReqFormatted"));//TODO FIX
				$("input[name='startTime-field']", "#"+outputDiv).val(jobAd.attr("startingDateFormatted"));
				$("input[name='expireTime-field']", "#"+outputDiv).val(jobAd.attr("expireDateFormatted"));
//				$("input[name='']", "#"+outputDiv).val(jobAd.attr(""));
//				$("input[name='']", "#"+outputDiv).val(jobAd.attr(""));
				break;
		}
	}
}

/************************************************************************************************
 * 					BUILD TABLE FOR OWNER's AD LIST
 * INSERT RETURNED DATA INTO THE TABLE
 * @param xmlObj: THE xmlObject name returned from the server
 * @param outputDiv: The DIV where the table is held
**************************************************************************************************/
function buildOwnerAdTb(targetXMLTag, outputDiv){
	var tbody  = $("tbody", outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
		$("#feedback").html("<h2 class='error'>No Results Found</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var tr = $('<tr></tr>');
		  $('<td></td>').attr("id", id='td-pDate').text(jobAd.attr("creationDateFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-title').text(jobAd.attr("jobAdTitle")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-eduReq').text(jobAd.attr("eduReqFormatted")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-jobAvail').text(jobAd.attr("jobAvail")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-loc').text(jobAd.children("location").attr("address")).appendTo(tr);
		  $('<td></td>').attr("id", id='td-status').text(jobAd.attr("status")).appendTo(tr);
		  
		  makeEdTool(tr,jobAd.attr("jobAdId"));
		  tr.appendTo(tbody);
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}
function makeEdTool(tRow, adId){
	var tool= $('<span></span>').addClass('edTool');
	$('<a></a>').addClass('jsBtn').addClass('view').text('view | ').appendTo(tool);
	$('<a></a>').addClass('jsBtn').addClass('edit').text('edit | ').appendTo(tool);
	$('<a></a>').addClass('jsBtn').addClass('del').text('Delete').appendTo(tool);
	tRow.hover(function() {
        tool.animate({opacity: "show", left: "-90"}, "slow");
    }, function() {
        tool.animate({opacity: "hide", left: "-100"}, "fast");
    });
	tool.appendTo(tRow);
	$.fn.DynaSmartTab.bindEdTool(tRow, adId);
}

function filterTable(filter, tbContainerId){
	var tbody  = $("tbody", tbContainerId);
	var rows   = $('tr', tbody).get();
	$(rows).each(function(){
		if($('#td-status', $(this)).html() != filter){
			$(this).hide();
		}
	});
}

function resetFields(formContainer){
	
	var temp = $(':input', "#"+formContainer);
	if(temp.length >0){//TODO ALERT USER TO CONFIRM
		
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
	var tbody  = $("tbody", outputDiv).html("");
	var xmlObj = $(targetXMLTag,xmlhttp.responseXML);
	if(xmlObj.length==0){//if no results
		$("#feedback").html("<h2 class='error'>No Results Found</h2>");
	}
	else{
		xmlObj.each(function() {//for All returned xml obj
		  var jobAd = $(this);
		  var rowText = "<tr><td>"  + jobAd.attr("creationDateFormatted") + 
		  
		  				"</td><td id=\"td-title\">" + jobAd.attr("jobAdTitle") +
		  				"</td><td>" + jobAd.attr("contactInfo")  + 
		  				"</td><td>" + jobAd.attr("eduReqFormatted") + 
		  				"</td><td>" + jobAd.attr("jobAvail") +
		  				"</td><td>" + jobAd.children("location").attr("address")+
		  				"</td><td>" + "</td></tr>";
		  
		  $(rowText).appendTo(tbody);
		  $("#td-title").click(function(){
			  getJobAdById("detail", jobAd.attr("jobAdId"),adDetailTable);
			 });
		});
		 
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
}

//function viewDetail(mode, adId, outputDiv){
//	getJobAdById(mode, adId, outputDiv);
//}
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
		
		
		switch(accountType){
		
		case("searcher"):
			
		  $(heading).html( profile.attr("name") + "'s Profile");
		  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td>"+ "<td><input id=\"name\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td>"+ "<td><input id=\"secondaryEmail\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td>"+ "<td><input id=\"educationFormatted\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>Your Job Type</td><td>"		+ profile.attr("empPref") 			+ "</td>"+ "<td><input id=\"empPref\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td>"+ "<td><input id=\"address\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>You're Available From</td><td>"+ profile.attr("startingDate")		+ "</td>"+ "<td><input id=\"startingDate\" type=\"hidden\"/></td>"+"</tr>" +
		  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td>"+ "<td><input id=\"selfDescription\" type=\"text\"/></td>"+"</tr>" +
		  				"<tr><td><button id=\"enableEditButton\" type=\"button\" onclick=\'enableProfileEdit(\""+accountType+"\")'>Enable Edit Button</button></td>" + 
		  				"<td><button id=\"submitButton\" style = \"DISPLAY: none;\" onclick=\"submitChangeProfile()\">Submit</button></td></tr>";
		  break;
		  
		case ("poster"):
			
			$(heading).html( profile.attr("name") + "'s Profile");
			  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td>"+ "<td><input id=\"name\" type=\"hidden\"/></td>"+"</tr>" +
			  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td>"+ "<td><input id=\"secondaryEmail\" type=\"hidden\"/></td>"+"</tr>" +
			  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td>"+ "<td><input id=\"educationFormatted\" type=\"hidden\"/></td>"+"</tr>" +
			  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td>"+ "<td><input id=\"address\" type=\"hidden\"/></td>"+"</tr>" +
			  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td>"+ "<td><input id=\"selfDescription\" type=\"text\"/></td>"+"</tr>" +
			  				"<tr><td><button id=\"enableEditButton\" type=\"button\" onclick=\'enableProfileEdit(\""+accountType+"\")\'>Enable Edit Button</button></td>" + 
			  				"<td><button id=\"submitButton\" style = \"DISPLAY: none;\" onclick=\"submitChangeProfile()\">Submit</button></td></tr>";
			
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
 * 						Enable Profile Edit
 *********************************************************************************************************************/

function enableProfileEdit(accountType)
{
	switch(accountType)
	{
	case ("searcher"):
		document.getElementById("name").setAttribute("type", "text");
		document.getElementById("secondaryEmail").setAttribute("type", "text");
		document.getElementById("educationFormatted").setAttribute("type", "text");
		document.getElementById("empPref").setAttribute("type", "text");
		document.getElementById("address").setAttribute("type", "text");
		document.getElementById("startingDate").setAttribute("type", "text");
		document.getElementById("selfDescription").setAttribute("type", "text");
		break;
	
	case ("poster"):
		document.getElementById("name").setAttribute("type", "text");
		document.getElementById("secondaryEmail").setAttribute("type", "text");
		document.getElementById("educationFormatted").setAttribute("type", "text");
		document.getElementById("address").setAttribute("type", "text");
		document.getElementById("selfDescription").setAttribute("type", "text");
		break;
	}

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
/********************************************************************************************************************
 * 						Build a table for ad detail
 * @param targetXMLTag
 * @param outputDiv
 * @param heading
 *********************************************************************************************************************/
function buildDetailTable(targetXMLTag, outputDiv, heading){
	var tbody  = $( "tbody", outputDiv).html("");
	var jobAd = $(targetXMLTag,xmlhttp.responseXML);
	if(jobAd.length==0){//if no results
		$("#detailFB").html("<h2 class='error'>Oops, you are looking at something not does not exist</h2>");
	}
	else{
//		xmlObj.each(function() {//for All returned xml obj
//		  var jobAd = $(this);
		  $(heading).html(jobAd.attr("jobAdTitle"));
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
	}
}


/************************************************************************************************
 * 					BUILD TABLE FOR AD LIST
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
		  var rowText = "<tr><td>"  + jobAd.attr("creationDateFormatted") + 
		  
		  				"</td><td><span onclick='viewDetail("+jobAd.attr("jobAdId")+", adDetailTable,adDetailHeading )'>" + jobAd.attr("jobAdTitle") +
		  				"</span></td><td class='hide'>" +jobAd.attr("jobAdId")+
		  				
		  				"</td><td>" + jobAd.attr("contactInfo")  + 
		  				"</td><td>" + jobAd.attr("eduReqFormatted") + 
		  				"</td><td>" + jobAd.attr("jobAvail") +
		  				"</td><td>" + jobAd.children("location").attr("address")+
		  				"</td><td>" + "</td></tr>";
		  
		  $(rowText).appendTo(tbody);
		});
		 $("tr:odd", tbody).addClass("oddRow");
		 $("#feedback").html('<h2 class="good">Found '+ xmlObj.length +' Records</h2>');
	}
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
		  
		  				"</td><td><span onclick='viewDetail("+jobAd.attr("jobAdId")+", adDetailTable,adDetailHeading )'>" + jobAd.attr("jobAdTitle") +
		  				"</span></td><td class='hide'>" +jobAd.attr("jobAdId")+
		  				
		  				"</td><td>" + jobAd.attr("contactInfo")  + 
		  				"</td><td>" + jobAd.attr("eduReqFormatted") + 
		  				"</td><td>" + jobAd.attr("jobAvail") +
		  				"</td><td>" + jobAd.children("location").attr("address")+
		  				"</td><td>" + "</td></tr>";
		  
		  $(rowText).appendTo(tbody);
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
		
		switch(profile.attr("accountType")){
		
		case("searcher"):
			
		  $(heading).html( profile.attr("name") + "'s Profile");
		  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td>"+ "<input id=\"name\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td>"+ "<input id=\"secondaryEmail\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td>"+ "<input id=\"educationFormatted\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>Your Job Type</td><td>"		+ profile.attr("empPref") 			+ "</td>"+ "<input id=\"empPref\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td>"+ "<input id=\"address\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>You're Available From</td><td>"+ profile.attr("startingDate")		+ "</td>"+ "<input id=\"startingDate\" type=\"hidden\"/>"+"</tr>" +
		  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td>"+ "<input id=\"selfDescription\" type=\"text\"/>"+"</tr>" +
		  				"<button id=\"enableEditButton\" type=\"button\" onclick=\'enableProfileEdit()\'>Enable Edit Button</button>"
		  				"<button id=\"submitButton\" style = \"DISPLAY: none;\">Submit</button>";
		  break;
		  
		case "poster":
			
			$(heading).html( profile.attr("name") + "'s Profile");
			  var rowText = "<tr><td>Your Name</td><td>" 			+ profile.attr("name") 				+ "</td>"+ "<input id=\"name\" type=\"hidden\"/>"+"</tr>" +
			  				"<tr><td>Your Backup Email</td><td>"	+ profile.attr("secondaryEmail")	+ "</td>"+ "<input id=\"secondaryEmail\" type=\"hidden\"/>"+"</tr>" +
			  				"<tr><td>Your Degree</td><td>"			+ profile.attr("educationFormatted")+ "</td>"+ "<input id=\"educationFormatted\" type=\"hidden\"/>"+"</tr>" +
			  				"<tr><td>Your Location</td><td>"		+ profile.attr("address")			+ "</td>"+ "<input id=\"address\" type=\"hidden\"/>"+"</tr>" +
			  				"<tr><td>More About You</td><td>"		+ profile.attr("selfDescription")	+ "</td>"+ "<input id=\"selfDescription\" type=\"text\"/>"+"</tr>" +
			  				"<button id=\"enableEditButton\" type=\"button\" onclick=\'enableProfileEdit()\'>Enable Edit Button</button>" +
			  				"<button id=\"submitButton\" style = \"DISPLAY: none;\">Submit</button>";
			
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

/**
 * Javascript file for GoogleMaps functions
 */
var map;
var latitude, longitude, currAddrNum, address;
var geocoder = new google.maps.Geocoder(); 

$("document").ready(function(){
	$("#addressTable button").bind("click",calculateLocation);
	
	
	
});

/***
 * Displays the map centered around the given location. Also marks the given location.
 */
function showMap(LatLng, formatted_address) {
	var options = { 
			zoom: 16, 
			center: LatLng,
			mapTypeId: google.maps.MapTypeId.ROADMAP 
	}; 
	map = new google.maps.Map(document.getElementById("mapCanvas"), options);
	
	// add a marker to the map indicating the location of the address
	var marker = new google.maps.Marker({
		map: map,
		position: LatLng,
		title: formatted_address
	});
	google.maps.event.addListener(marker, 'click', function() {
		var contentString = "<div><a href='http://www.google.com' target='_blank'>test URL</a></div>";
		var infowindow = new google.maps.InfoWindow({     
			content: contentString
		}); 
		infowindow.open(map,marker);
	}); 
} 

/***
 * Query data from Google maps
 */
function calculateLocation() {
	var rowNumber = $(this).parent().parent().index();
	currAddrNum = rowNumber;
	
	//CHANGED THIS
	address = $("#loc-filed").val(); //$("#address"+rowNumber).text();
	//var address = $("#address"+rowNumber).text();
	
	if(address == ""){
		alert("Please input an address");
	}
	
	geocoder.geocode( {'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			// list results after receiving data
			listLocationChoices(results);
		}
		else {
			alert("Geocode was not successful for the following reason: " + status);
		}
	});   
}

/***
 * List results from Google maps, and bind functions to buttons in dynamically generated table elements
 */
function listLocationChoices(googleMapsResults){
	$("#resultTableTitle").text("Select a location:");
	$("#lookUpTable tr").remove();
	
	var i;
	for (i = 0; i < googleMapsResults.length; i++){
		var currentResult = googleMapsResults[i];
		$("#lookUpTable").append("<tr title='Click to view this location in GoogleMaps'>" +
				"<td><a href=''>" + currentResult.formatted_address + "</a></td>" +
				"<td><button type='button'>Save</button><td></tr>");
	}
	$("#lookUpTable a").bind("click", function(){
		var rowNumber = $(this).parent().parent().index();
		showMap(googleMapsResults[rowNumber].geometry.location, googleMapsResults[rowNumber].formatted_address);
		return false;
	});
	$("#lookUpTable button").bind("click", function(){
		var rowNumber = $(this).parent().parent().index();
		latitude = googleMapsResults[rowNumber].geometry.location.lat();
		longitude = googleMapsResults[rowNumber].geometry.location.lng();
		address = currentResult.formatted_address; 
		
		//$("#tmp"+currAddrNum).text(latitude + ", " + longitude);
		$("#longitude").text("Longitude: " + longitude);
		$("#latitude").text("Latitude: " + latitude);
		$("#locFeedback").text("Location saved: " + address);
		
	});
}

function getAddress(){
	return address;	
}

function getLongitude(){
	return longitude;
}

function getLatitude(){
	return latitude;
}





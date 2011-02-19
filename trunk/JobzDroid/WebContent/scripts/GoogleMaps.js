/**
 * Javascript file for GoogleMaps functions
 */
var map;
var geocoder = new google.maps.Geocoder(); 

$("document").ready(function(){
	$("#viewMapButton").bind("click",calculateLocation);
});

/***
 * Displays the map centered around the given location. Also marks the given location.
 */
function showMap(location) {
	var myOptions = { 
			zoom: 16, 
			center: location,
			mapTypeId: google.maps.MapTypeId.ROADMAP 
	}; 
	map = new google.maps.Map(document.getElementById("mapCanvas"), myOptions);
	
	// add a marker to the map indicating the location of the address
	var marker = new google.maps.Marker({
		map: map,
		position: location
	});
} 

/***
 * Converts address into latitude and longitude, and then calls showMap to display the map
 */
function calculateLocation() {
	var address = $("#address").text();
	geocoder.geocode( {'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			showMap(results[0].geometry.location);
		}
		else {
			alert("Geocode was not successful for the following reason: " + status);
		}
	});   
}
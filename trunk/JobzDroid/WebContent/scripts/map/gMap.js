

/**********GLOBAL FUNCTION GROUP************************************************************************/
var map, // global var to store the google map
	locPts=[], //list of locations
	userLoc = null, //for detecting user's location
	infoWin=new google.maps.InfoWindow({});//the infowindows of loc pt
	defaultLoc = new google.maps.LatLng(49.261226,-123.113927); // if can't set user loc, default loc to be Vancouver =)

 function initMap(mapDiv, dftLoc, setUserLoc){
		 var opts = {
		          zoom: 10,
		          center: dftLoc,
		          mapTypeId: google.maps.MapTypeId.ROADMAP
		 };
		 map=new google.maps.Map($("#"+mapDiv), opts);
		 setUserLoc? setUserLocation():map.setCenter(dftLoc);
		 return map;
	 }
 function setUserLocation(){//TODO ADD Company name to info win
	   var factory = null;
	   if(navigator.geolocation){//use w3c std to find loc
		 navigator.geolocation.getCurrentPosition(
				 function(position){//on success
				      userLoc=new google.maps.LatLng(position.latitude, position.longitude, location);
				      map.setCenter(userLoc);
				      blueIcon = "http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png";
				      
				      var maker = new google.maps.Marker({
				    	  position: userLoc, 
					      map: map, 
					      title: 'You are here',
						  icon: blueIcon,
						  zIndex: 0
				      });
				      $("#feedback").text("Locating Your Position...");
				 },
				 function() {//on fail
						$('#feedback').text("We cannot locate you on the earth, try Google Star maybe?");
				    });
	 	}//ENDOF W3C loc

	  // Firefox
	  else if (typeof GearsFactory != 'undefined') {
	    factory = new GearsFactory();
	  } 
	  else { // IE
	    try {
	      factory = new ActiveXObject('Gears.Factory');
	      // privateSetGlobalObject is only required and supported on WinCE.
	      if (factory.getBuildInfo().indexOf('ie_mobile') != -1) {
	        factory.privateSetGlobalObject(this);
	      }
	    } 
	    catch (e) {// Safari
	      if ((typeof navigator.mimeTypes != 'undefined')
	           && navigator.mimeTypes["application/x-googlegears"]) {
	        factory = document.createElement("object");
	        factory.style.display = "none";
	        factory.width = 0;
	        factory.height = 0;
	        factory.type = "application/x-googlegears";
	        document.documentElement.appendChild(factory);
	      }
	    }
	  }//ENDOF IE/SAFRI

	  // *Do not* define any objects if Gears is not installed. This mimics the
	  // behavior of Gears defining the objects in the future.
	  if (!factory) {
	    return;
	  }

	  // Now set up the objects, being careful not to overwrite anything.
	  //
	  // Note: In Internet Explorer for Windows Mobile, you can't add properties to
	  // the window object. However, global objects are automatically added as
	  // properties of the window object in all browsers.
	  if (!window.google) {
	    google = {};
	  }
/**************ENDOF INIT GOOGLE GEAR*******************************************************************/
	  if (!google.gears) {
	    google.gears = {factory: factory};
	  }
	  if (window.google && google.gears) {//LockNLoad
	  
		  var geo = google.gears.factory.create('beta.geolocation');
		    geo.getCurrentPosition(function(position) 
			{
				blueIcon = "http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png";
				userLoc = new google.maps.LatLng(position.latitude,position.longitude);
				map.setCenter(userLoc);
				var marker = new google.maps.Marker({
			      position: userLoc, 
			      map: map, 
			      title: 'You are here',
				  icon: blueIcon,
				  zIndex: 0
			});
			
			//addMarker(browserDetectedLocation,'You are here!');
			$('#feedback').text("Locating Your Position...");
	    }, function() {
			// error getting location, though supported
			$('#feedback').text("We cannot locate you on the earth, try Google Star maybe?");
	    });
	}//ENDOF GOOGLE GEAR SETUP 
	  
	else{// Browser doesn't support Geolocation
		$('#feedback').text("Crap! Browser Not Supported");
	}	
}
 
 function bindEvent(){
	 
 }
 
 function addMarker(pos, title, infoWindow){
	 var markerAdded=false;
	 var mark;
	 if(locPts.length){
		 duplicate = false;
		 	var markcopy;
			var markersCopy = [];
			while(markcopy=locPts.pop()){
				duplicate = (markcopy.position.lat()==pos.lat())&&(markcopy.position.lng()==pos.lng())?  true:false;
				markersCopy.push(markcopy);
			}
			locPts = markersCopy;
			if(duplicate==false){
				marker = new google.maps.Marker({
				  	position: pos,
				  	map: map,
					title: title
				});
				locPts.push(marker);
				mark = locPts.pop();
				
				google.maps.event.addListener(mark, 'click', function() {
					infoWindow.open(map,mark);
					var stringContent = infoWindow;
					infoWindow.setContent("<div id=\"infowin-overlay\""+stringContent+"</div>");

					overlayHeight = $('#infowin-overlay').height();
					overlayWidth = $('#infowin-overlay').width();
					$('#infowin-overlay').parent().css('height',overlayHeight);
					$('#infowin-overlay').parent().css('width',overlayWidth);
				});
				locPts.push(mark);
			}
		}
		else{
			marker = new google.maps.Marker({
			  	position: pos,
			  	map: map,
				title: title
			});
			locPts.push(marker);
			mark = locPts.pop();
			google.maps.event.addListener(mark, 'click', function() {
				infoWindow.open(map,mark);
				var stringContent = infoWindow;
				infoWindow.setContent("<div id=\"infowin-overlay\""+stringContent+"</div>");

				overlayHeight = $('#infowin-overlay').height();
				overlayWidth = $('#infowin-overlay').width();
				$('#infowin-overlay').parent().css('height',overlayHeight);
				$('#infowin-overlay').parent().css('width',overlayWidth);
			});
			locPts.push(mark);
		}
	}
 
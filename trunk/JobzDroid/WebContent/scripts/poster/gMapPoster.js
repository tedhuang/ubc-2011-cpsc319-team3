(function($){
	$.fn.smartGMap=function(setting){
		var setting = $.extend({}, $.fn.smartMap.defaults, setting);
		
		return this.each(function(){
			map=$(this);
			var map, mark;
			var markr[]; 
			var geocoder=new google.maps.geocoder();
			var beginLat=settings.dftLat, 
				beginLng=setting.dftLng;
			// these are the links to activate the map plotting
			var domAddressConverter = '#geocode_address';
			var domCoordsConverter = '#geocode_coords';

		
		initMap(map);
		
/**********************************************************************************************/
		function initMap(map){
		    var defaultLoc = new google.maps.LatLng(beginLat, beginLng);
		    var mapOptions = {
		      zoom : 15,
		      center : defaultLoc,
		      mapTypeId : google.maps.MapTypeId.ROADMAP
		    };
		    map = new google.maps.Map((map).get(0), mapOptions);
		    // call a method to place the marker in the current map
		    pinMarkr(defaultLoc);
		}
		
		function pinMarkr(loc, inputLat, inptLng){
			    // clear previous markers
			    if(markr){
			      for(i in markr){
			    	  markr[i].setMap(null);
			      }
			    }
			    var marker = new google.maps.Marker({
			      position : loc,
			      map : map,
			      draggable : true
			    });
			    map.setCenter(loc);
			    // extract lat and lng from LatLng location and put values in form
			    $("#"+inputLat).val(loc.lat());
			    $("#"+inputLng).val(loc.lng());
			    /*
			     * when marker is dragged, extract coordinates,
			     * change form values and proceed with geocoding
			     */
			    google.maps.event.addListener(marker, 'dragend', function(){
			      var coords = marker.getPosition();//get current position
			      $("#"+inputLat).val(coords.lat());
			      $("#"+inputLng).val(coords.lng());
			      geocodeCoords(coords);
			      map.setCenter(coords);
			    });
		}
		
		function geocodeLocation(address)
		  {
		    geocoder.geocode({'address' : address}, function(result, status){
		      // this returns a latlng
		      var location = result[0].geometry.location;
		      map.setCenter(location);
		      // replace markers
		      pinMarkr(location);
		    });
		  }
		  function geocodeCoords(coords)
		  {
		    geocoder.geocode({'latLng':coords}, function(result, status){
		      switch(status)
		      {
		        case 'ZERO_RESULTS':
		          alert('Map does not contain details for the given address');
		          break;
		        case 'ERROR':
		          alert('There was a problem in processing. Please try again later.');
		          break;
		        case 'OK':
		          $(inputAddress).val(result[1].formatted_address);
		          break;
		      }
		    });
		  }
		  
		  function validateAndPlot()
		  {
		    // handle geocoding of given address
		    $(domAddressConverter).click(function(e){
		      e.preventDefault();
		      if($(inputAddress).val().trim() == '')
		      {
		        alert('No address specified!');
		      }
		      else
		      {
		        geocodeLocation($(inputAddress).val());
		      }
		    });
		    // handle geocoding of coordinates
		    $(domCoordsConverter).click(function(e){
		      e.preventDefault();
		      if($(inputLat).val().trim() == '' ||
		        $(inputLng).val().trim() == '')
		      {
		        alert('No coordinates or incomplete coordinates specified');
		      }
		      else
		      {
		        var lat = $(inputLat).val();
		        var lng = $(inputLng).val();
		        var location = new google.maps.LatLng(lat, lng);
		        geocodeCoords(location);
		      }
		    });
		  }


		
		});
	};
	
	 // Defaults jQuery(this).animate({width: 'show'}); jQuery(this).animate({width: 'hide'});
    $.fn.smartGMap.defaults = {
    		
    		dftLat : 49.26518835849344,
    		dftLng : -123.24735386655271

          
    };
})(jQuery);
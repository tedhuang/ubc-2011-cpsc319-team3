/*
 * Smart Google Map
 * Author: Cheng Chen
 * Based On Google Map V3 API
 */
(function($){
	$.fn.smartMap=function(setting){
		var setting = $.extend({}, $.fn.smartMap.defaults, setting);
		
		
		return this.each(function(){
			mapTool=$(this);
			var mapCanvas= $($("#map_canvas"),mapTool).get(0); //the map canvas
			var geocoder;
			var map;
			var maxAddrNum=3; //max 3 location allowed
//			var marker;
			var locList=[];
			var markers=[];
			var dfltLat=setting.dftLat;
			var dfltLng=setting.dftLng;
			var addrMap={addr:"", city:"",province:"",country:"",zip:""};
			var displayMap = setting.displayMap;
			
			var infoWindow = new google.maps.InfoWindow({});
			if(displayMap){
				
			}
			else{
				initMap();
//				initAutoCplt();
			}
			
/**************FUNCTION GROUP STARTS HERE****************************************************************/			
	function initMap(){
		//MAP
		  var UBC = new google.maps.LatLng(dfltLat,dfltLng);
		  var options = {
		    zoom: 14,
		    center: UBC,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		  };
		  map = new google.maps.Map(mapCanvas, options);
		  //GEOCODER
		  geocoder = new google.maps.Geocoder();
	}//eof initMap
	
	function initSearchLocAutoCplt(inputBar){
		locSearchBar=domObjById(inputBar);
		locSearchBar.data({"city":"", "province":"", "country":"", "zip":""});//init data of the input
		
		var location=({city:"", province:"", country:""});
		var	citySN, provinceSN, countrySN, zipSN;
		var cityLN, provinceLN, countryLN, zipLN;
		var addr;
		
		geocoder = new google.maps.Geocoder();
		locSearchBar.autocomplete({
		      source: //use jquery UI auto complete function to get the address
		    	  
		    	function(request, response) {
		    	  //reset all fields
		    	  	location=({city:"", province:"", country:""});
		  			citySN="";provinceSN="";countrySN="";zipSN="";
		  			cityLN="";provinceLN="";countryLN="";zipLN="";
		  			addr="";
		  			
			        geocoder.geocode( {'address': request.term + ', CA' }, function(results, status) {
			          response($.map(results, function(item) {
			        	  
			        	  $.each(item['address_components'], function(index){
					    		$.each(item['address_components'][index]['types'], function(i){
					    			
					    			if(item['address_components'][index]['types'][i]=="locality"){
//					    				addrMap.city=item['address_components'][index]['short_name'];
					    				citySN=item['address_components'][index]['short_name'];
					    				location.city=item['address_components'][index]['long_name'];
					    			} //city
					    				
					    			else if(item['address_components'][index]['types'][i]=="administrative_area_level_1"){
//					    				addrMap.province=item['address_components'][index]['short_name'];
					    				provinceSN=item['address_components'][index]['short_name'];
					    				location.province=item['address_components'][index]['long_name'];
					    			} //province or state
					    				
					    			else if(item['address_components'][index]['types'][i]=="country"){
//					    				addrMap.country=item['address_components'][index]['short_name'];
					    				countrySN=item['address_components'][index]['short_name'];
					    				location.country=item['address_components'][index]['long_name'];
					    			}
					    			else if(item['address_components'][index]['types'][i]=="postal_code"){
//					    				addrMap.zip=item['address_components'][index]['short_name'];
					    				zipSN=item['address_components'][index]['short_name'];
					    				zipLN=item['address_components'][index]['long_name'];
					    			}
					    		});
					    	});
			        	  
			        	  $.each(location, function(){
				    			if(typeof this != 'undefined' && this!=""){
				    				addr+=this+",";
				    			}
				    		});
			        	  addr=addr.substring(0, (addr.length-1));
			            return {
			            	
//			            	cityLN+","+provinceLN+","+countryLN,
			              label:  	  addr,
			              value: 	  addr, // value displayed when selected 
			              latitude:   item.geometry.location.lat(),
			              longitude:  item.geometry.location.lng()
			            };
			          }));
			        });
		      },
		      select: //on select desired location set the fields
		    	  function(event, ui) { 
			        var location = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
			        $(this).data({
			        	"city"		: citySN, 
			        	"province"  : provinceSN, 
			        	"country"   : countrySN, 
			        	"zip"       : zipSN
			        });
			      }
			    });
	}
	$.fn.smartMap.initSearchAutoCplt=function(inputBar){
		initSearchLocAutoCplt(inputBar);
	};
	$.fn.smartMap.resize=function(){//fix map when the map is resized or position changed
		google.maps.event.trigger(map, 'resize');
	};
	
	$.fn.smartMap.displayMap=function(container, locObjArray){//display the a new map according to info
		var dispMap = $('<div></div>').addClass("displayMap").appendTo(container);
		var dispList = $('<ul></ul>').addClass("dispLocList").appendTo(dispMap);
		var mapPatch = $('<div></div>').addClass("dispMapPatch").appendTo(dispMap);
		var location = new google.maps.LatLng(dfltLat,dfltLng);
		  var options = {
		    zoom: 14,
		    center: location,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		  };
		  
		  map = new google.maps.Map(mapPatch.get(0), options);
		 
		
		$.each(locObjArray, function(i){
			lat=this.latlng.split(",")[0];
			lng=this.latlng.split(",")[1];
			var location = new google.maps.LatLng(lat,lng);
			placeMarkr(location);
			buildDisplayList(dispList, lat, lng, this.addr ,i+1);
		});
		
		
	};
	
	function placeMarkr(location){ //place display marker
//					var markr=new google.maps.Marker({
		var markr=new google.maps.Marker({
			position  : location,
			map		  : map,
			animation : google.maps.Animation.DROP
		});
		map.setCenter(location);
		return markr;
	}
	function buildDisplayList(dispList, lat, lng, addr, idx){
		var li=	$("<li></li>")
				.addClass("jsBtn")
				.data({
						"lat":lat,
						"lng":lng
					 })
				.delegate("span", "click", function(){
					 loc = new google.maps.LatLng(lat, lng);
			         map.panTo(loc);
				})
				.prepend('<span class="title">Location '+ idx +'</span>')
				.append('<span class="addr">'+addr+'</span>')
				.appendTo(dispList); //ENDOF ADD <li>
	}
	/*****************************************************************************************************		 
	 *   Get User Location FUNCTION GROUP
	 *****************************************************************************************************/
				function getUserLoc(){
					// try to get user location via W3C standard Geolocation in browsers or via Google Gears
					if(navigator.geolocation){
					    navigator.geolocation.getCurrentPosition(markUserLoc, handle_error);
					    
					} else if (google.gears) 
					// if location not found using W3C standard try with Google Gears if browser supports it
					{
					    var geo = google.gears.factory.create('beta.geolocation');
					    geo.getCurrentPosition(markUserLoc, handle_error);
					} 
					else{
						// Browser doesn't support Geolocation
						$('#feedback').text("Appearently Your Browser Doesn't Support This function.");
					}	
				}
				
				function markUserLoc(position){//mark user's position on the map
					blueIcon = "http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png";
					userLoc = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);
					map.setCenter(userLoc);
					
					var marker = new google.maps.Marker({
					      position	: userLoc, 
					      map		: map, 
					      title		: 'You are here',
						  icon		: blueIcon,
						  draggable	: true,
						  zIndex	: 0
					});
					markers.push(marker);
					addDragListener.call(marker);
					
					$('#feedback').text("Location detected. Please wait...");
					
					geocoder.geocode({'latLng': marker.getPosition()}, function(results, status) {//reverse searching
						if (status == google.maps.GeocoderStatus.OK) {
							if (results[0]) {
						          $('#addrBar').val(results[0].formatted_address);
						          $('#latitude').val(marker.getPosition().lat());
						          $('#longitude').val(marker.getPosition().lng());
						        }
						      }
						    });
			    }
				function handle_error(err) {
					  switch(err){
					  case 1:
						  $('#feedback').text("Sorry, you refused to share your location, request cannot be finished");
					  }
				}
	/**************ENDOF Get User Location FUNCTION GROUP***************************************************/
				
	/**************MARKER LISTENER FUNCTIONS****************************************************************/				
				function addDragListener(){
					var marker = this;
					var addr, lat, lng;
					var results;
					
					google.maps.event.addListener(marker, 'dragend', function() {
						geoCodeLocation.call(marker, marker.getPosition());
//						if(!hasDupAddr(addr)){
//							
//						}
//						else{
//							console.log("You already have that location");
//						}
					});
					if(locList[marker.get("id")]){
						console.log($(locList[marker.get("id")]).data("addr"));
					}
					
				}//ENDOF ADDDRAGLISTENER
				
				function addMakrClkLstnr(){
					google.maps.event.addListener(mark, 'click', function() {
						infoWindow.open(map,mark);
						var stringContent = m_infowindow;
						infoWindow.setContent("<div id=\"infowin-overlay\""+stringContent+"</div>");
		
						overlayHeight = $('#infowin-overlay').height();
						overlayWidth = $('#infowin-overlay').width();
						$('#infowin-overlay').parent().css('height',overlayHeight);
						$('#infowin-overlay').parent().css('width',overlayWidth);
					});
				}	
	/*********************HELPER METHODS**************************************************************************/			
				Array.prototype.remove = function(value) {  //remove elements from array
				    var rest = $.grep(this, function(item){    
				        return (item !== value); // <- You may or may not want strict equality
				    });

				    this.length = 0;
				    this.push.apply(this, rest);
				    return this; // <- This seems like a jQuery-ish thing to do but is optional
				};
				
		});//return THIS.EACH
	};//ENDOF SMARTMAP
	$.fn.smartMap.defaults = {
    		
    		dftLat : 49.26518835849344,
    		dftLng : -123.24735386655271, //UBC COORD
    		displayMap: false

          
    };
})(jQuery);
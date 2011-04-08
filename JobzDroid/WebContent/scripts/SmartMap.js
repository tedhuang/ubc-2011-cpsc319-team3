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
			var mapCanvasId= setting.mapCanvasId; //the map Canvas Id
			var geocoder;
			var map;
			var maxAddrNum=3; //max 3 location allowed
//			var marker;
			var locList=[];
			var markers=[];
			var dfltLat=setting.dftLat;
			var dfltLng=setting.dftLng;
			
			var mapCanvas= $(domObjById(mapCanvasId),mapTool).get(0); //the map canvas
			var addrMap={addr:"", city:"",province:"",country:"",zip:""};
			var displayMap = setting.displayMap;
			
			//var infoWindow = new google.maps.InfoWindow({});
			if(!displayMap){
				initMap();
//				initAutoCplt();
			}
			else{
//				initDisplayMap(domObjById(mapCanvasId));
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
	
	function hideAllMarkrs(clearMarkr) {// Removes the markers from the map, but keeps them in the array
		  if (markers) {
		    $(markers).each(function(){
		    	this.setMap(null);
		    });
		  }
		  clearMarkr? markers.length = 0 : null;
	}
	
	function initSearchLocAutoCplt(inputBar){
		locSearchBar=domObjById(inputBar);
		locSearchBar.data({"city":"", "province":"", "country":""});//init data of the input
		
		var location=({citySN:"", provinceSN:"", countrySN:"",
					   cityLN:"", provinceLN:"", countryLN:""});
		var	citySN, provinceSN, countrySN, zipSN;
		var itemAddr;
		var dispAddr;
		var selected;
		
		geocoder = new google.maps.Geocoder();
		locSearchBar.autocomplete({
				autoFocus: true,
				
		    	source: //use jquery UI auto complete function to get the address
		    	  
		    	 function(request, response) {
		    	  
			        geocoder.geocode( {'address': request.term }, function(results, status) {
			          response($.map(results, function(item) {
			        	//reset all fields
			        	location=({citySN:"", provinceSN:"", countrySN:"",
			        				cityLN:"", provinceLN:"", countryLN:""});
				  		citySN="";provinceSN="";countrySN="";zipSN="";
				  		itemAddr="";
				  		dispAddr="";
				  		selected=false;
				  			
			        	  $.each(item['address_components'], function(index){
					    		$.each(item['address_components'][index]['types'], function(i){
					    			
					    			if(item['address_components'][index]['types'][i]=="locality"){
//					    				addrMap.city=item['address_components'][index]['short_name'];
					    				location.citySN=item['address_components'][index]['short_name'];
					    				location.cityLN=item['address_components'][index]['long_name'];
					    			} //city
					    				
					    			else if(item['address_components'][index]['types'][i]=="administrative_area_level_1"){
//					    				addrMap.province=item['address_components'][index]['short_name'];
					    				location.provinceSN=item['address_components'][index]['short_name'];
					    				location.provinceLN=item['address_components'][index]['long_name'];
					    			} //province or state
					    				
					    			else if(item['address_components'][index]['types'][i]=="country"){
//					    				addrMap.country=item['address_components'][index]['short_name'];
					    				location.countrySN=item['address_components'][index]['short_name'];
					    				location.countryLN=item['address_components'][index]['long_name'];
					    			}
//					    			else if(item['address_components'][index]['types'][i]=="postal_code"){
//					    				addrMap.zip=item['address_components'][index]['short_name'];
//					    				zipSN=item['address_components'][index]['short_name'];
//					    				zipLN=item['address_components'][index]['long_name'];
//					    			}
					    		});
					    	});
			        	  
			        	  $.each(location, function(key, value){
				    			if(typeof value != 'undefined' && value!=""){
				    				if(key.match(/(\w+)LN/)){
				    					itemAddr+=value+",";
				    				}
				    				else if(key.match(/(\w+)SN/)){
				    					dispAddr+=value+",";
				    				}
				    				
				    			}
				    		});
			        	  itemAddr=itemAddr.substring(0, (itemAddr.length-1));
			        	  dispAddr=dispAddr.substring(0, (dispAddr.length-1));
			            return {
			            	
			              label:  	  itemAddr,
			              value: 	  dispAddr, // value displayed when selected
			              citySN:     location.citySN,	  
			              provinceSN: location.provinceSN,
			              countrySN:  location.countrySN
			            };
			          })); //eof geocode response
			        }); // eof geocode
		      }, //eof source making
		      
		      select: //on select desired location set the fields
		    	  function(event, ui) { 
			        var location = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
			        $(this).data({
			        	"city"		: ui.item.citySN, 
			        	"province"  : ui.item.provinceSN, 
			        	"country"   : ui.item.countrySN 
//			        	"zip"       : zipSN
			        });
			        selected=true;
			      }, //eof select listener
			      
		      close: 
		    	  function (event, ui){
		    	  if(!selected){
		    		  //reset data and clear cache if not selected 
		    		  locSearchBar.val("");
		    		  locSearchBar.data({"city":"", "province":"", "country":""});
		    	  }
		      }
		      
	 });//eof auto-complete
	}
	$.fn.smartMap.initSearchAutoCplt=function(inputBar){
		initSearchLocAutoCplt(inputBar);
	};
	$.fn.smartMap.resize=function(){//fix map when the map is resized or position changed
		google.maps.event.trigger(map, 'resize');
	};
	$.fn.smartMap.locationListener=function(hasLoc, containerObj){//fix map when the map is resized or position changed
		if(hasLoc){
			containerObj.show();
			$.fn.smartMap.resize();
		}	
		else{
			hideAllMarkrs(true);
			containerObj.hide();
		}
	};
	
	$.fn.smartMap.initDisplayMap=function(containerObj){//display the a new map according to info
//		containerObj.show();
		  var mapCanvas = $('<div></div>').addClass("dispMapCanvas");
		  var defaultLoc = new google.maps.LatLng(dfltLat,dfltLng);
		  var options = {
		    zoom: 10,
		    center: defaultLoc,
		    mapTypeId: google.maps.MapTypeId.ROADMAP
		  };
		  
		if(!$(".displayMap").length){
			var dispMap = $('<div></div>').addClass("displayMap");
			
			dispMap.appendTo(containerObj);
			mapCanvas.appendTo(dispMap);
			map = new google.maps.Map(mapCanvas.get(0), options);
		}
		else{
			hideAllMarkrs(true);
			map = new google.maps.Map(mapCanvas.get(0), options);
		}
		
	};
	
	$.fn.smartMap.buildJobListMap=function(td, lat, lng){
		var location = new google.maps.LatLng(lat,lng);
		var marker=addMarkr(location);
		typeof marker !==undefined ? 
				markers.push(marker)
				:
				null;
		td.bind("click", function(){ //when click on location col, pan to loc
	        map.panTo(location);
		});
	};
	
	$.fn.smartMap.adDetailMapDisplay=function(container, locObjArray){//display map of ad detail according to info
		var dispMap = $('<div></div>').addClass("displayMap").appendTo(container);
		var dispList = $('<ul></ul>').addClass("dispLocList").appendTo(dispMap);
		var mapPatch = $('<div></div>').addClass("dispMapCanvas").appendTo(dispMap);
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
	function addMarkr(location) {//For multiple work locations
			var markr = new google.maps.Marker({
			    position 	: location,
			    map		 	: map
			  });
			  markers.push(markr);
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
    		displayMap: false,
    		mapCanvasId: "map_canvas"
          
    };
})(jQuery);
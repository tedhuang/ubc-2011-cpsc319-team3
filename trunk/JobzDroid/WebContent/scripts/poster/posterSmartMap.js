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
				initAutoCplt();
			}
//			getUserLoc();
			
			
			
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
				        
				}
			
			function addMarkr(location) {//For multiple work locations
				if(markers.length>=maxAddrNum){
					return false;
				}
				else{
					var markr = new google.maps.Marker({
					    position 	: location,
					    map		 	: map,
					    animation   : google.maps.Animation.DROP,
					    draggable	: true
					  });
					  markers.push(markr);
					  map.setCenter(location);
					  markr.set("id", $.inArray(markr, markers ));
					  addDragListener.call(markr);
					  return markr;
				}
			}
			
			function addToLocList(addr,list, marker){
				var list="#"+list;
				
				var li=	$("<li></li>")
						.addClass("jsBtn")
						.data({"marker":marker.get("id")
//								"addr" : addr,
//								"lat"  : marker.getPosition().lat(),
//								"lng"  : marker.getPosition().lng(),
//								"latlng":marker.getPosition().lat()+","+marker.getPosition().lng()
								})
						.click(function(){
							 loc = new google.maps.LatLng($(this).data("lat"), $(this).data("lng"));
					         map.panTo(loc);
						})
						.bind('udMarkrId', function(event, markerId){
							$(this).data("marker", markerId);
						})
						.bind('update', function(event, fulladdr, lat, lng, addrMap){
							
							if(hasDupAddr.call(this,addr)){
							  console.log("Duplicated Address!");
							  $(this).fadeOut(2000).remove();
							  locList.remove($(this).parent().get(0));
							  rmMarkr.call(marker);
							 }
							else{ //update the loc info
								$(this).find('span.addr').text(fulladdr);
								$(this).data({
											  "latlng"  :lat+","+lng,
											  "addr"    :fulladdr,
											  "city"    :addrMap.city,
											  "province":addrMap.province,
											  "country" :addrMap.country,
											  "zip"		:addrMap.zip,
											  "lat"     : lat,
											  "lng"     : lng
											  });
								    console.log($(this).data("province"));
							}
						})
						.append('<span class="jsBtn rm">Remove</span>')
						.delegate('span.rm','click',function(){
							var lis=$(this).parent().siblings().get();
							locList.remove($(this).parent().get(0));
							$(this).parent().remove();
							$.each(lis, function(){//update the location number
								$(this).find("span.title").text("Location "+($.inArray($(this).get(0), locList)+1));
							});
							rmMarkr.call(marker);
						})
						.append('<span class="addr">'+addr+'</span>')
						.appendTo(list); //ENDOF ADD <li>
				
				locList.length>maxAddrNum? null:locList.push(li.get(0));//get(0) needed to compare raw object
				li.prepend('<span class="title">Location '+ ($.inArray(li.get(0), locList)+1) +'</span>');
				addDragListener.call(marker);

			}
			function rmMarkr(){ //remove marker from the map
				if(this){
					this.setMap(null);
				}
				markers.remove(this);
			}
			function initAutoCplt(){
				$("#addrBar").autocomplete({
				      source: //use jquery UI auto complete function to get the address
				    	function(request, response) {
					        geocoder.geocode( {'address': request.term + ', CA' }, function(results, status) {
					          response($.map(results, function(item) {
					        	  
					        	  $.each(item['address_components'], function(index){
							    		$.each(item['address_components'][index]['types'], function(i){
							    			
							    			if(item['address_components'][index]['types'][i]=="locality"){
//							    				addrMap.city=item['address_components'][index]['short_name'];
							    				theCity=item['address_components'][index]['short_name'];
							    			} //city
							    				
							    			else if(item['address_components'][index]['types'][i]=="administrative_area_level_1"){
//							    				addrMap.province=item['address_components'][index]['short_name'];
							    				theProvince=item['address_components'][index]['short_name'];
							    			} //province or state
							    				
							    			else if(item['address_components'][index]['types'][i]=="country"){
//							    				addrMap.country=item['address_components'][index]['short_name'];
							    				theCountry=item['address_components'][index]['short_name'];
							    			}
							    			else if(item['address_components'][index]['types'][i]=="postal_code"){
//							    				addrMap.zip=item['address_components'][index]['short_name'];
							    				theZip=item['address_components'][index]['short_name'];
							    			}
							    		});
							    	});
					            return {
					              label:  	  item.formatted_address,
					              value: 	  item.formatted_address,
					              latitude:   item.geometry.location.lat(),
					              longitude:  item.geometry.location.lng(),
					              city:		  theCity,
					              province:   theProvince,
					              country:    theCountry,
					              zip:        theZip
					            };
					          }));
					        });
				      },
				      select: //on select desired location set the fields
				    	  function(event, ui) { 
					        $("#latitude").val(ui.item.latitude);
					        $("#longitude").val(ui.item.longitude);
					        var location = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
					        if(chkDuplicateLoc(location)){
						        var markr=addMarkr(location);
						        markr? addToLocList(ui.item.value, "locList", markr):null;
						        addrMap.city=ui.item.city;
						        addrMap.province=ui.item.province;
						        addrMap.country=ui.item.country;
						        addrMap.zip=ui.item.zip;
//						        geoCodeLocation.call(markr, markr.getPosition());
						        var li = locList[markr.get("id")];
							    $(li).trigger("update", [ui.item.value, ui.item.latitude, ui.item.longitude, addrMap]);
					        }
					      }
					    });
			}
			
			function hideAllMarkrs(clearMarkr) {// Removes the markers from the map, but keeps them in the array
				  if (markers) {
				    $(markers).each(function(){
				    	this.setMap(null);
				    });
				  }
				  clearMarkr? markers.length = 0 : null;
				}

				function showAllMarkrs() {// Shows any overlays currently in the array
				  if (markers) {
					  $(markers).each(function(){
						  this.setMap(map);
					  });
				  }
				}
				
				function chkDragDuLoc(latlng){//check for duplicated addr
					var duLoc;
					var markr =this;
					markers.length==0? duLoc = true :
						$.each(markers, function(){
							if(this.get("id") != markr.get("id")){
								if(this.getPosition().equals(latlng)){ //if not compared to itself and duplicated location
								duLoc = false;
								return false;
							}
							else{
								duLoc = true;
								return true;
							}
						}
						else{
							duLoc = true;
							return true;
						}
					});
					return duLoc;
				}
				function chkDuplicateLoc(latlng){//check for duplicated addr
					var duLoc;
					markers.length==0? duLoc = true :
						$.each(markers, function(){
							if(this.getPosition().equals(latlng)){ //if not compared to itself and duplicated location
								duLoc = false;
								return false;
							}
							else{
								duLoc = true;
								return true;
							}
					});
					return duLoc;
				}
				function hasDupAddr(addr){//check for duplicated addr
					var duAddr;
					var thisAddr=this;
					locList.length==0? duAddr = false :
						$.each(locList, function(){
						if(this!= thisAddr){
							if($(this).data("addr")==addr){ //if not compared to itself and duplicated location
								duAddr = true;
								return false;
							}
							else{
								duAddr = false;
								return true;
							}
						}
						else{
							duAddr=false;
						}
					});
					return duAddr;
				}
				
				function dragingGeoCoding(markr, pos) { 
					var markr=this;
					geocoder.geocode(
				   { 
					    latLng: pos 
				   }, 
				  function(results, status) { 
					  var city = "N/A";
					  
				    if (status == google.maps.GeocoderStatus.OK && results.length > 0) { 
				    	
				    	  addr = results[0].formatted_address,
					  	  lat  = markr.getPosition().lat(),
					  	  lng  = markr.getPosition().lng();
					 
						  $('#addrBar').val(addr);
				          $('#latitude').val(lat);
				          $('#longitude').val(lng);
					      var li = locList[markr.get("id")];
					      $(li).trigger("update", [addr, lat, lng]);
				          
				    } 
				    else if( status == google.maps.GeocoderStatus.ZERO_RESULTS){ 
				    	  addr = "UNKNOWN",
					  	  lat  = dfltLat,
					  	  lng  = dfltLng;
					 
						  $('#addrBar').val(addr);
				          $('#latitude').val(lat);
				          $('#longitude').val(lng);
				          var li = locList[markr.get("id")];
				          $(li).trigger("update", [addr, lat, lng]);
				    } 
				  }); 
				}
				
				function geoCodeLocation(pos) { 
					var markr=this;
					  geocoder.geocode({ 
					    latLng: pos 
					  }, function(results, status) { 
						  var city = "N/A";
						  
					    if (status == google.maps.GeocoderStatus.OK && results.length > 0) { //extract elements from the resultset
					    	$.each(results[0]['address_components'], function(index){
					    		$.each(results[0]['address_components'][index]['types'], function(i){
					    			
					    			if(results[0]['address_components'][index]['types'][i]=="locality"){
					    				addrMap.city=results[0]['address_components'][index]['short_name'];
					    			} //city
					    				
					    			else if(results[0]['address_components'][index]['types'][i]=="administrative_area_level_1"){
					    				addrMap.province=results[0]['address_components'][index]['short_name'];
					    			} //province or state
					    				
					    			else if(results[0]['address_components'][index]['types'][i]=="country"){
					    				addrMap.country=results[0]['address_components'][index]['short_name'];
					    			}
					    			else if(results[0]['address_components'][index]['types'][i]=="postal_code"){
					    				addrMap.zip=results[0]['address_components'][index]['short_name'];
					    			}
					    		});
					    	});//ENDOF MAKING ADDRESS DICT
					    	  var fulladdr = results[0].formatted_address,
					    	  	  lat  = markr.getPosition().lat(),
						  	      lng  = markr.getPosition().lng();
						 
							  $('#addrBar').val(fulladdr);
					          $('#latitude').val(lat);
					          $('#longitude').val(lng);
					          var li = locList[markr.get("id")];
					          $(li).trigger("update", [fulladdr, lat, lng, addrMap]);
					          
					    } 
					    else if( status == google.maps.GeocoderStatus.ZERO_RESULTS){ 
					    	  addr = "UNKNOWN",
						  	  lat  = dfltLat,
						  	  lng  = dfltLng;
						 
							  $('#addrBar').val(addr);
					          $('#latitude').val(lat);
					          $('#longitude').val(lng);
					          var li = locList[markr.get("id")];
					          $(li).trigger("update", [addr, lat, lng]);
					    } 
					  }); 
				}
				$.fn.smartMap.resize=function(){//fix map when the map is resized or position changed
					google.maps.event.trigger(map, 'resize');
				};
				
				$.fn.smartMap.adDetailMapDisplay=function(container, locObjArray){//display the a new map according to info
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
				function addDragEndListener(){
					var marker = this;
					var addr, lat, lng;
					
					google.maps.event.addListener(marker, 'dragend', function() {
						if(!hasDupAddr(addr)){
							
						}
						else{
							console.log("You already have that location");
						}
					});
				}
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
				
				jQuery.fn.allData = function() { //get the data stored in the cache
				    var intID = jQuery.data(this.get(0));
				    return(jQuery.cache[intID]);
				};
				
		});//return THIS.EACH
	};//ENDOF SMARTMAP
	$.fn.smartMap.defaults = {
    		
    		dftLat : 49.26518835849344,
    		dftLng : -123.24735386655271, //UBC COORD
    		displayMap: false

          
    };
})(jQuery);
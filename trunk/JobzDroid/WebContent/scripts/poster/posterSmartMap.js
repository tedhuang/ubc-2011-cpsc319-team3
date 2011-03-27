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
			
			var infoWindow = new google.maps.InfoWindow({});
			
			initMap();
			initAutoCplt();
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
			function placeMarkr(location){
//				var markr=new google.maps.Marker({
				var markr=new google.maps.Marker({
					position  : location,
					map		  : map,
					animation : google.maps.Animation.DROP,
					draggable : true
				});
				map.setCenter(location);
				addDragListener.call(markr);
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
					  console.log(markr.get("id"));
					  addDragListener.call(markr);
					  return markr;
				}
			}
			
			function addToLocList(addr,list, marker){
				var list="#"+list;
				
				var li=	$("<li></li>")
						.addClass("jsBtn")
						.data({"marker":marker.get("id"),
								"addr" : addr,
								"latlng":marker.getPosition().lat()+","+marker.getPosition().lng()})
						.click(function(){
							 loc = new google.maps.LatLng($(this).data("lat"), $(this).data("lng"));
					         map.panTo(loc);
						})
						.bind('udMarkrId', function(event, markerId){
							$(this).data("marker", markerId);
						})
						.bind('update', function(event, addr, latlng){
							$(this).find('span.addr').text(addr);
							$(this).data({"addr":addr});
							$(this).data({"latlng":latlng});
							console.log($(this).data("addr")+" - " +//TODO RM
										$(this).data("latlng"))	;
						})	
						.append('<span class="addr">'+addr+'</span>')
						.append('<span class="jsBtn rm">X</span>')
						.append('<span class="jsBtn info"> get loc info</span>')
						.delegate('span.rm','click',function(){
							$(this).parent().remove();
							locList.remove($(this).parent().get(0));
							rmMarkr.call(marker);
						})
						.delegate('span.info','click',function(){
							
							var info = $(this).parent().data("lat")+","+$(this).parent().data("lng");
							$("#feedback").html("").append(info);
						})
						.appendTo(list);
				locList.length>maxAddrNum? null:locList.push(li.get(0));//get(0) need to compare raw object
				console.log(li.data("latlng"));
				addDragListener.call(marker);

			}
			
			function rmMarkr(){ //remove marker from the map
				if(this){
					this.setMap(null);
				}
				markers.remove(this);
			}
			function initAutoCplt(){
				$("#address").autocomplete({
				      source: //use jquery UI auto complete function to get the address
				    	function(request, response) {
					        geocoder.geocode( {'address': request.term + ', CA' }, function(results, status) {
					          response($.map(results, function(item) {
					            return {
					              label:  	  item.formatted_address,
					              value: 	  item.formatted_address,
					              latitude:   item.geometry.location.lat(),
					              longitude:  item.geometry.location.lng()
					            };
					          }));
					        });
				      },
				      select: //on select desired location set the fields
				    	  function(event, ui) { 
					        $("#latitude").val(ui.item.latitude);
					        $("#longitude").val(ui.item.longitude);
					        var location = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
//					        placeMarkr(location);
					        var markr=addMarkr(location);
					        markr? addToLocList(ui.item.value, "locList", markr):null;
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
				
				$.fn.smartMap.resize=function(){//fix map when the map is resized or position changed
					google.maps.event.trigger(map, 'resize');
				};
				
				
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
						          $('#address').val(results[0].formatted_address);
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
					
//					$.each(locList, function(){
//						marker.getPosition().lat()==$(this).data("lat")&&
//			         	marker.getPosition().lng()==$(this).data("lng")?
//			         	li=$(this):null;
//					});
					google.maps.event.addListener(marker, 'drag', function() {
						
						geocoder.geocode({'latLng': marker.getPosition()}, function(results, status) {
							if (status == google.maps.GeocoderStatus.OK) {
								if (results[0]) {
									   	  addr = results[0].formatted_address,
									  	  lat  = marker.getPosition().lat(),
									  	  lng  = marker.getPosition().lng();
									  
									  $('#address').val(addr);
							          $('#latitude').val(lat);
							          $('#longitude').val(lng);
							          var li = locList[marker.get("id")];
							          $(li).trigger("update", [addr, lat+","+lng]);
							      }
							}
						});
				    });
					
				}//ENDOF ADDDRAGLISTENER
				function dragChangeListener(){
					var marker = this.markr;
					google.maps.event.addListener(marker, 'drag', function() {
						geocoder.geocode({'latLng': marker.getPosition()}, function(results, status) {
							if (status == google.maps.GeocoderStatus.OK) {
								if (results[0]) {
							          $('#address').val(results[0].formatted_address);
							          $('#latitude').val(marker.getPosition().lat());
							          $('#longitude').val(marker.getPosition().lng());
							          udLocListInfo.call(this.item,
							        		  			results[0].formatted_address, 
							        		  			marker.getPosition().lat(),
							        		  			marker.getPosition().lng() );
							          
							        }
							      }
							    });
							});
				}//ENDOF DRAGCHANGELISTENER
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
    		dftLng : -123.24735386655271 //UBC COORD

          
    };
})(jQuery);
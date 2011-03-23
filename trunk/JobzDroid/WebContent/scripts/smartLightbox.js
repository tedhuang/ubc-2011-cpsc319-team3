/*
 * LightBox
 * LightBox Plugin
 * Author: Cheng Chen
 * 
 */
(function($){
    $.fn.smartLightBox = function(options) {
       var options = $.extend({}, $.fn.smartLightBox.defaults, options);//keep default, only update from the "options"

        return this.each(function() {
        	
        		lightBox = $(this);
        		
//        		fullLightBox(buildInfoBox("test","alert"),true);
//        		divLightBox('loginbox','load','loading...');
   /************************STARTOF FUNCTION GROUP******************************************************************************/
        		function buildInfoBox(text, imgClass){
             		var infoBox= $('<div></div>').attr('id', 'info');
             		$('<img>').attr('id','lbImg').addClass(imgClass).appendTo(infoBox);
             		$('<h2></h2>').attr('id','lbMsg').text(text).appendTo(infoBox);
             		 return infoBox;
             	}
        		
        		function buildBtnBox(mode){
             	 	
        			var btnBox= $('<div></div>').attr('id', 'btnBox');
        			switch(mode){
        			case "closeNewAd":
        				$('<a></a>')
                 		.addClass("save jsBtn")
                 		.html("Save Draft")
                 		.appendTo(btnBox);
        				
        				$('<a></a>')
                 		.addClass("post jsBtn")
                 		.html("Post It")
                 		.appendTo(btnBox);
        				
                 		$('<a></a>')
                 		.addClass("no jsBtn")
                 		.html("keep writing")
                 		.appendTo(btnBox);
        				break;
        			
        			case "closeEdAd":
        				$('<a></a>')
                 		.addClass("save jsBtn")
                 		.html("Save Draft")
                 		.appendTo(btnBox);
        				
        				$('<a></a>')
                 		.addClass("post jsBtn")
                 		.html("Post It")
                 		.appendTo(btnBox);
        				
                 		$('<a></a>')
                 		.addClass("no jsBtn")
                 		.html("keep writing")
                 		.appendTo(btnBox);
        				break;
        				
        			default:
        				$('<a></a>')
                 		.addClass("yes jsBtn")
                 		.html("OK")
                 		.appendTo(btnBox);
                 		$('<a></a>')
                 		.addClass("no jsBtn")
                 		.html("Cancel")
                 		.appendTo(btnBox);
                 		
                 		btnBox.delegate('a.no', "click", function(){
                 			rmLightbox(0);
                 		});
        				break;
        			}
             		 return btnBox;
             	}
        		
        		function fullLightBox(infoBox){
     		    
     			// hide scrollbars!
     			$('body').css('overflow-y', 'hidden');
     			
     			$('<div id="overlay"></div>').addClass('whitebg')
     			.css('opacity', '0').animate({'opacity': '0.5'}, 1000)
     			.appendTo(lightBox);
     			
     			$('<div></div>').attr('id','midBox')
	 							.css('opacity', '0').animate({'opacity': '1'}, 1000)
	 							.appendTo(lightBox);
     			infoBox.appendTo('#midBox');
     			
     			lightBox
     			.addClass('on')
     			.css('top', $(document).scrollTop())
     			.appendTo('body');
     	}
     	
     	function smallLightBox(infoBox){
     	// hide scrollbars!
 			$('body').css('overflow-y', 'hidden');
 			$('<div id="overlay"></div>').appendTo(lightBox);
 			$('<div></div>').attr('id','midBox').css('opacity', '0').animate({'opacity': '1'}, 1000).appendTo(lightBox);
 			infoBox.appendTo('#midBox');
 			lightBox.addClass('on').css('top', $(document).scrollTop()).appendTo('body');
     	}
     	
     	function divLightBox(div,imgClass, msg){
//     		$("#"+div).css('overflow-y', 'hidden');
     		var lbClone=lightBox.clone();
     		
     		lbClone.addClass('on')
     			   .css('top', $(document).scrollTop())
     			   .appendTo("#"+div);
     		
     		$('<div id="overlay"></div>')
	     		.addClass('whitebg')
	 			.css('opacity', '0')
	 			.animate({'opacity': '0.5'}, 1000)
	 			.appendTo(lbClone);
     		
 			$('<div></div>').attr('id','divlb').appendTo(lbClone);
 			$('<img/>')
 				.attr('id','lbImg')
 				.addClass(imgClass)
 				.appendTo($("#divlb","#"+div));
 			$('<h2></h2>')
 				.attr('id','lbMsg')
 				.text(msg).css('opacity', '0')
 				.animate({'opacity': '1'}, 1000)
 				.appendTo($("#divlb","#"+div));
     	}
     	
     	function rmLightbox(dTime){
 			var time =(dTime==null?0:dTime);
 			$('#overlay, #midBox')
 			.delay(time)
 			.fadeOut('fast', function() {
 			$(this).remove();
 			$('body').css('overflow-y', 'auto'); // show scrollbars!
 			lightBox.removeClass();
 			});
 	};
 	
 	function rmDivlb(div, dTime){
 		   var time=(!dTime?0:dTime);
			$('#overlay, #divlb', "#"+div)
			.delay(time)
			.fadeOut("fast", function() {
//			$("#"+div).css('overflow-y', 'auto'); // show scrollbars!
			$(this).remove();
			$("#"+div).find("#lightBox").remove();
			});
	};
     	
     	$.fn.smartLightBox.openlb=function(mode, text, imgClass){
     		switch (mode){
     		
     			case "full":
     				fullLightBox(buildInfoBox(text,imgClass));
     				break;
     			case "small":
     				smallLightBox(buildInfoBox(text,imgClass));
     				break;
     			case "info":
     				smallLightBox(buildInfoBox(text,imgClass));
     	     		rmLightbox(2500);
     	     		break;
//     			case "diaBox":
//     				fullLightBox(buildInfoBox(text,imgClass),true);
//     				break;
     			default:
     				fullLightBox(buildInfoBox(text,imgClass));
 					break;
     		}
     	};
     	
     	
     	$.fn.smartLightBox.closeLightBox=function(dTime,div ){
 			if(!div){ rmLightbox(dTime);}
 			else	{rmDivlb(div, dTime);}
 		};
     	
     	$.fn.smartLightBox.openDivlb=function(div, imgClass, msg){
     		divLightBox(div, imgClass, msg);
     	};
     	
     	$.fn.smartLightBox.diaBox=function(msg,imgClass, mode ){
     	
     		$('body').css('overflow-y', 'hidden');
 			
 			$('<div id="overlay"></div>').addClass('whitebg')
 			.css('opacity', '0').animate({'opacity': '0.5'}, 1000)
 			.appendTo(lightBox);
 			
 			$('<div></div>').attr('id','midBox')
 							.css('opacity', '0').animate({'opacity': '1'}, 1000)
 							.appendTo(lightBox);
 			var infoBox = buildInfoBox(msg, imgClass);
 			infoBox.appendTo('#midBox');
 			if(!mode){
 				buildBtnBox().appendTo('#midBox');
 			}
 			else{
 				buildBtnBox(mode).appendTo('#midBox');
 			}
 			
 			lightBox
 			.addClass('dialog')
 			.css('top', $(document).scrollTop())
 			.appendTo('body');
				
     	};
     	
     	
  /************************ENDOF FUNCTION GROUP*********************************************/
        });  // ENDOF return Each
    };  //ENDOF smartLightBox
 
    $.fn.smartLightBox.defaults = {
    		lbClass: "on"
    };

})(jQuery);

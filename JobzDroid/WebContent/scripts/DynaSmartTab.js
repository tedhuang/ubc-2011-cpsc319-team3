/*
 * DynaSmartTab
 * jQuery Tab Control Plugin
 * Author: Cheng Chen
 * Inspired By "SmartTab", http://tech-laboratory.blogspot.com
 * 
 */
(function($){
    $.fn.DynaSmartTab = function(options) {
       var options = $.extend({}, $.fn.DynaSmartTab.defaults, options);//keep default, only update from the "options"

        return this.each(function() {
                obj = $(this);
                var curTabIdx = options.selected; // Set the current tab index to default tab
                var tabidPrefix = options.tabidPrefix;
                var frameidPrefix = options.frameidPrefix;
                var tabNum = 0;
                var tabs = updateTabSet(); // Get all anchors in this array
                var tabFrames = updateTabFrameSet(); // All Tab Frames
                var tabArray = updateTabArray(); // All <li> Tab elements  
                var closeBtn = updateCloseBtn();
                
                $(obj).addClass(options.tabPaneClass); // Set the CSS on top element	       
                hideAllFrames(); // Hide all content on the first load
     		    showTab();
     		    
   /************************STARTOF FUNCTION GROUP******************************************************************************/
     		   function updateCloseBtn(){
     			   closeBtn=$($(".close"),obj).get();
     			   bindCloseClick();
     			   return closeBtn;
               }
     		   
     		   function bindCloseClick(){
     		   	$(closeBtn).bind("click", function(){
     			  	var tabToRm = $($(this).parents().filter("a"),obj);
     			  	if(tabToRm.length>0){
     			  		$.each(tabToRm, function(){
     			  			$($(this).parent(), obj).remove();
     			  			$($(this).attr("href"), obj).remove();
     			  			return;
     			  		});
     			  	}
             	    curTabIdx=0;
             	    showTab();
             	  });
     		   }
     		   
                function bindOnClick()
                {
                	$(tabs).bind("click", function(e){
                        if(tabs.index(this)==curTabIdx)
                          return false;
                        var prevTabIdx = curTabIdx;
                        curTabIdx = tabs.index(this);
                        hideTab(prevTabIdx);
                        showTab();
                        return false;
                    });
                }
                
                function updateTabSet(){
                	tabs = $("ul > li > a", obj);
                	bindOnClick();
                	tabNum=tabs.length;
                	return tabs;
                }
                
                function updateTabArray(){ // TODO MERGE with updateTabSet
                	return tabArray = $("ul > li", obj).get();
                	
                }
                
                function updateTabFrameSet(){
                	return tabFrames = $("#tabFrame",obj).find('div').get();
                }
                
                function hideAllFrames(){
                	$(tabFrames, obj).each(function(){
                      $($(this, obj), obj).hide();
                });
                }
                
                function showTab(){
                    var curTab = tabs.eq(curTabIdx); 
                    $(tabs, obj).removeClass("curTab");
                    $($(curTab, obj), obj).addClass("curTab");
                    $($(curTab, obj).attr("href"), obj).show();
                    return true;
                }
                
                function hideTab(tabIdx){
                    var curTab = tabs.eq(tabIdx);
                    $($(curTab, obj).attr("href"), obj).hide();
//                    showTab();
                    return true;
                }
                Array.prototype.last = function() {return this[this.length-1];};
         /*******************************************************************************************************
          * 		ADD TAB FUNCTION
          * -Publicly Accessible
          * @params title: the title of the tab
          * ******************************************************************************************************/  
                $.fn.DynaSmartTab.addTab=function(title){ //TODO PASS "DESCRIPTION" AS WELL
                	
            	var tabid = tabidPrefix+ title;
            	var frameid = frameidPrefix + title;
            	var frameContentid = frameid +"_content";
            	
                //Check if the Tab with the title is already opened
                  if(tabNum<options.MAX_TAB_NUM){
                	var prevTabIdx = curTabIdx;
                	var found;
                	updateTabArray();
                	updateTabFrameSet();
                	
                	$.each(tabArray,function(index){
                		if($($(this),obj).attr("id") == tabid){
                			curTabIdx= index;
                			hideTab(prevTabIdx);
                			showTab();
                			found = true;
                			return;
                		}
                		
                	});
                	
                	if(!found){

                		$.each(tabFrames, function(){
                    		if($($(this),obj).attr("id")== frameid){
                    			$(this).remove();
                    			return;
                    		}
                    	});
                		
	                	var lastTab = $('li:last',obj);
	                	var lastFrame = $(tabFrames.last());
	                	// hide current tab
	                	hideTab(curTabIdx); 
	                	curTabIdx=tabs.length;
	                	// make a new tab
	                    lastTab.after('<li id="'+  tabid + '">' +
	                    			  '<a href=#'+ frameid + '>' + 
	                    			  '<span class="close">X</span>' +
	                    			  '<img class=logoImage2 border=0 width=50px src=images/Step1.png>'+
	                    			  '<h2>' + title + '<br /><small>This is tab\'s description</small>'+
	                    			  '</h2></a></li>');
	                    // make a new frame
	                    lastFrame.after('<div class="tabFrame" id="' + frameid + '">THIS IS THE TAB FRAME' +
	                    				'</div>');
	                    $($("#"+frameid),obj).append('<div id="' + frameContentid + '"></div>');
	                    $($("#"+frameContentid),obj).html("This is the Content of Tab"+ frameContentid);
	                    updateTabSet();
	                    updateCloseBtn();
	                    showTab();
	                    return true;
                	}//ENDOF NEW TAB CREATION
                  }//ENDOF TABNUM CHECK IF
                  else{
                	  //TODO ADD TAB FULL NOTIFICATION
                  }
                };
                
  /************************ENDOF FUNCTION GROUP*********************************************/
        });  // ENDOF return Each
    };  //ENDOF DynaSmartTab
 
    // Defaults jQuery(this).animate({width: 'show'}); jQuery(this).animate({width: 'hide'});
    $.fn.DynaSmartTab.defaults = {
          selected: 0,  // Selected Tab, 0 = first step 
          tabidPrefix: 'tab_',
          frameidPrefix: 'frame_',
          MAX_TAB_NUM: 4,
          tabPaneClass:'tabPane' // tab container css class name
          
    };

})(jQuery);

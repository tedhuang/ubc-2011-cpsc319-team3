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
     			  	var tabToRm = $($(this).parent().find("a"),obj);
     			  	if(tabToRm.length>0){
     			  		$.each(tabToRm, function(){
     			  			
     			  			if($($(this).parent(), obj).hasClass("hideOnly")){
     			  				$($(this).parent(), obj).hide();
     			  			}
     			  			else{
     			  				$($(this).parent(), obj).remove();
     			  			}
     			  			
     			  			if($($(this).attr("href"), obj).hasClass("unremovable")){
     			  				$($(this).attr("href"), obj).hide();
     			  			}
     			  			else{
     			  					$($(this).attr("href"), obj).remove();
     			  			}
     			  		});
     			  	}
     			  	tabNum--;
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
                	tabs = $($("ul > li > a ","#navBar"), obj);
                	bindOnClick();
                	tabNum=tabs.length;
                	return tabs;
                }
                
                function updateTabArray(){ // TODO MERGE with updateTabSet
                	return tabArray = $($("ul > li","#navBar"), obj).get();
                	
                }
                
                function updateTabFrameSet(){
                	return tabFrames = $("#tabFrame",obj).find('div.subFrame').get(); //TODO Will have problem
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
                    return false;
                }
                
                function hideTab(tabIdx){
                    var curTab = tabs.eq(tabIdx);
                    $($(curTab, obj).attr("href"), obj).hide();
//                    showTab();
                    return true;
                }
                
                $.fn.DynaSmartTab.showUnremovableTab=function(tabName){
                	
                	var tabToOpen = $('#'+tabName);
                	var frameToOpen = $(tabToOpen.find('a').attr("href"));
                	
                	if(tabToOpen.length>0 && frameToOpen.length >0){
                		
	                    var curTab = tabs.eq(curTabIdx); 
	                    
	                    var lastTab = $($('li:last',"#navBar"),obj);
	                	var lastFrame = $(tabFrames.last());
	                	// hide current tab
	                	hideTab(curTabIdx); 
	                	curTabIdx=tabs.length-1;
	                	if(lastTab.find('a').attr("id")!=tabToOpen.find('a').attr("id") 
	                			&& 
	                		lastFrame.find('a').attr("id")!=frameToOpen.find('a').attr("id"))
	                	{
	                		lastTab.after(tabToOpen);
	                    	lastFrame.after(frameToOpen);
	                	}
	                	
	                    showTab();
	                    return false;
                }
               };
                Array.prototype.last = function() {return this[this.length-1];};
         /*******************************************************************************************************
          * 		ADD TAB FUNCTION
          * -Publicly Accessible
          * @params title: the title of the tab
          * ******************************************************************************************************/  
             $.fn.DynaSmartTab.addTab=function(title){ //TODO PASS "DESCRIPTION" AS WELL
                
                titleId=title.replace(/\s/g,"");
            	var tabid = tabidPrefix+ titleId;
            	var frameid = frameidPrefix + titleId;
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
                    		if($($(this),obj).attr("id")== frameid){//make sure the old tabFrame is removed
                    			$(this).remove();
                    			return false;
                    		}
                    	});
                		
	                	var lastTab = $($('li:last',"#navBar"),obj);
	                	var lastFrame = $(tabFrames.last());
	                	// hide current tab
	                	hideTab(curTabIdx); 
	                	curTabIdx=tabs.length;
	                	// make a new tab
	                    lastTab.after('<li id="'+  tabid + '">' +
	                    			  '<a href=#'+ frameid + '>' + 
	                    			  '<span class="close">X</span>' +
	                    			  '<h2>' + title + '</h2></a></li>');
	                    // make a new frame
	                    lastFrame.after('<div class="subFrame" id="' + frameid + '"></div>');
	                    $($("#"+frameid),obj).append('<div id="' + frameContentid + '"></div>');
	                    $($("#"+frameContentid),obj).html("This is the Content of Tab"+ frameContentid);
	                    updateTabSet();
	                    updateCloseBtn();
	                    showTab();
	                    return false;
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

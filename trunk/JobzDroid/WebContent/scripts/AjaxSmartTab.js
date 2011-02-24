/*
 * AjaxSmartTab
 * jQuery Tab Control Plugin
 * Author: Cheng Chen
 * Inspired By "SmartTab", http://tech-laboratory.blogspot.com
 * 
 */
 
(function($){
    $.fn.AjaxSmartTab = function(options) {
       var options = $.extend({}, $.fn.AjaxSmartTab.defaults, options);//keep default, only update from the "options"

        return this.each(function() {
                obj = $(this);
                var curTabIdx = options.selected; // Set the current tab index to default tab
                var tabs = updateTabSet(); // Get all anchors in this array
                var tabFrames = updateTabFrameSet(); // All Tab Frames

                $(obj).addClass(options.tabContainerClass); // Set the CSS on top element		       
                hideAllFrames(); // Hide all content on the first load
     		    showTab();
     		    
 /************************STARTOF FUNCTION GROUP******************************************************************************/

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
      		  
      		 function updateTabFrameSet(){
             	return tabFrames = $("#tabFrame",obj).find('div').get();
             }
                
//                if(options.keyNavigation){
//                    $(document).keyup(function(e){
//                        if(e.which==39){ // Right Arrow
//                          doForwardProgress();
//                          if(options.autoProgress){
//                            restartAutoProgress();
//                          }
//                        }else if(e.which==37){ // Left Arrow
//                          doBackwardProgress();
//                          if(options.autoProgress){
//                            restartAutoProgress();
//                          }
//                        }
//                    });
//                }
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
                showTab();
                return true;
            }
                
        });  // ENDOF RETURN EACH
    };  // ENDOF AJAXSMARTTAB
 
    // Defaults jQuery(this).animate({width: 'show'}); jQuery(this).animate({width: 'hide'});
    $.fn.AjaxSmartTab.defaults = {
          selected: 0,  // Selected Tab, 0 = first step   
          keyNavigation:true, // Enable/Disable key navigation(left and right keys are used if enabled)
          transitionEffect:'none', // Effect on navigation, none/fade/slide
          tabContainerClass:'verticalTabs' // tab container css class name
    };

})(jQuery); // ENDOF CLASS

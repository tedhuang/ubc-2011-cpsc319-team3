/*
 * DynaSmartTab
 * jQuery Tab Control Plugin
 * Author: Cheng Chen
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
                var closeBtn;
                var tabs ; 		 // Get all anchors in this array
                var tabFrames ; // All Tab Frames
                var tabIdList; // All TabID Array   
                refreshTabs();
                
                $(obj).addClass(options.tabPaneClass); // Set the CSS on top element
                
                bindFuncToBtn();//bind function to btns
                
                hideAllFrames(); // Hide all content on the first load
     		    showTab();
     		    initHome();
     		   
   /************************STARTOF FUNCTION GROUP******************************************************************************/
     		   function initHome(){
//     			  $.fn.smartLightBox.openDivlb("home-frame",'load','loading...');
     			  getJobAdByOwner("ownerAdTable");
//     			 $.fn.smartLightBox.closeLightBox(1000,"home-frame");
     			  
     		   }
     		    function refreshTabs(){
     			   //update to the latest tab info, any change involving position-changing, visual-changing need to call it at the end  
	     			  tabs 		= updateTabSet(); // Get all anchors in this array
	                  tabFrames = updateTabFrameSet(); // All Tab Frames
	                  tabIdList = updatetabIdList(); // All TabID Array   
	                  closeBtn	= updateCloseBtn();
     		   }

               function updateTabSet(){
	               	tabs = $($("ul > li > a ","#navBar"), obj);
	               	bindOnClick();
	               	tabNum=tabs.length;
	               	return tabs;
               }
               
               function updatetabIdList(){ 
	               	var IdList =new Array();
	               	var tabArray = $($("ul > li","#navBar"), obj).get();
	               	$.each(tabArray, function(){
	               		IdList.push($(this).attr("id"));
	               	});
	               	return IdList;
               }
               
               function updateTabFrameSet(){
               		return tabFrames = $("#tabFrame",obj).find('div.subFrame').get(); //TODO Will have problem
               }
     		    function updateCloseBtn(){
     			   closeBtn=$($(".close"),obj).get();
     			   bindCloseClick();
     			   return closeBtn;
               }
     		   function bindFuncToBtn(){
     			   $('.newadbtn')
     			   .attr('title', 'Compose New Ad')	
     			   .bind("click", function(){
     			   		openTab('newAdTab'); 
     			   		open_newAd_form();
     			   	});
     		   }
     		   
     		   function bindCloseClick(){
     		   	$(closeBtn).unbind('click').bind("click", function(){
     		   	var kpwrt=true;
     		   	 if($(this).hasClass('confirmReq')){
     		   		 var tabToRm=$(this);
     		   		$.fn.smartLightBox.diaBox("you have unsaved data here, do you want to save?", "alert", "closeNewAd");
     		   		$('a.save', "#btnBox").click(function(){
						$("#btnBox", "#lightBox").hide();
						$("#lbImg", "#lightBox").removeClass("alert").addClass("load");
						$("#lbMsg","#lightBox").html("Saving Draft");
						postJobAd("draft", "newAdForm", "feedback");
						closeTab.call(tabToRm);
					});
	     		   	$('a.notsave', "#btnBox").click(function(){
	     		   		$.fn.smartLightBox.closeLightBox(0);
	     		   		closeTab.call(tabToRm);
					});
	     		   		
     		   	 }
     		   	 else{
     		   		 closeTab.call(this);
     		   	 }
     		   	});
     		   }
     		   function closeTab(){
     			  tabToRm = $($(this).parent().find("a"),obj);
     		   	  if(tabToRm.length){
 			  		$.each(tabToRm, function(){
 			  			
 			  			if($($(this).parent(), obj).hasClass("hideOnly")){
 			  				$($(this).parent(), obj).hide();
 			  			}
 			  			else{
 			  				$($(this).parent(), obj).remove();
 			  			}
 			  			
 			  			if($($(this).attr("href"), obj).hasClass("unremovable")){
 			  				$($(this).attr("href"), obj).empty().hide();
 			  			}
 			  			else{
 			  					$($(this).attr("href"), obj).remove();
 			  			}
 			  		});
     		   	  }
     		   	refreshTabs();
 			  	tabNum--;
         	    curTabIdx=0;
         	    showTab();
     		   }
     		   function autoCloseTab(){
     			  
     				tabToRm = tabs.eq(curTabIdx); 
     			  	if(tabToRm.length){
     			  		$.each(tabToRm, function(){
     			  			
     			  			if($($(this).parent(), obj).hasClass("hideOnly")){
     			  				$($(this).parent(), obj).hide();
     			  			}
     			  			else{
     			  				$($(this).parent(), obj).remove();
     			  			}
     			  			
     			  			if($($(this).attr("href"), obj).hasClass("unremovable")){
     			  				$($(this).attr("href"), obj).empty().hide();
     			  			}
     			  			else{
     			  					$($(this).attr("href"), obj).remove();
     			  			}
     			  		});
     			  	}
     			    refreshTabs();
     			  	tabNum--;
             	    curTabIdx=0;
             	    showTab();
     			  	
             	  }
     		   
                function bindOnClick()
                {
                	$(tabs).bind("click", function(e){
                        if(tabs.index(this)==curTabIdx)
                          return false;
                        var prevTabIdx = curTabIdx;
                        curTabIdx = tabs.index(this);
                        hideFrame(prevTabIdx);
                        showTab();
                        return false;
                    });
                	
                }
                
                function hideAllFrames(){
                	$(tabFrames, obj).each(function(){
                      $($(this, obj), obj).hide();
                	});
                }
                
                function showTab(){
                	var curTab = tabs.eq(curTabIdx); 
                    $(tabs, obj).removeClass("curTab");
//                    $($(curTab, obj).parent(), obj).show().addClass("curTab");
                    curTab.addClass("curTab");
                    curTab.parent().show();
                    $($(curTab, obj).attr("href"), obj).show();
                    return true;
                }
                
/*****************************************************************************************************************************
 * 										Show TAB FUNCTION
 * -Publicly Accessible
 * @params tabId: the Id of the tab
 * ****************************************************************************************************************************/  
                function openTab(tabId){
                	   var openingTab = $('#'+tabId);
                	   var openingFrame = $(openingTab.find('a').attr("href"));
                	   var curTab = tabs.eq(curTabIdx); 
                       
                   	if(openingTab.length && openingFrame.length){//if found
                   	  if(curTab.attr("id") != openingTab.attr("id") && openingTab.css("display")=="block"){//not closed
                   		hideFrame(curTabIdx);
                   		curTabIdx = $.inArray(openingTab.attr("id"), tabIdList);
                        showTab();
                        return false;
                   	  }
                   	  else{	
   	                    var lastTab = $($('li:last',"#navBar"),obj);
   	                	var lastFrame = $(tabFrames.last());
   	                	
   	                	if(lastTab.attr("id") != openingTab.attr("id") 
   	                		&& 
   	                	   lastFrame.attr("id")!= openingFrame.attr("id")){
   	                		
   	                		openingTab.insertAfter(lastTab);
   	                		openingFrame.insertAfter(lastFrame);
   	                		
   	                	}
                    	hideFrame(curTabIdx);
                    	refreshTabs();
                    	curTabIdx = $.inArray(openingTab.attr("id"), tabIdList);
                    	showTab();                   	  }
                  }return false;
                }//ENDOF SHOWTAB
                
                function hideFrame(tabIdx){
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
                
                titleId=title.replace(/\s/g,"");
            	var tabid = tabidPrefix+ titleId;
            	var frameid = frameidPrefix + titleId;
            	var frameContentid = frameid +"_content";
            	
                //Check if the Tab with the title is already opened
                  if(tabNum<options.MAX_TAB_NUM){
                	var prevTabIdx = curTabIdx;
                	var found;
                	updatetabIdList();
                	updateTabFrameSet();
                	
                	$.each(tabIdList,function(index){
                		if($($(this),obj).attr("id") == tabid){
                			curTabIdx= index;
                			hideFrame(prevTabIdx);
                			showTab();
                			found = true;
                			return false;
                		}
                 		
                	});
                	
                	if(!found){

                		$.each(tabFrames, function(){
                    		if($($(this),obj).attr("id")== frameid){//make sure the old tabFrame is removed
                    			$(this).remove();
                    		}
                    	});
                		
	                	var lastTab = $($('li:last',"#navBar"),obj);
	                	var lastFrame = $(tabFrames.last());
	                	// hide current tab
	                	hideFrame(curTabIdx); 
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
	                    refreshTabs();
	                    showTab();
	                    return false;
                	}//ENDOF NEW TAB CREATION
                  }//ENDOF TABNUM CHECK IF
                  else{
                	  //TODO ADD TAB FULL NOTIFICATION
                  }
                };
    /*********************************************************************************************************************
     * 						floatingTool
     * - Add FLOATING TOOL BAR TO THE LIST table row and bind them with different functions
     * - Publicly Accessible
     *
     * ********************************************************************************************************************/ 
                $.fn.DynaSmartTab.floatingTool=function(tRow, adId){
         			
         			var tool= $('<span></span>').addClass('edTool');
         			$('<a></a>').addClass('jsBtn').addClass('view').text('view | ').appendTo(tool);
         			$('<a></a>').addClass('jsBtn').addClass('edit').text('edit | ').appendTo(tool);
         			$('<a></a>').addClass('jsBtn').addClass('del').text('Delete').appendTo(tool);
         			tRow.hover(function() {
         		        tool.animate({opacity: "show", left: "0"}, 0);
         		    }, function() {
         		        tool.animate({opacity: "hide", left: "0"}, 0);
         		    });
         			tool.unbind('click').appendTo(tRow);
         			
//         			tRow.find('a.edit').click(function(){ loadEdit(adId);});
         			var status=tRow.find('td.td-status').text();
         			tRow.delegate("a.edit", "click", function(){
         				openTab('edAdTab');
                 		open_edAd_form(status);
         				getJobAdById("edit",adId, "edAdForm" );
         				
         			});
         			tRow.delegate('a.view', "click", function(){
         				openTab('adDetailTab'); 
            			open_adDetail(adId);
            			getJobAdById("detail",adId, "adDetailTable");
         				
         			});
         			tRow.delegate('a.del', "click", function(){
         				$.fn.smartLightBox.diaBox("are you sure you want to delete this ad?", "alert");
     					$('a.yes', "#btnBox").click(function(){
     						$("#btnBox", "#lightBox").hide();
     						$("#lbImg", "#lightBox").removeClass("alert").addClass("load");
     						$("#lbMsg","#lightBox").html("Deleting Your Ad...");
     						delJobAd(tRow, adId);
     					});
         			});
         };
 /************************************************************************************************************************
  * 
  ************************************************************************************************************************/
         function open_newAd_form(){
        	 $("#newAdFrame").load("DOMs/formDOM.jsp #newAdForm",function(){//TODO move this to server side for security reason
         		$( "#startTime-field","#newAdFrame" ).datepicker({ minDate: "+1M", maxDate: "+3M +10D" });
         		$( "#expireTime-field" ,"#newAdFrame").datepicker({minDate: "+1M", maxDate: "+3M"	});//ad expires in max 3 months
         		$("#mapPanel", "#newAdFrame").smartMap({});
         		$(".btn-slide").click(function(){
       			 $("#mapPanel").slideToggle("slow");
       	 			$.fn.smartMap.resize();
       	 			$(this).toggleClass("active"); return false;
       	    });
         	});
        }
        
        function open_edAd_form(status){
        	$("#edAdFrame").load("DOMs/formDOM.jsp #edAdForm",function(){//TODO move this to server side for security reason
        		$.fn.smartLightBox.openDivlb("edAdFrame", 'load','loading data...');
        		$( "#startTime-field","#edAdFrame" ).datepicker({ minDate: "+1M", maxDate: "+3M +10D" });
        		$( "#expireTime-field" ,"#edAdFrame").datepicker({minDate: "+1M", maxDate: "+3M"	});//ad expires in max 3 months
        		bindHeadToolBar("edAdTool", status);
        	});
        }
        function bindHeadToolBar(toolBarId, status){
        	var toolBar=$("#"+toolBarId);
        	
        	switch(toolBarId){
        	
        	case "edAdTool":
        		
        		toolBar.delegate('a.ed_saveDraft', "click", function(){
        			status=="draft"?
        			postJobAd("updateDraft",'edAdForm','edAdfb'):postJobAd('draft', 'edAdForm','edAdfb');
        		});
        		toolBar.delegate('a.ed_update', "click", function(){
        			postJobAd('edit', 'edAdForm','edAdfb');//TODO CHANGE UPDATE DRAFT
        		});
        		toolBar.delegate('a.ed_reset', "click", function(){
        			$.fn.smartLightBox.diaBox("you unsaved data will be reset continue?", "alert");
 					$('a.yes', "#btnBox").click(function(){
 						resetFields('edAdForm');
 						$.fn.smartLightBox.closeLightBox(0);
 					});
        		});
        	
        	}
        	
        }
        function open_adDetail(adId){
       	 $("#adDetailFrame").load("DOMs/formDOM.jsp #adDetailFrame",function(){//TODO move this to server side for security reason
       		$.fn.smartLightBox.openDivlb("adDetailFrame", 'load','loading data...');
       		$("div.headToolBar").unbind('click');
       		
       		$("div.headToolBar", "#adDetailFrame").delegate("a.edit", "click", function(){
 				openTab('edAdTab');
         		open_edAd_form(status);
 				getJobAdById("edit",adId, "edAdForm" );
 			});
       		
       		$("div.headToolBar", "#adDetailFrame").delegate('a.del', "click", function(){
 				$.fn.smartLightBox.diaBox("are you sure you want to delete this ad?", "alert");
					$('a.yes', "#btnBox").click(function(){
						$("#btnBox", "#lightBox").hide();
						$("#lbImg", "#lightBox").removeClass("alert").addClass("load");
						$("#lbMsg","#lightBox").html("Deleting Your Ad...");
						delJobAd(tRow, adId);
					});
        	});
       	 });
       }
        $.fn.DynaSmartTab.close=function(){
        	autoCloseTab();//for close tab after action performed
        };
//     function loadEdData(targetXMLTag, edFormContainer, mode){
        $.fn.DynaSmartTab.loadEdData=function(targetXMLTag, edFormContainer, mode){
    	 var xmlData= $(targetXMLTag,xmlhttp.responseXML);
        	
	        	switch(mode){
	        	
	        	case "edit":
	        	$("input[name='title-field']", "#"+edFormContainer).val(xmlData.attr("jobAdTitle"));
	        	$("input[name='adId-field']", "#"+edFormContainer).val(xmlData.attr("jobAdId"));
				$("input[name='company-field']", "#"+edFormContainer).val(xmlData.attr("contactInfo"));
				$("input[name='tag-field']", "#"+edFormContainer).val(xmlData.attr("tags"));
				$("textarea[name='desc-field']", "#"+edFormContainer).val(xmlData.attr("jobAdDescription"));//Type-in Forms
				
				$("#edu-field option","#"+edFormContainer).each(function(){
					if($(this).text()==xmlData.attr("eduReqFormatted")){
						$(this).attr("selected", "selected");
						return false;
					}
				});
				$("input[name='startTime-field']", "#"+edFormContainer).val(xmlData.attr("startingDateFormatted"));
				$("input[name='expireTime-field']", "#"+edFormContainer).val(xmlData.attr("expiryDateFormatted"));
				
				$($(":input","#jobAvailField"), "#"+edFormContainer).each(function(){
					console.log($(this).val().toLowerCase());
					console.log(xmlData.attr("jobAvail").replace(/\s/g,"").toLowerCase());
					if($(this).val().toLowerCase()==xmlData.attr("jobAvail").replace(/\s/g,"").toLowerCase()){
						$(this).attr('checked', true);
						return false;
					}
				});
				$.fn.smartLightBox.closeLightBox(0, $("#"+edFormContainer).parent(".subFrame").attr('id'));
				break;
          }//ENDOF SWITCH
      };
     
  /************************ENDOF FUNCTION GROUP*********************************************/
        });  // ENDOF return Each
    };  //ENDOF DynaSmartTab
 
    // Defaults jQuery(this).animate({width: 'show'}); jQuery(this).animate({width: 'hide'});
    $.fn.DynaSmartTab.defaults = {
          selected: 0,  // Selected Tab, 0 = first step 
          tabidPrefix: 'tab_',
          frameidPrefix: 'frame_',
          MAX_TAB_NUM: 6,
          tabPaneClass:'tabPane' // tab container css class name
          
    };

})(jQuery);

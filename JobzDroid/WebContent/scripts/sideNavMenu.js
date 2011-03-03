(function($){
	$.fn.sideNavMenu=function(options){
		var options = $.extend({}, $.fn.sideNavMenu.defaults, options);
		
		
		return this.each(function(){
			obj = $(this);
			var topMenus = $("li > a", obj).get(); //get all top menus
			var subMenus = $("li > ul", obj).get();
			var sliding = options.sliding;
			
			
			
			$(topMenus).bind("click", function(e){ //bind the click behavior to the top menu
				var selfClick = $(this).next().is(':visible');
				if(!selfClick){
					$(this)
					.parent()
					.siblings()
					.find('> ul:visible')
					.slideToggle();
				}
				
				$(this).next().slideToggle();
				return false;
			});
			
		  if(sliding){
				 obj.bind("mouseenter", function(e){
					$(this).stop(true).animate(
							{
								'left': '0px'
							},
							"slow",
							"swing"
					);
					});
					
				obj.bind("mouseleave", function(e){
					hideNavMenu();
					
				});
				
				hideNavMenu();
		  }
		/********************INIT********************************************************/	
			hideAllSubs();
			
			
 /*********************STARTOF FUNCTION GROUP*************************************************/
		 function hideAllSubs(){
			 
			 $(subMenus, obj).each(function(){
				 $($(this), obj).hide();
			 });
		 }
		 
		 function showNavMenu(){
			 
		 }
		 
		 function hideNavMenu(){
			 obj
			 .delay(3500)
			 .animate(
					 {
						 'left' : '-150px'
					 },
					 
					 2000,
					 "swing",
					 
					 function(){
						 hideAllSubs(); 
			});
		 }
		
		 function resetTimer(){
			 
		 }
			
		}); //ENDOF RETURN THIS.EACH
	}; //ENDOF sideNavMenu
	
	$.fn.sideNavMenu.defaults = {
//		fix: false,
		sliding: true
//		
	};
})(jQuery);

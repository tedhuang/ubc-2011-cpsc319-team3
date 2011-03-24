(function($){
	$.fn.sideNavMenu=function(options){
		var options = $.extend({}, $.fn.sideNavMenu.defaults, options);
		
		
		return this.each(function(){
				navMenu  = $(this);
			var topMenus = $("li > a.topMenu", navMenu).get(); //get all top menus
			var subMenus = $("li > ul", navMenu).get();
			var sliding  = options.sliding;
			
			
			
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
				 navMenu.bind("mouseenter", function(e){
					$(this).stop(true).animate(
							{
								'right': '0px'
							},
							"slow",
							"swing"
					);
					});
					
				navMenu.bind("mouseleave", function(e){
					hideNavMenu();
					
				});
				
				hideNavMenu();
		  }
		/********************INIT********************************************************/	
			hideAllSubs();
			
			
 /*********************STARTOF FUNCTION GROUP*************************************************/
		 function hideAllSubs(){
			 
			 $(subMenus, navMenu).each(function(){
				 $($(this), navMenu).hide();
			 });
		 }
		 
		 function showNavMenu(){
			 
		 }
		 
		 function hideNavMenu(){
			 navMenu
			 .delay(1700) //Delay hiding
			 .animate(
					 {
						 'right' : '-121px'
					 },
					 
					 1500,
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

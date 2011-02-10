/*
 * Dock Menu plug-in 
 * Author: Cheng Chen
 * For UBC CPSC319 Project JobzDroid
 */

(function($){
	$.fn.dockNav = function(args){//def of the plugin: dockNav, "args" are the parameters 
		args=$.extend( { iconSize:48, scale: 2, speed: 300, tips: true /*,ajax: false*/}, args);
		this.each(function(){ //iterate through the args
			var $menu=$(this);
			var iconArray = $menu.find('li a').get();
			var numIcons = iconArray.length;
			//var marginMove = 
			var iconSizeXL = args.iconSize * args.scale;
			var iconSizeL = args.iconSize * ((args.scale+1)/2);//neighbor icon size
			var dockSize = numIcons*args.iconSize; //this plogin will explore the height, maybe not needed
			var dockSizeL = 1+ dockSize + iconSizeXL + (args.iconSize*(numIcons-1)); 
			if(args.tips){
				var tips=new Array();
				for ( i=0;i<=numIcons;i++)
				{
					tips[i]= $('li a img:eq('+i+')').attr("alt");
					$menu.find('li a img:eq('+i+')').after('<span class="tips">'+tips[i]+'</span>');
				}
				$menu.find('span.tips').animate( {opacity:0},0,'linear');
			}
	function mouseOver(){
			var curIndex=$menu.find('li a').index(this);
//			if(curIndex==0||curIndex==(numIcons-1)){
//				$menu.stop().animate({height: dockSizeL}, args.speed, 'linear');
//			}
//			else{
//				$menu.stop().animate({height: dockSizeXL}, args.speed, 'linear');
//			}
			//enlarge icon
			$(this).stop().animate(
					{ width: iconSizeXL, height: iconSizeXL, marginTop: 0}, args.speed, 'linear',
					  function(){
									if(args.tips){
										$(this).find('span.tips').stop().animate( {opacity:1}, args.speed,'linear');
									}
								});
			//semi-enlarge the neighbors
			if(curIndex>0){
				$menu.find('li a: eq('+(curIndex-1)+')').stop().animate(
									{width: iconSizeL, height: iconSizeL}, args.speed, 'linear');
			}
			if(curIndex< (numIcons-1)){
				$menu.find('li a: eq('+(curIndex+1)+')').stop().animate(
						{width: iconSizeL, height: iconSizeL}, args.speed, 'linear');
			}
			
	};//end of mouseOver
	
	function mouseLeft(){
		$(this).stop().animate({width: args.iconSize, height:args.iconSize}, args.speed, 'linear');
		var outIndex = $menu.find('li a').index(this);
		if(outIndex>0){
			$menu.find('li a: eq('+(outIndex-1)+')').stop().animate({width: args.iconSize, height: args.iconSize}, args.speed, 'linear');
		}
		if(outIndex<(numIcon-1)){
			$menu.find('li a: eq('+(outIndex+1)+')').stop().animate({width: args.iconSize, height: args.iconSize}, args.speed, 'linear');
		}
		if(args.tips){
			$menu.find('span.tips').stop().animate({opacity:0}, args.speed, 'linear');
		}
	};//end of mouseLeft
	$menu.find('li a').bind('mouseleave', mouseLeft);
	$menu.find('li a').bind("mouseenter", mouseOver);
		});//end of each function
		
		return this;//plugin return
	};//end of "dockNav"
})(jQuery);//end of the plugin